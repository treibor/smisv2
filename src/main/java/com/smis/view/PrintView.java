package com.smis.view;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.smis.dbservice.Dbservice;
import com.smis.dbservice.FileStorageService;
import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Installment;
import com.smis.entity.InstallmentReportNotes;
import com.smis.entity.ProcessFlow;
import com.smis.entity.ProcessHistory;
import com.smis.entity.Scheme;
import com.smis.entity.Users;
import com.smis.entity.Work;
import com.smis.entity.Year;
import com.smis.entity.master.District;
import com.smis.util.ButtonUtil;
import com.smis.util.NotificationUtil;
import com.smis.util.UploadUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.VaadinIcon;
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
	@Autowired
	FileStorageService fileStorageService;
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
	Button printButton = new Button("Generate RO");
	Button uploadButton = new Button("Upload RO");
	Button save = new Button("Save");
	private Users loggedUser;
	Anchor link = new Anchor();
	private AtomicReference<byte[]> uploadedPdf1;
	//MemoryBuffer buffer = new MemoryBuffer();
	//Upload upload1 = new Upload(buffer);
	boolean isUser;
	boolean isAdmin;
	boolean isSuper;
	private boolean gridConfigured = false;
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
		mainLayout.setPadding(true);
		setSizeFull();
		loggedUser = service.getLoggedUser();
		boolean allowed = loggedUser != null &&
		        (
		            service.hasAuthorityForStep(loggedUser, "GENERATE_RELEASE_ORDER") ||
		            service.hasAuthorityForStep(loggedUser, "UPLOAD_RELEASE_ORDER")
		        );
		if (!allowed) {
		    add(new H1("You Are Not Authorised To View this Page"));
		} else {
		    add(mainLayout);
		}
		
	}

	

	public Component getLeftLayout() {
		VerticalLayout vlx = new VerticalLayout();
		vlx.add(configureTopLayout(), configureMiddleLayout(), configureBottomLayout());
		vlx.setSizeFull();
		return vlx;
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
	public Component configureMiddleLayout() {
		HorizontalLayout middleLayout = new HorizontalLayout(grid);
		middleLayout.setFlexGrow(1, grid);
		//middleLayout.setFlexGrow(1, hl4);

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

	public Component configureSideLayout() {
		FormLayout form2 = new FormLayout();
		//form2.setWidth("100%");
		form2.add(instletter, 2);
		form2.add(instdate, 2);
		form2.add(printButton,1);
		form2.add(uploadButton,1);
		ButtonUtil.applyPrintStyle(printButton);
		ButtonUtil.applyUploadStyle(uploadButton);
		printButton.addClickListener(e -> printReport());
		uploadButton.addClickListener(e -> openUploadDialog());
		vlayout.setWidth("30px");
		vlayout.add(form2);
		return form2;
	}
	private void openUploadDialog() {

	    if (instletter.getValue() == null || instletter.getValue().trim().isEmpty() || instdate.getValue() == null) {
	        Notification.show("Release Letter, Release Date Cannot Be Empty", 5000, Position.TOP_CENTER);
	        return;
	    }

	    uploadedPdf1 = new AtomicReference<>();

	    Component uploadComponent = UploadUtil.createPdfUpload(
	            uploadedPdf1,
	            "Upload The Release Order and click 'Save'",
	            "Select Document To Upload"
	    );

	    Dialog dialog = new Dialog();

	    Button saveBtn = new Button("Save", e -> uploadRo(dialog));  // ✅ pass dialog
	    Button closeBtn = new Button("Close", e -> dialog.close());

	    dialog.add(uploadComponent);
	    dialog.getHeader().add(new H6("Upload Release Order"));
	    dialog.getFooter().add(saveBtn, closeBtn);

	    dialog.setModal(true);
	    dialog.setCloseOnOutsideClick(false);
	    dialog.setCloseOnEsc(false);

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
	private void uploadRo(Dialog dialog) {
	    Users user = service.getLoggedUser();

	    Set<Installment> selected = grid.getSelectedItems();
	    if (selected == null || selected.isEmpty()) {
	        Notification.show("Please select at least one installment", 4000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    if (uploadedPdf1 == null || uploadedPdf1.get() == null || uploadedPdf1.get().length == 0) {
	        Notification.show("Please upload Release Order as PDF", 3000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    List<Installment> installments = new ArrayList<>(selected);

	    try {
	        int install = instNo.getValue();
	        String safeFileName = fileStorageService.generateSafeFileName("ROsigned", "release_order.pdf");

	        try (InputStream in = new ByteArrayInputStream(uploadedPdf1.get())) {
	            fileStorageService.save(in, safeFileName);
	        }

	        for (Installment singleinstallment : installments) {

	            Work singlework = service.getWorkById(singleinstallment.getWork().getWorkId());

	            // ✅ use stepCode check (recommended) OR keep stepOrder if you haven't migrated yet
	            if (singlework.getProcessflow() == null || singlework.getProcessflow().getStepOrder() != 4) {
	                NotificationUtil.showError("This Page Has Expired and will be Reloaded");
	                UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
	                return;
	            }

	            singleinstallment.setInstallmentDate(instdate.getValue());
	            singleinstallment.setInstallmentLetter(instletter.getValue());
	            singleinstallment.setReleaseOrder(safeFileName);

	            ProcessFlow from = service.getProcessFlowByOrder(4);
	            ProcessFlow to = service.getProcessFlowByOrder(5);

	            ProcessHistory ph = new ProcessHistory();
	            ph.setWork(singlework);
	            ph.setUser(user);
	            ph.setFromStep(from);
	            ph.setToStep(to);
	            ph.setProcessName(from.getStepName() + "-" + install);
	            ph.setDocument(safeFileName);
	            ph.setEnteredOn(LocalDateTime.now());
	            ph.setReversed(false);

	            service.saveProcessHistory(ph);
	            service.saveInstallment(singleinstallment);

	            singlework.setProcessflow(to);
	            singlework.setWorkStatus(to.getStepName() + "-" + install);
	            service.saveWork(singlework);
	        }

	        populateGrid();
	        Notification.show("Release Order Uploaded Successfully", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	        dialog.close(); // ✅ closes the correct dialog

	    } catch (Exception e) {
	        Notification.show("Error: " + e.getMessage(), 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        e.printStackTrace();
	    }
	}
	

	

	@Transactional
	private void printReport() {
	    Users currentUser = service.getLoggedUser();

	    // --------- Basic validations ----------
	    if (instletter.getValue() == null || instletter.getValue().trim().isEmpty() || instdate.getValue() == null) {
	        Notification.show("Release Letter, Release Date Cannot Be Empty", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    if (inlineEditor.getValue() != null && inlineEditor.getValue().length() > 2900) {
	        Notification.show("'Copy To' Data has exceeded Permitted Limit", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    Set<Installment> selected = grid.getSelectedItems();
	    if (selected == null || selected.isEmpty()) {
	        Notification.show("Please select at least one installment", 4000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    List<Installment> installments = new ArrayList<>(selected);

	    try {
	        // --------- Ensure all installments belong to SAME WORK ----------
	        Long workId = installments.get(0).getWork() != null ? installments.get(0).getWork().getWorkId() : null;
	        if (workId == null || installments.stream().anyMatch(i -> i.getWork() == null || i.getWork().getWorkId() != workId)) {
	            Notification.show("Please select installments from the same Work only.", 5000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        // --------- Load fresh managed Work ----------
	        Work work0 = service.getWorkById(workId);
	        if (work0 == null) {
	            NotificationUtil.showError("Work not found. Reloading...");
	            UI.getCurrent().getPage().reload();
	            return;
	        }

	        // --------- Expiry check using STEP CODE ----------
	        ProcessFlow currentStep = work0.getProcessflow();
	        String stepCode = (currentStep != null) ? currentStep.getStepCode() : null;

	        if (!"GENERATE_RELEASE_ORDER".equals(stepCode)) {
	            NotificationUtil.showError("This Page Has Expired and will be Reloaded");
	            UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
	            return;
	        }

	        // --------- Date validations ----------
	        if (work0.getSanctionDate() != null && work0.getSanctionDate().isAfter(instdate.getValue())) {
	            Notification.show("Release Date cannot be before the sanction Date", 5000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        // Determine installment number safely (use selection, not instNo field)
	        int maxInstallNo = installments.stream()
	                .map(Installment::getInstallmentNo)
	                .filter(Objects::nonNull)
	                .max(Integer::compareTo)
	                .orElse(1);

	        // Previous installment UC date check (only if applicable)
	        if (maxInstallNo > 1) {
	            Installment prev = service.getInstallmentByWorkAndNo(maxInstallNo - 1, work0);
	            if (prev != null && prev.getUcDate() != null && prev.getUcDate().isAfter(instdate.getValue())) {
	                Notification.show("Invalid Release Date. Must be after UC date of previous installment", 5000,
	                                Position.TOP_CENTER)
	                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
	                return;
	            }
	        }

	        // --------- Labels & report selection ----------
	        String schemelabel = changeAmp(work0.getScheme().getSchemeLabel());
	        String blocklabel = changeAmp(work0.getBlock().getBlockLabel());
	        String yearlabel = changeAmp(work0.getYear().getYearLabel());
	        String sanctionNo = changeAmp(work0.getSanctionNo());

	        int reportType = work0.getScheme().getSchemeReport();
	        int installNoForReport = (maxInstallNo < 3) ? maxInstallNo : 3;

	        // --------- Calculate total amount ----------
	        BigDecimal totalamount = installments.stream()
	                .map(Installment::getInstallmentAmount)
	                .filter(Objects::nonNull)
	                .reduce(BigDecimal.ZERO, BigDecimal::add);

	        String totalAmountwords = convertToIndianCurrency(totalamount.toPlainString());
	        String totalAmountnumbers = totalamount.stripTrailingZeros().toPlainString();

	        // --------- Prepare data (but don't persist yet) ----------
	        for (Installment inst : installments) {
	            inst.setInstallmentDate(instdate.getValue());
	            inst.setInstallmentLetter(instletter.getValue());
	        }

	        // --------- Generate Jasper report ----------
	        Resource jrxml = new ClassPathResource("report/Release" + reportType + installNoForReport + ".jrxml");

	        byte[] pdfBytes;
	        try (InputStream jrxmlStream = jrxml.getInputStream()) {
	            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
	            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(installments);

	            Map<String, Object> parameters = new HashMap<>();
	            parameters.put("copyTo", inlineEditor.getValue());
	            parameters.put("Note", note.getValue());
	            parameters.put("ComplDate", "");
	            parameters.put("scheme", schemelabel);
	            parameters.put("block", blocklabel);
	            parameters.put("year", yearlabel);
	            parameters.put("sanctionNo", sanctionNo);
	            parameters.put("amount", totalAmountnumbers + " (" + totalAmountwords + ")");

	            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, ds);
	            pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
	        }

	        // --------- Store PDF ----------
	        String safeFileName = fileStorageService.generateSafeFileName("RO", "releaseorder.pdf");
	        try (InputStream in = new ByteArrayInputStream(pdfBytes)) {
	            fileStorageService.save(in, safeFileName);
	        }
	        UI.getCurrent().getPage().open("/files/" + safeFileName, "_blank");
	        // --------- Save notes ----------
	        InstallmentReportNotes notes = new InstallmentReportNotes();
	        notes.setCopyTo(inlineEditor.getValue());
	        notes.setUpdatedBy(currentUser);
	        notes.setUpdatedOn(LocalDateTime.now());
	        service.saveInstallmentReport(notes);

	        // --------- Advance workflow using nextStep chain ----------
	        ProcessFlow nextStep = currentStep.getNextStep(); // expected: UPLOAD_RELEASE_ORDER
	        if (nextStep == null) {
	            NotificationUtil.showError("Workflow misconfigured: GENERATE_RELEASE_ORDER has no next step.");
	            return;
	        }

	        // ✅ Save Work transition first (managed)
	        work0.setProcessflow(nextStep);
	        // work0.setWorkStatus(nextStep.getStepName() + "-" + maxInstallNo); // optional
	        work0.setUpdatedBy(currentUser);
	        work0.setUpdatedOn(LocalDateTime.now());
	        service.saveWork(work0);

	        // --------- Update installments + history ----------
	        for (Installment inst : installments) {
	            inst.setReportNotes(notes);
	            inst.setGeneratedReleaseOrder(safeFileName);
	            service.saveInstallment(inst);

	            ProcessHistory ph = new ProcessHistory();
	            ph.setWork(work0);                       // ✅ managed work
	            ph.setUser(currentUser);
	            ph.setFromStep(currentStep);
	            ph.setToStep(nextStep);
	            ph.setProcessName(currentStep.getStepName() + "-" + maxInstallNo);
	            ph.setDocument(safeFileName);
	            ph.setEnteredOn(LocalDateTime.now());
	            ph.setReversed(false);
	            ph.setRemarks(null);
	            service.saveProcessHistory(ph);
	        }

	        populateGrid();

	        Notification.show("Release Order generated successfully", 4000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	    } catch (Exception e) {
	        e.printStackTrace();
	        Notification.show("Unable To Generate Report. Error: " + e.getMessage(), 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	    }
	}
	
	

	
	


	public String changeAmp(String label) {
		if (label.contains("&")) {
			String replacedstring = label.replace("&", "&amp;");
			return replacedstring;
		} else {
			return label;
		}
	}

	private void populateAllFields() {
		year.setItems(service.getAllYears());
		year.setItemLabelGenerator(year -> year.getYearLabel());
		scheme.setItems(service.getAllSchemes());
		scheme.setItemLabelGenerator(scheme -> scheme.getSchemeLabel());
		block.setItems(service.getAllBlocks(true));
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
		grid.addColumn(installment-> installment.getInstallmentNo()).setHeader("Installment No").setAutoWidth(true);
		grid.addColumn(installment -> installment.getWork().getProcessflow().getStepName()).setHeader("Current Process")
				.setAutoWidth(true);
		// grid.getColumns().forEach(col-> col.setAutoWidth(true));
		grid.addSelectionListener(event -> doSomething(event));
	}
	public void doSomething(SelectionEvent<Grid<Installment>, Installment> e) {

	    Set<Installment> selected = e.getAllSelectedItems();

	    if (selected == null || selected.isEmpty()) {
	        //hl4.setVisible(false);
	        printButton.setEnabled(false);
	        uploadButton.setEnabled(false);
	        instletter.clear();
	        instdate.clear();
	        return;
	    }
	    //hl4.setVisible(false);
	    uploadButton.setEnabled(true);
	    List<Installment> installs = new ArrayList<>(selected);
	    Installment first = installs.get(0);
	    boolean selectionAllowsPrint = installs.stream()
	            .map(Installment::getWork)
	            .filter(Objects::nonNull)
	            .allMatch(w -> w.getProcessflow() != null && w.getProcessflow().getStepOrder() == 3);

	    boolean selectionAllowsUpload = installs.stream()
	            .map(Installment::getWork)
	            .filter(Objects::nonNull)
	            .allMatch(w -> w.getProcessflow() != null && w.getProcessflow().getStepOrder() == 4);
	    boolean authPrint  = service.hasAuthorityForStep(loggedUser, "GENERATE_RELEASE_ORDER"); // step for print action
	    boolean authUpload = service.hasAuthorityForStep(loggedUser, "UPLOAD_RELEASE_ORDER");
	    printButton.setEnabled(authPrint && selectionAllowsPrint);
	    uploadButton.setEnabled(authUpload && selectionAllowsUpload);
	    populateEditor(installs);
	    // Safe set values (avoid NPE)
	    String letter = first.getInstallmentLetter();
	    instletter.setValue(letter != null ? letter : "");

	    LocalDate date = first.getInstallmentDate();
	    instdate.setValue(date); // can be null

	   
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
	        Integer instnoObj = instNo.getValue();
	        int instno = (instnoObj != null) ? instnoObj : 1; // default

	        boolean hasAnyFilter =
	                scheme.getValue() != null
	                || year.getValue() != null
	                || constituency.getValue() != null
	                || block.getValue() != null
	                || (instno >= 1 && instno <= 5);

	        if (!gridConfigured) {
	            configureGrid();          // ✅ run once only
	            gridConfigured = true;
	        }

	        if (hasAnyFilter) {
	        	grid.setItems(service.getFilteredInstallments(
	        	        scheme.getValue(),
	        	        constituency.getValue(),
	        	        List.of(
	        	            service.getStepByCode("GENERATE_RELEASE_ORDER"),
	        	            service.getStepByCode("UPLOAD_RELEASE_ORDER")
	        	        ),
	        	        block.getValue(),
	        	        year.getValue(),
	        	        instnoObj.intValue()
	        	        
	        	));
	        } else {
	            grid.setItems(java.util.Collections.emptyList());
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        Notification.show("Unable to load data: " + e.getMessage(), 4000, Notification.Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
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
