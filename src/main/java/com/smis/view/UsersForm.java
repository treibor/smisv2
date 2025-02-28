package com.smis.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.ProcessFlow;
import com.smis.entity.ProcessFlowUser;
import com.smis.entity.Users;
import com.smis.entity.UsersRoles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;


public class UsersForm extends FormLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dbservice service;
	Binder<Users> binder=new BeanValidationBinder<>(Users.class);
	Checkbox enabled=new Checkbox("Enabled");
	TextField districtLabel=new TextField("Label");
	Button save= new Button("Update");
	CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
	public Grid<ProcessFlowUser> pfugrid=new Grid<ProcessFlowUser>();
	//CheckboxGroup<Scheme> schemeGroup = new CheckboxGroup<>();
	public ComboBox<ProcessFlow> processflow=new ComboBox<ProcessFlow>("Process");
	public ComboBox<Block> block=new ComboBox<Block>("Block");
	//ComboBox<MasterProcess> schemeprocess=new ComboBox<MasterProcess>("Assigned Task");
	Button savetask= new Button("Add Process");
	Button deletetask= new Button("Delete Process");
	private Users user;
	//private Impldistrict impldist;
	public UsersForm(Dbservice service) {
		this.service=service;
		//schemes.setValue(null);
		binder.bindInstanceFields(this);
		add(createForm());
	
	}

	private Component createForm() {
		checkboxGroup.setLabel("Roles");
		checkboxGroup.setItems("ADMIN", "USER");
		//checkboxGroup.select("Order ID", "Customer");
		checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
		processflow.setItems(service.getAllProcessFlow());
		processflow.setItemLabelGenerator(ProcessFlow::getStepName);
		//processflow.addValueChangeListener(e->addProcessScheme(e.getValue()));
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.addClickShortcut(Key.ENTER);
		block.setItems(service.getAllBlocks());
		block.setItemLabelGenerator(Block::getBlockName);
		save.addClickListener(event-> validateandSave());
		processflow.addValueChangeListener(e-> getBlocks(e.getValue()));
		savetask.addClickListener(e->addProcess());
		deletetask.addClickListener(e->deleteProcess());
		block.setVisible(false);
		pfugrid.setHeight("200px");
		return new VerticalLayout(enabled,checkboxGroup,save,  processflow,block,  savetask,deletetask,pfugrid);
	}
	private void deleteProcess() {
		try {
		service.deleteProcessFlowUser(pfugrid.asSingleSelect().getValue());
			Notification.show("Process Deleted");
		}catch(Exception e) {
			Notification.show("Error:"+e);
		}
	}
	private void addProcess() {
		ProcessFlowUser existingPFU = service.getProcessFlowUser(user, processflow.getValue());
		if (existingPFU != null) {
		    // Update existing entry
		    existingPFU.setAssignedDate(LocalDate.now());
		    service.saveProcessFlowUser(existingPFU);
		    
		} else {
		    // Create new entry
		    ProcessFlowUser pfu = new ProcessFlowUser();
		    pfu.setUser(user);
		    pfu.setProcessFlow(processflow.getValue());
		    pfu.setAssignedDate(LocalDate.now());
		    service.saveProcessFlowUser(pfu);
		}
		Notification.show("Process Assigned to User");
		refreshpfugrid(user);
	}
	
	public void refreshpfugrid(Users user) {
		pfugrid.removeAllColumns();
		pfugrid.addColumn(processflowuser->processflowuser.getProcessFlow().getStepOrder()).setHeader("Order").setResizable(true);
		pfugrid.addColumn(processflowuser->processflowuser.getProcessFlow().getStepName()).setHeader("Process").setResizable(true).setWidth("95%");
		pfugrid.setItems(service.getProcessFlowUser(user));
	}
	
	
	public void getBlocks(ProcessFlow pf) {
		if (pf != null) {
			String process = pf.getStepName();
			if (process.equals("Enter UC")) {
				block.setVisible(true);
			} else {
				block.setVisible(false);
			}
		}
	}
	
	

	private Component createButtonsLayout() {
		
		return new HorizontalLayout(save);
	}
	
	private void validateandSave() {
		try {
			binder.writeBean(user);
			saveOrUpdateRoles(user);
			fireEvent(new SaveEvent(this, user));
			//System.out.println(checkboxGroup.getValue());
			//Set<String> selectedRoles = ;
			
		} catch (ValidationException e) {
			Notification.show("Please Enter All Required Fields", 3000, Position.TOP_CENTER);
			
		} catch (Exception e) {
			
		}

	}
	private void saveOrUpdateRoles(Users user) {
		//System.out.println("User"+user.getUserName());
	    // Get selected roles from the CheckboxGroup
	    Set<String> selectedRoles = checkboxGroup.getValue();

	    // Fetch existing roles for the user from the database
	    List<UsersRoles> existingRoles = service.getRolesByUser(user);

	    // Convert existing roles to a Set for easy comparison
	    Set<String> existingRoleNames = existingRoles.stream()
	                                                 .map(UsersRoles::getRoleName)
	                                                 .collect(Collectors.toSet());

	    // Save new roles (roles in `selectedRoles` but not in `existingRoleNames`)
	    selectedRoles.stream()
	                 .filter(role -> !existingRoleNames.contains(role))
	                 .forEach(roleName -> {
	                     UsersRoles newRole = new UsersRoles();
	                     
	                     newRole.setUser(user);
	                     newRole.setRoleName(roleName);
	                     //System.out.println(newRole.getUser());
	                     service.saveRole(newRole); // Save the new role
	                 });

	    // Remove roles no longer selected (roles in `existingRoleNames` but not in `selectedRoles`)
	    existingRoles.stream()
	                 .filter(role -> !selectedRoles.contains(role.getRoleName()))
	                 .forEach(roleToRemove -> {
	                     service.deleteRole(roleToRemove); // Remove the role
	                 });
	}
	public void setUsers(Users user) {
		this.user=user;
		binder.readBean(user);
	}
	
	public static abstract class UsersFormEvent extends ComponentEvent<UsersForm> {
		private Users user;

		protected UsersFormEvent(UsersForm source, Users user) {
			super(source, false);
			this.user = user;
		}

		public Users getUsers() {
			return user;
		}
	}

	public static class SaveEvent extends UsersFormEvent {
		SaveEvent(UsersForm source, Users user) {
			super(source, user);
		}
	}

	public static class DeleteEvent extends UsersFormEvent {
		DeleteEvent(UsersForm source, Users user) {
			super(source, user);
		}

	}

	public static class CloseEvent extends UsersFormEvent {
		CloseEvent(UsersForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}

	
}
