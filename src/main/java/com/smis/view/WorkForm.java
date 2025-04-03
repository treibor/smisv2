package com.smis.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import com.smis.audit.Audit;
import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Installment;
import com.smis.entity.InstallmentDocument;
import com.smis.entity.ProcessFlow;
import com.smis.entity.ProcessHistory;
import com.smis.entity.Scheme;
import com.smis.entity.Users;
import com.smis.entity.Village;
import com.smis.entity.Work;
import com.smis.entity.Year;
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
import com.vaadin.flow.component.button.ButtonVariant;
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

import jakarta.transaction.Transactional;

public class WorkForm extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dbservice service;
	//@Autowired
	private Audit audit;
	private Work work;
	//private WorkView workview;
	private Installment installment;
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
	Button rosave = new Button("Save");
	Button roclose = new Button("Close");
	BigDecimalField installmentAmount = new BigDecimalField("Amount");
	TextField ucletter = new TextField("UC Number");
	TextField instRemarks = new TextField("Remarks");
	TextField ucRemarks = new TextField("Remarks");
	TextField roRemarks = new TextField("Remarks");
	TextField complRemarks = new TextField("Remarks");
	DatePicker ucDate = new DatePicker("UC Date");
	public TextField instLetter = new TextField("Release Letter No.");
	public DatePicker instDate = new DatePicker("Release Date");
	//Notification notify = new Notification();
	Accordion accordion = new Accordion();
	public AccordionPanel workaccordion = new AccordionPanel();
	public AccordionPanel installaccordion = new AccordionPanel();
	public AccordionPanel roaccordion = new AccordionPanel();
	public AccordionPanel ucaccordion = new AccordionPanel();
	public AccordionPanel complaccordion = new AccordionPanel();
	// Label installmentmaster=new Label("");
	H6 installmentmaster = new H6("");
	H6 ucmaster = new H6("");
	private final ProcessFlow pf1;
	private final ProcessFlow pf2;
	private final ProcessFlow pf3;
	private final ProcessFlow pf4;
    private final ProcessFlow pf5;
	public RadioButtonGroup<String> ucAction = new RadioButtonGroup<>();
	public RadioButtonGroup<String> roAction = new RadioButtonGroup<>();
	public RadioButtonGroup<String> instAction = new RadioButtonGroup<>();
	boolean isAdmin;
	boolean isUser;
	private AtomicReference<byte[]> uploadedPdf1 = new AtomicReference<>();
	private AtomicReference<byte[]> uploadedPdf2 = new AtomicReference<>();
	Upload ucUpload;
	Upload roUpload;
	VerticalLayout vlayout1;
	VerticalLayout vlayout2;
	public WorkForm(Dbservice service, Audit audit) {
		block.addValueChangeListener(e -> getVillages(e.getValue()));
		this.service = service;
		this.audit=audit;
		//System.out.println("Audit"+audit);
		binder.bindInstanceFields(this);
		pf1=service.getProcessFlowByOrder(1);
		pf2=service.getProcessFlowByOrder(2);
		pf3=service.getProcessFlowByOrder(3);
		pf4=service.getProcessFlowByOrder(4);
		pf5=service.getProcessFlowByOrder(5);
		isAdmin = service.isAdmin();
		isUser = service.isUser();
		add(createFinalPanel());

	}

	public void getVillages(Block block) {
		village.setItems(service.getVillage(block));
		village.setItemLabelGenerator(village -> village.getVillageName());
	}

	public Component createFinalPanel() {
		workaccordion = accordion.add(pf1.getStepName(), new VerticalLayout(configureForm(), createButtonsLayout()));
		installaccordion = accordion.add(pf2.getStepName(),new VerticalLayout(configureInstallmentForm(), createInstallButtons()));
		roaccordion = accordion.add(pf3.getStepName(),new VerticalLayout(configureROForm()));
		ucaccordion = accordion.add(pf4.getStepName(),new VerticalLayout(configureUcForm(), createUcButtons()));
		complaccordion = accordion.add(pf5.getStepName(),new VerticalLayout(configureCompleteForm()));
		return accordion;
	}
	
	public Component configureForm() {
		ValidationUtil.applyTextAreaValidation(workName);
		ValidationUtil.applyValidation(ucletter);
		noOfInstallments.setStepButtonsVisible(true);
		noOfInstallments.setMin(1);
		noOfInstallments.setMax(3);
		workName.setHeight("100px");
		noOfInstallments.setValue(1);
		scheme.setItems(service.getAllSchemes());
		year.setItems(service.getAllYears());
		constituency.setItems(service.getAllConstituencies());
		block.setItems(service.getAllBlocks());
		scheme.setItemLabelGenerator(Scheme::getSchemeName);
		year.setItemLabelGenerator(Year::getYearName);
		constituency.setItemLabelGenerator(constituency -> constituency.getConstituencyNo() + "-"
				+ constituency.getConstituencyName() + "-" + constituency.getConstituencyMLA());
		block.setItemLabelGenerator(block -> block.getBlockName());
		workName.setMinLength(5);
		workName.setMaxLength(999);
		// Work work=new Work();
		workSelect.setItems(service.getWorkNames());
		workSelect.setAllowCustomValue(true);
		workSelect.addCustomValueSetListener(e -> {
			String workname = e.getDetail();
			//System.out.println(workname);
			workSelect.setItems(workname);
			workSelect.setValue(workname);
		});
		workSelect.addValueChangeListener(e->workName.setValue(workSelect.getValue()));
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
		
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		save.addClickShortcut(Key.ENTER);
		close.addClickShortcut(Key.ESCAPE);
		save.addClickListener(event -> validatandSave());
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
			dialog.setText("Are You sure you want to delete this item.You will loose all details and you will not be able to undo this Action");
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

	public Component configureInstallmentForm() {
		ValidationUtil.applyValidation(instRemarks);
		FormLayout form2 = new FormLayout();
		form2.setWidth("100%");
		form2.add(installmentmaster, 2);
		form2.add(instAction, 2);
		
		form2.add(installmentAmount, 2);
		form2.add(instRemarks, 2);
		// form2.add(installmentCheque, 2);
		form2.setResponsiveSteps(new ResponsiveStep("0", 2),
				// Use two columns, if layout's width exceeds 500px
				new ResponsiveStep("500px", 2));
		//instRemarks.setVisible(false);
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
		return form2;
	}

	private Component createInstallButtons() {
		installsave.setEnabled(isUser);
		// installsave.setWidthFull();
		// installclose.setWidthFull();
		installsave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		installsave.addClickShortcut(Key.ENTER);
		installclose.addClickShortcut(Key.ESCAPE);
		installsave.addClickListener(event -> {
		    if (instAction.getValue()=="Forward") {
		        SaveInstallments();
		    } else {
		        returnInstallment();
		    }
		});
		installclose.addClickListener(event -> fireEvent(new CloseEvent(this)));
		HorizontalLayout hl1 = new HorizontalLayout(installsave, installclose);
		// return new HorizontalLayout(save, delete, close);
		hl1.setWidthFull();
		return hl1;
	}
	

	public Component configureUcForm() {
		ValidationUtil.applyValidation(ucRemarks);
		vlayout1=new VerticalLayout();
		Upload ucUpload=UploadUtil.createPdfUpload(uploadedPdf1,"Upload Document","Select UC Document");
		vlayout1.add(ucUpload);
		FormLayout form2 = new FormLayout();
		form2.setWidth("100%");
		form2.add(ucAction, 2);
		form2.add(ucDate, 1);
		form2.add(ucletter, 1);
		//form2.add(createUpload(upload1, buffer), 2);
		form2.add(vlayout1, 2);
		form2.add(ucRemarks,2);
		form2.setResponsiveSteps(new ResponsiveStep("0", 2),
				// Use two columns, if layout's width exceeds 500px
				new ResponsiveStep("500px", 2));
		//ucRemarks.setVisible(false);
		ucAction.addValueChangeListener(event -> {
		    String selectedValue = event.getValue();

		    if ("Forward".equals(selectedValue)) {
		    	//ucRemarks.setVisible(false);
		    	//ucAction.setVisible(true);
		    	ucDate.setVisible(true);
		    	ucletter.setVisible(true);
		    	vlayout1.setVisible(true);
		    }else {
		    	//ucRemarks.setVisible(true);
		    	//ucAction.setVisible(false);
		    	ucDate.setVisible(false);
		    	ucletter.setVisible(false);
		    	vlayout1.setVisible(false);
		    }
		});

		return form2;
	}
	public Component configureROForm() {
		ValidationUtil.applyValidation(roRemarks);
		FormLayout form2 = new FormLayout();
		vlayout2=new VerticalLayout();
		roUpload = UploadUtil.createPdfUpload(uploadedPdf2, "Upload Document", "Select Signed Release Order");
		vlayout2.add(roUpload);
		
		form2.setWidth("100%");
		//form2.add(roAction, 2);
		form2.add(instLetter, 1);
		form2.add(instDate, 1);
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
		    if (roAction.getValue()=="Forward") {
		        saveRo();
		    } else {
		        returnRo();
		    }
		});
		roAction.addValueChangeListener(event -> {
		    String selectedValue = event.getValue();
		    if ("Forward".equals(selectedValue)) {
		    	//roRemarks.setVisible(false);
		    	vlayout2.setVisible(true);
		    	instLetter.setVisible(true);
		    	instDate.setVisible(true);
		    }else {
		    	//roRemarks.setVisible(true);
		    	vlayout2.setVisible(false);
		    	instLetter.setVisible(false);
		    	instDate.setVisible(false);
		    }
		});

		return form2;
	}
	public Component configureCompleteForm() {
		ValidationUtil.applyValidation(complRemarks);
		FormLayout form2 = new FormLayout();
		H6 returnlabel=new H6("Return to "+pf4.getStepName());
		Button complsave=new Button("Save");
		Button complclose=new Button("Close");
		form2.setWidth("100%");
		form2.add(returnlabel, 2);
		form2.add(complRemarks, 2);
		form2.add(complsave, 1);
		form2.add(complclose, 1);
		form2.setResponsiveSteps(new ResponsiveStep("0", 2),
				// Use two columns, if layout's width exceeds 500px
				new ResponsiveStep("500px", 2));
		//roRemarks.setVisible(false);
		complclose.addClickListener(event -> fireEvent(new CloseEvent(this)));
		complsave.addClickListener(event -> {
		    if (complRemarks.getValue()==null||complRemarks.getValue().trim().isEmpty()) {
		    	Notification.show("Please Enter Remarks", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		    } else {
		    	work = service.getWorkById(work.getWorkId());
				if(work.getProcessflow().getStepOrder()!=5) {
					NotificationUtil.showError("This Page Has Expired and will be Reloaded");
					UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
					return;
				}
		    	try {
					Users user=service.getLoggedUser();
					ProcessHistory ph=new ProcessHistory();
					ph.setWork(work);
					ph.setProcessFlow(pf5);
					ph.setProcessName("Return To "+pf4.getStepName());
					ph.setReversed(true);
					ph.setUser(user);
					ph.setRemarks(complRemarks.getValue());
					ph.setEnteredOn(LocalDateTime.now());
					service.saveProcessHistory(ph);
					updateWork("Return To "+pf4.getStepName(),pf4);
					Notification.show("Returned Successfully to "+pf4.getStepName(), 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
					clearFields();
				} catch (Exception e) {
					Notification.show("Something Went Wrong :" + e).addThemeVariants(NotificationVariant.LUMO_ERROR);

				}
		    }
		});
		
		return form2;
	}
	private Component createUcButtons() {
		ucsave.setEnabled(isUser);
		ucsave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		ucsave.addClickShortcut(Key.ENTER);
		ucclose.addClickShortcut(Key.ESCAPE);
		ucsave.addClickListener(event -> {
		    if (ucAction.getValue()=="Forward") {
		        saveUc();
		    } else {
		        returnUc();
		    }
		});
		ucclose.addClickListener(event -> fireEvent(new CloseEvent(this)));
		HorizontalLayout hl1 = new HorizontalLayout(ucsave, ucclose);
		// return new HorizontalLayout(save, delete, close);
		hl1.setWidthFull();
		return hl1;
	}
	
	public void setWork(Work work) {
		this.work = work;
		binder.readBean(work);
	}
	
	
	@Transactional
	private void validatandSave() {
		if (work == null) {
			Notification.show("Unable To Identify The Work", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		} else if (noOfInstallments.getValue() < 1 || noOfInstallments.getValue() > 5) {
			Notification.show("Failure: Number of Installments Should Be Between 1 and 5", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		} else if (workAmount.getValue() == null || workAmount.getValue().compareTo(BigDecimal.ZERO) == -1
				|| workAmount.getValue().compareTo(BigDecimal.ZERO) == 0) {
			Notification.show("Failure: Amount  Must Be Entered .", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		} else if (sanctionDate.getValue() == null) {
			Notification.show("Failure: Sanction Date Must Be Entered .", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		} else if(!ValidationUtil.applyValidation(sanctionNo.getValue())) {
			Notification.show("Sanction No: Special Characters like *, ?, ^,%, $ ,#  are not allowed.", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
			else {
			try {
				long singlework = work.getWorkCode();
				binder.writeBean(work);
				long newWorkCode = service.getWorkCode() + 1;
				Users user=service.getLoggedUser();
				ProcessHistory ph = null;
				if (singlework == 0) {
					work.setWorkCode(newWorkCode);
					work.setWorkStatus("Entered");
					work.setUpdatedBy(user);
					work.setUpdatedOn(LocalDateTime.now());
					work.setProcessflow(pf2);
					ph = new ProcessHistory();
					ph.setWork(work);
					ph.setEnteredOn(LocalDateTime.now());
					ph.setProcessFlow(pf1);
					ph.setProcessName(pf1.getStepName());
					ph.setReversed(false);
					ph.setUser(user);
					
				}
				work.setDistrict(service.getDistrict());
				//audit=new Audit(service);
				if(singlework == 0) {
					audit.saveAudit(work,pf1.getStepName(), "Entry");
				}else {
					audit.saveAudit(work, pf1.getStepName(), "Update");
				}
				fireEvent(new SaveEvent(this, work));
				if (ph != null) {
			        service.saveProcessHistory(ph);
			    }
				
				String message = (singlework == 0) 
				        ? "Work Entered Successfully With Work Code: " + newWorkCode
				        : "Work Updated Successfully";

				    Notification.show(message, 5000, Notification.Position.TOP_CENTER)
				                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				clearFields();
			} catch (Exception e) {
				Notification.show("Unable to Save Work. Please Enter All Mandatory Fields" + e, 5000, Position.TOP_CENTER)
						.addThemeVariants(NotificationVariant.LUMO_ERROR);
				e.printStackTrace();
			}
		}
	}
	
	
	@Transactional
	private void SaveInstallments() {
		int workinstallments = work.getNoOfInstallments();
		int tableinstallments = service.getInstallments(work).size();
		int toEnterInstallment = tableinstallments + 1;
		if (work == null) {
			Notification.show("Unable To Retrieve Work. Please Select Work From The Table").addThemeVariants(NotificationVariant.LUMO_WARNING);
		} else if (workinstallments == tableinstallments) {
			Notification.show("Failure: All Installments Have Been Entered For The Selected Work.", 5000,
					Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_WARNING);
		} else if (installmentAmount.getValue() == null || installmentAmount.getValue().compareTo(BigDecimal.ZERO) == 0
				|| installmentAmount.getValue().compareTo(BigDecimal.ZERO) == -1) {
			Notification.show("Failure:Please Enter A Valid Amount .", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_WARNING);
		} else if (work.getWorkAmount().compareTo(installmentAmount.getValue()) == -1) {
			Notification.show("Failure: Please Check Released Amount. It Should Be less Or Equal To The Sanctioned Amount",
					5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_WARNING);
		} else if ((calculateReleasedInstAmount(work).add(installmentAmount.getValue()))
				.compareTo(work.getWorkAmount()) == 1) {
			Notification.show("Failure: Released Amount:" + calculateReleasedInstAmount(work)
					+ " & Amount To Be Released has Exceeded The Sanctioned Amount:" + work.getWorkAmount() + "", 5000,
					Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_WARNING);
		} else {
			work = service.getWorkById(work.getWorkId());
			
			if(work.getProcessflow().getStepOrder()!=2) {
				NotificationUtil.showError("This Page Has Expired and will be Reloaded");
				UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
				return;
			}
			try {
				Users user=service.getLoggedUser();
				Installment installment = new Installment();
				installment.setInstallmentAmount(installmentAmount.getValue());
				installment.setInstallmentNo(service.getInstallments(work).size() + 1);
				installment.setInstallmentAmountPrev(calculateReleasedInstAmount(work));
				installment.setEnteredBy(user);
				installment.setEnteredOn(LocalDateTime.now());
				installment.setWork(work);
				ProcessHistory ph=new ProcessHistory();
				ph.setWork(work);
				ph.setProcessFlow(pf2);
				ph.setProcessName(pf2.getStepName()+"-"+toEnterInstallment);
				ph.setReversed(false);
				ph.setUser(user);
				ph.setEnteredOn(LocalDateTime.now());
				ph.setRemarks(instRemarks.getValue());
				service.saveInstallment(installment);
				service.saveProcessHistory(ph);
				
				audit.saveAudit(work, installment, pf2.getStepName()+"-"+toEnterInstallment, "Entry");
				updateWork("Installment " + toEnterInstallment + "",pf3);
				Notification.show(pf2.getStepName()+ "-" + toEnterInstallment + " Completed Sucessfully", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				clearFields();
			} catch (Exception e) {
				Notification.show("Something Went Wrong :" + e).addThemeVariants(NotificationVariant.LUMO_ERROR);;

			}
		}
	}
	public void returnInstallment() {
		if (instRemarks.getValue() == null || instRemarks.getValue().trim().isEmpty()) {
			Notification.show("Please Enter Remarks", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		}else {
			work = service.getWorkById(work.getWorkId());
			if(work.getProcessflow().getStepOrder()!=2) {
				NotificationUtil.showError("This Page Has Expired and will be Reloaded");
				UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
				return;
			}
			try {
				Users user=service.getLoggedUser();
				ProcessHistory ph=new ProcessHistory();
				ph.setWork(work);
				ph.setProcessFlow(pf2);
				ph.setProcessName("Return To "+pf4.getStepName());
				ph.setReversed(true);
				ph.setUser(user);
				ph.setRemarks(instRemarks.getValue());
				ph.setEnteredOn(LocalDateTime.now());
				service.saveProcessHistory(ph);
				audit.saveAuditReturn(work, "Return To "+pf4.getStepName(), "Return");
				updateWork("Return To "+pf4.getStepName(),pf4);
				Notification.show("Returned Successfully to "+pf4.getStepName(), 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				clearFields();
			} catch (Exception e) {
				Notification.show("Something Went Wrong :" + e).addThemeVariants(NotificationVariant.LUMO_ERROR);

			}
		}
	}

	

	public void saveUc() {
		int tableinstallments = service.getInstallments(work).size();
		int toEnterInstallment = tableinstallments;
		int index = tableinstallments - 1;
		if (ucletter.getValue() == null || ucletter.equals("") || ucDate.getValue() == null
				|| ucDate.getValue().equals(null)) {
			Notification.show("Please Enter Letter No and Date", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		} else if (ucDate.getValue().isBefore(service.getInstallments(work).get(index).getInstallmentDate())) {
			Notification.show("UC Date Cannot Be Before Installment Release Date", 5000, Position.TOP_CENTER);
		} else {
			try {
				work = service.getWorkById(work.getWorkId());
				if(work.getProcessflow().getStepOrder()!=4) {
					NotificationUtil.showError("This Page Has Expired and will be Reloaded");
					UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
					return;
				}
				if (uploadedPdf1 == null || uploadedPdf1.get() == null || uploadedPdf1.get().length == 0) {
				    Notification.show("Please Upload UC, Images etc as PDF", 3000, Position.TOP_CENTER)
				                .addThemeVariants(NotificationVariant.LUMO_ERROR);
				    return;
				}
				
				Users user=service.getLoggedUser();
				InstallmentDocument instdoc=new InstallmentDocument();
				instdoc.setDocument(uploadedPdf1.get());
				instdoc.setUpdatedBy(user);
				instdoc.setUpdatedOn(LocalDateTime.now());
				service.saveDocuments(instdoc);
				this.installment = service.getInstallments(work).get(index);
				installment.setUcDate(ucDate.getValue());
				installment.setUcLetter(ucletter.getValue());
				installment.setEnteredBy(user);
				installment.setUcDocument(instdoc);
				//audit.saveAudit(work,installment, pf4.getStepName()+"-"+toEnterInstallment, "Entry");
				service.saveInstallment(installment);
				service.saveWork(work);
				ProcessHistory ph=new ProcessHistory();
				ph.setWork(work);
				ph.setReversed(false);
				ph.setUser(user);
				ph.setEnteredOn(LocalDateTime.now());
				ph.setRemarks(ucRemarks.getValue());
				ph.setProcessFlow(pf4);
				ph.setProcessName(pf4.getStepName()+"-"+toEnterInstallment);
				service.saveProcessHistory(ph);
				audit.saveAudit(work,installment, pf4.getStepName()+"-"+toEnterInstallment, "Entry");
				if (work.getNoOfInstallments() == tableinstallments) {
					ProcessHistory ph1=new ProcessHistory();
					ph1.setWork(work);
					ph1.setProcessFlow(pf5);
					ph1.setProcessName("Forward");
					ph1.setUser(user);
					ph1.setReversed(false);
					ph1.setEnteredOn(LocalDateTime.now());
					ph1.setProcessName(pf5.getStepName());
					
					updateWork("Completed",pf5);
					service.saveProcessHistory(ph1);
				} else {
					updateWork(pf2.getStepName()+"-"+ toEnterInstallment + "",pf2);
					
				}
				
				Notification.show(pf2.getStepName()+ "-" + toEnterInstallment + " Completed Sucessfully", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				clearFields();
				service.deleteUnreferencedData();
			} catch (NullPointerException e) {
				Notification.show("Please Select A File To Upload", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);

			}catch (Exception e) {
				Notification.show("Something Went Wrong :" + e).addThemeVariants(NotificationVariant.LUMO_ERROR);

			}
		}
	}
	public void returnUc() {
		if (ucRemarks.getValue() == null|| ucRemarks.getValue().trim().isEmpty()) {
			Notification.show("Please Enter Remarks", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		}else {
			work = service.getWorkById(work.getWorkId());
			if(work.getProcessflow().getStepOrder()!=4) {
				NotificationUtil.showError("This Page Has Expired and will be Reloaded");
				UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
				return;
			}
			try {
				Users user=service.getLoggedUser();
				ProcessHistory ph=new ProcessHistory();
				ph.setWork(work);
				ph.setProcessFlow(pf4);
				ph.setProcessName("Return To "+pf3.getStepName());
				ph.setReversed(true);
				ph.setUser(user);
				ph.setEnteredOn(LocalDateTime.now());
				ph.setRemarks(ucRemarks.getValue());
				service.saveProcessHistory(ph);
				audit.saveAuditReturn(work, "Return To "+pf3.getStepName(), "Entry");
				updateWork("Returned to "+pf3.getStepName(), pf3);
				Notification.show("Returned to "+pf3.getStepName()+" Sucessfully", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				clearFields();
			} catch (Exception e) {
				Notification.show("Something Went Wrong" + e).addThemeVariants(NotificationVariant.LUMO_ERROR);

			}
		}
	}

	public void saveRo() {
		int tableinstallments = service.getInstallments(work).size();
		int toEnterInstallment = tableinstallments;
		int index = tableinstallments - 1;
		try {
			work = service.getWorkById(work.getWorkId());
			if(work.getProcessflow().getStepOrder()!=3) {
				NotificationUtil.showError("This Page Has Expired and will be Reloaded");
				UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
				return;
			}
			if (instLetter.getValue() == "" || instDate.getValue() == null) {
				Notification.show("Release Letter, Release Date Cannot Be Empty", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
				return;
			}
			if (uploadedPdf2 == null || uploadedPdf2.get().length == 0) {
				Notification.show("Please Select The Release Order To be Uploaded", 3000, Position.TOP_CENTER)
						.addThemeVariants(NotificationVariant.LUMO_ERROR);
				return;
			}
			Users user = service.getLoggedUser();
			InstallmentDocument instdoc = new InstallmentDocument();
			instdoc.setDocument(uploadedPdf2.get());
			instdoc.setUpdatedBy(service.getLoggedUser());
			instdoc.setUpdatedOn(LocalDateTime.now());
			service.saveDocuments(instdoc);
			this.installment = service.getInstallments(work).get(index);
			installment.setReleaseOrder(instdoc);
			installment.setInstallmentDate(instDate.getValue());
			installment.setInstallmentLetter(instLetter.getValue());
			service.saveInstallment(installment);
			//work.setProcessflow(service.getProcessFlowByOrder(4));
			//service.saveWork(work);
			ProcessHistory ph = new ProcessHistory();
			ph.setWork(work);
			ph.setProcessFlow(pf3);
			ph.setProcessName(pf3.getStepName()+"-" +toEnterInstallment);
			ph.setReversed(false);
			ph.setUser(user);
			ph.setEnteredOn(LocalDateTime.now());
			ph.setRemarks(roRemarks.getValue());
			audit.saveAudit(work, installment, pf3.getStepName()+"-"+toEnterInstallment, "Entry");
			service.saveProcessHistory(ph);
			updateWork(pf3.getStepName() + toEnterInstallment + "", pf4);
			Notification.show(pf3.getStepName()+"-" + toEnterInstallment + " Completed Sucessfully", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			clearFields();
			service.deleteUnreferencedData();
		} catch (NullPointerException npe) {
			Notification.show("Please Select A File To Upload", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
			//npe.printStackTrace();
		}catch (Exception e) {
			Notification.show("Something Went Wrong :" + e).addThemeVariants(NotificationVariant.LUMO_ERROR);
			//e.printStackTrace();
		}
	}
	public void returnRo() {
		if (ucRemarks.getValue() == null ||ucRemarks.getValue().trim().isEmpty()) {
			Notification.show("Please Enter Remarks", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_WARNING);
		}else {
			work = service.getWorkById(work.getWorkId());
			
			if(work.getProcessflow().getStepOrder()!=3) {
				NotificationUtil.showError("This Page Has Expired and will be Reloaded");
				UI.getCurrent().getPage().executeJs("setTimeout(() => location.reload(), 2000);");
				return;
			}
			try {
				work.setProcessflow(pf2);
				service.saveWork(work);
				Users user=service.getLoggedUser();
				ProcessHistory ph=new ProcessHistory();
				//String prevTask=service.getProcessFlowByOrder(2).getStepName();
				ph.setWork(work);
				ph.setProcessFlow(pf3);
				ph.setProcessName("Return To "+pf2.getStepName());
				ph.setReversed(true);
				ph.setUser(user);
				ph.setEnteredOn(LocalDateTime.now());
				ph.setRemarks(roRemarks.getValue());
				service.saveProcessHistory(ph);
				audit.saveAuditReturn(work, "Return To "+pf2.getStepName(), "Return");
				Notification.show("Returned to "+pf2.getStepName()+" Sucessfully", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				clearFields();
			} catch (Exception e) {
				Notification.show("Something Went Wrong :" + e);

			}
		}
	}
	
	public void clearFields() {
		
		installmentAmount.setValue(BigDecimal.ZERO);
		instLetter.setValue("");
		instDate.setValue(null);
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
	private void updateWork(String text, ProcessFlow pf ) {
		try {
			binder.writeBean(work);
			work.setWorkStatus(text);
			work.setProcessflow(pf);
			work.setUpdatedBy(service.getLoggedUser());
			work.setUpdatedOn(LocalDateTime.now());
			fireEvent(new SaveEvent(this, work));
		} catch (Exception e) {
			Notification.show("Something Went Wrong" + e).addThemeVariants(NotificationVariant.LUMO_ERROR);

		}

	}
	
	public BigDecimal calculateReleasedInstAmount(Work work) {
		int tablecount = service.getInstallments(work).size();

		if (tablecount == 0) {
			return BigDecimal.ZERO;
		} else {
			BigDecimal totalamount = BigDecimal.ZERO;
			for (int i = 0; i < tablecount; i++) {
				BigDecimal amount = service.getInstallments(work).get(i).getInstallmentAmount();
				totalamount = totalamount.add(amount);
			}
			return totalamount;
		}

	}
	
	public static abstract class WorkFormEvent extends ComponentEvent<WorkForm> {
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
		SaveEvent(WorkForm source, Work work) {
			super(source, work);
		}
	}

	public static class DeleteEvent extends WorkFormEvent {
		DeleteEvent(WorkForm source, Work work) {
			super(source, work);
		}

	}

	public static class CloseEvent extends WorkFormEvent {
		CloseEvent(WorkForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}