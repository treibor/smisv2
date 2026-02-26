package com.smis.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.smis.dbservice.AuditService;
import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Scheme;
import com.smis.entity.Users;
import com.smis.entity.Year;
import com.smis.util.NotificationUtil;
import com.smis.util.StatusBadgeUtil;
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
	Users loggeduser;
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	public MasterView(Dbservice services) {
		this.service=services;
		isSuperAdmin=services.isSuperAdmin();
		this.loggeduser=service.getLoggedUser();
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
		yeargrid.setSizeFull();
		schemegrid.setSizeFull();
		
		constigrid.removeAllColumns();
		constigrid.addColumn(constituency->constituency.getDistrict().getDistrictName()).setSortable(true).setVisible(isSuperAdmin);
		constigrid.addColumn(constituency->constituency.getDistrict().getState().getStateName()).setSortable(true).setVisible(isSuperAdmin);
		constigrid.addColumn(constituency->constituency.getMasterConstituency().getConstituencyNo()+"-"+constituency.getMasterConstituency().getConstituencyName()).setHeader("Constituency").setSortable(true);
		constigrid.addColumn(constituency->constituency.getConstituencyLabel()).setHeader("Constituency Label").setSortable(true);
		constigrid.addColumn(constituency->constituency.getConstituencyMLA()).setHeader("MLA").setSortable(true);
		constigrid.addComponentColumn(constituency -> StatusBadgeUtil.yesNo(constituency.isInUse())).setHeader("In Use") .setAutoWidth(true).setComparator(constituency -> constituency.isInUse());
		constigrid.addColumn(constituency->constituency.getUpdatedBy()!=null ? constituency.getUpdatedBy().getProfileName():"").setHeader("Updated By").setSortable(true);
		constigrid.addColumn(constituency->constituency.getUpdatedOn()!=null ? constituency.getUpdatedOn().format(timeFormatter):"").setHeader("Updated On").setSortable(true);
		//scheme Grid
		schemegrid.removeAllColumns();
		schemegrid.addColumn(scheme->scheme.getDistrict().getState().getStateName()).setHeader("State").setSortable(true).setVisible(isSuperAdmin);
		schemegrid.addColumn(scheme->scheme.getDistrict().getDistrictName()).setHeader("District").setSortable(true).setVisible(isSuperAdmin);
		//schemegrid.setColumns( "schemeDuration",  "schemeDept", "schemeLabel");
		schemegrid.addColumn(scheme->scheme.getMasterScheme().getSchemeName()).setHeader("Scheme");
		schemegrid.addColumn(scheme->scheme.getSchemeLabel()).setHeader("Scheme Label");
		schemegrid.addColumn(scheme->scheme.getSchemeDuration()).setHeader("Duration");
		schemegrid.addColumn(scheme->scheme.getSchemeReport()).setHeader("Report Type");
		schemegrid.addColumn(scheme->scheme.getSchemeDept()).setHeader("Department");
		schemegrid.addComponentColumn(scheme -> StatusBadgeUtil.yesNo(scheme.isInUse())).setHeader("In Use") .setAutoWidth(true).setComparator(scheme -> scheme.isInUse());
		schemegrid.addColumn(scheme->scheme.getUpdatedBy() !=null ? scheme.getUpdatedBy().getProfileName():"").setHeader("Updated By");
		schemegrid.addColumn(scheme->scheme.getUpdatedOn()!=null ? scheme.getUpdatedOn().format(timeFormatter):"").setHeader("Updated On");
		
		blockgrid.removeAllColumns();
		blockgrid.addColumn(block ->block.getMasterBlock().getDistrict().getDistrictName()).setHeader("District").setSortable(true).setVisible(isSuperAdmin);
		blockgrid.addColumn(block ->block.getDistrict().getState().getStateName()).setHeader("State").setSortable(true).setVisible(isSuperAdmin);
		blockgrid.addColumn(block ->block.getMasterBlock().getBlockName()).setHeader("Block").setSortable(true);
		blockgrid.addColumn(block ->block.getBlockLabel()).setHeader("Block Label").setSortable(true);
		blockgrid.addColumn(block ->block.getBdoName()).setHeader("Office Head");
		//blockgrid.addColumn(block ->block.isInUse()).setHeader("In Use");
		blockgrid.addComponentColumn(block -> StatusBadgeUtil.yesNo(block.isInUse())).setHeader("In Use") .setAutoWidth(true).setComparator(block -> block.isInUse());
		blockgrid.addColumn(block ->block.getUpdatedBy()!=null ? block.getUpdatedBy().getProfileName():"").setHeader("Updated By");
		blockgrid.addColumn(block ->block.getUpdatedOn() !=null ? block.getUpdatedOn().format(timeFormatter):"").setHeader("Updated On");
		
		yeargrid.removeAllColumns();
		yeargrid.addColumn( year -> year.getDistrict().getState().getStateName()).setHeader("State").setSortable(true).setVisible(isSuperAdmin);
		yeargrid.addColumn( year -> year.getDistrict().getDistrictName()).setHeader("District").setSortable(true).setVisible(isSuperAdmin);
		yeargrid.addColumn( year -> year.getMasterYear().getYearName()).setHeader("Year").setSortable(true);
		yeargrid.addColumn( year -> year.getYearLabel()).setHeader("Year Label").setSortable(true);
		yeargrid.addComponentColumn(year -> StatusBadgeUtil.yesNo(year.isInUse())).setHeader("In Use") .setAutoWidth(true).setComparator(year -> year.isInUse());
		yeargrid.addColumn( year -> year.getUpdatedBy()!=null ? year.getUpdatedBy().getProfileName():"").setHeader("Updated By").setSortable(true);
		yeargrid.addColumn( year -> year.getUpdatedOn()!=null ? year.getUpdatedOn().format(timeFormatter):"").setHeader("Updated On").setSortable(true);
		
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
		constigrid.setItems(service.getAllConstituencies());
		schemegrid.setItems(service.getAllSchemes());
		blockgrid.setItems(service.getAllBlocks());
		yeargrid.setItems(service.getAllYearsForAdmin());
	}
	
	private void closeConstiEditor() {
		constiform.setConstituency(null);
		constiform.save.setEnabled(false);
		constiform.delete.setEnabled(false);
		//constiform.setVisible(false);

	}

	public void saveConstituency(ConstiForm.SaveEvent event) {
		Constituency consti=event.getConstituency();
		consti.setUpdatedOn(LocalDateTime.now());
		consti.setUpdatedBy(loggeduser);
		service.saveConstituency(consti);
		NotificationUtil.showSuccess("Constituency Updated Successfully");
		updateGrids();
		closeConstiEditor();
	}

	public void deleteConstituency(ConstiForm.DeleteEvent event) {
		Constituency consti=event.getConstituency();
		service.deleteConstituency(consti);
		NotificationUtil.showSuccess("Constituency Deleted Successfully");
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
			year.setUpdatedOn(LocalDateTime.now());
			year.setUpdatedBy(loggeduser);
			service.saveYear(year);
			NotificationUtil.showSuccess("Year Updated Successfully");
			updateGrids();
			closeYearEditor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteYear(YearForm.DeleteEvent event) {
		Year year=event.getYear();
		service.deleteYear(year);
		NotificationUtil.showSuccess("Constituency Deleted Successfully");
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
		scheme.setUpdatedOn(LocalDateTime.now());
		scheme.setUpdatedBy(loggeduser);
		service.saveScheme(scheme);
		NotificationUtil.showSuccess("Scheme Updated Successfully");
		updateGrids();
		closeSchemeEditor();
	}

	public void deleteScheme(SchemeForm.DeleteEvent event) {
		
		//Scheme scheme=event.getScheme();
		service.deleteScheme(event.getScheme());
		NotificationUtil.showSuccess("Scheme Deleted Successfully");
		updateGrids();
		closeSchemeEditor();
	}

	private void addScheme() {
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
		block.setUpdatedOn(LocalDateTime.now());
		block.setUpdatedBy(loggeduser);
		service.saveBlock(block);
		NotificationUtil.showSuccess("Block Updated Successfully");
		updateGrids();
		closeBlockEditor();
	}

	public void deleteBlock(BlockForm.DeleteEvent event) {
		Block block=event.getBlock();
		service.deleteBlock(block);
		updateGrids();
		NotificationUtil.showSuccess("Block Deleted Successfully");
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
  