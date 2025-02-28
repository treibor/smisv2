package com.smis.view;

import com.smis.dbservice.Dbservice;
import com.smis.entity.State;
import com.smis.util.ValidationUtil;
import com.smis.entity.State;
import com.smis.view.ConstimpForm.ConstiFormEvent;
import com.smis.view.ConstimpForm.DeleteEvent;
import com.smis.view.ConstimpForm.SaveEvent;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class StateForm extends FormLayout{
	Dbservice service;
	Binder<State> binder=new BeanValidationBinder<>(State.class);
	//IntegerField schemeDuration=new IntegerField("Scheme Duration");
	TextField stateName=new TextField("State Name");
	TextField stateHq=new TextField("State Hq");
	TextField stateLabel=new TextField("State Label");
	Button save= new Button("Save");
	Button delete= new Button("Delete");
	Notification notify=new Notification();
	private State state;
	public StateForm(Dbservice service) {
		this.service=service;
		binder.bindInstanceFields(this);
		ValidationUtil.applyTextOnly(stateHq);
		ValidationUtil.applyTextOnly(stateLabel);
		ValidationUtil.applyTextOnly(stateName);
		add(stateName, stateHq, stateLabel, createButtonsLayout());
	}
	
	private Component createButtonsLayout() {
		// TODO Auto-generated method stub
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.addClickShortcut(Key.ENTER);
		save.addClickListener(event-> validateandSave());
		delete.addClickListener(event-> fireEvent(new DeleteEvent(this, state)));
		delete.setEnabled(service.isAdmin());
		return new HorizontalLayout(save, delete);
	}
	
	private void validateandSave() {
		try {

			binder.writeBean(state);
			fireEvent(new SaveEvent(this, state));

		} catch (ValidationException e) {
			notify.show("Please Enter All Required Fields", 3000, Position.TOP_CENTER);
			
		} catch (Exception e) {
			
		}

	}

	public void setState(State state) {
		this.state=state;
		binder.readBean(state);
	}
	
	public static abstract class StateFormEvent extends ComponentEvent<StateForm> {
		private State state;

		protected StateFormEvent(StateForm source, State state) {
			super(source, false);
			this.state = state;
		}

		public State getState() {
			return state;
		}
	}

	public static class SaveEvent extends StateFormEvent {
		SaveEvent(StateForm source, State state) {
			super(source, state);
		}
	}

	public static class DeleteEvent extends StateFormEvent {
		DeleteEvent(StateForm source, State state) {
			super(source, state);
		}

	}

	public static class CloseEvent extends StateFormEvent {
		CloseEvent(StateForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}

	
	

