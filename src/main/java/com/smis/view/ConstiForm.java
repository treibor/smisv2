package com.smis.view;


import com.smis.dbservice.Dbservice;
import com.smis.entity.Constituency;
import com.smis.util.ValidationUtil;
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

public class ConstiForm extends FormLayout{
	Dbservice service;
	Binder<Constituency> binder=new BeanValidationBinder<>(Constituency.class);
	IntegerField constituencyNo=new IntegerField("Constituency Number");
	TextField constituencyName=new TextField("Constituency Name");
	TextField constituencyMLA=new TextField("Constituency MLA");
	TextField constituencyLabel=new TextField("Constituency Label");
	Button save= new Button("Save");
	Button delete= new Button("Delete");
	Notification notify=new Notification();
	Checkbox inUse=new Checkbox("In Use");
	private Constituency consti;
	public ConstiForm(Dbservice service) {
		this.service=service;
		binder.bindInstanceFields(this);
		ValidationUtil.applyTextOnly(constituencyLabel);
		ValidationUtil.applyTextOnly(constituencyMLA);
		
		add(constituencyNo, constituencyName, constituencyMLA, inUse,createButtonsLayout());
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
			if (constituencyNo.getValue()==null||constituencyNo.getValue() < 1) {
				notify.show("Constituency Number is Invalid. Please Check", 5000, Position.TOP_CENTER);
			} else {
				binder.writeBean(consti);
				consti.setDistrict(service.getDistrict());
				fireEvent(new SaveEvent(this, consti));
			}
		} catch (ValidationException e) {
			//notification.show("Please Enter All Required Fields",3000,Position.TOP_CENTER);
			
		}catch (Exception e) {
			
		}
		
	}

	public void setConstituency(Constituency consti) {
		this.consti=consti;
		binder.readBean(consti);
	}
	
	public static abstract class ConstiFormEvent extends ComponentEvent<ConstiForm> {
		private Constituency consti;

		protected ConstiFormEvent(ConstiForm source, Constituency consti) {
			super(source, false);
			this.consti = consti;
		}

		public Constituency getConstituency() {
			return consti;
		}
	}

	public static class SaveEvent extends ConstiFormEvent {
		SaveEvent(ConstiForm source, Constituency consti) {
			super(source, consti);
		}
	}

	public static class DeleteEvent extends ConstiFormEvent {
		DeleteEvent(ConstiForm source, Constituency consti) {
			super(source, consti);
		}

	}

	public static class CloseEvent extends ConstiFormEvent {
		CloseEvent(ConstiForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}
