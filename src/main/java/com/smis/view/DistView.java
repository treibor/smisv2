package com.smis.view;

import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.District;
import com.smis.entity.Scheme;
import com.smis.entity.Year;
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

@PageTitle("District Data")
@Route(value="districtmaster", layout=MainLayout.class)
@RolesAllowed({"SUPER"})

public class DistView extends VerticalLayout{
	Dbservice service;
	Grid<District> distgrid=new Grid<>(District.class);
	VerticalLayout doublegrid=new VerticalLayout();
	
	DistrictForm distform;
	boolean isSuperAdmin;
	public DistView(Dbservice services) {
		this.service=services;
		isSuperAdmin=services.isSuperAdmin();
		setSizeFull();
		configureGrids();
		configureForms();
		updateGrids();
		
		add(getToolbar(),getContent());
		
	}
	private void configureForms() {
		distform=new DistrictForm(service);
		distform.setWidth("40%");
		distform.addListener(DistrictForm.SaveEvent.class, this::saveDistrict);
		
		
	}
	private void configureGrids() {
		distgrid.setSizeFull();
		distgrid.setColumns( "state","districtCode", "districtName", "districtAddress", "districtHq","districtLabel","deputyCommissioner");
		//distgrid.addColumn(constituency->constituency.getDistrict().getDistrictName()).setSortable(true).setVisible(isSuperAdmin);
		//distgrid.addColumn(constituency->constituency.getDistrict().getState().getStateName()).setSortable(true).setVisible(isSuperAdmin);
		//constigrid.asSingleSelect().addValueChangeListener(e-> editConsti(e.getValue()));
		distgrid.asSingleSelect().addValueChangeListener(e->editDistrict(e.getValue()));
	}
	
	private Component getContent() {
		doublegrid.add(distgrid);
		doublegrid.setPadding(false);
		HorizontalLayout content=new HorizontalLayout(doublegrid, distform);
		content.setFlexGrow(1, doublegrid);
		content.setFlexGrow(1, distform);
		//content.setFlexGrow(1, constiform);
		
		content.setSizeFull();
		return content;
	}
	private Component getToolbar() {
		Button addButton=new  Button("New District");
		addButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));
		addButton.addClickListener(e->addDistrict());
		HorizontalLayout toolbar=new HorizontalLayout(addButton);
		toolbar.setWidthFull();
		return toolbar;
	}
	public void updateGrids() {
		
		distgrid.setItems(service.getAllDistrictsOfAllStates());
	}
	private void addDistrict() {
		distgrid.asSingleSelect().clear();
		editDistrict(new District());
	}
	private void editDistrict(District dist) {
		// TODO Auto-generated method stub
		distform.setVisible(false);
		if (dist == null) {
			//closeConstiEditor();
		} else {
			distform.setDistrict(dist);
			distform.setVisible(true);
			//yearform.setYear(year);
			
		}
	}

	public void saveDistrict(DistrictForm.SaveEvent event) {
		service.saveDistrict(event.getDistrict());
		updateGrids();
		//closeConstiEditor();
	}

	
}
  