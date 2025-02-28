package com.smis.view;

import com.smis.dbservice.Dbservice;
import com.smis.entity.Scheme;
import com.smis.util.ValidationUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class SchemeForm extends FormLayout{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dbservice service;
	Binder<Scheme> binder=new BeanValidationBinder<>(Scheme.class);
	//Binder<Scheme> binder2=new BeanValidationBinder<>(SchemeProcess.class);
	//IntegerField constituencyNo=new IntegerField("Scheme Number");
	TextField schemeName=new TextField("Scheme Name");
	TextField schemeNameLong=new TextField("Scheme Full Name");
	TextField schemeDept=new TextField("Scheme Department");
	//TextField schemeDeptLong=new TextField("Scheme Department");
	IntegerField schemeDuration=new IntegerField("Scheme Duration in Months");
	//NumberField schemeAllocation=new NumberField("Allocation Amount Per Financial Year");
	BigDecimalField schemeAllocation= new BigDecimalField("Allocation Amount Per Financial Year");
	TextField schemeLabel=new TextField("Scheme Label");
	IntegerField schemeReport=new IntegerField("Type of Report");
	Button save= new Button("Save");
	Button delete= new Button("Delete");
	Checkbox inUse=new Checkbox("In Use");
	private Scheme scheme;
	Notification notify=new Notification();
	Accordion accordion = new Accordion();
	AccordionPanel schememasteraccordion = new AccordionPanel();
	AccordionPanel schemeprocessaccordion = new AccordionPanel();
	
	//AccordionPanel ucaccordion = new AccordionPanel();
	public SchemeForm(Dbservice service) {
		this.service=service;
		binder.bindInstanceFields(this);
		schemeLabel.setHelperText("Eg: SRWP(MLA Scheme) or CMSDF(NGO)");
		schemeReport.setHelperText("1-For SRWP,CRRP etc 2-For ISYDP,DTFM etc 3-For CMSDF, 4-For CMSDF(Ngo)");
		//schemeReport.setHasControls(true);
		schemeReport.setMax(4);
		schemeReport.setMin(1);
		schemeReport.setValue(1);
		ValidationUtil.applyValidation(schemeDept);
		ValidationUtil.applyValidation(schemeLabel);
		ValidationUtil.applyValidation(schemeName);
		ValidationUtil.applyValidation(schemeNameLong);
		
		//add(schemeName, schemeNameLong, schemeDept, schemeDuration,  schemeLabel, schemeReport,inUse, createButtonsLayout());
		add (createFinalPanel());
	}
	
	public Component createFinalPanel() {
		schememasteraccordion = accordion.add("Master", createSchemeLayout());
		//schemeprocessaccordion = accordion.add("Scheme Process", createSchemeProcessLayout());
		return accordion;
	}
	
	
	public Component createSchemeProcessLayout() {
	    // Fetch and initialize the list of SchemeProcess objects
		
		/*
		 * //System.out.println("Scheme"+this.scheme); List<MasterProcess>
		 * schemeProcesses = new ArrayList<>(service.getSchemeProcess(scheme));
		 * 
		 * // Create a ListBox for SchemeProcess ListBox<MasterProcess> listBox = new
		 * ListBox<>(); listBox.setItems(schemeProcesses);
		 * listBox.setItemLabelGenerator(MasterProcess::getProcessName); // Adjust the
		 * method as needed listBox.setSizeFull();
		 */
		/*
		 * // Create buttons Button moveUp = new Button("Move Up", event ->
		 * moveItemUp(listBox, schemeProcesses)); Button moveDown = new
		 * Button("Move Down", event -> moveItemDown(listBox, schemeProcesses));
		 */
	    
	    // Layout and return
	    VerticalLayout layout = new VerticalLayout();
	    return layout;
	}
	
	
	
	private Component createSchemeLayout() {
		return new VerticalLayout(schemeName, schemeNameLong, schemeDept, schemeDuration,  schemeLabel, schemeReport,inUse, createButtonsLayout());
	}
	private Component createButtonsLayout() {
		// TODO Auto-generated method stub
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.addClickShortcut(Key.ENTER);
		save.addClickListener(event-> validateandSave());
		delete.addClickListener(event-> fireEvent(new DeleteEvent(this, scheme)));
		delete.setEnabled(service.isAdmin());
		return new HorizontalLayout(save, delete);
	}
	private void validateandSave() {
		if(schemeReport.getValue()<1 || schemeReport.getValue()>4) {
			notify.show("Invalid Report Type Entered. Please Select a Valid Report Type", 5000, Position.TOP_CENTER);
		}else {
		try {
			binder.writeBean(scheme);
			scheme.setDistrict(service.getDistrict());
			fireEvent(new SaveEvent(this, scheme));
		} catch (ValidationException e) {
			//notify.show("Error",3000,Position.TOP_CENTER);
			
		}catch (Exception e) {
			
		}
		}
	}

	public void setScheme(Scheme scheme) {
		this.scheme=scheme;
		binder.readBean(scheme);
	}
	
	public static abstract class SchemeFormEvent extends ComponentEvent<SchemeForm> {
		private Scheme scheme;

		protected SchemeFormEvent(SchemeForm source, Scheme scheme) {
			super(source, false);
			this.scheme = scheme;
		}

		public Scheme getScheme() {
			return scheme;
		}
	}

	public static class SaveEvent extends SchemeFormEvent {
		SaveEvent(SchemeForm source, Scheme scheme) {
			super(source, scheme);
		}
	}

	public static class DeleteEvent extends SchemeFormEvent {
		DeleteEvent(SchemeForm source, Scheme scheme) {
			super(source, scheme);
		}

	}

	public static class CloseEvent extends SchemeFormEvent {
		CloseEvent(SchemeForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}
