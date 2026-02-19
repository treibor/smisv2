package com.smis.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.smis.dbservice.AuditService;
import com.smis.dbservice.Dbservice;
import com.smis.dbservice.FileStorageService;
import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Installment;
import com.smis.entity.ProcessFlow;
import com.smis.entity.ProcessHistory;
import com.smis.entity.Scheme;
import com.smis.entity.Users;
import com.smis.entity.Work;
import com.smis.entity.Year;
import com.smis.entity.master.Village;
import com.smis.util.ButtonUtil;
import com.smis.util.NotificationUtil;
import com.smis.util.UploadUtil;
import com.smis.util.ValidationUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.transaction.Transactional;
@org.springframework.stereotype.Component
@UIScope
public class WorkForm extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dbservice service;
	private FileStorageService fileStorageService;
	//FileStorageService fileStorageService;
	private Work work;
	//private WorkView workview;
	//private Installment installment;
	Binder<Work> binder = new BeanValidationBinder<>(Work.class);
	ComboBox<Scheme> scheme = new ComboBox<>("Schemes");
	ComboBox<Year> year = new ComboBox<>("Financial Year");
	ComboBox<Constituency> constituency = new ComboBox<>("Assembly Constituency");
	ComboBox<Block> block = new ComboBox<>("Block/MB");
	ComboBox<Village> village = new ComboBox<>("Village");
	// TextField workName=new TextField("Work Name");
	TextArea workName=new TextArea("Work Name");
	ComboBox<String> workSelect = new ComboBox<>("Select From Previous Works");
	IntegerField noOfInstallments = new IntegerField("No Of Installments");
	BigDecimalField workAmount = new BigDecimalField("Sanctioned Amount");
	ComboBox<String> sanctionNo = new ComboBox<>("Sanction No");
	DatePicker sanctionDate = new DatePicker("Sanction Date");
	Button save = new Button("Save");
	Button delete = new Button("Delete");
	Button close = new Button("Close");
	Button installsave = new Button("Save");
	Button installclose = new Button("Close");
	Button ucsave = new Button("Save");
	Button ucclose = new Button("Close");
	Button genrosave = new Button("Save");
	Button genroclose = new Button("Close");
	Button rosave = new Button("Save");
	Button roclose = new Button("Close");
	Button rfsave = new Button("Save");
	Button rfclose = new Button("Close");
	Button complsave = new Button("Save");
	Button complclose = new Button("Close");
	BigDecimalField installmentAmount = new BigDecimalField("Amount");
	TextField ucletter = new TextField("UC Number");
	TextField instRemarks = new TextField("Remarks");
	TextField ucRemarks = new TextField("Remarks");
	TextField roRemarks = new TextField("Remarks");
	TextField complRemarks = new TextField("Remarks");
	TextField rfRemarks = new TextField("Remarks");
	TextField genroRemarks = new TextField("Remarks");
	DatePicker ucDate = new DatePicker("UC Date");
	//public TextField instLetter = new TextField("Release Letter No.");
	//public DatePicker instDate = new DatePicker("Release Date");
	//Notification notify = new Notification();
	Accordion accordion = new Accordion();
	public AccordionPanel workaccordion = new AccordionPanel();
	public AccordionPanel installaccordion = new AccordionPanel();
	public AccordionPanel genroaccordion = new AccordionPanel();
	public AccordionPanel uproaccordion = new AccordionPanel();
	public AccordionPanel rfaccordion = new AccordionPanel();
	public AccordionPanel ucaccordion = new AccordionPanel();
	public AccordionPanel complaccordion = new AccordionPanel();
	// Label installmentmaster=new Label("");
	H6 installmentmaster = new H6("");
	H6 ucmaster = new H6("");
	H6 genroText = new H6("");
	H6 complText = new H6("");
	public RadioButtonGroup<String> ucAction = new RadioButtonGroup<>();
	public RadioButtonGroup<String> roAction = new RadioButtonGroup<>();
	public RadioButtonGroup<String> instAction = new RadioButtonGroup<>();
	public RadioButtonGroup<String> rfAction = new RadioButtonGroup<>();
	
	boolean isUser;
	boolean isAdmin;
	boolean isSuper;
	private AtomicReference<byte[]> uploadedPdf1 = new AtomicReference<>();
	private AtomicReference<byte[]> uploadedPdf2 = new AtomicReference<>();
	private AtomicReference<byte[]> uploadedRfPdf = new AtomicReference<>();
	Upload ucUpload;
	Upload roUpload;
	Upload rfUpload;
	VerticalLayout vlayout1;
	VerticalLayout vlayout2;
	VerticalLayout vlayoutrf;
	Users loggedUser;
	public WorkForm(Dbservice service, FileStorageService fileStorageService ) {
		block.addValueChangeListener(e -> getVillages(e.getValue()));
		this.service = service;
		
		this.fileStorageService=fileStorageService;
		//System.out.println("Audit"+audit);
		this.loggedUser=service.getLoggedUser();
		binder.bindInstanceFields(this);
		
		isAdmin = service.hasRole("ADMIN");
		isSuper = service.hasRole("SUPER"); // or SUPER_ADMIN / DIST_ADMIN etc.
		isUser  = service.hasRole("USER");
		add(createFinalPanel());

	}

	public void getVillages(Block block) {
		village.setItems(service.getVillage(block));
		village.setItemLabelGenerator(village -> village.getVillageName());
	}
	private String stepTitle(String stepCode, String fallback) {
	    ProcessFlow pf = service.getStepByCode(stepCode); // or injected service in form
	    return (pf != null && pf.getStepName() != null && !pf.getStepName().isBlank())
	            ? pf.getStepName()
	            : fallback;
	}
	public Component createFinalPanel() {

	    workaccordion = accordion.add(stepTitle("WORK_ENTRY", "Work Entry"),
	            new VerticalLayout(configureWorkForm(), createButtonsLayout()));

	    installaccordion = accordion.add(stepTitle("RELEASE_INSTALLMENT", "Release Installment"),
	            new VerticalLayout(releaseInstallmentForm()));
	    genroaccordion = accordion.add(stepTitle("GENERATE_RELEASE_ORDER", "Generate Release Order"),
	            new VerticalLayout(generateROForm()));

	    uproaccordion = accordion.add(stepTitle("UPLOAD_RELEASE_ORDER", "Upload Release Order"),
	            new VerticalLayout(uploadROForm()));

	    rfaccordion = accordion.add(stepTitle("RELEASE_FUNDS", "Release Funds"),
	            new VerticalLayout(releaseFundsForm()));

	    ucaccordion = accordion.add(stepTitle("UPLOAD_UC", "Upload UC"),
	            new VerticalLayout(uploadUcForm()));

	    complaccordion = accordion.add(stepTitle("COMPLETED", "Completion"),
	            new VerticalLayout(completeForm()));

	    return accordion;
	}
	
	public Component configureWorkForm() {
		ValidationUtil.applyTextAreaValidation(workName);
		ValidationUtil.applyValidation(ucletter);
		noOfInstallments.setStepButtonsVisible(true);
		noOfInstallments.setMin(1);
		noOfInstallments.setMax(3);
		workName.setHeight("100px");
		noOfInstallments.setValue(1);
		scheme.setItems(service.getSchemesByUser());
		year.setItems(service.getAllYears());
		constituency.setItems(service.getConstituenciesByUser());
		block.setItems(service.getBlocksByUser());
		scheme.setItemLabelGenerator(Scheme::getSchemeLabel);
		year.setItemLabelGenerator(Year::getYearLabel);
		constituency.setItemLabelGenerator(constituency ->  constituency.getConstituencyLabel() + "-" + constituency.getConstituencyMLA());
		block.setItemLabelGenerator(block -> block.getBlockLabel());
		workName.setMinLength(5);
		workName.setMaxLength(999);
		// Work work=new Work();
		workSelect.setItems(service.getWorkNamesList());
		workSelect.setAllowCustomValue(true);
		workSelect.addCustomValueSetListener(e -> {
			String workname = e.getDetail();
			//System.out.println(workname);
			workSelect.setItems(workname);
			workSelect.setValue(workname);
		});
		workSelect.addValueChangeListener(e -> {
		    String val = workSelect.getValue();
		    workName.setValue(val != null ? val : "");
		});
		//sanctionNo.setMinLength(2);
		//sanctionNo.setMaxLength(50);
		sanctionNo.setItems(service.getSanctionNos());
		sanctionNo.setAllowCustomValue(true);
		sanctionNo.addCustomValueSetListener(e->{
			String sancno=e.getDetail();
			sanctionNo.setItems(sancno);
			sanctionNo.setValue(sancno);
		});
		
		workName.addClassName("custom-combobox");
		
		// village.setItemLabelGenerator(village->village.getVillageName());
		FormLayout form1 = new FormLayout();
		form1.setWidth("100%");
		form1.add(scheme, 1);
		form1.add(year, 1);
		form1.add(constituency, 2);
		form1.add(block, 1);
		form1.add(village, 1);
		form1.add(workSelect, 2);
		form1.add(workName, 2);
		form1.add(workAmount, 1);
		form1.add(noOfInstallments, 1);
		form1.add(sanctionNo, 1);
		form1.add(sanctionDate, 1);
		// form1.add(createButtonsLayout(), 2);
		form1.setResponsiveSteps(new ResponsiveStep("0", 2),
				// Use two columns, if layout's width exceeds 500px
				new ResponsiveStep("500px", 2));
		return form1;
	}
	
	private Component createButtonsLayout() {
		ButtonUtil.applySaveStyle(save);
		ButtonUtil.applyCloseStyle(close);
		ButtonUtil.applyDeleteStyle(delete);
		//save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		//delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		save.addClickShortcut(Key.ENTER);
		close.addClickShortcut(Key.ESCAPE);
		save.addClickListener(event -> saveNewWork());
		// delete.addClickListener(event -> fireEvent(new DeleteEvent(this, work)));
		delete.addClickListener(event -> confirmDelete(work));
		close.addClickListener(event -> fireEvent(new CloseEvent(this)));
		HorizontalLayout hl1 = new HorizontalLayout(save, delete, close);
		// return new HorizontalLayout(save, delete, close);
		hl1.setFlexGrow(1, save);
		hl1.setFlexGrow(1, delete);
		hl1.setFlexGrow(1, close);
		hl1.setWidthFull();
		return hl1;

	}

	public void confirmDelete(Work work) {
		ConfirmDialog dialog = new ConfirmDialog();
		if (work == null) {

		} else {
			dialog.setHeader("Delete??");
			dialog.setText("Are You sure you want to delete this item.You will lose all details and you will not be able to undo this Action");
			dialog.setCancelable(true);
			dialog.addCancelListener(event -> dialog.close());
			dialog.setRejectable(true);
			dialog.setRejectText("Discard");
			dialog.addRejectListener(event -> dialog.close());
			dialog.setConfirmText("Delete");
			dialog.addConfirmListener(event -> fireEvent(new DeleteEvent(this, work)));
			dialog.open();

		}
	}

	public Component releaseInstallmentForm() {
		ValidationUtil.applyValidation(instRemarks);
		FormLayout form2 = new FormLayout();
		form2.setWidth("100%");
		form2.add(installmentmaster, 2);
		form2.add(instAction, 2);
		form2.add(installmentAmount, 2);
		form2.add(instRemarks, 2);
		form2.add(installsave, 1);
		form2.add(installclose, 1);
		form2.setResponsiveSteps(new ResponsiveStep("0", 2),
				// Use two columns, if layout's width exceeds 500px
				new ResponsiveStep("500px", 2));
		//instRemarks.setVisible(false);
		installsave.addClickShortcut(Key.ENTER);
		installclose.addClickShortcut(Key.ESCAPE);
		ButtonUtil.applySaveStyle(installsave);
		ButtonUtil.applyCloseStyle(installclose);
		instAction.addValueChangeListener(event -> {
		    String selectedValue = event.getValue();

		    if ("Forward".equals(selectedValue)) {
		    	//instRemarks.setVisible(false);
		    	installmentAmount.setVisible(true);
		    }else {
		    	//instRemarks.setVisible(true);
		    	installmentAmount.setVisible(false);
		    }
		});
		installsave.addClickListener(event -> {
		    if (instAction.getValue().equals("Forward")) {
		        SaveInstallments();
		    } else {
		        reverseFlow(instRemarks);
		    }
		});
		installclose.addClickListener(event -> fireEvent(new CloseEvent(this)));
		return form2;
	}

	

	public Component uploadUcForm() {
		ValidationUtil.applyValidation(ucRemarks);
		this.ucUpload = UploadUtil.createPdfUpload(uploadedPdf1, "Upload Document", "Select UC Document");
	    FormLayout form2 = new FormLayout();
		form2.setWidth("100%");
		form2.add(ucAction, 2);
		form2.add(ucDate, 1);
		form2.add(ucletter, 1);
		form2.add(ucUpload, 2);
		form2.add(ucRemarks,2);
		form2.add(ucsave,1);
		form2.add(ucclose,1);
		ButtonUtil.applySaveStyle(ucsave);
		ButtonUtil.applyCloseStyle(ucclose);
		form2.setResponsiveSteps(new ResponsiveStep("0", 2),
				// Use two columns, if layout's width exceeds 500px
				new ResponsiveStep("500px", 2));
		ucsave.addClickShortcut(Key.ENTER);
		ucclose.addClickShortcut(Key.ESCAPE);
		ucsave.addClickListener(event -> {
		    if (ucAction.getValue()=="Forward") {
		        uploadUc();
		    } else {
		        reverseFlow(ucRemarks);
		    }
		});
		ucclose.addClickListener(event -> fireEvent(new CloseEvent(this)));
		ucAction.addValueChangeListener(event -> {
		    String selectedValue = event.getValue();
		    if ("Forward".equals(selectedValue)) {
		    	ucDate.setVisible(true);
		    	ucletter.setVisible(true);
		    	ucUpload.setVisible(true);
		    }else {
		    	ucDate.setVisible(false);
		    	ucletter.setVisible(false);
		    	ucUpload.setVisible(false);
		    }
		});

		return form2;
	}

	public Component releaseFundsForm() {
		vlayoutrf=new VerticalLayout();
		this.rfUpload = UploadUtil.createPdfUpload(uploadedRfPdf, "Upload Transfer of Funds Receipt", "Select Fund Receipt");
	    vlayoutrf.add(rfUpload);
	    ButtonUtil.applySaveStyle(rfsave);
	    ButtonUtil.applyCloseStyle(rfclose);
		FormLayout rfform = new FormLayout();
		rfform.setWidth("100%");
		rfform.add(rfAction, 2);
		//form2.add(createUpload(upload1, buffer), 2);
		//rfform.add(null);
		rfform.add(vlayoutrf, 2);
		rfform.add(rfRemarks,2);
		rfform.add(rfsave,1);
		rfform.add(rfclose,1);
		rfform.setResponsiveSteps(new ResponsiveStep("0", 2),
				// Use two columns, if layout's width exceeds 500px
				new ResponsiveStep("500px", 2));
		rfAction.addValueChangeListener(event -> {
		    String selectedValue = event.getValue();

		    if ("Forward".equals(selectedValue)) {
		    	vlayoutrf.setVisible(true);
		    }else {
		    	//ucRemarks.setVisible(true);
		    	//ucAction.setVisible(false);
		    	vlayoutrf.setVisible(false);
		    }
		});
		rfsave.addClickListener(event -> {
		    if (rfAction.getValue()=="Forward") {
		        saveRf();
		    } else {
		        reverseFlow(rfRemarks);
		    }
		});
		return rfform;
	}
	public Component uploadROForm() {
		ValidationUtil.applyValidation(roRemarks);
		FormLayout form2 = new FormLayout();
		vlayout2=new VerticalLayout();
		roUpload = UploadUtil.createPdfUpload(uploadedPdf2, "Upload Document", "Select Signed Release Order");
		vlayout2.add(roUpload);
		ButtonUtil.applySaveStyle(rosave);
		ButtonUtil.applyCloseStyle(roclose);
		form2.setWidth("100%");
		form2.add(roAction, 2);
		//form2.add(instLetter, 1);
		//form2.add(instDate, 1);
		//form2.add(UploadUtil.createPdfUpload(uploadedPdf2,"Upload Document","Select Document"), 2);
		form2.add(vlayout2, 2);
		form2.add(roRemarks,2);
		form2.add(rosave,1);
		form2.add(roclose,1);
		 
		form2.setResponsiveSteps(new ResponsiveStep("0", 2),
				// Use two columns, if layout's width exceeds 500px
				new ResponsiveStep("500px", 2));
		//roRemarks.setVisible(false);
		roclose.addClickListener(event -> fireEvent(new CloseEvent(this)));
		rosave.addClickListener(event -> {
		    if (roAction.getValue().equals("Forward")) {
		        uploadRo();
		    } else {
		        reverseFlow(roRemarks);
		    }
		});
		roAction.addValueChangeListener(event -> {
		    String selectedValue = event.getValue();
		    if ("Forward".equals(selectedValue)) {
		    	//roRemarks.setVisible(false);
		    	vlayout2.setVisible(true);
		    	//instLetter.setVisible(true);
		    	//instDate.setVisible(true);
		    }else {
		    	//roRemarks.setVisible(true);
		    	vlayout2.setVisible(false);
		    	//instLetter.setVisible(false);
		    	//instDate.setVisible(false);
		    }
		});

		return form2;
	}
	
	public Component generateROForm() {

		ValidationUtil.applyValidation(complRemarks);
		ButtonUtil.applySaveStyle(genrosave);
		ButtonUtil.applyCloseStyle(genroclose);
		FormLayout form2 = new FormLayout();
		form2.setWidthFull();
		form2.add(genroText, 2);
		form2.add(genroRemarks, 2);
		form2.add(genrosave, 1);
		form2.add(genroclose, 1);
		
		form2.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2), new FormLayout.ResponsiveStep("500px", 2));

		genroclose.addClickListener(event -> fireEvent(new CloseEvent(this)));
		genrosave.addClickListener(event -> {
			reverseFlow(genroRemarks);
			clearFields();

		});
		return form2;
	}
	public Component completeForm() {

		ValidationUtil.applyValidation(complRemarks);
		FormLayout form2 = new FormLayout();
		form2.setWidthFull();
		form2.add(complText, 2);
		form2.add(complRemarks, 2);
		form2.add(complsave, 1);
		form2.add(complclose, 1);

		form2.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2), new FormLayout.ResponsiveStep("500px", 2));

		complclose.addClickListener(event -> fireEvent(new CloseEvent(this)));
		complsave.addClickListener(event -> {

			reverseFlow(complRemarks);
			clearFields();

		});
		return form2;
	}
	
	
	public void setWork(Work work) {
		this.work = work;
		binder.readBean(work);
	}
	
	private void saveNewWork() {

	    if (work == null) {
	        Notification.show("Unable To Identify The Work", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    if (noOfInstallments.getValue() < 1 || noOfInstallments.getValue() > 5) {
	        Notification.show("Failure: Number of Installments Should Be Between 1 and 5", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    if (workAmount.getValue() == null || workAmount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
	        Notification.show("Failure: Amount Must Be Entered.", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    if (sanctionDate.getValue() == null) {
	        Notification.show("Failure: Sanction Date Must Be Entered.", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    if (!ValidationUtil.applyValidation(sanctionNo.getValue())) {
	        Notification.show("Sanction No: Special Characters like *, ?, ^, %, $, # are not allowed.", 5000,
	                Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    try {
	        boolean isNew = work.getWorkId() == 0;
	        if (!binder.validate().isOk()) {
	            Notification.show("Please fill all mandatory fields.", 5000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }
	        binder.writeBean(work);

	        Users user = service.getLoggedUser();
	        LocalDateTime now = LocalDateTime.now();
	        
	        work.setDistrict(service.getDistrict());
	        work.setUpdatedBy(user);
	        work.setUpdatedOn(now);

	        Long newWorkCode = null;
	        ProcessHistory ph = null;

	        if (isNew) {
	            newWorkCode = service.getWorkCode() + 1;
	            work.setWorkCode(newWorkCode);

	            
	        }
	        ProcessFlow entryStep = service.getStepByCode("WORK_ENTRY");
            if (entryStep == null) {
                Notification.show("Workflow misconfigured: WORK_ENTRY step not found.", 6000, Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            ProcessFlow nextStep = entryStep.getNextStep();
            if (nextStep == null) {
                Notification.show("Workflow misconfigured: WORK_ENTRY has no next step.", 6000, Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            work.setProcessflow(nextStep);

            ph = new ProcessHistory();
            ph.setWork(work);
            ph.setUser(user);
            ph.setFromStep(entryStep);
            ph.setToStep(nextStep);
            ph.setProcessName(entryStep.getStepName()); // or "Entry"
            ph.setReversed(false);
            ph.setEnteredOn(now);
            ph.setRemarks(null);

	        // Save Work
	        fireEvent(new SaveEvent(this, work));

	        // Save history for new work
	        if (ph != null) {
	            service.saveProcessHistory(ph);
	        }

	        String message = isNew
	                ? "Work Entered Successfully With Work Code: " + newWorkCode
	                : "Work Updated Successfully";

	        Notification.show(message, 5000, Notification.Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	        clearFields();

	    } catch (Exception e) {
	        Notification.show("Unable to Save Work. Please Enter All Mandatory Fields. " + e.getMessage(),
	                5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
	        e.printStackTrace();
	    }
	}
	
	private void updateWork(ProcessFlow nextPf) {
	    try {
	        Work dbWork = service.getWorkById(work.getWorkId());

	        if (dbWork == null || nextPf == null) {
	            throw new IllegalStateException("Invalid workflow transition");
	        }

	        dbWork.setProcessflow(nextPf);
	        dbWork.setUpdatedBy(service.getLoggedUser());
	        dbWork.setUpdatedOn(LocalDateTime.now());

	        fireEvent(new SaveEvent(this, dbWork));

	    } catch (Exception e) {
	        Notification.show("Something Went Wrong: " + e.getMessage())
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	    }
	}
	
	@Transactional
	private void SaveInstallments() {

	    if (work == null) {
	        Notification.show("Unable To Retrieve Work. Please Select Work From The Table")
	                .addThemeVariants(NotificationVariant.LUMO_WARNING);
	        return;
	    }

	    work = service.getWorkById(work.getWorkId());
	    if (work == null) {
	        NotificationUtil.showError("Unable to reload Work. Please try again.");
	        return;
	    }

	    ProcessFlow current = work.getProcessflow();
	    String currentCode = (current != null) ? current.getStepCode() : null;

	    if (!"RELEASE_INSTALLMENT".equals(currentCode)) {
	        NotificationUtil.showError("This Page Has Expired and will be Reloaded");
	        UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
	        return;
	    }

	    int allowed = work.getNoOfInstallments();
	    int alreadyEntered = service.getInstallments(work).size();
	    int toEnter = alreadyEntered + 1;

	    if (allowed == alreadyEntered) {
	        Notification.show("Failure: All Installments Have Been Entered For The Selected Work.", 5000,
	                Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_WARNING);
	        return;
	    }

	    if (installmentAmount.getValue() == null || installmentAmount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
	        Notification.show("Failure: Please Enter A Valid Amount.", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_WARNING);
	        return;
	    }

	    BigDecimal releasedSoFar = calculateReleasedInstAmount(work);
	    if (releasedSoFar.add(installmentAmount.getValue()).compareTo(work.getWorkAmount()) > 0) {
	        Notification.show("Failure: Released Amount Exceeds Sanctioned Amount.", 5000,
	                Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_WARNING);
	        return;
	    }

	    try {
	        Users user = service.getLoggedUser();
	        LocalDateTime now = LocalDateTime.now();

	        // Save installment
	        Installment installment = new Installment();
	        installment.setInstallmentAmount(installmentAmount.getValue());
	        installment.setInstallmentNo(toEnter);
	        installment.setInstallmentAmountPrev(releasedSoFar);
	        installment.setEnteredBy(user);
	        installment.setEnteredOn(now);
	        installment.setWork(work);

	        // Decide next step (IMMEDIATE move)
	        ProcessFlow nextStep = current.getNextStep();
	        if (nextStep == null) {
	            NotificationUtil.showError("Workflow misconfigured: RELEASE_INSTALLMENT has no next step.");
	            return;
	        }

	        // History: FROM current -> TO next
	        ProcessHistory ph = new ProcessHistory();
	        ph.setWork(work);
	        ph.setUser(user);
	        ph.setFromStep(current);
	        ph.setToStep(nextStep);
	        ph.setProcessName(current.getStepName()+" - " + toEnter);  // clearer than repeating step name
	        ph.setReversed(false);
	        ph.setEnteredOn(now);
	        ph.setRemarks(instRemarks.getValue());

	        service.saveInstallment(installment);
	        service.saveProcessHistory(ph);
	        updateWork(nextStep);
	        fireEvent(new RefreshEvent(this, work));
	        Notification.show("Installment " + toEnter + " Entered. Moved to " + nextStep.getStepName(),
	                5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	        clearFields();

	    } catch (Exception e) {
	        Notification.show("Something Went Wrong: " + e.getMessage())
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        e.printStackTrace();
	    }
	}
	public void reverseFlow(TextField remarks) {
		
	    if (remarks.getValue() == null || remarks.getValue().trim().isEmpty()) {
	        Notification.show("Please Enter Remarks", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    work = service.getWorkById(work.getWorkId());
	    if (work == null) {
	        NotificationUtil.showError("Unable to reload Work. Please try again.");
	        return;
	    }

	    ProcessFlow current = work.getProcessflow();
	    if (current == null) {
	        NotificationUtil.showError("Work step not found.");
	        return;
	    }

	    // Don't allow reverse from entry step
	    if ("WORK_ENTRY".equals(current.getStepCode())) {
	        Notification.show("Cannot return from Work Entry.", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    try {
	        ProcessFlow returnTo = service.getPrevStepFromHistory(work);
	        if ("RELEASE_INSTALLMENT".equals(returnTo.getStepCode())) {
	            service.markLatestInstallmentDeletedIfExists(work);
	        }
	        ProcessHistory ph = new ProcessHistory();
	        ph.setWork(work);
	        ph.setUser(loggedUser);
	        ph.setFromStep(current);      // returning FROM current
	        ph.setToStep(returnTo);       // returning TO previous
	        ph.setProcessName("Returned To : "+ returnTo.getStepName());  // keep processName generic (optional)
	        ph.setReversed(true);
	        ph.setRemarks(remarks.getValue());
	        ph.setEnteredOn(LocalDateTime.now());

	        service.saveProcessHistory(ph);
	        updateWork(returnTo);
	        Notification.show("Returned Successfully to " + returnTo.getStepName(), 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	        clearFields();

	    } catch (Exception e) {
	        Notification.show("Something Went Wrong: " + e.getMessage(), 7000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        e.printStackTrace();
	    }
	}
	
	

	public void uploadUc() {

	    try {
	        work = service.getWorkById(work.getWorkId());

	        if (work.getProcessflow() == null ||
	            !"UPLOAD_UC".equals(work.getProcessflow().getStepCode())) {

	            NotificationUtil.showError("This Page Has Expired and will be Reloaded");
	            UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
	            return;
	        }

	        if (ucletter.getValue() == null || ucletter.getValue().trim().isEmpty()
	                || ucDate.getValue() == null) {

	            Notification.show("Please Enter Letter No and Date", 5000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        List<Installment> installments = service.getInstallments(work);
	        if (installments == null || installments.isEmpty()) {
	            Notification.show("No installment found", 4000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        int tableInstallments = installments.size();
	        Installment latestInstallment = installments.get(tableInstallments - 1);

	        if (latestInstallment.getInstallmentDate() != null &&
	            ucDate.getValue().isBefore(latestInstallment.getInstallmentDate())) {

	            Notification.show("UC Date Cannot Be Before Installment Release Date",
	                    5000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        if (uploadedPdf1 == null || uploadedPdf1.get() == null || uploadedPdf1.get().length == 0) {
	            Notification.show("Please Upload UC as PDF", 3000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        Users user = service.getLoggedUser();
	        LocalDateTime now = LocalDateTime.now();

	        String safeFileName = fileStorageService.generateSafeFileName("UC", "uc.pdf");
	        try (InputStream in = new ByteArrayInputStream(uploadedPdf1.get())) {
	            fileStorageService.save(in, safeFileName);
	        }

	        latestInstallment.setUcDate(ucDate.getValue());
	        latestInstallment.setUcLetter(ucletter.getValue());
	        latestInstallment.setEnteredBy(user);
	        latestInstallment.setUcDocument(safeFileName);
	        service.saveInstallment(latestInstallment);

	        ProcessFlow current = work.getProcessflow();
	        ProcessFlow next = (work.getNoOfInstallments() == tableInstallments)
	                ? service.getStepByCode("COMPLETED")
	                : service.getStepByCode("RELEASE_INSTALLMENT");

	        if (next == null) {
	            NotificationUtil.showError("Workflow misconfigured: next step not found.");
	            return;
	        }

	        ProcessHistory ph = new ProcessHistory();
	        ph.setWork(work);
	        ph.setFromStep(current);
	        ph.setToStep(next);
	        ph.setProcessName(current.getStepName() + "-" + latestInstallment.getInstallmentNo());
	        ph.setUser(user);
	        ph.setEnteredOn(now);
	        ph.setReversed(false);
	        ph.setRemarks(ucRemarks.getValue());
	        ph.setDocument(safeFileName);
	        service.saveProcessHistory(ph);

	        // ✅ advance workflow
	        work.setProcessflow(next);
	        work.setUpdatedBy(user);
	        work.setUpdatedOn(now);
	        work.setWorkStatus(next.getStepName() + "-" + latestInstallment.getInstallmentNo()); // optional
	        service.saveWork(work);

	        fireEvent(new RefreshEvent(this, work));

	        Notification.show("UC Uploaded Successfully", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	        clearFields();

	    } catch (Exception e) {
	        Notification.show("Something Went Wrong: " + e.getMessage(),
	                5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
	        e.printStackTrace();
	    }
	}
	
	public void saveRf() {
	    try {
	        // 1) Null checks
	        if (work == null || work.getWorkId() == 0) {
	            Notification.show("Please select a Work first.", 4000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        // 2) Reload fresh managed Work
	        Work dbWork = service.getWorkById(work.getWorkId());
	        if (dbWork == null) {
	            NotificationUtil.showError("Work not found. Page will reload.");
	            UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
	            return;
	        }
	        work = dbWork;

	        // 3) Validate step using stepCode (NOT stepOrder)
	        ProcessFlow current = work.getProcessflow();
	        String currentCode = (current != null) ? current.getStepCode() : null;

	        // ✅ change this to your actual stepCode for “Fund Release / Upload Receipt”
	        if (!"RELEASE_FUNDS".equals(currentCode)) {
	            NotificationUtil.showError("This Page Has Expired and will be Reloaded");
	            UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
	            return;
	        }

	        // 4) Validate upload bytes
	        if (uploadedRfPdf == null || uploadedRfPdf.get() == null || uploadedRfPdf.get().length == 0) {
	            Notification.show("Please Select The Receipt Document to be Uploaded", 3000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        // 5) Load installments ONCE, pick latest
	        List<Installment> installments = service.getInstallments(work);
	        if (installments == null || installments.isEmpty()) {
	            Notification.show("No installment found for this Work.", 5000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        Installment latest = installments.get(installments.size() - 1);
	        int installmentNo = latest.getInstallmentNo();

	        Users user = service.getLoggedUser();
	        LocalDateTime now = LocalDateTime.now();

	        // 6) Save receipt file
	        String safeFileName = fileStorageService.generateSafeFileName("Fund", "fund_receipt.pdf");
	        try (InputStream in = new ByteArrayInputStream(uploadedRfPdf.get())) {
	            fileStorageService.save(in, safeFileName);
	        }

	        // 7) Update installment
	        latest.setFundDocument(safeFileName);
	        service.saveInstallment(latest);

	        // 8) Move to next step via chain
	        ProcessFlow nextStep = current.getNextStep();
	        if (nextStep == null) {
	            NotificationUtil.showError("Workflow misconfigured: RELEASE_FUNDS has no next step.");
	            return;
	        }

	        // 9) Save process history (from -> to)
	        ProcessHistory ph = new ProcessHistory();
	        ph.setWork(work);
	        ph.setUser(user);
	        ph.setFromStep(current);
	        ph.setToStep(nextStep);
	        ph.setProcessName(current.getStepName() + "-" + installmentNo);
	        ph.setReversed(false);
	        ph.setEnteredOn(now);
	        ph.setRemarks(rfRemarks.getValue()); // ✅ use rfRemarks (better than roRemarks)
	        ph.setDocument(safeFileName);
	        service.saveProcessHistory(ph);

	        // 10) Audit
	        //audit.saveAudit(work, latest, current.getStepName() + "-" + installmentNo, "Entry");

	        // 11) Update work
	        work.setProcessflow(nextStep);
	        work.setUpdatedBy(user);
	        work.setUpdatedOn(now);

	        // If grid/UI still uses workStatus, keep it non-null
	        work.setWorkStatus(nextStep.getStepName() + "-" + installmentNo);

	        service.saveWork(work);
	        fireEvent(new RefreshEvent(this, work));
	        Notification.show("Fund Release saved successfully", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	        clearFields();

	    } catch (Exception e) {
	        Notification.show("Something went wrong: " + e.getMessage(), 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        e.printStackTrace();
	    }
	}
	public void uploadRo() {

	    try {
	        // 1) Null check first
	        if (work == null || work.getWorkId() == 0) {
	            Notification.show("Please select a Work first.", 4000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        // 2) Reload fresh managed Work (prevents stale / transient issues)
	        Work dbWork = service.getWorkById(work.getWorkId());
	        if (dbWork == null) {
	            NotificationUtil.showError("Work not found. Page will reload.");
	            UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
	            return;
	        }
	        work = dbWork;

	        // 3) Validate correct step using stepCode (NOT stepOrder)
	        ProcessFlow current = work.getProcessflow();
	        String currentCode = (current != null) ? current.getStepCode() : null;

	        // ✅ change to your actual stepCode for "Upload RO"
	        if (!"UPLOAD_RELEASE_ORDER".equals(currentCode)) {
	            NotificationUtil.showError("This Page Has Expired and will be Reloaded");
	            UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
	            return;
	        }

	       
	        if (uploadedPdf2 == null || uploadedPdf2.get() == null || uploadedPdf2.get().length == 0) {
	            Notification.show("Please Select The Release Order To be Uploaded", 3000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        // 6) Load installments ONCE
	        List<Installment> installments = service.getInstallments(work);
	        if (installments == null || installments.isEmpty()) {
	            Notification.show("No installment found for this Work. Enter installment first.", 5000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }

	        // Latest installment (your workflow assumes RO is for latest installment)
	        Installment latest = installments.get(installments.size() - 1);
	        int installmentNo = latest.getInstallmentNo(); // better than using list size

	        Users user = service.getLoggedUser();
	        LocalDateTime now = LocalDateTime.now();

	        // 7) Save RO file to filesystem
	        String safeFileName = fileStorageService.generateSafeFileName("ROsigned", "release_order.pdf");
	        try (InputStream in = new ByteArrayInputStream(uploadedPdf2.get())) {
	            fileStorageService.save(in, safeFileName);
	        }

	        // 8) Update installment
	        latest.setReleaseOrder(safeFileName);
	        service.saveInstallment(latest);

	        // 9) Advance to next step via chain
	        ProcessFlow nextStep = current.getNextStep();
	        if (nextStep == null) {
	            NotificationUtil.showError("Workflow misconfigured: UPLOAD_RELEASE_ORDER has no next step.");
	            return;
	        }

	        // 10) Save ProcessHistory (from -> to)
	        ProcessHistory ph = new ProcessHistory();
	        ph.setWork(work);
	        ph.setUser(user);
	        ph.setFromStep(current);
	        ph.setToStep(nextStep);
	        ph.setProcessName(current.getStepName() + "-" + installmentNo);
	        ph.setReversed(false);
	        ph.setEnteredOn(now);
	        ph.setRemarks(roRemarks.getValue());
	        ph.setDocument(safeFileName);
	        service.saveProcessHistory(ph);
	        // 12) Update Work current step (and optional status)
	        work.setProcessflow(nextStep);
	        work.setUpdatedBy(user);
	        work.setUpdatedOn(now);

	        // if you still use workStatus in UI/grid, keep it non-null:
	        work.setWorkStatus(nextStep.getStepName() + "-" + installmentNo);

	        service.saveWork(work);
	        fireEvent(new RefreshEvent(this, work));
	        Notification.show("Release Order Uploaded Successfully", 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	        clearFields();
	        
	    } catch (Exception e) {
	        Notification.show("Something Went Wrong: " + e.getMessage(), 5000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        e.printStackTrace();
	    }
	}
	
	
	public void clearFields() {
		
		installmentAmount.setValue(BigDecimal.ZERO);
		ucletter.setValue("");
		ucDate.setValue(null);
		fireEvent(new CloseEvent(this));
		clearBuffer();
		instRemarks.setValue("");
		ucRemarks.setValue("");
		complRemarks.setValue("");
		roRemarks.setValue("");
		
	}
	public void clearBuffer() {
	    uploadedPdf1.set(null);
	    uploadedPdf2.set(null);
	    
	    try {
			roUpload.clearFileList();
			ucUpload.clearFileList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	    
	}

	
	public BigDecimal calculateReleasedInstAmount(Work work) {
	    List<Installment> list = service.getInstallments(work);
	    BigDecimal total = BigDecimal.ZERO;
	    for (Installment i : list) {
	        total = total.add(i.getInstallmentAmount());
	    }
	    return total;
	}
	
	public static abstract class WorkFormEvent extends ComponentEvent<WorkForm> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Work work;

		protected WorkFormEvent(WorkForm source, Work work) {
			super(source, false);
			this.work = work;
		}

		public Work getWork() {
			return work;
		}
	}

	public static class SaveEvent extends WorkFormEvent {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		SaveEvent(WorkForm source, Work work) {
			super(source, work);
		}
	}

	public static class DeleteEvent extends WorkFormEvent {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		DeleteEvent(WorkForm source, Work work) {
			super(source, work);
		}

	}

	public static class CloseEvent extends WorkFormEvent {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		CloseEvent(WorkForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
	public static class RefreshEvent extends WorkFormEvent {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RefreshEvent(WorkForm source, Work work) {
	        super(source, work);
	    }
	}
}