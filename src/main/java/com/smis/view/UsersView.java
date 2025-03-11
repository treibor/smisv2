package com.smis.view;

import java.time.format.DateTimeFormatter;

import javax.print.attribute.standard.PrinterMoreInfoManufacturer;

import com.smis.dbservice.Dbservice;
import com.smis.entity.ProcessFlowUser;
import com.smis.entity.Users;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@RolesAllowed({"ADMIN", "SUPER"})
@PageTitle("Users")
@Route(value = "users", layout = MainLayout.class)
public class UsersView extends HorizontalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Grid<Users> usergrid=new Grid<>(Users.class);
	Dbservice service;
	UsersForm form;
	Tab tab1=new Tab("Users");
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	public UsersView(Dbservice service) {
		this.service=service;
		form=new UsersForm(service);
		// TODO Auto-generated constructor stub
		setSizeFull();
		configureForms();
		getUsergrid();
		closeEditor();
		add(getContent());
		//add(getTabs());
	}
	public Component getTabs() {
		TabSheet tabSheet = new TabSheet();
		tabSheet.add("Villages",getContent());
		//tabSheet.add(tab1, getAnnexure1());
		tabSheet.setSizeFull();
		return tabSheet;
	}
	private Component getContent() {
		
		HorizontalLayout content=new HorizontalLayout(usergrid, form);
		content.setFlexGrow(1, usergrid);
		content.setFlexGrow(1, form);
		//content.setFlexGrow(1, constiform);
		getUsergrid();
		content.setSizeFull();
		return content;
	}
	
	
	public void getUsergrid() {
		usergrid.removeAllColumns();
		//usergrid.addColumn(users->users.getUserId()).setHeader("Id").setSortable(true).setResizable(true);
		usergrid.addColumn(users->users.getProfileName()).setHeader("Display Name").setSortable(true).setResizable(true);
		usergrid.addColumn(users->users.getUserName()).setHeader("User Name").setSortable(true).setResizable(true);
		usergrid.addColumn(users->users.getEmail()).setHeader("Email").setSortable(true).setResizable(true);
		usergrid.addColumn(users -> users.isEnabled() ? "Yes" : "No")
        .setHeader("Enabled?")
        .setSortable(true)
        .setResizable(true);
		usergrid.addColumn(users->users.getDistrict().getDistrictName()).setHeader("District").setSortable(true).setResizable(true);
		usergrid.addColumn(users->users.getEnteredBy().getProfileName()).setHeader("Created By").setSortable(true).setResizable(true);
		usergrid.addColumn(users->users.getEnteredOn().format(timeFormatter)).setHeader("Created On").setSortable(true).setResizable(true);
		usergrid.setItems(service.findUsers());
		usergrid.asSingleSelect().addValueChangeListener(e->editUser(e.getValue()));
		usergrid.setSizeFull();
		
	}
	
	private void editUser(Users user) {
		// TODO Auto-generated method stub
		form.setVisible(false);
		if (user == null) {
			form.setVisible(false);
		} else {
			form.setUsers(user);
			form.setVisible(true);
			form.checkboxGroup.clear();
			form.checkboxGroup.select(service.fetchRolesForSelectedUser(user));
			// ProcessFlowUser processFlow = null;
			//form.block.setVisible(false);
			if (service.getProcessFlowUser(user).size() > 0) {
				form.refreshpfugrid(user);
				form.refreshblockgrid(user);
				form.refreshschemegrid(user);
			}else {
				form.pfugrid.removeAllColumns();
				form.blugrid.removeAllColumns();
				form.scgrid.removeAllColumns();
			}
		}
	}
	private void configureForms() {
		form.setVisible(false);
		form=new UsersForm(service);
		form.setWidth("30%");
		form.addListener(UsersForm.SaveEvent.class, this::saveUser);
		
		
	}
	public void saveUser(UsersForm.SaveEvent event) {
		service.saveUser(event.getUsers());
		updateGrids();
		closeEditor();
	}

	private void updateGrids() {
		usergrid.setItems(service.findUsers());
		//usergrid.setItems(service.findUsersByDistrictAndUserNameNot(service.getLoggedUser().getDistrict(), "superadmin"));
	}
	private void closeEditor() {
		form.setUsers(null);
		form.setVisible(false);

	}
}
