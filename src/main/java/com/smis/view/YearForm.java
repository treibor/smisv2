package com.smis.view;

import com.smis.dbservice.Dbservice;
import com.smis.entity.Year;
import com.smis.entity.master.MasterYear;
import com.smis.util.ButtonUtil;
import com.smis.util.ValidationUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class YearForm extends FormLayout{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dbservice service;
	Binder<Year> binder=new BeanValidationBinder<>(Year.class);
	//TextField yearName=new TextField("Financial Year");
	TextField yearLabel=new TextField("Year Label");
	Button save= new Button("Save");
	Button delete= new Button("Delete");
	Checkbox inUse=new Checkbox("In Use");
	private Year year;
	public Button addButton=new  Button("New");
	ComboBox<MasterYear> masterYear = new ComboBox<>("Financial Year");
	public YearForm(Dbservice service) {
		this.service=service;
		binder.bindInstanceFields(this);
		//yearName.setValueChangeMode(ValueChangeMode.LAZY);
		ValidationUtil.applyValidation(yearLabel);
		//ValidationUtil.applyValidation(yearName);
		//yearName.addValueChangeListener(e->yearLabel.setValue(e.getValue()));
		masterYear.setItems(service.getMasterYears());
		masterYear.setItemLabelGenerator(masteryear->masteryear.getYearName());
		masterYear.addValueChangeListener(e->yearLabel.setValue(e.getValue().getYearName()));
		add(new Span("* Click New Button To Add New Item"),masterYear, yearLabel, inUse, createButtonsLayout());
	}
	
	private Component createButtonsLayout() {
		// TODO Auto-generated method stub
		ButtonUtil.applySaveStyle(save);
		ButtonUtil.applyDeleteStyle(delete);
		ButtonUtil.applyNewStyle(addButton);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.addClickShortcut(Key.ENTER);
		save.addClickListener(event-> validateandSave());
		delete.addClickListener(event-> fireEvent(new DeleteEvent(this, year)));
		delete.setEnabled(service.hasRole("Admin"));
		addButton.addClickListener(event->setYear(new Year()));
		return new HorizontalLayout(save, delete, addButton);
	}
	private void validateandSave() {
		try {
			binder.writeBean(year);
			year.setDistrict(service.getDistrict());
			fireEvent(new SaveEvent(this, year));
		} catch (ValidationException e) {
			//notification.show("Please Enter All Required Fields",3000,Position.TOP_CENTER);
			
		}catch (Exception e) {
			
		}
		
	}

	public void setYear(Year year) {
		this.year=year;
		binder.readBean(year);
		save.setEnabled(true);
		delete.setEnabled(false);
	}
	
	public static abstract class YearFormEvent extends ComponentEvent<YearForm> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Year Year;

		protected YearFormEvent(YearForm source, Year Year) {
			super(source, false);
			this.Year = Year;
		}

		public Year getYear() {
			return Year;
		}
	}

	public static class SaveEvent extends YearFormEvent {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		SaveEvent(YearForm source, Year Year) {
			super(source, Year);
		}
	}

	public static class DeleteEvent extends YearFormEvent {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		DeleteEvent(YearForm source, Year Year) {
			super(source, Year);
		}

	}

	public static class CloseEvent extends YearFormEvent {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		CloseEvent(YearForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}
