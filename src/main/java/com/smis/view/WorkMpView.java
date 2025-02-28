package com.smis.view;


import java.math.BigDecimal;
import java.util.List;

import com.smis.dbservice.DbserviceMp;
import com.smis.entity.Constituencymp;
import com.smis.entity.Impldistrict;
import com.smis.entity.Installmentmp;
import com.smis.entity.Workmp;
import com.smis.entity.Year;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
@PageTitle("MP Schemes")
@Route(value="mpschemes", layout=MainLayout.class)
@RolesAllowed({"USER","SUPER", "ADMIN"})
public class WorkMpView extends VerticalLayout{
	DbserviceMp service;
	Grid <Workmp> grid=new Grid<>(Workmp.class, false);
	TextField filterText=new TextField();
	WorkFormMp workform;
	boolean isAdmin;
	boolean isUser;
	ComboBox<Year> year=new ComboBox("");
	ComboBox<Constituencymp> constituencymp=new ComboBox();
	//ComboBox<Block> block=new ComboBox();
	ComboBox<Impldistrict> implDistrict=new ComboBox();
	public WorkMpView(DbserviceMp service) {
		this.service=service;
		setSizeFull();
		isAdmin=service.isAdmin();
		isUser=service.isUser();
		configureGrid();
		configureForm();
		add(getToolbar(), getContent());
		updateGrid();
		closeEditor();
	}
	
	
	private void configureGrid() {
		constituencymp.setItems(service.getAllConstituencies());
		implDistrict.setItems(service.getAllImplDistricts());
		year.setItems(service.getAllYears());
		constituencymp.setWidthFull();
		implDistrict.setWidthFull();
		year.setWidthFull();
		year.setClearButtonVisible(true);
		implDistrict.setClearButtonVisible(true);
		constituencymp.setClearButtonVisible(true);
		year.setItemLabelGenerator(Year:: getYearName);
		implDistrict.setItemLabelGenerator(Impldistrict::getDistrictName);
		constituencymp.setItemLabelGenerator(constituencymp-> constituencymp.getConstituencyType()+"-"+constituencymp.getConstituencyMp());
		//block.addValueChangeListener(e-> filterGrid());
		constituencymp.addValueChangeListener(e-> filterGrid());
		year.addValueChangeListener(e-> filterGrid());
		implDistrict.addValueChangeListener(e-> filterGrid());
		grid.setSizeFull();
		grid.setColumns("workCode");
		grid.addColumn(work->work.getPriority()).setHeader("Priority").setSortable(true);
		grid.addColumn(work ->work.getWorkName()).setHeader("Name of The Work").setAutoWidth(false).setResizable(true).setWidth("20%").setSortable(true);
		Grid.Column<Workmp> implDistrictColumn = grid.addColumn(work-> work.getImplDistrict().getDistrictName()).setAutoWidth(true).setHeader("Impl District").setSortable(true).setResizable(true);
		Grid.Column<Workmp> yearColumn = grid.addColumn(work-> work.getYear().getYearName()).setAutoWidth(true).setHeader("Year").setSortable(true).setResizable(true);
		Grid.Column<Workmp> constiColumn = grid.addColumn(work -> work.getConstituencymp().getConstituencyType()+"-"+work.getConstituencymp().getConstituencyMp()).setHeader("Parliamentary Constituency").setResizable(true).setWidth("20%").setSortable(true);
		Grid.Column<Workmp> implAgencyColumn=grid.addColumn(work -> work.getImplAgency()).setHeader("Impl. Agency").setAutoWidth(true).setSortable(true);
		grid.addColumn(work-> work.getNoOfInstallments()).setHeader("Installments").setResizable(true).setAutoWidth(true).setSortable(true);
		grid.addColumn(work->work.getWorkAmount()).setHeader("Recom. Amount").setResizable(true).setAutoWidth(true).setSortable(true);
		grid.addColumn(work->work.getSanctionNo()).setHeader("Recom. No").setResizable(true).setAutoWidth(true).setSortable(true);
		grid.addColumn(work->work.getSanctionDate()).setHeader("Recom. Date").setResizable(true).setAutoWidth(true).setSortable(true);
		grid.addColumn(work -> work.getWorkStatus()).setHeader("Status").setAutoWidth(true).setResizable(true).setSortable(true);
		grid.addColumn(work -> work.getEnteredBy()).setHeader("Entered By").setAutoWidth(true).setResizable(true).setSortable(true);
		grid.addColumn(work -> work.getEnteredOn()).setHeader("Entered On").setAutoWidth(true).setResizable(true).setSortable(true);
		grid.asSingleSelect().addValueChangeListener(e-> editWork(e.getValue()));
		grid.getHeaderRows().clear();
		HeaderRow headerRow = grid.appendHeaderRow();
		headerRow.getCell(implDistrictColumn).setComponent(implDistrict);
		headerRow.getCell(constiColumn).setComponent(constituencymp);
		//headerRow.getCell(implAgencyColumn).setComponent(block);
		headerRow.getCell(yearColumn).setComponent(year);
	}
	public void filterGrid() {
		
		grid.setItems(service.getFilteredWorks(implDistrict.getValue(), year.getValue(), constituencymp.getValue()));
	}
	
	
	
	private Component getContent() {
		HorizontalLayout content=new HorizontalLayout(grid, workform);
		content.setFlexGrow(1, grid);
		content.setFlexGrow(1, workform);
		content.addClassName("content");
		content.setSizeFull();
		return content;
	}
	public void updateGrid() {
		grid.setItems(service.getAllWorks());
		
	}
	private Component getToolbar() {
		filterText.setPlaceholder("Filter By Work Name or Sanction Number");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e-> updateList());
		filterText.setWidth("30%");
		Button addButton=new  Button("New Work");
		addButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));
		addButton.addClickListener(e-> addWork());
		HorizontalLayout toolbar=new HorizontalLayout(filterText, addButton);
		toolbar.setWidthFull();
		return toolbar;
	}
	public void configureForm() {
		workform=new WorkFormMp(service);
		workform.setWidth("45%");
		workform.addListener(WorkFormMp.SaveEvent.class, this::saveWork);
		workform.addListener(WorkFormMp.DeleteEvent.class, this::deleteWork);
		workform.addListener(WorkFormMp.CloseEvent.class, e->closeEditor());
	}

	public void saveWork(WorkFormMp.SaveEvent event) {
		long a = event.getWork().getWorkCode();
		service.saveWork(event.getWork());
		
		long b = service.getWorkCode();
		if (a == b) {
			addWorkAgain(event.getWork());
		} else {
			closeEditor();
		}
		updateList();
	}
	private void addWorkAgain(Workmp work) {
		//workform.save.setEnabled(true);
		grid.asSingleSelect().clear();
		workform.installaccordion.setEnabled(false);
		workform.ucaccordion.setEnabled(false);
		
		Workmp newWork=new Workmp();
		//newWork.setImplDistrict(work.getImplDistrict());
		
		newWork.setConstituencymp(work.getConstituencymp());
		newWork.setSanctionDate(work.getSanctionDate());
		newWork.setPriority(work.getPriority());
		newWork.setYear(work.getYear());
		//newWork.setWorkAmount(work.getWorkAmount());
		newWork.setSanctionNo(work.getSanctionNo());
		newWork.setNoOfInstallments(work.getNoOfInstallments());
		newWork.setImplDistrict(work.getImplDistrict());
		//workform.implDistrict.setValue(work.getImplDistrict());
		
		editWork(newWork);
		
	}
	
	public void deleteWork(WorkFormMp.DeleteEvent event) {
		//service.deleteInstallments(event.getWork());
		service.deleteWork(event.getWork());
		updateList();
		closeEditor();
	}
	private void updateList() {
		if (filterText.getValue().length() > 0) {
			constituencymp.clear();
			year.clear();
			implDistrict.clear();
			constituencymp.setEnabled(false);
			year.setEnabled(false);
			implDistrict.setEnabled(false);
		}else {
			constituencymp.setEnabled(true);
			year.setEnabled(true);
			implDistrict.setEnabled(true);
		}
		grid.setItems(service.getFilteredWorks(filterText.getValue()));
		//configureGrid();
	}
	private void closeEditor() {
		workform.setWork(null);
		workform.setVisible(false);
		
	}
	private void addWork() {
		//workform.save.setEnabled(true);
		
		grid.asSingleSelect().clear();
		workform.installaccordion.setEnabled(false);
		workform.ucaccordion.setEnabled(false);
		
		editWork(new Workmp());
		
	}
	
	private void editWork(Workmp work) {
		int workinstallment=0;
		workform.noOfInstallments.setEnabled(false);
		if (work == null) {
			closeEditor();
		} else {
			workform.setWork(work);
			workform.setVisible(true);
			workform.save.setEnabled(isUser);
			enableFields();
			workinstallment = work.getNoOfInstallments();
			
			if (work.getWorkAmount()!=null) {
				
				//check if work is entered or not by checking if installment is greater than 0
					int tablecount = service.getInstallmentMpCount(work);
					int toEnter = tablecount + 1;
					
					if (tablecount > 0) {
						
						// check if any installment is entered
						List<Installmentmp> installments = service.getInstallmentMps(work);
						workform.delete.setEnabled(isAdmin);
						disableFields();
						//workform.setEnabled(isAdmin);
						int tablecountindex = tablecount - 1;
						if (workinstallment == tablecount) {
							// check if all installments are entered, (if yes check if uc is enetered
							if (installments.get(tablecountindex).getInstallmentLetter() == null) {
								closeAllAccordion();
								//workform.ucmaster.setText("UC: " + tablecount);
							}else if (installments.get(tablecountindex).getUcLetter() == null) {
								openUcAccordion();
								workform.ucmaster.setText("UC: " + tablecount);
							} else {
								// Work is completed
								closeAllAccordion();
								workform.save.setEnabled(false);
							}
						} else {
							// Not All Installments are entered
							if (installments.get(tablecountindex).getInstallmentLetter() == null) {
								//check if release order is not printed 
								closeAllAccordion();
							}else if (installments.get(tablecountindex).getUcLetter() == null) {
									openUcAccordion();
									workform.ucmaster.setText("UC: " + tablecount);
								
							} else {
								openInstallAccordion();
								workform.installmentAmount.setValue(work.getWorkAmount().subtract( installments.get(tablecountindex).getInstallmentAmount()));
								workform.installmentmaster.setText("Installment: " + toEnter);
							}
						}
					} else {
						// "No Installments In the table"-Enter New Installment
						workform.delete.setEnabled(true);
						openInstallAccordion();
						workform.installmentAmount.setValue(work.getWorkAmount().divide(new BigDecimal(work.getNoOfInstallments())));
						workform.installmentmaster.setText("Installment: " + toEnter);
					}
				
			}else {
				
				closeAllAccordion();
				//workform.delete.setEnabled(isA);
				enableFields();
				
			}
		}
	}
	public void closeAllAccordion() {
		workform.workaccordion.setOpened(true);
		workform.workaccordion.setEnabled(true);
		workform.installaccordion.setEnabled(false);
		workform.installaccordion.setOpened(false);
		workform.ucaccordion.setEnabled(false);
		workform.ucaccordion.setOpened(false);
	}
	public void openInstallAccordion() {
		workform.workaccordion.setOpened(false);
		workform.implaccordion.setOpened(false);
		workform.installaccordion.setEnabled(true);
		workform.installaccordion.setOpened(true);
		workform.ucaccordion.setEnabled(false);
		workform.ucaccordion.setOpened(false);
		workform.workaccordion.setOpened(false);
	}
	public void openUcAccordion() {
		workform.workaccordion.setOpened(false);
		workform.implaccordion.setOpened(false);
		workform.installaccordion.setEnabled(false);
		workform.installaccordion.setOpened(false);
		workform.ucaccordion.setEnabled(true);
		workform.ucaccordion.setOpened(true);
		workform.workaccordion.setOpened(false);
	}
	
	public void enableFields() {
		//workform.scheme.setEnabled(true);
		workform.constituencymp.setEnabled(true);
		workform.block.setEnabled(true);
		workform.year.setEnabled(true);
		workform.workAmount.setEnabled(true);
		workform.implAgency.setEnabled(true);
		workform.implAddress.setEnabled(true);
		workform.implHead.setEnabled(true);
		workform.implDistrict.setEnabled(true);
		workform.noOfInstallments.setEnabled(true);
	}
	public void disableFields() {
		//workform.scheme.setEnabled(false);
		workform.constituencymp.setEnabled(false);
		workform.block.setEnabled(false);
		workform.year.setEnabled(false);
		workform.workAmount.setEnabled(false);
		workform.noOfInstallments.setEnabled(false);
		workform.implDistrict.setEnabled(false);
		workform.implAgency.setEnabled(false);
		workform.implAddress.setEnabled(false);
		workform.implHead.setEnabled(false);
	}
}
