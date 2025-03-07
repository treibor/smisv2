package com.smis.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.BlockUser;
import com.smis.entity.ProcessFlow;
import com.smis.entity.ProcessFlowUser;
import com.smis.entity.Scheme;
import com.smis.entity.SchemeUser;
import com.smis.entity.Users;
import com.smis.entity.UsersRoles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
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
	public Grid<BlockUser> blugrid=new Grid<BlockUser>();
	public Grid<SchemeUser> scgrid=new Grid<SchemeUser>();
	//CheckboxGroup<Scheme> schemeGroup = new CheckboxGroup<>();
	public ComboBox<ProcessFlow> processflow=new ComboBox<ProcessFlow>("Process");
	public ComboBox<Block> blockc=new ComboBox<Block>("Block");
	public ComboBox<Scheme> scheme=new ComboBox<Scheme>("Scheme");
	//ComboBox<MasterProcess> schemeprocess=new ComboBox<MasterProcess>("Assigned Task");
	Button savetask= new Button("Add");
	Button deletetask= new Button("Delete");
	Button saveblock= new Button("Add");
	Button deleteblock= new Button("Delete");
	Button savescheme= new Button("Add");
	Button deletescheme= new Button("Delete");
	private Users user;
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
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
	    checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

	    processflow.setItems(service.getAllProcessFlow());
	    processflow.setItemLabelGenerator(ProcessFlow::getStepName);
	    blockc.setItems(service.getAllBlocks());
	    blockc.setItemLabelGenerator(Block::getBlockName);
	    scheme.setItems(service.getAllSchemes());
	    scheme.setItemLabelGenerator(Scheme::getSchemeName);

	    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    save.addClickShortcut(Key.ENTER);
	    save.addClickListener(event -> validateandSave());

	    savetask.addClickListener(e -> addProcess());
	    saveblock.addClickListener(e -> addBlock());
	    savescheme.addClickListener(e -> addScheme());
	    deletetask.addClickListener(e -> deleteProcess());
	    deleteblock.addClickListener(e -> deleteBlockUser());
	    deletescheme.addClickListener(e -> deleteSchemeUser());

	    deletetask.setEnabled(false);
	    deleteblock.setEnabled(false);
	    deletescheme.setEnabled(false);

	    checkboxGroup.setVisible(service.isSuperAdmin());

	    // Process Flow Section
	    HorizontalLayout processlayout = new HorizontalLayout(processflow, savetask, deletetask);
	    processlayout.setAlignItems(FlexComponent.Alignment.BASELINE);
	    VerticalLayout processContent = new VerticalLayout(processlayout, pfugrid);
	    processContent.setSpacing(true);
	    pfugrid.setHeight("400px");

	    // Block Section
	    HorizontalLayout blocklayout = new HorizontalLayout(blockc, saveblock, deleteblock);
	    blocklayout.setAlignItems(FlexComponent.Alignment.BASELINE);
	    VerticalLayout blockContent = new VerticalLayout(blocklayout, blugrid);
	    blockContent.setSpacing(true);
	    blugrid.setHeight("400px");

	    // Scheme Section
	    HorizontalLayout schemelayout = new HorizontalLayout(scheme, savescheme, deletescheme);
	    schemelayout.setAlignItems(FlexComponent.Alignment.BASELINE);
	    VerticalLayout schemeContent = new VerticalLayout(schemelayout, scgrid);
	    schemeContent.setSpacing(true);
	    scgrid.setHeight("400px");

	    // Accordion with sections
	    Accordion accordion = new Accordion();
	    //accordion.add("Roles", checkboxGroup);
	    accordion.add("Process Flow", processContent);
	    accordion.add("Block Assignment", blockContent);
	    accordion.add("Scheme Assignment", schemeContent);

	    // Make the accordion scrollable
	    VerticalLayout layout = new VerticalLayout(checkboxGroup, enabled, save,accordion);
	    layout.setSizeFull();
	    layout.getStyle().set("overflow", "auto");

	    Scroller scroller = new Scroller(layout);
	    scroller.setSizeFull();

	    return scroller;
	}


	private void deleteProcess() {
		try {
			service.deleteProcessFlowUser(pfugrid.asSingleSelect().getValue());
			Notification.show("Process Deleted");
		} catch (Exception e) {
			Notification.show("Error:" + e);
		}
		refreshpfugrid(user);
	}
	private void addProcess() {
		ProcessFlowUser existingPFU = service.getProcessFlowUser(user, processflow.getValue());
		if (existingPFU != null) {
		    // Update existing entry
		    existingPFU.setAssignedDate(LocalDateTime.now());
		    existingPFU.setAssignedBy(service.getLoggedUser());
		    service.saveProcessFlowUser(existingPFU);
		    
		} else {
		    // Create new entry
		    ProcessFlowUser pfu = new ProcessFlowUser();
		    pfu.setUser(user);
		    pfu.setProcessFlow(processflow.getValue());
		    pfu.setAssignedDate(LocalDateTime.now());
		    pfu.setAssignedBy(service.getLoggedUser());
		    service.saveProcessFlowUser(pfu);
		}
		Notification.show("Process Assigned to User");
		refreshpfugrid(user);
	}
	
	public void refreshpfugrid(Users user) {
		pfugrid.removeAllColumns();
		//pfugrid.addColumn(processflowuser->processflowuser.getProcessFlow().getStepOrder()).setHeader("Order").setResizable(true);
		pfugrid.addColumn(processflowuser->processflowuser.getProcessFlow().getStepName()).setHeader("Process").setResizable(true);
		pfugrid.addColumn(processflowuser->processflowuser.getAssignedDate().format(timeFormatter)).setHeader("Updated On").setResizable(true);
		List<ProcessFlowUser> items = service.getProcessFlowUser(user);
		pfugrid.setItems(items);
		pfugrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		pfugrid.asSingleSelect().addValueChangeListener(event -> {
	    ProcessFlowUser selectedItem = event.getValue(); // Replace MyObject with your actual item type
		    if (selectedItem != null) {
		        deletetask.setEnabled(true);
		    } else {
		    	deletetask.setEnabled(false);
		    }
		});
	
	}
	private void deleteBlockUser() {
		try {
			service.deleteBlockUser(blugrid.asSingleSelect().getValue());
			Notification.show("Block Deleted");
		} catch (Exception e) {
			Notification.show("Error:" + e);
		}
		refreshblockgrid(user);
	}
	private void addBlock() {
		BlockUser existingBU = service.getBlockUser(user, blockc.getValue());
		if (existingBU != null) {
		    existingBU.setAssignedDate(LocalDateTime.now());
		    existingBU.setAssignedBy(service.getLoggedUser());
		    service.saveBlockUser(existingBU);
		    
		} else {
		    BlockUser pfu = new BlockUser();
		    pfu.setUser(user);
		    pfu.setBlock(blockc.getValue());
		    pfu.setAssignedDate(LocalDateTime.now());
		    pfu.setAssignedBy(service.getLoggedUser());
		    service.saveBlockUser(pfu);
		}
		Notification.show("Block Assigned to User");
		refreshblockgrid(user);
	}
	
	public void refreshblockgrid(Users user) {
		blugrid.removeAllColumns();
		//blugrid.addColumn(block->block.getBlock().getBlockId()).setHeader("Order").setResizable(true);
		blugrid.addColumn(block->block.getBlock().getBlockName()).setHeader("Process").setResizable(true);
		blugrid.addColumn(block->block.getAssignedDate().format(timeFormatter)).setHeader("Updated On").setResizable(true);
		List<BlockUser> items = service.getBlockUser(user);
		blugrid.setItems(items);
		blugrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		blugrid.asSingleSelect().addValueChangeListener(event -> {
	    BlockUser selectedItem = event.getValue(); // Replace MyObject with your actual item type
		    if (selectedItem != null) {
		        deleteblock.setEnabled(true);
		    } else {
		    	deleteblock.setEnabled(false);
		    }
		});
		//pfugrid.setVisible(!items.isEmpty());
	}
	private void deleteSchemeUser() {
		try {
			service.deleteSchemeUser(scgrid.asSingleSelect().getValue());
			Notification.show("Scheme Deleted");
		} catch (Exception e) {
			Notification.show("Error:" + e);
		}
		refreshschemegrid(user);
	}
	private void addScheme() {
		SchemeUser existingSU = service.getSchemeUser(user, scheme.getValue());
		if (existingSU != null) {
		    existingSU.setAssignedDate(LocalDateTime.now());
		    existingSU.setAssignedBy(service.getLoggedUser());
		    service.saveSchemeUser(existingSU);
		    
		} else {
		    SchemeUser pfu = new SchemeUser();
		    pfu.setUser(user);
		    pfu.setScheme(scheme.getValue());
		    pfu.setAssignedDate(LocalDateTime.now());
		    pfu.setAssignedBy(service.getLoggedUser());
		    service.saveSchemeUser(pfu);
		}
		Notification.show("Scheme Assigned to User");
		refreshschemegrid(user);
	}
	
	public void refreshschemegrid(Users user) {
		scgrid.removeAllColumns();
		//scgrid.addColumn(scheme->scheme.getId()).setHeader("No.").setResizable(true);
		scgrid.addColumn(scheme->scheme.getScheme().getSchemeName()).setHeader("Scheme").setResizable(true);
		scgrid.addColumn(scheme->scheme.getAssignedDate().format(timeFormatter)).setHeader("Updated On").setResizable(true);
		List<SchemeUser> items = service.getSchemeUser(user);
		scgrid.setItems(items);
		scgrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		scgrid.asSingleSelect().addValueChangeListener(event -> {
	    SchemeUser selectedItem = event.getValue(); // Replace MyObject with your actual item type
		    if (selectedItem != null) {
		        deletescheme.setEnabled(true);
		    } else {
		    	deletescheme.setEnabled(false);
		    }
		});
		
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
