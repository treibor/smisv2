package com.smis.view;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.smis.audit.Audit;
//import com.identity.views.CheckBox;
import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Installment;
import com.smis.entity.ProcessFlow;
import com.smis.entity.ProcessFlowUser;
import com.smis.entity.ProcessHistory;
import com.smis.entity.Scheme;
import com.smis.entity.Users;
import com.smis.entity.Work;
import com.smis.entity.Year;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import software.xdev.vaadin.grid_exporter.GridExporter;

@PageTitle("MLA Schemes")
@Route(value = "workhistory", layout = MainLayout.class)
@RolesAllowed({ "USER", "SUPER", "ADMIN" })
//@CssImport(value = "../components/vaadin-grid.css", themeFor = "vaadin-grid")
public class WorkViewHistory extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dbservice service;
	Grid<Work> grid = new Grid<>(Work.class);
	Grid<Work> gridhistory = new Grid<>(Work.class);
	TextField filterText = new TextField();
	ComboBox<Block> block = new ComboBox<Block>();
	ComboBox<Constituency> consti = new ComboBox<Constituency>();
	ComboBox<Year> year = new ComboBox<Year>();
	ComboBox<Scheme> scheme = new ComboBox<Scheme>();
	Button expButton = new Button("Export");
	// Checkbox displayFilter= new Checkbox("Show More Filters");
	WorkForm workform;
	boolean isAdmin;
	boolean isUser;
	@Autowired
	private Audit audit;

	public WorkViewHistory(Dbservice service) {
		this.service = service;
		setSizeFull();
		isAdmin = service.isAdmin();
		isUser = service.isUser();
		// displayFilter.addValueChangeListener(e-> displayFilters());
		
		configureGridHistory();
		add(getToolbar(), getContent());
		updateGrid();
		

	}

	public boolean checkAuthority(ProcessFlow pf) {
		Users user = service.getLoggedUser();
		ProcessFlowUser pfu = service.getProcessFlowUser(user, pf);
		if (pfu == null) {
			return false;
		} else {
			return true;
		}
	}

	private void configureCombos() {
		block.setItems(service.getAllBlocks());
		// block.setClearButtonVisible(true);
		consti.setItems(service.getAllConstituencies());
		scheme.setItems(service.getAllSchemes());
		year.setItems(service.getAllYears());
		block.setClearButtonVisible(true);
		year.setClearButtonVisible(true);
		scheme.setClearButtonVisible(true);
		consti.setClearButtonVisible(true);
		block.setItemLabelGenerator(Block::getBlockName);
		year.setItemLabelGenerator(Year::getYearName);
		scheme.setItemLabelGenerator(Scheme::getSchemeName);
		consti.setItemLabelGenerator(constituency -> constituency.getConstituencyNo() + "-"
				+ constituency.getConstituencyName() + "-" + constituency.getConstituencyMLA());
		block.setPlaceholder("Block");
		consti.setPlaceholder("Constituency");
		year.setPlaceholder("Year");
		scheme.setPlaceholder("Scheme");
		block.setWidthFull();
		scheme.setWidthFull();
		year.setWidthFull();
		consti.setWidthFull();
		block.addValueChangeListener(e -> filterGrid());
		consti.addValueChangeListener(e -> filterGrid());
		year.addValueChangeListener(e -> filterGrid());
		scheme.addValueChangeListener(e -> filterGrid());
	}

	
	private void configureGridHistory() {
		gridhistory.setSizeFull();
		gridhistory.setColumns("workCode");
		gridhistory.addColumn(work -> work.getWorkName()).setHeader("Name of The Work").setWidth("20%").setResizable(true)
				.setSortable(true);
		gridhistory.addColumn(work -> work.getWorkAmount()).setHeader("Sanc. Amount").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		gridhistory.addColumn(work -> work.getBlock().getBlockName()).setAutoWidth(true).setHeader("Block/MB")
				.setSortable(true).setResizable(true);
		gridhistory.addColumn(work -> work.getScheme().getSchemeName()).setAutoWidth(true).setHeader("Scheme")
				.setSortable(true).setResizable(true);
		gridhistory.addColumn(work -> work.getConstituency().getConstituencyNo() + "-"
				+ work.getConstituency().getConstituencyName() + "-" + work.getConstituency().getConstituencyMLA())
				.setWidth("20%").setHeader("Constituency").setSortable(true).setResizable(true);
		gridhistory.addColumn(work -> work.getYear().getYearName()).setAutoWidth(true).setHeader("Year").setSortable(true)
				.setResizable(true);
		gridhistory.addColumn(work -> work.getSanctionNo()).setHeader("Sanc. No").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		gridhistory.addColumn(
				work -> work.getSanctionDate() != null ? work.getSanctionDate().format(dateFormatter) : "No Date")
				.setHeader("Sanc. Date").setResizable(true).setSortable(true).setAutoWidth(true);
		gridhistory.addColumn(work -> work.getNoOfInstallments()).setHeader("Installments").setResizable(true)
				.setSortable(true).setAutoWidth(true);
		gridhistory.addColumn(work -> work.getWorkStatus()).setHeader("Status").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		gridhistory.addColumn(work -> work.getEnteredBy().getUserName()).setHeader("Updated By").setResizable(true).setSortable(true)
				.setAutoWidth(true);

		gridhistory.addColumn(work -> work.getEnteredOn() != null ? work.getEnteredOn().format(dateFormatter) : "No Date")
				.setHeader("Entered On").setResizable(true).setSortable(true).setAutoWidth(true);
		//gridhistory.asSingleSelect().addValueChangeListener(e -> editWork(e.getValue()));
		gridhistory.getHeaderRows().clear();
//		HeaderRow headerRow = gridhistory.appendHeaderRow();
//		headerRow.getCell(blockColumn).setComponent(block);
//		headerRow.getCell(constiColumn).setComponent(consti);
//		headerRow.getCell(schemeColumn).setComponent(scheme);
//		headerRow.getCell(yearColumn).setComponent(year);
		gridhistory.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		gridhistory.setClassNameGenerator(work -> {
			if (work.getWorkStatus().equals("Completed"))
				return "high-rating";
			if (work.getWorkStatus().equals("Entered"))
				return "low-rating";
			return null;
		});
		
		GridContextMenu<Work> contextMenu = new GridContextMenu<>(gridhistory);

		// Add a menu item for viewing installments
		contextMenu.addItem("View Installments", event -> {
			Optional<Work> selectedWork = event.getItem();
			selectedWork.ifPresent(work -> {
				// Show a dialog or a new component with installments
				showInstallmentsDialog(work);
			});
		});
		contextMenu.addItem("View History", event -> {
			Optional<Work> selectedWork = event.getItem();
			selectedWork.ifPresent(work -> {
				// Show a dialog or a new component with installments
				showHistoryDialog(work);
			});
		});


	}
	private void showInstallmentsDialog(Work work) { // Create a dialog
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle(work.getWorkCode() + "-" + work.getWorkName());
		Grid<Installment> installmentGrid = new Grid<>(Installment.class, false);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		installmentGrid.addColumn(Installment::getInstallmentNo).setHeader("Installment Number").setResizable(true);
		installmentGrid.addColumn(Installment::getInstallmentAmount).setHeader("Amount Released").setResizable(true);
		// installmentGrid.addColumn(Installment::getInstallmentDate).setHeader("Released
		// Date").setResizable(true);
		installmentGrid.addColumn(installment -> installment.getInstallmentDate() != null
				? installment.getInstallmentDate().format(dateFormatter)
				: "No Date").setHeader("Released Date").setResizable(true).setSortable(true).setAutoWidth(true);

		installmentGrid.addColumn(Installment::getInstallmentLetter).setHeader("Letter No.").setResizable(true);
		installmentGrid.addColumn(Installment::getUcLetter).setHeader("UC Letter No").setResizable(true);
		// installmentGrid.addColumn(Installment::getUcDate).setHeader("UC
		// Date").setResizable(true);
		installmentGrid.addColumn(
				installment -> installment.getUcDate() != null ? installment.getUcDate().format(dateFormatter) : "")
				.setHeader("UC. Date").setResizable(true).setSortable(true).setAutoWidth(true);
		installmentGrid.addColumn(Installment::getEnteredBy).setHeader("Entered By").setResizable(true);
		// installmentGrid.addColumn(Installment::getEnteredOn).setHeader("Entered
		// On").setResizable(true);
		installmentGrid.addColumn(
				installment -> installment.getEnteredOn() != null ? installment.getEnteredOn().format(dateFormatter)
						: "No Date")
				.setHeader("Entered On").setResizable(true).setSortable(true).setAutoWidth(true);

		List<Installment> installments = service.getInstallments(work);
		installmentGrid.setItems(installments);
		installmentGrid.setAllRowsVisible(true);
		Button closeButton = new Button("Close", e -> dialog.close());
		dialog.add(installmentGrid);
		dialog.getFooter().add(closeButton);
		dialog.open();
	}
	private void showHistoryDialog(Work work) { // Create a dialog
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle(work.getWorkCode() + "-" + work.getWorkName());
		Grid<ProcessHistory> grid = new Grid<>(ProcessHistory.class, false);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		grid.addColumn(processhistory->processhistory.getProcessFlow().getStepName()).setHeader("Process").setAutoWidth(true);
		grid.addColumn(processhistory->processhistory.getUser().getUserName()).setHeader("Performed By").setAutoWidth(true);
		grid.addColumn(processhistory -> processhistory.getEnteredOn() != null
				? processhistory.getEnteredOn().format(dateFormatter)
				: "No Date").setHeader("Action Taken On").setResizable(true).setSortable(true).setAutoWidth(true);

		List<ProcessHistory> history = service.getProcessHistory(work);
		grid.setItems(history);
		grid.setAllRowsVisible(true);
		Button closeButton = new Button("Close", e -> dialog.close());
		dialog.add(grid);
		dialog.getFooter().add(closeButton);
		dialog.open();
	}
	public void filterGrid() {

		//grid.setItems(service.getFilteredWorks(scheme.getValue(), consti.getValue(), block.getValue(), year.getValue()));
		grid.setItems(service.getFilteredWorksByUser(scheme.getValue(), consti.getValue(), block.getValue(), year.getValue()));
		gridhistory.setItems(service.getWorkHistory());
	}

	private Component getContent() {
		//var grids=new VerticalLayout(grid, gridhistory);
		//grids.setSizeFull();
		HorizontalLayout content = new HorizontalLayout(gridhistory);
		content.setFlexGrow(1, gridhistory);
		//content.setFlexGrow(1, workform);
		content.addClassName("content");
		content.setSizeFull();
		return content;
	}

	public void updateGrid() {
		//grid.setItems(service.getAllWorks());
		//grid.setItems(service.getAllWorks());
		grid.setItems(service.getWorksAssignedToUser());
		gridhistory.setItems(service.getWorkHistory());
	}

	private Component getToolbar() {
		filterText.setPlaceholder("Filter By Work Code, Name or Sanction Number");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateList());
		filterText.setWidth("10%");
		expButton.addClickListener(e -> GridExporter.newWithDefaults(grid).open());
		expButton.setIcon(new Icon(VaadinIcon.EXTERNAL_LINK));
		configureCombos();
		FormLayout toolbar = new FormLayout();
		toolbar.add(filterText, 2);
		toolbar.add(consti, 2);
		toolbar.add(block, 2);
		toolbar.add(scheme, 1);
		toolbar.add(year, 1);
		
		toolbar.add(expButton, 1);
		toolbar.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2), // 1 column by default
				new FormLayout.ResponsiveStep("600px", 4), // 2 columns for screens wider than 600px
				new FormLayout.ResponsiveStep("800px", 10) // 3 columns for screens wider than 800px
		);
		toolbar.setWidthFull();
		return toolbar;
	}

	

	



	

	

	private void updateList() {
		block.clear();
		scheme.clear();
		consti.clear();
		year.clear();
		/// grid.setItems(service.getFilteredWorks(filterText.getValue()));
		//grid.setItems(service.getFilteredWorkss(filterText.getValue()));
		grid.setItems(service.getFilteredWorksAndSearch(filterText.getValue()));
		// configureGrid();
	}

	
	
	
}
