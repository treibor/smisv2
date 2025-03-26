package com.smis.view;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

//import com.identity.views.CheckBox;
import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Installment;
import com.smis.entity.InstallmentDocument;
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
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

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
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	public WorkView(Dbservice service) {
		this.service = service;
		setSizeFull();
		isAdmin = service.isAdmin();
		isUser = service.isUser();
		// displayFilter.addValueChangeListener(e-> displayFilters());
		configureGrid();
		//configureGridHistory();
		configureForm();
		add(getToolbar(), getContent());
		updateGrid();
		closeEditor();

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

	private void configureGrid() {
		grid.setSizeFull();
		grid.setColumns("workCode");
		grid.addColumn(work -> work.getWorkName()).setHeader("Name of The Work").setWidth("20%").setResizable(true)
				.setSortable(true);
		grid.addColumn(work -> work.getWorkAmount()).setHeader("Sanc. Amount").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		grid.addColumn(work -> work.getBlock().getBlockName()).setAutoWidth(true).setHeader("Block/MB")
				.setSortable(true).setResizable(true);
		grid.addColumn(work -> work.getScheme().getSchemeName()).setAutoWidth(true).setHeader("Scheme")
				.setSortable(true).setResizable(true);
		grid.addColumn(work -> work.getConstituency().getConstituencyNo() + "-"
				+ work.getConstituency().getConstituencyName() + "-" + work.getConstituency().getConstituencyMLA())
				.setWidth("20%").setHeader("Constituency").setSortable(true).setResizable(true);
		grid.addColumn(work -> work.getYear().getYearName()).setAutoWidth(true).setHeader("Year").setSortable(true)
				.setResizable(true);
		grid.addColumn(work -> work.getSanctionNo()).setHeader("Sanc. No").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		grid.addColumn(
				work -> work.getSanctionDate() != null ? work.getSanctionDate().format(dateFormatter) : "No Date")
				.setHeader("Sanc. Date").setResizable(true).setSortable(true).setAutoWidth(true);
		grid.addColumn(work -> work.getNoOfInstallments()).setHeader("Installments").setResizable(true)
				.setSortable(true).setAutoWidth(true);
		grid.addColumn(work -> work.getProcessflow().getStepName()).setHeader("Current Process").setResizable(true).setSortable(true)
		.setAutoWidth(true);
		//grid.addColumn(work -> work.getWorkStatus()).setHeader("Status").setResizable(true).setSortable(true).setAutoWidth(true);
		grid.addColumn(work -> work.getUpdatedBy().getProfileName()).setHeader("Sent By").setResizable(true).setSortable(true)
				.setAutoWidth(true);

		grid.addColumn(work -> work.getUpdatedOn() != null ? work.getUpdatedOn().format(timeFormatter) : "No Date")
				.setHeader("Sent On").setResizable(true).setSortable(true).setAutoWidth(true);
		grid.asSingleSelect().addValueChangeListener(e -> editWork(e.getValue()));
		grid.getHeaderRows().clear();
		grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		grid.setClassNameGenerator(work -> {
			if (work.getWorkStatus().equals("Completed"))
				return "high-rating";
			if (work.getWorkStatus().equals("Entered"))
				return "low-rating";
			return null;
		});
		GridContextMenu<Work> contextMenu = new GridContextMenu<>(grid);

		// Add a menu item for viewing installments
		contextMenu.addItem("View Details", event -> {
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
	
	private void showInstallmentsDialog(Work work) { 
	    // Create a dialog
	    Dialog dialog = new Dialog();
	    dialog.setHeaderTitle(work.getWorkCode() + " - " + work.getWorkName());

	    Grid<Installment> installmentGrid = new Grid<>(Installment.class, false);
	    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	    installmentGrid.addColumn(Installment::getInstallmentNo)
	        .setHeader("Installment Number").setResizable(true);
	    installmentGrid.addColumn(Installment::getInstallmentAmount)
	        .setHeader("Amount Released").setResizable(true);
	    installmentGrid.addColumn(installment -> installment.getInstallmentDate() != null
	        ? installment.getInstallmentDate().format(dateFormatter)
	        : "")
	        .setHeader("Released Date").setResizable(true).setSortable(true).setAutoWidth(true);
	    installmentGrid.addColumn(Installment::getInstallmentLetter)
	        .setHeader("Letter No.").setResizable(true);
	    installmentGrid.addComponentColumn(installment -> {
	        InstallmentDocument doc = installment.getReleaseOrder(); // Get the linked document

	        if (doc != null && doc.getDocument() != null) {
	            // Create a StreamResource for the PDF
	            StreamResource resource = new StreamResource("document.pdf",
	                () -> new ByteArrayInputStream(doc.getDocument()));

	            Anchor downloadLink = new Anchor(resource, "View");
	            downloadLink.setTarget("_blank"); // Open in a new tab

	            return downloadLink;
	        } else {
	            return new Span(""); // Show message if no document exists
	        }
	    }).setHeader("Release Order").setAutoWidth(true);
	    installmentGrid.addColumn(Installment::getUcLetter)
	        .setHeader("UC Letter No").setResizable(true);
	    installmentGrid.addColumn(installment -> installment.getUcDate() != null
	        ? installment.getUcDate().format(dateFormatter) 
	        : "")
	        .setHeader("UC Date").setResizable(true).setSortable(true).setAutoWidth(true);
	    installmentGrid.addComponentColumn(installment -> {
	        InstallmentDocument doc = installment.getUcDocument(); // Get the linked document

	        if (doc != null && doc.getDocument() != null) {
	            // Create a StreamResource for the PDF
	            StreamResource resource = new StreamResource("document.pdf",
	                () -> new ByteArrayInputStream(doc.getDocument()));

	            Anchor downloadLink = new Anchor(resource, "View");
	            downloadLink.setTarget("_blank"); // Open in a new tab

	            return downloadLink;
	        } else {
	            return new Span(""); // Show message if no document exists
	        }
	    }).setHeader("UC Documents").setAutoWidth(true);
	    installmentGrid.addColumn(installment -> installment.getEnteredBy().getProfileName()).setHeader("Entered By").setResizable(true).setVisible(isAdmin);
	    installmentGrid.addColumn(installment -> installment.getEnteredOn() != null? installment.getEnteredOn().format(timeFormatter): "").setHeader("Entered On").setResizable(true).setSortable(true).setAutoWidth(true).setVisible(isAdmin);

	    // 🔹 Add "View Document" column
	    
	    Button closeButton = new Button("Close", e -> dialog.close());
	    Button deleteButton = new Button("Delete", e ->deleteInstallment(installmentGrid.asSingleSelect().getValue()) );
	    deleteButton.setEnabled(false);
	    // Load Installments
	    List<Installment> installments = service.getInstallments(work);
	    installmentGrid.setItems(installments);
	    installmentGrid.setAllRowsVisible(true);
	    installmentGrid.asSingleSelect().addValueChangeListener(event -> {
		    Installment selectedItem = event.getValue(); // Replace MyObject with your actual item type
			    if (selectedItem != null ) {
			        deleteButton.setEnabled(true);
			    } else {
			    	deleteButton.setEnabled(false);
			    }
			});
	    // Close button
	    // Add components to the dialog
	    dialog.add(installmentGrid);
	    dialog.getFooter().add(deleteButton,closeButton);
	    dialog.open();
	}
	public void deleteInstallment(Installment inst) {
		
	}
	private void showHistoryDialog(Work work) { // Create a dialog
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle(work.getWorkCode() + "-" + work.getWorkName());
		Grid<ProcessHistory> grid = new Grid<>(ProcessHistory.class, false);
		//DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		grid.addColumn(processhistory->processhistory.getProcessFlow().getStepName()).setHeader("Task").setAutoWidth(true);
		grid.addColumn(processhistory->processhistory.getProcessName()).setHeader("Action Performed").setAutoWidth(true);
		grid.addColumn(processhistory->processhistory.getRemarks()).setHeader("Remarks").setAutoWidth(true);
		grid.addColumn(processhistory->processhistory.getUser().getProfileName()).setHeader("Performed By").setAutoWidth(true);
		grid.addColumn(processhistory -> processhistory.getEnteredOn() != null
				? processhistory.getEnteredOn().format(timeFormatter)
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
		HorizontalLayout content = new HorizontalLayout(grid, workform);
		content.setFlexGrow(1, grid);
		content.setFlexGrow(1, workform);
		content.addClassName("content");
		content.setSizeFull();
		return content;
	}

	public void updateGrid() {

		// grid.setItems(service.getWorksAssignedToUser());
		grid.setItems(service.getWorks());
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
		Button addButton = new Button("New Work");
		addButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE_O));
		addButton.addClickListener(e -> addWork());
		addButton.setVisible(checkAuthority(service.getProcessFlowByOrder(1)));
		Button testButton = new Button("Generate Test Data");

		testButton.addClickListener(e -> generateTestData());
		configureCombos();
		// HorizontalLayout toolbar = new HorizontalLayout(filterText, addButton,
		// testButton);
		// HorizontalLayout toolbar = new HorizontalLayout(filterText,consti, block,
		// scheme, year, addButton, expButton);
		FormLayout toolbar = new FormLayout();
		toolbar.add(filterText, 2);
		toolbar.add(consti, 2);
		toolbar.add(block, 2);
		toolbar.add(scheme, 1);
		toolbar.add(year, 1);
		toolbar.add(addButton, 1);
		toolbar.add(expButton, 1);
		toolbar.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2), // 1 column by default
				new FormLayout.ResponsiveStep("600px", 4), // 2 columns for screens wider than 600px
				new FormLayout.ResponsiveStep("800px", 10) // 3 columns for screens wider than 800px
		);
		toolbar.setWidthFull();
		return toolbar;
	}

	private void generateTestData() {
		// TODO Auto-generated method stub
		try {
			for (int a = 0; a < service.getAllSchemes().size(); a++) {
				Scheme scheme = service.getAllSchemes().get(a);
				for (int b = 0; b < service.getAllConstituencies().size(); b++) {
					Constituency consti = service.getAllConstituencies().get(b);
					for (int c = 0; c < service.getAllBlocks().size(); c++) {
						// for (int c = service.getAllBlocks().size()-1; c <
						// service.getAllBlocks().size(); c++) {
						Block block = service.getAllBlocks().get(c);

						for (int d = 0; d < service.getAllYears().size(); d++) {
							// service.getAllYears().size(); d++) {
							Year year = service.getAllYears().get(d);
							Work testwork = new Work();
							testwork.setBlock(block);
							testwork.setVillage(service.getVillage(service.getAllBlocks().get(1)).get(1));
							testwork.setConstituency(consti);
							testwork.setScheme(scheme);
							testwork.setYear(year);
							testwork.setNoOfInstallments(2);
							testwork.setWorkAmount(BigDecimal.valueOf(10000));
							testwork.setWorkName("Construction at " + consti.getConstituencyName() + " for "
									+ scheme.getSchemeName() + "");
							testwork.setWorkCode(service.getWorkCode() + 1);
							testwork.setDistrict(service.getDistrict());
							testwork.setSanctionNo("ABC/SANC/" + scheme.getSchemeName());
							testwork.setWorkStatus("Entered");
							// testwork.setSanctionDate(new LocalDateTime());
							service.saveWork(testwork);
						}
					}
				}
			}
			updateGrid();
		} catch (Exception e) {

		}
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
		
		updateGrid();
		//updateList();
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
		// service.deleteInstallments(event.getWork());
		//audit.saveAudit(event.getWork(), "Delete");
		service.deleteWork(event.getWork());
		updateList();
		closeEditor();

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

	private void closeEditor() {
		workform.setWork(null);
		workform.setVisible(false);

	}

	private void addWork() {
		// workform.save.setEnabled(true);
		grid.asSingleSelect().clear();
		openWorkAccordion();
		workform.workSelect.setValue("");
		editWork(new Work());

	}

	private void editWork(Work work) {
		try {
			workform.accordion.close();
			if (work == null) {
				closeEditor();
				//System.out.println("Null Work");
				return;
			}
			workform.setWork(work);
			workform.setVisible(true);
			workform.save.setEnabled(isUser);
			int instcount = service.getInstallmentCount(work);
			List<Installment> installments = service.getInstallments(work);
			workform.delete.setEnabled(isAdmin);
			workform.save.setEnabled(isAdmin);
			if (!isAdmin)  {
				disableFields();
			}
			int step = work.getProcessflow().getStepOrder();
			if (step == 2) {
				workform.instAction.setItems("Forward", "Return to "+	 service.getProcessFlowByOrder(4).getStepName());
				workform.instAction.setValue("Forward");
				if (instcount > 0) {
					workform.installmentAmount.setValue(
							work.getWorkAmount().subtract(installments.get(instcount - 1).getInstallmentAmount()));
					workform.installmentmaster.setText("Installment: " + (instcount + 1));
					workform.instAction.setVisible(true);
				}else {
					workform.installmentAmount
					.setValue(work.getWorkAmount().divide(new BigDecimal(work.getNoOfInstallments())));
					workform.installmentmaster.setText("Installment: 1" );
					workform.instAction.setVisible(false);
				}
				openInstallAccordion();
				
			} else if (step == 3) {
				openRoAccordion();
				workform.roAction.setItems("Forward", "Return to " + service.getProcessFlowByOrder(2).getStepName());
				workform.roAction.setValue("Forward");
			} else if (step == 4) {
				openUcAccordion();
				workform.ucAction.setItems("Forward", "Return to " + service.getProcessFlowByOrder(3).getStepName());
				workform.ucAction.setValue("Forward");
			} else {
				closeAllAccordion();
			}

			if (!checkAuthority(service.getProcessFlowByOrder(1))) {
				workform.workaccordion.setVisible(false);
			}
			if (!checkAuthority(service.getProcessFlowByOrder(2))) {
				workform.installaccordion.setVisible(false);
			}
			if (!checkAuthority(service.getProcessFlowByOrder(3))) {
				workform.roaccordion.setVisible(false);
			}
			if (!checkAuthority(service.getProcessFlowByOrder(4))) {
				workform.ucaccordion.setVisible(false);
			}

		} catch (ArithmeticException aE) {

		} catch (Exception e) {
			// System.out.println(e);
		}
	}
	
	
	private void editWorkOriginal(Work work) {
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
					//check if any installment is entered
					if (tablecount > 0) {
						List<Installment> installments = service.getInstallments(work);
						workform.delete.setEnabled(isAdmin);
						workform.save.setEnabled(isAdmin);
						if (!isAdmin) {
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
								workform.ucAction.setItems("Enter UC", "Return to "+service.getProcessFlowByOrder(work.getProcessflow().getStepOrder()).getStepName());
								workform.ucAction.setValue("Enter UC");
								workform.ucmaster.setText("UC: " + tablecount);
								openUcAccordion();
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
								workform.ucAction.setItems("Enter UC", "Return to "+service.getProcessFlowByOrder(work.getProcessflow().getStepOrder()).getStepName());
								workform.ucAction.setValue("Enter UC");
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
					enableFields();
				}
				if(!checkAuthority(service.getProcessFlowByOrder(1))) {
					workform.workaccordion.setVisible(false);
				}
				if(!checkAuthority(service.getProcessFlowByOrder(2))) {
					workform.installaccordion.setVisible(false);
				}
				if(!checkAuthority(service.getProcessFlowByOrder(4))) {
					workform.ucaccordion.setVisible(false);
				}
			}
		} catch (ArithmeticException aE) {

		} catch (Exception e) {
			//System.out.println(e);
		}
	}
	public void closeAllAccordion() {
		workform.workaccordion.setOpened(false);
		workform.workaccordion.setEnabled(false);
		workform.installaccordion.setEnabled(false);
		workform.installaccordion.setOpened(false);
		workform.ucaccordion.setEnabled(false);
		workform.ucaccordion.setOpened(false);
		workform.roaccordion.setOpened(false);
		workform.roaccordion.setEnabled(false);
	}
	public void openWorkAccordion() {
		workform.workaccordion.setOpened(true);
		workform.workaccordion.setEnabled(true);
		workform.installaccordion.setEnabled(false);
		workform.installaccordion.setOpened(false);
		workform.ucaccordion.setEnabled(false);
		workform.ucaccordion.setOpened(false);
		//workform.workaccordion.setOpened(false);
		workform.roaccordion.setOpened(false);
		workform.roaccordion.setEnabled(false);
		
	}
	public void openInstallAccordion() {
		workform.workaccordion.setOpened(false);
		workform.installaccordion.setEnabled(true);
		workform.installaccordion.setOpened(true);
		workform.ucaccordion.setEnabled(false);
		workform.ucaccordion.setOpened(false);
		//workform.workaccordion.setOpened(false);
		workform.roaccordion.setOpened(false);
		workform.roaccordion.setEnabled(false);
		
	}

	public void openUcAccordion() {
		workform.workaccordion.setOpened(false);
		workform.installaccordion.setEnabled(false);
		workform.installaccordion.setOpened(false);
		workform.ucaccordion.setEnabled(true);
		workform.ucaccordion.setOpened(true);
		workform.roaccordion.setOpened(false);
		workform.roaccordion.setEnabled(false);
	}
	public void openRoAccordion() {
		workform.workaccordion.setOpened(false);
		workform.installaccordion.setEnabled(false);
		workform.installaccordion.setOpened(false);
		workform.ucaccordion.setEnabled(false);
		workform.ucaccordion.setOpened(false);
		
		workform.roaccordion.setOpened(true);
		workform.roaccordion.setEnabled(true);
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
