package com.smis.view;

import com.smis.audit.Audit;
import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Scheme;
import com.smis.entity.Year;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@PageTitle("Master Data")
@Route(value="master", layout=MainLayout.class)
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
	Button addButton=new  Button("Constituency");
	Button addYear=new  Button("Financial Year");
	Button addScheme=new  Button("Scheme");
	Button addBlock=new  Button("Block");
	Audit audit;
	public MasterView(Dbservice services, Audit audit) {
		this.service=services;
		isSuperAdmin=services.isSuperAdmin();
		this.audit=audit;
		setSizeFull();
		configureGrids();
		configureForms();
		configureButtons();
		updateGrids();
		closeConstiEditor();
		closeYearEditor();
		closeSchemeEditor();
		closeBlockEditor();
		
		add(createTabs());
	}
	private Span createText() {
	    return new Span("Click New Button To Add new Item");
	}
	private Component createTabs() {
		TabSheet tabSheet = new TabSheet();
		tabSheet.add("Schemes",getSchemeTab());
		tabSheet.add("Blocks",getBlockTab());
		tabSheet.add("Constituency",getConstiTab());
		tabSheet.add("Year",getYearTab());
		tabSheet.setSizeFull();
		return tabSheet;
	}
	
	private Component getSchemeTab() {
		HorizontalLayout schemeLayout=new HorizontalLayout();
		schemeLayout.add(schemegrid, schemeform);
		schemeLayout.setSizeFull();
		return schemeLayout;
	}
	private Component getBlockTab() {
		HorizontalLayout blockLayout=new HorizontalLayout();
		blockLayout.add(blockgrid, blockform);
		blockLayout.setSizeFull();
		return blockLayout;
	}
	private Component getConstiTab() {
		HorizontalLayout constiLayout=new HorizontalLayout();
		constiLayout.add(constigrid, constiform);
		constiLayout.setSizeFull();
		return constiLayout;
	}
	private Component getYearTab() {
		HorizontalLayout yearLayout=new HorizontalLayout();
		yearLayout.add(yeargrid, yearform);
		yearLayout.setSizeFull();
		return yearLayout;
	}
	private void configureForms() {
		constiform=new ConstiForm(service);
		constiform.setWidth("20%");
		constiform.addListener(ConstiForm.SaveEvent.class, this::saveConstituency);
		constiform.addListener(ConstiForm.DeleteEvent.class, this::deleteConstituency);
		constiform.addListener(ConstiForm.DeleteEvent.class, e->closeConstiEditor());
		yearform=new YearForm(service);
		yearform.setWidth("20%");
		yearform.addListener(YearForm.SaveEvent.class, this::saveYear);
		yearform.addListener(YearForm.DeleteEvent.class, this::deleteYear);
		yearform.addListener(YearForm.DeleteEvent.class, e->closeYearEditor());
		schemeform=new SchemeForm(service);
		schemeform.setWidth("20%");
		schemeform.addListener(SchemeForm.SaveEvent.class, this::saveScheme);
		schemeform.addListener(SchemeForm.DeleteEvent.class, this::deleteScheme);
		schemeform.addListener(SchemeForm.DeleteEvent.class, e->closeSchemeEditor());
		blockform=new BlockForm(service);
		blockform.setWidth("20%");
		blockform.addListener(BlockForm.SaveEvent.class, this::saveBlock);
		blockform.addListener(BlockForm.DeleteEvent.class, this::deleteBlock);
		blockform.addListener(BlockForm.DeleteEvent.class, e->closeBlockEditor());
		
	}
	private void configureGrids() {
		constigrid.setSizeFull();
		blockgrid.setSizeFull();
		blockgrid.removeAllColumns();
		yeargrid.setSizeFull();
		schemegrid.setSizeFull();
		constigrid.setColumns("constituencyLabel", "constituencyMLA", "inUse");
		constigrid.addColumn(constituency->constituency.getMasterConstituency().getConstituencyName()).setHeader("Constituency").setSortable(true);
		constigrid.addColumn(constituency->constituency.getDistrict().getDistrictName()).setSortable(true).setVisible(isSuperAdmin);
		constigrid.addColumn(constituency->constituency.getDistrict().getState().getStateName()).setSortable(true).setVisible(isSuperAdmin);
		schemegrid.setColumns( "schemeDuration",  "schemeDept", "schemeLabel");
		schemegrid.addColumn(scheme->scheme.getSchemeReport()).setHeader("Report Type");
		schemegrid.addColumn(scheme->scheme.isInUse()).setHeader("In Use");
		schemegrid.addColumn(scheme->scheme.getDistrict().getDistrictName()).setHeader("District").setSortable(true).setVisible(isSuperAdmin);
		schemegrid.addColumn(scheme->scheme.getDistrict().getState().getStateName()).setHeader("State").setSortable(true).setVisible(isSuperAdmin);
		blockgrid.addColumn(block ->block.getMasterBlock().getBlockName()).setHeader("Block").setSortable(true);
		blockgrid.addColumn(block ->block.getBdoName()).setHeader("Office Head");
		blockgrid.addColumn(block ->block.getBlockLabel()).setHeader("Label").setSortable(true);
		blockgrid.addColumn(block ->block.isInUse()).setHeader("In Use");
		//blockgrid.addColumn(block ->block.getMasterBlock().getDistrict().getDistrictName()).setHeader("District").setSortable(true).setVisible(isSuperAdmin);
		//blockgrid.addColumn(block ->block.getDistrict().getState().getStateName()).setHeader("State").setSortable(true).setVisible(isSuperAdmin);
		yeargrid.setColumns("yearLabel", "inUse");
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
	private void configureButtons() {
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
		//return toolbar;
	}
	public void updateGrids() {
		constigrid.setItems(service.getAllConstituenciesWIthNotInUse());
		schemegrid.setItems(service.getAllSchemesWIthNotInUse());
		blockgrid.setItems(service.getAllBlocks(false));
		yeargrid.setItems(service.getAllYearsWIthNotInUse());
	}
	
	private void closeConstiEditor() {
		constiform.setConstituency(null);
		constiform.save.setEnabled(false);
		constiform.delete.setEnabled(false);
		//constiform.setVisible(false);

	}

	public void saveConstituency(ConstiForm.SaveEvent event) {
		Constituency consti=event.getConstituency();
		audit.saveLoginAudit("Save", "Save Constituency", consti.getConstituencyLabel(), consti.getConstituencyMLA());
		service.saveConstituency(consti);
		updateGrids();
		closeConstiEditor();
	}

	public void deleteConstituency(ConstiForm.DeleteEvent event) {
		Constituency consti=event.getConstituency();
		audit.saveLoginAudit("Delete", "Delete Constituency", consti.getConstituencyLabel(), consti.getConstituencyMLA());
		service.deleteConstituency(consti);
		updateGrids();
		closeConstiEditor();
	}

	private void addConsti() {
		constigrid.asSingleSelect().clear();
		editConsti(new Constituency());
	}

	private void editConsti(Constituency consti) {
		if (consti == null) {
			closeConstiEditor();
		} else {
			constiform.setConstituency(consti);
			constiform.inUse.setValue(consti.isInUse());
			constiform.save.setEnabled(true);
			constiform.delete.setEnabled(true);
		}
	}
	
	private void closeYearEditor() {
		yearform.setYear(null);
		yearform.save.setEnabled(false);
		yearform.delete.setEnabled(false);
	}

	public void saveYear(YearForm.SaveEvent event) {
		
		try {
			Year year=event.getYear();
			audit.saveLoginAudit("Save", "Save Year", year.getYearLabel(), "");
			service.saveYear(year);
			updateGrids();
			closeYearEditor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteYear(YearForm.DeleteEvent event) {
		Year year=event.getYear();
		audit.saveLoginAudit("Delete", "Delete Year", year.getId()+"-"+year.getYearLabel(), "");
		service.deleteYear(year);
		updateGrids();
		closeYearEditor();
	}

	private void addYear() {
		yeargrid.asSingleSelect().clear();
		editYear(new Year());
	}

	private void editYear(Year year) {
		if (year == null) {
			closeYearEditor();
		} else {
			yearform.setYear(year);
			yearform.inUse.setValue(year.isInUse());
			yearform.save.setEnabled(true);
			yearform.delete.setEnabled(true);
		}
	}
	private void closeSchemeEditor() {
		schemeform.setScheme(null);
		schemeform.save.setEnabled(false);
		schemeform.delete.setEnabled(false);
	}

	public void saveScheme(SchemeForm.SaveEvent event) {
		Scheme scheme=event.getScheme();
		audit.saveLoginAudit("Save", "Save Scheme", scheme.getSchemeLabel(), scheme.getSchemeNameLong());
		service.saveScheme(scheme);
		updateGrids();
		closeSchemeEditor();
	}

	public void deleteScheme(SchemeForm.DeleteEvent event) {
		Scheme scheme=event.getScheme();
		audit.saveLoginAudit("Delete", "Delete Scheme", scheme.getId()+"-"+scheme.getSchemeLabel(), scheme.getSchemeNameLong());
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
		if (year == null) {
			closeSchemeEditor();
		} else {
			schemeform.setScheme(year);
			schemeform.inUse.setValue(year.isInUse());
			schemeform.save.setEnabled(true);
			schemeform.delete.setEnabled(true);
			//schemeform.schemeprocessaccordion.add(schemeform.createSchemeProcessLayout(year));
		}
	}
	private void closeBlockEditor() {
		blockform.setBlock(null);
		blockform.save.setEnabled(false);
		blockform.delete.setEnabled(false);
		
	}

	public void saveBlock(BlockForm.SaveEvent event) {
		Block block=event.getBlock();
		audit.saveLoginAudit("Save", "Save Block", block.getId()+"-"+block.getBlockLabel(), block.getBlockLabel());
		service.saveBlock(block);
		updateGrids();
		closeBlockEditor();
	}

	public void deleteBlock(BlockForm.DeleteEvent event) {
		Block block=event.getBlock();
		audit.saveLoginAudit("Delete", "Delete Block", block.getId()+"-"+block.getBlockLabel(), block.getBlockLabel());
		service.deleteBlock(block);
		updateGrids();
		closeBlockEditor();
	}

	private void addBlock() {
		blockgrid.asSingleSelect().clear();
		editBlock(new Block());
	}

	private void editBlock(Block block) {
		// TODO Auto-generated method stub
		
		if (block == null) {
			closeBlockEditor();
		} else {
			blockform.setBlock(block);
			blockform.inUse.setValue(block.isInUse());
			blockform.save.setEnabled(true);
			blockform.delete.setEnabled(true);
		}
	}
}
  