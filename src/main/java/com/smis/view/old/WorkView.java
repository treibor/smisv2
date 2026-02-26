package com.smis.view.old;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

//import com.identity.views.CheckBox;
import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Installment;
import com.smis.entity.Scheme;
import com.smis.entity.Work;
import com.smis.entity.Year;
import com.smis.view.MainLayout;
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
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import software.xdev.vaadin.grid_exporter.GridExporter;

@PageTitle("MLA Schemes")
@Route(value = "mlaschemes", layout = MainLayout.class)
@RolesAllowed({ "USER", "SUPER", "ADMIN" })
//@CssImport(value = "../components/vaadin-grid.css", themeFor = "vaadin-grid")
public class WorkView extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataProvider<Work, Void> workDp;
	Dbservice service;
	Grid<Work> grid = new Grid<>(Work.class);
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
	//private Audit audit;
	public WorkView(Dbservice service) {
		this.service = service;
		setSizeFull();
		isAdmin = service.isAdmins();
		isUser = service.isUser();
		// displayFilter.addValueChangeListener(e-> displayFilters());
		configureGrid();
		configureForm();
		add(getToolbar(), getContent());
		updateGrid();
		closeEditor();

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
		block.setItemLabelGenerator(Block::getBlockLabel);
		year.setItemLabelGenerator(Year::getYearLabel);
		scheme.setItemLabelGenerator(Scheme::getSchemeLabel);
		consti.setItemLabelGenerator(
				constituency -> constituency.getConstituencyLabel() + "-" + constituency.getConstituencyMLA());
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

	private void configureGrid() {
		
		grid.setSizeFull();
		grid.setColumns("workCode");
		grid.addColumn(work -> work.getWorkName()).setHeader("Name of The Work").setWidth("20%").setResizable(true)
				.setSortable(true);
		grid.addColumn(work -> work.getWorkAmount()).setHeader("Sanc. Amount").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		grid.addColumn(work -> work.getBlock().getBlockLabel()).setAutoWidth(true).setHeader("Block/MB").setSortable(true).setResizable(true);
		grid.addColumn(work -> work.getScheme().getSchemeLabel()).setAutoWidth(true).setHeader("Scheme").setSortable(true).setResizable(true);
		grid.addColumn(work -> work.getConstituency().getConstituencyLabel() + "-"+ work.getConstituency().getConstituencyMLA()).setWidth("20%").setHeader("Constituency").setSortable(true).setResizable(true);
		grid.addColumn(work -> work.getYear().getYearLabel()).setAutoWidth(true).setHeader("Year").setSortable(true).setResizable(true);
		grid.addColumn(work -> work.getSanctionNo()).setHeader("Sanc. No").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		grid.addColumn(work -> 
			work.getSanctionDate()!=null? 
					work.getSanctionDate().format(dateFormatter):"No Date").setHeader("Sanc. Date").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		grid.addColumn(work -> work.getNoOfInstallments()).setHeader("Installments").setResizable(true)
				.setSortable(true).setAutoWidth(true);
		grid.addColumn(work -> work.getWorkStatus()).setHeader("Status").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		grid.addColumn(work -> work.getUpdatedBy().getProfileName()).setHeader("Entered By").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		
		grid.addColumn(work ->
		work.getUpdatedOn()!=null?
			work.getUpdatedOn().format(dateFormatter):"No Date").setHeader("Entered On").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		grid.asSingleSelect().addValueChangeListener(e -> editWork(e.getValue()));
		grid.getHeaderRows().clear();
		grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		grid.setClassNameGenerator(work -> {
		    if ("Completed".equals(work.getWorkStatus()))
		        return "high-rating";

		    if ("Entered".equals(work.getWorkStatus()))
		        return "low-rating";

		    return null;
		});
		GridContextMenu<Work> contextMenu = new GridContextMenu<>(grid);

		// Add a menu item for viewing installments
		contextMenu.addItem("View Installments", event -> {
		    Optional<Work> selectedWork = event.getItem();
		    selectedWork.ifPresent(work -> {
		        // Show a dialog or a new component with installments
		        showInstallmentsDialog(work);
		    });
		});

	}

	
	private void showInstallmentsDialog(Work work) { // Create a dialog
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle(work.getWorkCode()+"-"+work.getWorkName());
		Grid<Installment> installmentGrid = new Grid<>(Installment.class, false);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		installmentGrid.addColumn(Installment::getInstallmentNo).setHeader("Installment Number").setResizable(true);
		installmentGrid.addColumn(Installment::getInstallmentAmount).setHeader("Amount Released").setResizable(true);
		//installmentGrid.addColumn(Installment::getInstallmentDate).setHeader("Released Date").setResizable(true);
		installmentGrid.addColumn(installment->
		installment.getInstallmentDate()!=null?
				installment.getInstallmentDate().format(dateFormatter):"No Date").setHeader("Released Date").setResizable(true).setSortable(true)
					.setAutoWidth(true);
	
		installmentGrid.addColumn(Installment::getInstallmentLetter).setHeader("Letter No.").setResizable(true);
		installmentGrid.addColumn(Installment::getUcLetter).setHeader("UC Letter No").setResizable(true);
		//installmentGrid.addColumn(Installment::getUcDate).setHeader("UC Date").setResizable(true);
		installmentGrid.addColumn(installment->
			installment.getUcDate()!=null?
					installment.getUcDate().format(dateFormatter):"").setHeader("UC. Date").setResizable(true).setSortable(true)
						.setAutoWidth(true);
		installmentGrid.addColumn(installment->installment.getEnteredBy().getProfileName()).setHeader("Entered By").setResizable(true);
		//installmentGrid.addColumn(Installment::getEnteredOn).setHeader("Entered On").setResizable(true);
		installmentGrid.addColumn(installment->
		installment.getEnteredOn()!=null?
				installment.getEnteredOn().format(dateFormatter):"No Date").setHeader("Entered On").setResizable(true).setSortable(true)
					.setAutoWidth(true);
	
		List<Installment> installments = service.getInstallments(work);
		installmentGrid.setItems(installments);
		installmentGrid.setAllRowsVisible(true);
		Button closeButton = new Button("Close", e -> dialog.close());
		dialog.add(installmentGrid);
		dialog.getFooter().add(closeButton);
		dialog.open();
	}
	
	//test
	
	public void filterGrid() {
		
		// selected
		// filterText.setValue("");
		grid.setItems(
				service.getFilteredWorks(scheme.getValue(), consti.getValue(), block.getValue(), year.getValue()));
	}

	private Component getContent() {
		HorizontalLayout content = new HorizontalLayout(grid, workform);
		content.setFlexGrow(1, grid);
		content.setFlexGrow(1, workform);
		content.addClassName("content");
		content.setSizeFull();
		return content;
	}
	public void updateGrid() {
		grid.setItems(
				service.getFilteredWorks(scheme.getValue(), consti.getValue(), block.getValue(), year.getValue()));
	}

	private Sort toSpringSort(List<QuerySortOrder> orders, Sort defaultSort) {
	    if (orders == null || orders.isEmpty()) return defaultSort;
	    Sort sort = Sort.unsorted();
	    for (QuerySortOrder o : orders) {
	        // Map your grid column keys to entity fields
	        String prop = switch (o.getSorted()) {
	            case "workCode" -> "workCode";
	            case "name"     -> "name";
	            // add more mappings as needed
	            default         -> "workCode";
	        };
	        Sort s = (o.getDirection() == SortDirection.ASCENDING)
	                ? Sort.by(Sort.Direction.ASC, prop)
	                : Sort.by(Sort.Direction.DESC, prop);
	        sort = sort.and(s);
	    }
	    return sort.isUnsorted() ? defaultSort : sort;
	}

	private Component getToolbar() {
		filterText.setPlaceholder("Filter By Work Code, Name or Sanction Number");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateList());
		filterText.setWidth("10%");
		expButton.addClickListener(e -> GridExporter.newWithDefaults(grid).open());
		expButton.setIcon(new Icon(VaadinIcon.EXTERNAL_LINK));
		Button addButton = new Button("New Work");
		addButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE_O));
		addButton.addClickListener(e -> addWork());
		// for testing purpose: generate dummy data
		Button testButton = new Button("Generate Test Data");

		//testButton.addClickListener(e -> generateTestData());
		configureCombos();
		//HorizontalLayout toolbar = new HorizontalLayout(filterText, addButton, testButton);
		//HorizontalLayout toolbar = new HorizontalLayout(filterText,consti, block, scheme, year, addButton, expButton);
		FormLayout toolbar = new FormLayout();
		toolbar.add(filterText, 2);
		toolbar.add(consti, 2);
		toolbar.add(block, 2);
		toolbar.add(scheme,1);
		toolbar.add(year, 1);
		toolbar.add(addButton, 1);
		toolbar.add(expButton,1);
		toolbar.setResponsiveSteps(
		    new FormLayout.ResponsiveStep("0", 2),   // 1 column by default
		    new FormLayout.ResponsiveStep("600px", 4), // 2 columns for screens wider than 600px
		    new FormLayout.ResponsiveStep("800px", 10)  // 3 columns for screens wider than 800px
		);
		toolbar.setWidthFull();
		return toolbar;
	}

	
	

	public void configureForm() {
		workform = new WorkForm(service);
		workform.setWidth("40%");
		workform.addListener(WorkForm.SaveEvent.class, this::saveWork);
		workform.addListener(WorkForm.DeleteEvent.class, this::deleteWork);
		workform.addListener(WorkForm.CloseEvent.class, e -> closeEditor());
	}

	public void saveWork(WorkForm.SaveEvent event) {
		long a = event.getWork().getWorkCode();

		service.saveWork(event.getWork());
		updateList();
		long b = service.getWorkCode();
		

		// closeEditor();
		if (a == b) {
			addWorkAgain(event.getWork());
		} else {
			closeEditor();
		}
	}

	private void addWorkAgain(Work work) {
		// workform.save.setEnabled(true);
		grid.asSingleSelect().clear();
		workform.installaccordion.setEnabled(false);
		workform.ucaccordion.setEnabled(false);
		
		Work newWork = new Work();
		newWork.setBlock(work.getBlock());
		newWork.setConstituency(work.getConstituency());
		newWork.setSanctionDate(work.getSanctionDate());
		newWork.setScheme(work.getScheme());
		newWork.setYear(work.getYear());
		newWork.setSanctionDate(work.getSanctionDate());
		newWork.setSanctionNo(work.getSanctionNo());
		
		editWork(newWork);

	}

	public void deleteWork(WorkForm.DeleteEvent event) {
		service.deleteWork(event.getWork(),"");
		updateList();
		closeEditor();

	}

	private void updateList() {
		block.clear();
		scheme.clear();
		consti.clear();
		year.clear();
		///grid.setItems(service.getFilteredWorks(filterText.getValue()));
		grid.setItems(service.getFilteredWorks(filterText.getValue()));
		// configureGrid();
	}

	private void closeEditor() {
		workform.setWork(null);
		workform.setVisible(false);

	}

	private void addWork() {
		// workform.save.setEnabled(true);
		grid.asSingleSelect().clear();
		workform.installaccordion.setEnabled(false);
		workform.ucaccordion.setEnabled(false);
		workform.workSelect.setValue("");
		editWork(new Work());

	}

	private void editWork(Work work) {
		try {
			
			int workinstallment = 0;
			if (work == null) {
				closeEditor();
			} else {
				
				workform.setWork(work);
				workform.setVisible(true);
				workform.save.setEnabled(isUser);
				enableFields();
				workinstallment = work.getNoOfInstallments();
				
				if (work.getWorkAmount() != null) {
					// check if work is entered or not by checking if installment is greater than 0
					int tablecount = service.getInstallmentCount(work);
					int toEnter = tablecount + 1;
					
					if (tablecount > 0) {
						
						// check if any installment is entered
						List<Installment> installments = service.getInstallments(work);
						workform.delete.setEnabled(isAdmin);
						if(!isAdmin) {
							disableFields();
						}
						// workform.setEnabled(isAdmin);
						int tablecountindex = tablecount - 1;
						if (workinstallment == tablecount) {
							// check if all installments are entered, (if yes check if uc is enetered
							
							if (installments.get(tablecountindex).getInstallmentLetter() == null) {
								closeAllAccordion();
								
								// workform.ucmaster.setText("UC: " + tablecount);
							} else if (installments.get(tablecountindex).getUcLetter() == null) {
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
								// check if release order is not printed
								closeAllAccordion();
							} else if (installments.get(tablecountindex).getUcLetter() == null) {
								
								openUcAccordion();
								workform.ucmaster.setText("UC: " + tablecount);

							} else {
								openInstallAccordion();
								workform.installmentAmount.setValue(work.getWorkAmount()
										.subtract(installments.get(tablecountindex).getInstallmentAmount()));
								workform.installmentmaster.setText("Installment: " + toEnter);
							}
						}
					} else {
						// "No Installments In the table"-Enter New Installment
						workform.delete.setEnabled(true);
						openInstallAccordion();
						workform.installmentAmount
								.setValue(work.getWorkAmount().divide(new BigDecimal(work.getNoOfInstallments())));
						workform.installmentmaster.setText("Installment: " + toEnter);
					}

				} else {
					
					closeAllAccordion();
					// workform.delete.setEnabled(isA);
					enableFields();
				}
			}
		} catch (ArithmeticException aE) {
			
		} catch (Exception e) {
			//System.out.println(e);
		}
	}

	public void closeAllAccordion() {
		workform.workaccordion.setOpened(true);
		workform.installaccordion.setEnabled(false);
		workform.installaccordion.setOpened(false);
		workform.ucaccordion.setEnabled(false);
		workform.ucaccordion.setOpened(false);
	}

	public void openInstallAccordion() {
		workform.workaccordion.setOpened(false);
		workform.installaccordion.setEnabled(true);
		workform.installaccordion.setOpened(true);
		workform.ucaccordion.setEnabled(false);
		workform.ucaccordion.setOpened(false);
		workform.workaccordion.setOpened(false);
	}

	public void openUcAccordion() {
		workform.workaccordion.setOpened(false);
		workform.installaccordion.setEnabled(false);
		workform.installaccordion.setOpened(false);
		workform.ucaccordion.setEnabled(true);
		workform.ucaccordion.setOpened(true);
		workform.workaccordion.setOpened(false);
	}

	public void enableFields() {
		workform.scheme.setEnabled(true);
		workform.constituency.setEnabled(true);
		workform.block.setEnabled(true);
		workform.year.setEnabled(true);
		workform.workAmount.setEnabled(true);
		workform.noOfInstallments.setEnabled(true);
		workform.workSelect.setEnabled(true);
	}

	public void disableFields() {
		workform.scheme.setEnabled(false);
		workform.constituency.setEnabled(false);
		workform.block.setEnabled(false);
		workform.year.setEnabled(false);
		workform.workAmount.setEnabled(false);
		workform.noOfInstallments.setEnabled(false);
		workform.workSelect.setEnabled(false);
	}
}
