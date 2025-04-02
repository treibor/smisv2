package com.smis.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.tika.Tika;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.dom.DomEventListener;

public class UploadUtil {

    public static Upload createPdfUpload(AtomicReference<byte[]> uploadedPdfRef, String uploadText, String buttonText) {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setReceiver(buffer);

        // Upload Button
        Button uploadButton = new Button(buttonText);
        uploadButton.getStyle().set("font-size", "12px");
        upload.setUploadButton(uploadButton);

        // Upload Constraints
        upload.setMaxFiles(1);
        upload.setMaxFileSize(5 * 1024 * 1024); // 5MB
        upload.setAcceptedFileTypes("application/pdf");

        // Handle File Rejections
        upload.addFileRejectedListener(e -> 
            Notification.show("Invalid File: Please select only PDF files less than 5MB", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR)
        );

        // Handle File Upload
        attachUploadListener(upload, buffer, uploadedPdfRef);

        // Handle File Removal (Detect when a file is removed)
        upload.getElement().addEventListener("file-remove", (DomEventListener) event -> resetUploadComponent(upload, uploadedPdfRef));
        upload.clearFileList();
        return upload;
    }

    private static void attachUploadListener(Upload upload, MemoryBuffer buffer, AtomicReference<byte[]> uploadedPdfRef) {
        upload.addSucceededListener(event -> {
            try (InputStream inputStream = buffer.getInputStream()) {
                // Validate MIME Type using Apache Tika
                Tika tika = new Tika();
                String detectedMimeType = tika.detect(inputStream);

                if (!"application/pdf".equals(detectedMimeType)) {
                    Notification.show("Invalid file type: Please upload a valid PDF.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    resetUploadComponent(upload, uploadedPdfRef);
                    return;
                }

                // Store the uploaded PDF
                uploadedPdfRef.set(inputStream.readAllBytes());

            } catch (IOException e) {
                Notification.show("Error uploading file", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }

    public static void resetUploadComponent(Upload upload, AtomicReference<byte[]> uploadedPdfRef) {
        upload.clearFileList();
        uploadedPdfRef.set(null); // Explicitly reset the file reference
    }
}