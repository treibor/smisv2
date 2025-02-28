package com.smis.view;

import com.smis.dbservice.Dbservice;
import com.smis.dbservice.DbserviceMp;
import com.smis.entity.Impldistrict;
import com.smis.entity.Impldistrict;
import com.smis.entity.Impldistrict;
import com.smis.entity.State;
import com.smis.view.ImpldistrictForm.ConstiFormEvent;
import com.smis.view.ImpldistrictForm.DeleteEvent;
import com.smis.view.ImpldistrictForm.SaveEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;


public class ImpldistrictForm extends FormLayout {
	Dbservice service;
	Binder<Impldistrict> binder=new BeanValidationBinder<>(Impldistrict.class);
	ComboBox<State> state=new ComboBox();
	TextField districtName=new TextField("Impldistrict Name");
	TextField deputyCommissioner=new TextField("DC Label");
	TextField deputyCommissionerName=new TextField("DC Name");
	TextField districtHq=new TextField("HQ/Capital");
	TextField districtLabel=new TextField("Label");
	Button save= new Button("Save");
	Button delete= new Button("Delete");
	Notification notify=new Notification();
	private Impldistrict district;
	//private Impldistrict impldist;
	public ImpldistrictForm(Dbservice service) {
		this.service=service;
		state.setItems(service.getAllStates());
		state.setItemLabelGenerator(state-> state.getStateName());
		binder.bindInstanceFields(this);
		
		add(districtName, deputyCommissioner,districtHq,deputyCommissionerName, createButtonsLayout());
	
	}

	private Component createButtonsLayout() {
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.addClickShortcut(Key.ENTER);
		save.addClickListener(event-> validateandSave());
		delete.addClickListener(event-> fireEvent(new DeleteEvent(this, district)));
		///delete.setEnabled(service.isAdmin());
		return new HorizontalLayout(save);
	}
	
	private void validateandSave() {
		try {
			//long dist_code=service.getMaxImpldistrictCode()+1;
			binder.writeBean(district);
			fireEvent(new SaveEvent(this, district));
		} catch (ValidationException e) {
			notify.show("Please Enter All Required Fields", 3000, Position.TOP_CENTER);
			
		} catch (Exception e) {
			
		}

	}

	public void setImpldistrict(Impldistrict district) {
		this.district=district;
		binder.readBean(district);
	}
	
	public static abstract class ConstiFormEvent extends ComponentEvent<ImpldistrictForm> {
		private Impldistrict district;

		protected ConstiFormEvent(ImpldistrictForm source, Impldistrict district) {
			super(source, false);
			this.district = district;
		}

		public Impldistrict getImpldistrict() {
			return district;
		}
	}

	public static class SaveEvent extends ConstiFormEvent {
		SaveEvent(ImpldistrictForm source, Impldistrict district) {
			super(source, district);
		}
	}

	public static class DeleteEvent extends ConstiFormEvent {
		DeleteEvent(ImpldistrictForm source, Impldistrict district) {
			super(source, district);
		}

	}

	public static class CloseEvent extends ConstiFormEvent {
		CloseEvent(ImpldistrictForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}

	
}
