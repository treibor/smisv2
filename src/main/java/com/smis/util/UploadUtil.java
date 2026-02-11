package com.smis.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.tika.Tika;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.DomEventListener;
public class UploadUtil {

    private static final long MAX_SIZE_BYTES = 5L * 1024L * 1024L; // 5MB
    private static final String PDF_MIME = "application/pdf";

    private UploadUtil() { }

    public static Upload createPdfUpload(AtomicReference<byte[]> uploadedPdfRef,
                                         String uploadText,
                                         String buttonText) {

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setReceiver(buffer);

        // Upload button
        Button uploadButton = new Button(buttonText);
        uploadButton.getStyle().set("font-size", "12px");
        upload.setUploadButton(uploadButton);

        // Optional helper text
        if (uploadText != null && !uploadText.isBlank()) {
            upload.setDropLabel(new Span(uploadText));
        }

        // Constraints
        upload.setMaxFiles(1);
        upload.setMaxFileSize((int) MAX_SIZE_BYTES);

        // Vaadin accepted types works best with extension filters
        upload.setAcceptedFileTypes(".pdf");

        // Rejects (size / type)
        upload.addFileRejectedListener(e ->
                Notification.show("Invalid File: Please select only PDF files less than 5MB",
                                3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR)
        );

        // Success handler (secure validation)
        attachUploadListener(upload, buffer, uploadedPdfRef);

        // Reset when user removes file from the list (Vaadin 24 compatible)
        upload.getElement().addEventListener("file-remove",
                (DomEventListener) (DomEvent event) -> resetUploadComponent(upload, uploadedPdfRef));

        // Reset on failed upload too
        upload.addFailedListener(e -> resetUploadComponent(upload, uploadedPdfRef));

        // Start clean
        resetUploadComponent(upload, uploadedPdfRef);

        return upload;
    }

    private static void attachUploadListener(Upload upload,
                                             MemoryBuffer buffer,
                                             AtomicReference<byte[]> uploadedPdfRef) {

        upload.addSucceededListener(event -> {
            try (InputStream in = buffer.getInputStream()) {

                byte[] bytes = in.readAllBytes();

                if (bytes.length == 0) {
                    throw new SecurityException("Empty file");
                }
                if (bytes.length > MAX_SIZE_BYTES) {
                    throw new SecurityException("File too large");
                }

                // 1) Extension check
                String fileName = event.getFileName();
                if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
                    throw new SecurityException("Invalid extension");
                }

                // 2) MIME check (Tika) on bytes
                Tika tika = new Tika();
                String detected = tika.detect(bytes);
                if (!PDF_MIME.equals(detected)) {
                    throw new SecurityException("Invalid MIME: " + detected);
                }

                // 3) PDF magic header check
                String header = new String(bytes, 0, Math.min(bytes.length, 5), StandardCharsets.ISO_8859_1);
                if (!header.startsWith("%PDF-")) {
                    throw new SecurityException("Invalid PDF header");
                }

                // ✅ ok
                uploadedPdfRef.set(bytes);

            } catch (SecurityException ex) {
                Notification.show("Invalid PDF file. Upload a genuine PDF only.",
                                4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                resetUploadComponent(upload, uploadedPdfRef);

            } catch (IOException ex) {
                Notification.show("Error uploading file",
                                3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                resetUploadComponent(upload, uploadedPdfRef);
            }
        });
    }

    public static void resetUploadComponent(Upload upload, AtomicReference<byte[]> uploadedPdfRef) {
        if (upload != null) {
            upload.clearFileList();
        }
        if (uploadedPdfRef != null) {
            uploadedPdfRef.set(null);
        }
    }
}