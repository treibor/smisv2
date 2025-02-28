package com.smis.view;

import com.smis.dbservice.DbserviceMp;
import com.smis.entity.Constituencymp;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
@PageTitle("MP Master Data")
@Route(value="mpmaster", layout=MainLayout.class)
@RolesAllowed({"ADMIN","SUPER"})
public class MastermpView extends VerticalLayout{
	DbserviceMp service;
	Grid<Constituencymp> grid=new Grid<>(Constituencymp.class);
	ConstimpForm form;
	boolean isSuper;
	boolean isAdmin;
	public MastermpView(DbserviceMp service) {
		this.service = service;
		isSuper=service.isSuperAdmin();
		isAdmin=service.isAdmin();
		setSizeFull();
		configureForm();
		configureGrid();
		updateGrid();
		closeConstiEditor();
		add(getToolbar(), getContent());
	}
	private Component getToolbar() {
		Button addButton=new  Button("Constituency");
		addButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));
		addButton.addClickListener(e-> addConsti());
		HorizontalLayout toolbar=new HorizontalLayout(addButton);
		toolbar.setWidthFull();
		return toolbar;
	}
	
	private void configureGrid() {
		grid.setSizeFull();
		grid.setColumns("constituencyName", "constituencyType");
		grid.addColumn(constituency-> constituency.getConstituencyMp()).setHeader("MP Name");
		grid.addColumn(constituency-> constituency.getAddress1()).setHeader("Address Line 1");
		grid.addColumn(constituency-> constituency.getAddress2()).setHeader("Address Line 2");
		grid.addColumn(constituency-> constituency.getAddress3()).setHeader("Address Line 3");
		grid.addColumn(constituency-> constituency.getSchemeDuration()).setHeader("Scheme Duration");
		grid.addColumn(constituency-> constituency.isInUse()).setHeader("In Use");
		grid.addColumn(constituency-> constituency.getDistrict().getDistrictName()).setSortable(true).setHeader("District").setVisible(isSuper);
		grid.addColumn(constituency-> constituency.getDistrict().getState().getStateName()).setSortable(true).setHeader("State").setVisible(isSuper);
		grid.getColumns().forEach(col-> col.setAutoWidth(true));
		grid.asSingleSelect().addValueChangeListener(e-> editConsti(e.getValue()));
	}
	
	private void configureForm() {
		form=new ConstimpForm(service);
		form.setWidth("30%");
		form.addListener(ConstimpForm.SaveEvent.class, this::saveConstituency);
		form.addListener(ConstimpForm.DeleteEvent.class, this::deleteConstituency);
		form.addListener(ConstimpForm.DeleteEvent.class, e->closeConstiEditor());
		
	}
	
	private Component getContent() {
		HorizontalLayout content=new HorizontalLayout(grid, form);
		content.setFlexGrow(2, grid);
		content.setFlexGrow(1, form);
		content.setSizeFull();
		return content;
	}
	public void updateGrid() {
		grid.setItems(service.getAllConstituenciesWIthNotInUse());
		
	}
	public void saveConstituency(ConstimpForm.SaveEvent event) {
		service.saveConstituencymp(event.getConstituencymp());
		updateGrid();
		closeConstiEditor();
	}

	public void deleteConstituency(ConstimpForm.DeleteEvent event) {
		service.deleteConstituencymp(event.getConstituencymp());
		updateGrid();
		closeConstiEditor();
	}

	private void addConsti() {
		grid.asSingleSelect().clear();
		editConsti(new Constituencymp());
	}

	private void editConsti(Constituencymp consti) {
		// TODO Auto-generated method stub
		form.setVisible(false);
		if (consti == null) {
			closeConstiEditor();
		} else {
			form.setConstituencymp(consti);
			form.setVisible(true);
			form.inUse.setValue(true);
				//constiform.setVisible(false);
		}
	}
	private void closeConstiEditor() {
		form.setConstituencymp(null);
		form.setVisible(false);
		// TODO Auto-generated method stub
		
	}

}
