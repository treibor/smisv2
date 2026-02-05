package com.smis.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Installment;
import com.smis.entity.InstallmentDocument;
import com.smis.entity.InstallmentReportNotes;
import com.smis.entity.ProcessFlow;
import com.smis.entity.ProcessFlowUser;
import com.smis.entity.ProcessHistory;
import com.smis.entity.Scheme;
import com.smis.entity.Users;
import com.smis.entity.Work;
import com.smis.entity.Year;
import com.smis.entity.master.District;
import com.smis.util.NotificationUtil;
import com.smis.util.UploadUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.wontlost.ckeditor.Constants.EditorType;
import com.wontlost.ckeditor.VaadinCKEditor;
import com.wontlost.ckeditor.VaadinCKEditorBuilder;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@PageTitle("MLA Release Order")
@Route(value = "releaseorder", layout = MainLayout.class)
@RolesAllowed({ "USER", "SUPER", "ADMIN" })
public class PrintView extends HorizontalLayout {
	// Binder <Work> binder=new BeanValidationBinder<>(Work.class);
	Dbservice service;
	Grid<Installment> grid = new Grid<>(Installment.class);
	Set<Installment> selectedPersons;
	IntegerField instNo = new IntegerField("Installment No:");
	FormLayout layout = new FormLayout();
	ComboBox<Scheme> scheme = new ComboBox<>("Schemes");
	ComboBox<Year> year = new ComboBox<>("Financial Year");
	ComboBox<Constituency> constituency = new ComboBox<>("Assembly Constituency");
	ComboBox<Block> block = new ComboBox<>("Block");
	TextField instletter = new TextField("Release Letter No.");
	TextField installmentcheque = new TextField("Cheque No.");
	DatePicker instdate = new DatePicker("Release Date");
	// DatePicker compldate=new DatePicker("Completion Date");
	TextField copyTo = new TextField("Copy To:");
	TextField note = new TextField("Note:");
	Button printButton = new Button("Print");
	Button uploadButton = new Button("Upload RO");
	Button save = new Button("Save");
	HorizontalLayout hl4 = new HorizontalLayout();
	Anchor link = new Anchor();
	private AtomicReference<byte[]> uploadedPdf1;
	//MemoryBuffer buffer = new MemoryBuffer();
	//Upload upload1 = new Upload(buffer);
	boolean isUser;
	boolean isAdmin;
	boolean isSuper;
	VerticalLayout vlayout = new VerticalLayout();
	Dialog dialog;
	VaadinCKEditor inlineEditor = new VaadinCKEditorBuilder().with(builder -> {
		builder.editorData = "<p></p>";
		builder.editorType = EditorType.INLINE;
		// builder.theme = ThemeType.DARK;
	}).createVaadinCKEditor();

	public PrintView(Dbservice service) {
		this.service = service;
		configureGrid();
		populateAllFields();
		printButton.setEnabled(false);
		uploadButton.setEnabled(false);
		isAdmin = service.hasRole("ADMIN");
		isSuper = service.hasRole("SUPER"); // or SUPER_ADMIN / DIST_ADMIN etc.
		isUser  = service.hasRole("USER");
		HorizontalLayout mainLayout = new HorizontalLayout(getLeftLayout(), configureSideLayout());
		mainLayout.setSizeFull();
		Users u = service.getLoggedUser();
		boolean allowed = (u != null) && service.hasAuthorityForStep(u, 3);

		if (!allowed) {
		    add(new H1("You Are Not Authorised To View this Page"));
		} else {
		    add(mainLayout);
		}
		setSizeFull();
	}

	

	public Component getLeftLayout() {
		VerticalLayout vl = new VerticalLayout();
		vl.add(configureTopLayout(), configureMiddleLayout(), configureBottomLayout());
		vl.setSizeFull();
		return vl;
	}

	public Component configureTopLayout() {
		FormLayout layout = new FormLayout();
		instNo.setStepButtonsVisible(true);
		instNo.setMin(1);
		instNo.setMax(5);
		instNo.setValue(1);

		scheme.addValueChangeListener(e -> populateGrid());
		constituency.addValueChangeListener(e -> populateGrid());
		block.addValueChangeListener(e -> populateGrid());
		year.addValueChangeListener(e -> populateGrid());
		instNo.addValueChangeListener(e -> populateGrid());

		layout.add(instNo, 1);
		layout.add(scheme, 1);
		layout.add(year, 1);
		layout.add(block, 2);
		layout.add(constituency, 2);

		layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2), // 1 column by default
				new FormLayout.ResponsiveStep("500px", 3), // 2 columns for larger screens
				new FormLayout.ResponsiveStep("800px", 4), // 3 columns for even larger screens
				new FormLayout.ResponsiveStep("1000px", 7) // 5 columns for very large screens
		);

		layout.setWidthFull();
		return layout;
	}

	public Component configureSideLayout() {

		vlayout.setWidth("300px"); // Set a fixed width for the side layout
		// compldate.setHelperText("As Per Scheme Duration");
		printButton.addClickListener(e -> printReport());
		uploadButton.addClickListener(e -> openUploadDialog());
		vlayout.add(instletter, instdate, printButton, uploadButton);
		vlayout.setHeightFull();
		return vlayout;
	}

	private void openUploadDialog() {
		if (instletter.getValue() == null || instletter.getValue().trim().isEmpty() || instdate.getValue() == null) {
			Notification.show("Release Letter, Release Date Cannot Be Empty", 5000, Position.TOP_CENTER);
			return;
		}
		uploadedPdf1 = new AtomicReference<>();

		Component uploadComponent = UploadUtil.createPdfUpload(uploadedPdf1,
		    "Upload The Release Order and click 'Save'", 
		    "Select Document To Upload"
		    
		);
		
        // Create a new Upload component
        //Upload pdfUpload = uploadFactory.createPdfUpload();
		dialog = new Dialog();
		 Button closeButton = new Button("Close", e -> {
		        dialog.close();
		        dialog = null; // ✅ Completely destroys the dialog
		    });
		save.addClickListener(e -> uploadRo());
		dialog.add(uploadComponent);
		dialog.getHeader().add(new H6("Upload Release Order"));
		dialog.getFooter().add(save, closeButton);
		dialog.open();

	}
	public boolean checkProcessFlow(Work work, int flow) {
		if (work.getProcessflow().getStepOrder() != 3) {
	        NotificationUtil.showError("This Page Has Expired. Please Refresh.");
	        UI.getCurrent().getPage().reload();
	        return false; // Indicate that the page expired
	    }
	    return true; 
	}
	private void uploadRo() {
		Users user = service.getLoggedUser();
		Set<Installment> installmentset = grid.getSelectedItems();
		List<Installment> installments = new ArrayList<>(installmentset);
		try {
			if (uploadedPdf1 == null || uploadedPdf1.get().length == 0) {
				Notification.show("Please Upload UC, Images etc as PDF", 3000, Position.TOP_CENTER)
						.addThemeVariants(NotificationVariant.LUMO_ERROR);
				return;
			}
			int install = instNo.getValue();
			int selecteditems = installments.size();
			BigDecimal totalamount = BigDecimal.ZERO;
			for (int i = 0; i < selecteditems; i++) {
				totalamount = installments.get(i).getInstallmentAmount().add(totalamount);
				Installment singleinstallment = installments.get(i);
				Work singlework = singleinstallment.getWork();
				singlework = service.getWorkById(singlework.getWorkId());
				if (singlework.getProcessflow().getStepOrder() != 3) {
					NotificationUtil.showError("This Page Has Expired and will be Reloaded");
					UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
					return;
				}
				singleinstallment.setInstallmentDate(instdate.getValue());
				singleinstallment.setInstallmentLetter(instletter.getValue());
				// Users user = service.getLoggedUser();
				ProcessFlow pf3 = service.getProcessFlowByOrder(3);
				ProcessFlow pf4 = service.getProcessFlowByOrder(4);
				// boolean exists = service.processHistoryExists(singlework, pf, user);
				// if (!exists) {
				ProcessHistory ph = new ProcessHistory();
				ph.setWork(singlework);
				ph.setProcessFlow(pf3);
				ph.setProcessName(pf3.getStepName() + "-" + install);
				ph.setUser(user);
				ph.setEnteredOn(LocalDateTime.now());
				ph.setReversed(false);
				service.saveProcessHistory(ph);
				// }
				singlework.setProcessflow(pf4);
				singlework.setWorkStatus(pf4.getStepName() + "-" + install);
				service.saveWork(singlework);

			}
			InstallmentDocument instdoc = new InstallmentDocument();
			instdoc.setUpdatedBy(user);
			instdoc.setUpdatedOn(LocalDateTime.now());
			instdoc.setDocument(uploadedPdf1.get());
			service.saveDocuments(instdoc);
			for (Installment installment : installments) {
				installment.setReleaseOrder(instdoc);
				service.saveInstallment(installment);
			}
			populateGrid();
			Notification.show(" Release Order Uploaded Successfully", 5000, Position.TOP_CENTER)
					.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			service.deleteUnreferencedData();
			//UploadUtil.resetUploadComponent(upload1, buffer, uploadedPdf1);
			dialog.close();
		} catch (NullPointerException e) {
			Notification.show("Please Select A File To Upload", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);

		}catch (Exception e) {
			Notification.show(" Error:" + e, 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);

		}

	}

	public Component configureMiddleLayout() {
		HorizontalLayout middleLayout = new HorizontalLayout(grid);
		middleLayout.setFlexGrow(1, grid);
		middleLayout.setFlexGrow(1, hl4);

		middleLayout.setSizeFull();
		return middleLayout;
	}

	public Component configureBottomLayout() {
		HorizontalLayout bLayout = new HorizontalLayout(inlineEditor);
		inlineEditor.setSizeFull();
		
		bLayout.setWidthFull();
		bLayout.setHeight("40%");
		return bLayout;
	}

	@Transactional
	private void printReport() {
		Users currentUser = service.getLoggedUser();
		int installno = instNo.getValue();

		if (instletter.getValue() == null || instletter.getValue().trim().isEmpty() || instdate.getValue() == null) {
			Notification.show("Release Letter, Release Date Cannot Be Empty", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		}else if(inlineEditor.getValue().length()>2900) {
			Notification.show("'Copy To' Data has exceeded Permitted Limit", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);;
		} else {
			Set<Installment> installmentset = grid.getSelectedItems();
			List<Installment> installments = new ArrayList<>(installmentset);
			if (installments.get(0).getWork().getSanctionDate().isAfter(instdate.getValue())) {
				Notification.show("Release Date  Cannot be before the sanction Date", 5000, Position.TOP_CENTER);

			} else if (installno > 1 && service.getInstallmentByWorkAndNo(installments.get(0).getInstallmentNo() - 1,
					installments.get(0).getWork()).getUcDate().isAfter(instdate.getValue())) {
				Notification.show(
						"Invalid Release  Date. Release Date Has to Be After the UC date of Previous Installment", 5000,
						Position.TOP_CENTER);
			} else {
				try {

					int selecteditems = installments.size();
					String schemelabel = changeAmp(installments.get(0).getWork().getScheme().getSchemeLabel());
					String blocklabel = changeAmp(installments.get(0).getWork().getBlock().getBlockLabel());
					String yearlabel = changeAmp(installments.get(0).getWork().getYear().getYearLabel());
					String sanctionNo = changeAmp(installments.get(0).getWork().getSanctionNo());
					// LocalDate completion=compldate.getValue();
					int reportType = installments.get(0).getWork().getScheme().getSchemeReport();
					int installNo;

					if (instNo.getValue() < 3) {
						installNo = instNo.getValue();
					} else {
						installNo = 3;
					}
					BigDecimal totalamount = BigDecimal.ZERO;
					for (int i = 0; i < selecteditems; i++) {
						totalamount = installments.get(i).getInstallmentAmount().add(totalamount);
						Installment singleinstallment = installments.get(i);
						singleinstallment.setInstallmentDate(instdate.getValue());
						singleinstallment.setInstallmentLetter(instletter.getValue());
						// service.saveInstallment(singleinstallment);
						Work singlework = singleinstallment.getWork();
						if (singleinstallment.getUcLetter() == null) {
							singlework.setWorkStatus("Release Order " + singleinstallment.getInstallmentNo());
						}
						service.saveWork(singlework);

					}
					String totalAmountwords = convertToIndianCurrency(totalamount + "");
					String totalAmountnumbers = totalamount.stripTrailingZeros().toPlainString();
					populateGrid();
					// Report Generation starts here
					Resource resource = new ClassPathResource(
							"report/Release" + reportType + "" + installNo + ".jrxml"); // removePdfViewer();
					URL res = getClass().getClassLoader()
							.getResource("report/Release" + reportType + "" + installNo + ".jrxml");
					File file = Paths.get(res.toURI()).toFile();
					String absolutePath = file.getAbsolutePath();
					String reportPath = absolutePath.substring(0, absolutePath.length() - 15);
					// String reportPath="D:"; // before production
					InputStream employeeReportStream = resource.getInputStream();

					JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
					JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(
							installments);
					Map<String, Object> parameters = new HashMap<>();

					parameters.put("copyTo", inlineEditor.getValue());

					parameters.put("Note", note.getValue());
					parameters.put("ComplDate", "");
					parameters.put("scheme", schemelabel);
					parameters.put("block", blocklabel);
					parameters.put("year", yearlabel);
					parameters.put("sanctionNo", sanctionNo);
					parameters.put("amount", totalAmountnumbers + " (" + totalAmountwords + ")");

					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
							jrBeanCollectionDataSource);
					// String username=service.getloggeduser().trim();
					long userid = currentUser.getUserId();
					JasperExportManager.exportReportToPdfFile(jasperPrint,
							reportPath + "//" + userid + "releaseorder.pdf");
					File a = new File(reportPath + "//" + userid + "releaseorder.pdf");
					InstallmentReportNotes notes = new InstallmentReportNotes();
					notes.setCopyTo(inlineEditor.getValue());
					notes.setUpdatedBy(currentUser);
					notes.setUpdatedOn(LocalDateTime.now());
					service.saveInstallmentReport(notes);
					for (Installment installment : installments) {
						installment.setReportNotes(notes);
						service.saveInstallment(installment);
					}
					addLinkToFile(a);

				} catch (Exception e) {
					Notification.show("Unable To Generate Report. Error:" + e, 5000, Position.TOP_CENTER);
					// Position.TOP_CENTER);
					// e.printStackTrace();

				}
			}
		}
	}

	private void addLinkToFile(File file) {
		if (link != null) {
			vlayout.remove(link);
		}
		StreamResource streamResource = new StreamResource(file.getName(), () -> getStream(file));

		link = new Anchor(streamResource, String.format("%s (%d KB)", file.getName(), (int) file.length() / 1024));
		link.setTarget("_blank"); // Opens in a new tab

		vlayout.add(link);
	}

	private InputStream getStream(File file) {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {

		}

		return stream;
	}

	public String changeAmp(String label) {
		if (label.contains("&")) {
			String replacedstring = label.replace("&", "&amp;");
			return replacedstring;
		} else {
			return label;
		}
	}

	private InputStream createResource(File path) {// get generated pdf file and create Resource
		try {
			return FileUtils.openInputStream(path);
		} catch (Exception ex) {
		}
		return null;
	}

	private void populateAllFields() {
		year.setItems(service.getAllYears());
		year.setItemLabelGenerator(year -> year.getYearName());
		scheme.setItems(service.getAllSchemes());
		scheme.setItemLabelGenerator(scheme -> scheme.getSchemeName());
		block.setItems(service.getAllBlocks(false));
		block.setItemLabelGenerator(block -> block.getBlockLabel());
		constituency.setItems(service.getAllConstituencies());
		// constituency.setItemLabelGenerator(constituency->constituency.getConstituencyNo()+"
		// - "+constituency.getConstituencyName()+" -
		// "+constituency.getConstituencyMLA());
		constituency.setItemLabelGenerator(
				constituency -> constituency.getConstituencyLabel() + " - " + constituency.getConstituencyMLA());
	}

	public void configureGrid() {
		grid.setSizeFull();

		grid.setSelectionMode(Grid.SelectionMode.MULTI);
		grid.setColumns("installmentAmount");
		grid.addColumn(installment -> installment.getWork().getWorkCode()).setHeader("Work Code").setResizable(true)
				.setSortable(true);
		grid.addColumn(installment -> installment.getWork().getWorkName()).setHeader("Work").setWidth("20%")
				.setResizable(true);
		grid.addColumn(installment -> installment.getWork().getSanctionDate()).setHeader("Sanction Date")
				.setAutoWidth(true);
		grid.addColumn(installment -> installment.getWork().getNoOfInstallments()).setHeader("No of Inst")
				.setAutoWidth(true);
		grid.addColumn(installment -> installment.getInstallmentLetter()).setHeader("Release No").setAutoWidth(true);
		grid.addColumn(installment -> installment.getInstallmentDate()).setHeader("Release Date").setAutoWidth(true);
		// grid.addColumn(installment->
		// installment.getInstallmentCheque()).setHeader("Cheque
		// No.").setAutoWidth(true);
		grid.addColumn(installment -> installment.getWork().getProcessflow().getStepName()).setHeader("Current Process")
				.setAutoWidth(true);
		// grid.getColumns().forEach(col-> col.setAutoWidth(true));
		grid.addSelectionListener(event -> doSomething(event));
	}

	public void doSomething(SelectionEvent e) {
		LocalDate complDate = null;
		if (e.getAllSelectedItems().size() > 0) {
			hl4.setVisible(false);
			printButton.setEnabled(true);
			uploadButton.setEnabled(true);
			Set<Installment> selected = grid.getSelectedItems();
			List<Installment> installs = new ArrayList<>(selected);
			Installment installsingle = installs.get(0);
			int schemeduration = installsingle.getWork().getScheme().getSchemeDuration();
			LocalDate sancDate = installsingle.getWork().getSanctionDate();
			complDate = sancDate.plusMonths(schemeduration);
			// compldate.setValue(complDate);
			instdate.setValue(installsingle.getInstallmentDate());
			populateEditor(installs);
			// String letterNo=installsingle.getInstallmentLetter()+"";
			try {
				instletter.setValue(installsingle.getInstallmentLetter());
				// installmentcheque.setValue(installsingle.getInstallmentCheque());

			} catch (NullPointerException npe) {
				// npe.printStackTrace();
				instletter.setValue("");
				// installmentcheque.setValue("");
			}
		} else {
			printButton.setEnabled(false);
			uploadButton.setEnabled(false);
			instletter.setValue("");
			instdate.setValue(null);
			// installmentcheque.setValue("");
			// inlineEditor.setValue("");
		}
	}

	public void populateEditor(List<Installment> installs) {
		Work work = installs.get(0).getWork();
		BigDecimal total = BigDecimal.ZERO;
		for (Installment installment : installs) {
			total = total.add(installment.getInstallmentAmount());
		}
		String mla = work.getConstituency().getConstituencyMLA();
		String consti = work.getConstituency().getConstituencyLabel();
		String dept = work.getScheme().getSchemeDept();
		String block = work.getBlock().getBlockLabel();
		District district = work.getDistrict();
		String districtname = district.getDistrictName();
		String districthq = district.getDistrictHq();
		String statehq = district.getState().getStateHq();
		String state = district.getState().getStateName();
		int schemeduration = work.getScheme().getSchemeDuration();
		LocalDate sancDate = work.getSanctionDate();
		LocalDate complDate = sancDate.plusMonths(schemeduration);
		String bdo = work.getBlock().getBdoName();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		// compldate.setValue(complDate);
		// if(installs.get(0).getCopyTo().!=null) {
		if (!installs.isEmpty() && installs.get(0).getReportNotes() != null
				&& installs.get(0).getReportNotes().getCopyTo() != null) {
			inlineEditor.setValue(installs.get(0).getReportNotes().getCopyTo());
		} else {
			if (work.getScheme().getSchemeReport() == 1) {
				inlineEditor.setValue(
						"<p><span style=\"font-family:'Times New Roman', Times, serif;\">Copy To:&nbsp;</span></p><ol>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">" + mla + ", MLA, "
								+ consti + " Constituency for favour of information.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">The Director, "
								+ dept + ", " + statehq + ", " + state + " for information.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">The Project Director, DRDA, "
								+ districtname + ", " + districthq
								+ " with a request to release the amount accordingly.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">" + bdo + ", "
								+ block
								+ " with a direction to ensure that implementation of the scheme is strictly adhered to the relevant guidelines and to submit Utilisation Certificates, Completion Report on or before "
								+ complDate.format(formatter) + ".&nbsp;</span><br>&nbsp;</li>"
								+ "</ol><p>&nbsp;</p><p>&nbsp;</p>");
			} else if (work.getScheme().getSchemeReport() == 2) {
				inlineEditor.setValue(
						"<p><span style=\"font-family:'Times New Roman', Times, serif;\">Copy To:&nbsp;</span></p><ol>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">" + mla + ", MLA, "
								+ consti + " Constituency for favour of information.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">The Under Secretary to the Government of "
								+ state + ", " + dept + ", " + statehq + ", " + state
								+ " for information.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">The Director, "
								+ dept + ", " + statehq + ", " + state + " for information.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">The Project Director, DRDA, "
								+ districtname + ", " + districthq + " for information.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">" + bdo + ", "
								+ block
								+ " with a direction to ensure that implementation of the scheme is strictly adhered to the relevant guidelines and to submit Utilisation Certificates, Completion Report on or before "
								+ complDate.format(formatter) + ". The cheque No. ______ of Rs." + total
								+ " is enclosed herewith for implementation of the scheme. He/She will also forward relevant records including APRs and UC to The Deputy Commissioner. He/She would keep custody of all records of the scheme at the District level on completion of the scheme for purpose of future audit under para 3.&nbsp;</span><br>&nbsp;</li>"
								+ "</ol><p>&nbsp;</p><p>&nbsp;</p>");

			} else if (work.getScheme().getSchemeReport() == 3) {
				inlineEditor.setValue(
						"<p><span style=\"font-family:'Times New Roman', Times, serif;\">Copy To:&nbsp;</span></p><ol>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">" + mla + ", MLA, "
								+ consti + " Constituency for favour of information.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">The Under Secretary to the Government of "
								+ state + ", Chief Minister's Secretariat, " + statehq + ", " + state
								+ " for information.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">The Project Director, DRDA, "
								+ districtname + ", " + districthq
								+ " with a request to release the amount accordingly.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">" + bdo + ", "
								+ block
								+ " with a direction to ensure that implementation of the scheme is strictly adhered to the relevant guidelines. The "
								+ bdo
								+ " shall release the amount to the beneficiary in one installment for amounts below one Lakh and in two installments for amount above one Lakh and for purchase shall release in one installment only. The "
								+ bdo
								+ " will also forward relevant records, completion report and UC to the Deputy Commissioner accompanied by photographic evidence to enable onward submission to the Chief Minister's Secretariat.&nbsp;</span><br>&nbsp;</li>"
								+ "</ol><p>&nbsp;</p><p>&nbsp;</p>");
			} else if (work.getScheme().getSchemeReport() == 4) {
				inlineEditor.setValue(
						"<p><span style=\"font-family:'Times New Roman', Times, serif;\">Copy To:&nbsp;</span></p><ol>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">The Deputy Secretary to the Government of "
								+ state + ", Chief Minister's Secretariat, " + statehq + ", " + state
								+ " for information.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">The Project Director, DRDA, "
								+ districtname + ", " + districthq
								+ " with a request to release the amount accordingly.&nbsp;&nbsp;</span></li>"
								+ "<li><span style=\"font-family:'Times New Roman', Times, serif;\">" + bdo + ", "
								+ block
								+ " with a direction to ensure that implementation of the scheme is strictly adhered to the relevant guidelines. The "
								+ bdo
								+ " shall release the amount to the beneficiary in one installment for amounts below one Lakh and in two installments for amount above one Lakh and for purchase shall release in one installment only. The "
								+ bdo
								+ " will also forward relevant records, completion report and UC to the Deputy Commissioner accompanied by photographic evidence to enable onward submission to the Chief Minister's Secretariat.&nbsp;</span><br>&nbsp;</li>"
								+ "</ol><p>&nbsp;</p><p>&nbsp;</p>");
			} else {
				inlineEditor.setValue("");
			}
		}
	}

	public void populateGrid() {
		try {
			int instno = instNo.getValue();
			if (scheme.getValue() != null || year.getValue() != null || constituency.getValue() != null
					|| block.getValue() != null || instno > 0 || instno <= 5) {
				if (hl4 != null) {
					hl4.removeAll();
				}
				// grid.setItems(service.getFilteredInstallments(scheme.getValue(),
				// constituency.getValue(),block.getValue(), year.getValue(), instno));
				grid.setItems(service.getFilteredInstallments(scheme.getValue(), constituency.getValue(),
						service.getProcessFlowByOrder(3), block.getValue(), year.getValue(), instno));

				configureGrid();
			}
		} catch (Exception e) {

		}
	}

	public static String convertToIndianCurrency(String num) {
		BigDecimal bd = new BigDecimal(num);
		long number = bd.longValue();
		long no = bd.longValue();
		int decimal = (int) (bd.remainder(BigDecimal.ONE).doubleValue() * 100);
		int digits_length = String.valueOf(no).length();
		int i = 0;
		ArrayList<String> str = new ArrayList<>();
		HashMap<Integer, String> words = new HashMap<>();
		words.put(0, "");
		words.put(1, "One");
		words.put(2, "Two");
		words.put(3, "Three");
		words.put(4, "Four");
		words.put(5, "Five");
		words.put(6, "Six");
		words.put(7, "Seven");
		words.put(8, "Eight");
		words.put(9, "Nine");
		words.put(10, "Ten");
		words.put(11, "Eleven");
		words.put(12, "Twelve");
		words.put(13, "Thirteen");
		words.put(14, "Fourteen");
		words.put(15, "Fifteen");
		words.put(16, "Sixteen");
		words.put(17, "Seventeen");
		words.put(18, "Eighteen");
		words.put(19, "Nineteen");
		words.put(20, "Twenty");
		words.put(30, "Thirty");
		words.put(40, "Forty");
		words.put(50, "Fifty");
		words.put(60, "Sixty");
		words.put(70, "Seventy");
		words.put(80, "Eighty");
		words.put(90, "Ninety");
		String digits[] = { "", "Hundred", "Thousand", "Lakh", "Crore" };
		while (i < digits_length) {
			int divider = (i == 2) ? 10 : 100;
			number = no % divider;
			no = no / divider;
			i += divider == 10 ? 1 : 2;
			if (number > 0) {
				int counter = str.size();
				String plural = (counter > 0 && number > 9) ? "" : "";
				String tmp = (number < 21) ? words.get(Integer.valueOf((int) number)) + " " + digits[counter] + plural
						: words.get(Integer.valueOf((int) Math.floor(number / 10) * 10)) + " "
								+ words.get(Integer.valueOf((int) (number % 10))) + " " + digits[counter] + plural;
				str.add(tmp);
			} else {
				str.add("");
			}
		}

		Collections.reverse(str);
		String Rupees = String.join(" ", str).trim();

		String paise = (decimal) > 0
				? " And Paise " + words.get(Integer.valueOf((int) (decimal - decimal % 10))) + " "
						+ words.get(Integer.valueOf((int) (decimal % 10)))
				: "";
		return Rupees + paise;
	}

}
