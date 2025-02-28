package com.smis.view;

import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.Constituency;
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

@PageTitle("Master Data")
@Route(value="mlamaster", layout=MainLayout.class)
@RolesAllowed({"ADMIN","SUPER"})

public class MasterView extends VerticalLayout{
	Dbservice service;
	Grid<Scheme> schemegrid=new Grid<>(Scheme.class);
	Grid<Constituency> constigrid=new Grid<>(Constituency.class);
	Grid<Block> blockgrid =new Grid<>(Block.class);
	Grid<Year> yeargrid= new Grid<>(Year.class);
	VerticalLayout doublegrid=new VerticalLayout();
	VerticalLayout doublegrid1=new VerticalLayout();
	ConstiForm constiform;
	YearForm yearform;
	SchemeForm schemeform;
	BlockForm blockform;
	boolean isSuperAdmin;
	public MasterView(Dbservice services) {
		this.service=services;
		isSuperAdmin=services.isSuperAdmin();
		setSizeFull();
		configureGrids();
		configureForms();
		updateGrids();
		closeConstiEditor();
		closeYearEditor();
		closeSchemeEditor();
		closeBlockEditor();
		add(getToolbar(),getContent());
		
	}
	private void configureForms() {
		constiform=new ConstiForm(service);
		constiform.setWidth("40%");
		constiform.addListener(ConstiForm.SaveEvent.class, this::saveConstituency);
		constiform.addListener(ConstiForm.DeleteEvent.class, this::deleteConstituency);
		constiform.addListener(ConstiForm.DeleteEvent.class, e->closeConstiEditor());
		yearform=new YearForm(service);
		yearform.setWidth("40%");
		yearform.addListener(YearForm.SaveEvent.class, this::saveYear);
		yearform.addListener(YearForm.DeleteEvent.class, this::deleteYear);
		yearform.addListener(YearForm.DeleteEvent.class, e->closeYearEditor());
		schemeform=new SchemeForm(service);
		schemeform.setWidth("40%");
		schemeform.addListener(SchemeForm.SaveEvent.class, this::saveScheme);
		schemeform.addListener(SchemeForm.DeleteEvent.class, this::deleteScheme);
		schemeform.addListener(SchemeForm.DeleteEvent.class, e->closeSchemeEditor());
		blockform=new BlockForm(service);
		blockform.setWidth("40%");
		blockform.addListener(BlockForm.SaveEvent.class, this::saveBlock);
		blockform.addListener(BlockForm.DeleteEvent.class, this::deleteBlock);
		blockform.addListener(BlockForm.DeleteEvent.class, e->closeBlockEditor());
	}
	private void configureGrids() {
		constigrid.setSizeFull();
		blockgrid.setSizeFull();
		yeargrid.setSizeFull();
		schemegrid.setSizeFull();
		constigrid.setColumns("constituencyNo", "constituencyName", "constituencyMLA", "inUse");
		constigrid.addColumn(constituency->constituency.getDistrict().getDistrictName()).setSortable(true).setVisible(isSuperAdmin);
		constigrid.addColumn(constituency->constituency.getDistrict().getState().getStateName()).setSortable(true).setVisible(isSuperAdmin);
		schemegrid.setColumns("schemeName", "schemeDuration",  "schemeDept", "schemeLabel");
		schemegrid.addColumn(scheme->scheme.getSchemeReport()).setHeader("Report Type");
		schemegrid.addColumn(scheme->scheme.isInUse()).setHeader("In Use");
		schemegrid.addColumn(scheme->scheme.getDistrict().getDistrictName()).setHeader("District").setSortable(true).setVisible(isSuperAdmin);
		schemegrid.addColumn(scheme->scheme.getDistrict().getState().getStateName()).setHeader("State").setSortable(true).setVisible(isSuperAdmin);
		blockgrid.setColumns("blockName");
		blockgrid.addColumn(block ->block.getBlockDevelopmentOfficer()).setHeader("Office Head");
		blockgrid.addColumn(block ->block.getBlockLabel()).setHeader("Label");
		blockgrid.addColumn(block ->block.isInUse()).setHeader("In Use");
		blockgrid.addColumn(block ->block.getDistrict().getDistrictName()).setHeader("District").setSortable(true).setVisible(isSuperAdmin);
		blockgrid.addColumn(block ->block.getDistrict().getState().getStateName()).setHeader("State").setSortable(true).setVisible(isSuperAdmin);
		yeargrid.setColumns("yearName", "yearLabel", "inUse");
		yeargrid.addColumn( year -> year.getDistrict().getDistrictName()).setHeader("District").setSortable(true).setVisible(isSuperAdmin);
		yeargrid.addColumn( year -> year.getDistrict().getState().getStateName()).setHeader("State").setSortable(true).setVisible(isSuperAdmin);
		constigrid.getColumns().forEach(col-> col.setAutoWidth(true));
		schemegrid.getColumns().forEach(col-> col.setAutoWidth(true));
		blockgrid.getColumns().forEach(col-> col.setAutoWidth(true));
		yeargrid.getColumns().forEach(col-> col.setAutoWidth(true));
		constigrid.asSingleSelect().addValueChangeListener(e-> editConsti(e.getValue()));
		yeargrid.asSingleSelect().addValueChangeListener(e-> editYear(e.getValue()));
		schemegrid.asSingleSelect().addValueChangeListener(e-> editScheme(e.getValue()));
		blockgrid.asSingleSelect().addValueChangeListener(e-> editBlock(e.getValue()));
	}
	
	private Component getContent() {
		doublegrid.add(blockgrid, yeargrid);
		doublegrid1.add(constigrid, schemegrid);
		doublegrid.setPadding(false);
		doublegrid1.setPadding(false);
		HorizontalLayout content=new HorizontalLayout(doublegrid1, doublegrid, constiform, yearform, schemeform, blockform);
		content.setFlexGrow(1, doublegrid);
		content.setFlexGrow(1, doublegrid1);
		content.setFlexGrow(1, constiform);
		
		content.setSizeFull();
		return content;
	}
	private Component getToolbar() {
		Button addButton=new  Button("Constituency");
		Button addYear=new  Button("Financial Year");
		Button addScheme=new  Button("Scheme");
		Button addBlock=new  Button("Block");
		addButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));
		addButton.addClickListener(e-> addConsti());
		addYear.addClickListener(e-> addYear());
		addYear.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));
		addScheme.addClickListener(e-> addScheme());
		addScheme.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));
		addBlock.addClickListener(e-> addBlock());
		addBlock.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));
		HorizontalLayout toolbar=new HorizontalLayout(addButton,addScheme, addBlock, addYear);
		toolbar.setWidthFull();
		return toolbar;
	}
	public void updateGrids() {
		constigrid.setItems(service.getAllConstituenciesWIthNotInUse());
		schemegrid.setItems(service.getAllSchemesWIthNotInUse());
		blockgrid.setItems(service.getAllBlocksWithNotInUse());
		yeargrid.setItems(service.getAllYearsWIthNotInUse());
	}
	
	private void closeConstiEditor() {
		constiform.setConstituency(null);
		constiform.setVisible(false);

	}

	public void saveConstituency(ConstiForm.SaveEvent event) {
		service.saveConstituency(event.getConstituency());
		updateGrids();
		closeConstiEditor();
	}

	public void deleteConstituency(ConstiForm.DeleteEvent event) {
		service.deleteConstituency(event.getConstituency());
		updateGrids();
		closeConstiEditor();
	}

	private void addConsti() {
		constigrid.asSingleSelect().clear();
		editConsti(new Constituency());
	}

	private void editConsti(Constituency consti) {
		// TODO Auto-generated method stub
		constiform.setVisible(false);
		if (consti == null) {
			closeConstiEditor();
		} else {
			constiform.setConstituency(consti);
			constiform.setVisible(true);
			//yearform.setYear(year);
			yearform.setVisible(false);
			schemeform.setVisible(false);
			blockform.setVisible(false);
			constiform.inUse.setValue(true);
		}
	}
	
	private void closeYearEditor() {
		yearform.setYear(null);
		yearform.setVisible(false);

	}

	public void saveYear(YearForm.SaveEvent event) {
		service.saveYear(event.getYear());
		updateGrids();
		closeYearEditor();
	}

	public void deleteYear(YearForm.DeleteEvent event) {
		service.deleteYear(event.getYear());
		updateGrids();
		closeYearEditor();
	}

	private void addYear() {
		
		yeargrid.asSingleSelect().clear();
		editYear(new Year());
	}

	private void editYear(Year year) {
		// TODO Auto-generated method stub
		yearform.setVisible(false);
		if (year == null) {
			closeYearEditor();
		} else {
			yearform.setYear(year);
			yearform.setVisible(true);
			schemeform.setVisible(false);
			blockform.setVisible(false);
			constiform.setVisible(false);
			yearform.inUse.setValue(true);
		}
	}
	private void closeSchemeEditor() {
		schemeform.setScheme(null);
		schemeform.setVisible(false);

	}

	public void saveScheme(SchemeForm.SaveEvent event) {
		service.saveScheme(event.getScheme());
		updateGrids();
		closeSchemeEditor();
	}

	public void deleteScheme(SchemeForm.DeleteEvent event) {
		service.deleteScheme(event.getScheme());
		updateGrids();
		closeSchemeEditor();
	}

	private void addScheme() {
		schemegrid.asSingleSelect().clear();
		editScheme(new Scheme());
	}

	private void editScheme(Scheme year) {
		// TODO Auto-generated method stub
		schemeform.setVisible(false);
		if (year == null) {
			closeSchemeEditor();
		} else {
			schemeform.setScheme(year);
			schemeform.setVisible(true);
			yearform.setVisible(false);
			blockform.setVisible(false);
			constiform.setVisible(false);
			schemeform.inUse.setValue(true);
			//schemeform.schemeprocessaccordion.add(schemeform.createSchemeProcessLayout(year));
		}
	}
	private void closeBlockEditor() {
		blockform.setBlock(null);
		blockform.setVisible(false);

	}

	public void saveBlock(BlockForm.SaveEvent event) {
		service.saveBlock(event.getBlock());
		updateGrids();
		closeBlockEditor();
	}

	public void deleteBlock(BlockForm.DeleteEvent event) {
		service.deleteBlock(event.getBlock());
		updateGrids();
		closeBlockEditor();
	}

	private void addBlock() {
		blockgrid.asSingleSelect().clear();
		editBlock(new Block());
	}

	private void editBlock(Block block) {
		// TODO Auto-generated method stub
		constiform.setVisible(false);
		if (block == null) {
			closeBlockEditor();
		} else {
			blockform.setBlock(block);
			blockform.setVisible(true);
			yearform.setVisible(false);
			schemeform.setVisible(false);
			constiform.setVisible(false);
			blockform.inUse.setValue(true);
		}
	}
}
  