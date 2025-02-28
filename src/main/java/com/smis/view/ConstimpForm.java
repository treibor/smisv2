package com.smis.view;


import com.smis.dbservice.Dbservice;
import com.smis.dbservice.DbserviceMp;
import com.smis.entity.Constituency;
import com.smis.entity.Constituencymp;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class ConstimpForm extends FormLayout{
	DbserviceMp service;
	Binder<Constituencymp> binder=new BeanValidationBinder<>(Constituencymp.class);
	IntegerField schemeDuration=new IntegerField("Scheme Duration");
	TextField constituencyName=new TextField("Constituency Name");
	TextField constituencyMp=new TextField("MP Name");
	TextField constituencyType=new TextField("Constituency Type");
	TextField address1=new TextField("Address Line 1");
	TextField address2=new TextField("Address Line 2");
	TextField address3=new TextField("Address Line 3");
	Checkbox inUse=new Checkbox("In Use");
	Button save= new Button("Save");
	Button delete= new Button("Delete");
	Notification notify=new Notification();
	private Constituencymp consti;
	public ConstimpForm(DbserviceMp service) {
		this.service=service;
		binder.bindInstanceFields(this);
		//schemeDuration.setHasControls(true);
		schemeDuration.setValue(1);
		constituencyType.setHelperText("Eg: 16th-Lok Sabha");
		
		add( constituencyName, constituencyType, constituencyMp,address1, address2, address3, schemeDuration, inUse, createButtonsLayout());
	}
	
	private Component createButtonsLayout() {
		// TODO Auto-generated method stub
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.addClickShortcut(Key.ENTER);
		save.addClickListener(event-> validateandSave());
		delete.addClickListener(event-> fireEvent(new DeleteEvent(this, consti)));
		delete.setEnabled(service.isAdmin());
		return new HorizontalLayout(save, delete);
	}
	private void validateandSave() {
		try {
			
				binder.writeBean(consti);
				consti.setDistrict(service.getDistrict());
				//consti.setConstituencyLabel("-");
				fireEvent(new SaveEvent(this, consti));
			
		} catch (ValidationException e) {
			notify.show("Please Enter All Required Fields",3000,Position.TOP_CENTER);
			
		}catch (Exception e) {
			
		}
		
	}

	public void setConstituencymp(Constituencymp consti) {
		this.consti=consti;
		binder.readBean(consti);
	}
	
	public static abstract class ConstiFormEvent extends ComponentEvent<ConstimpForm> {
		private Constituencymp consti;

		protected ConstiFormEvent(ConstimpForm source, Constituencymp consti) {
			super(source, false);
			this.consti = consti;
		}

		public Constituencymp getConstituencymp() {
			return consti;
		}
	}

	public static class SaveEvent extends ConstiFormEvent {
		SaveEvent(ConstimpForm source, Constituencymp consti) {
			super(source, consti);
		}
	}

	public static class DeleteEvent extends ConstiFormEvent {
		DeleteEvent(ConstimpForm source, Constituencymp consti) {
			super(source, consti);
		}

	}

	public static class CloseEvent extends ConstiFormEvent {
		CloseEvent(ConstimpForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}
