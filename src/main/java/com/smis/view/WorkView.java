package com.smis.view;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import org.springframework.beans.factory.annotation.Autowired;

import com.smis.audit.Audit;
//import com.identity.views.CheckBox;
import com.smis.dbservice.Dbservice;
import com.smis.dbservice.FileStorageService;
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
import com.smis.util.ButtonUtil;
import com.smis.util.NotificationUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.AccordionPanel;
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

@PageTitle("Inbox")
@Route(value = "inbox", layout = MainLayout.class)
@RolesAllowed({ "USER", "SUPER", "ADMIN" })
//@CssImport(value = "../components/vaadin-grid.css", themeFor = "vaadin-grid")
public class WorkView extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dbservice service;
	@Autowired
	Audit audit;
	//@Autowired
	FileStorageService fileStorageService;
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
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	boolean isUser;
	boolean isAdmin;
	boolean isSuper;
	private Users loggedUser;

	public WorkView(Dbservice service, Audit audit, FileStorageService fss) {
		this.service = service;
		this.audit = audit;
		this.fileStorageService=fss;
		setSizeFull();
		this.loggedUser = service.getLoggedUser();
		isAdmin = service.hasRole("ADMIN");
		isSuper = service.hasRole("SUPER"); // or SUPER_ADMIN / DIST_ADMIN etc.
		isUser = service.hasRole("USER");
		configureGrid();
		// configureGridHistory();
		configureForm();
		add(getToolbar(), getContent());
		updateGrid();
		closeEditor();

	}

	public boolean checkAuthorityx(ProcessFlow pf) {
		Users user = service.getLoggedUser();
		ProcessFlowUser pfu = service.getProcessFlowUser(user, pf);
		if (pfu == null) {
			return false;
		} else {
			return true;
		}
	}

	private void configureCombos() {
		block.setItems(service.getAllBlocks(true));
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
		consti.setItemLabelGenerator(constituency ->constituency.getConstituencyLabel() + "-" + constituency.getConstituencyMLA());
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
		grid.addColumn(work -> work.getBlock().getBlockLabel()).setAutoWidth(true).setHeader("Block/MB")
				.setSortable(true).setResizable(true);
		grid.addColumn(work -> work.getScheme().getSchemeLabel()).setAutoWidth(true).setHeader("Scheme")
				.setSortable(true).setResizable(true);
		grid.addColumn(work -> work.getConstituency().getConstituencyLabel() + "-" + work.getConstituency().getConstituencyMLA())
				.setWidth("20%").setHeader("Constituency").setSortable(true).setResizable(true);
		grid.addColumn(work -> work.getYear().getYearLabel()).setAutoWidth(true).setHeader("Year").setSortable(true)
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
		grid.addColumn(work -> work.getProcessflow().getStepName()).setHeader("Current Process").setResizable(true)
				.setSortable(true).setAutoWidth(true);
		// grid.addColumn(work ->
		// work.getWorkStatus()).setHeader("Status").setResizable(true).setSortable(true).setAutoWidth(true);
		grid.addColumn(work -> work.getUpdatedBy().getProfileName()).setHeader("Sent By").setResizable(true)
				.setSortable(true).setAutoWidth(true);

		grid.addColumn(work -> work.getUpdatedOn() != null ? work.getUpdatedOn().format(timeFormatter) : "No Date")
				.setHeader("Sent On").setResizable(true).setSortable(true).setAutoWidth(true);
		grid.asSingleSelect().addValueChangeListener(e -> editWork(e.getValue()));
		grid.getHeaderRows().clear();
		grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		grid.setClassNameGenerator(work -> {
		    String status = work.getWorkStatus();

		    if ("Completed".equals(status)) {
		        return "high-rating";
		    }
		    if ("Entered".equals(status)) {
		        return "low-rating";
		    }
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
	    Dialog dialog = new Dialog();
	    dialog.setHeaderTitle(work.getWorkCode() + " - " + work.getWorkName());
	    dialog.setWidth("90vw");
	    dialog.addClassName("history-dialog");

	    Grid<Installment> installmentGrid = new Grid<>(Installment.class, false);
	    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	    installmentGrid.addColumn(Installment::getInstallmentNo).setHeader("Installment Number").setResizable(true);
	    installmentGrid.addColumn(Installment::getInstallmentAmount).setHeader("Amount Released").setResizable(true);
	    installmentGrid.addColumn(inst -> inst.getInstallmentDate() != null ? inst.getInstallmentDate().format(dateFormatter) : "")
	            .setHeader("Released Date").setResizable(true).setSortable(true).setAutoWidth(true);
	    installmentGrid.addColumn(Installment::getInstallmentLetter).setHeader("Letter No.").setResizable(true);

	    // Release Order (filesystem)
	    installmentGrid.addComponentColumn(installment -> {
	        String ro = installment.getGeneratedReleaseOrder();

	        if (ro == null || ro.isBlank()) {
	            return new Span("");
	        }

	        if (!fileStorageService.exists(ro)) {
	            Span missing = new Span("Missing file");
	            missing.getStyle().set("color", "var(--lumo-error-text-color)");
	            missing.getStyle().set("font-weight", "500");
	            return missing;
	        }

	        StreamResource resource = new StreamResource(ro, () -> {
	            try {
	                return fileStorageService.open(ro);
	            } catch (IOException ex) {
	                throw new UncheckedIOException(ex);
	            }
	        });
	        resource.setContentType("application/pdf");

	        Anchor link = new Anchor(resource, "View");
	        link.setTarget("_blank");
	        return link;

	    }).setHeader("Generated RO").setAutoWidth(true);

	    installmentGrid.addComponentColumn(installment -> {
	        String roPath = installment.getReleaseOrder();

	        if (roPath == null || roPath.isBlank()) {
	            return new Span("");
	        }

	        if (!fileStorageService.exists(roPath)) {
	            Span missing = new Span("Missing file");
	            missing.getStyle().set("color", "var(--lumo-error-text-color)");
	            missing.getStyle().set("font-weight", "500");
	            return missing;
	        }

	        StreamResource resource = new StreamResource(roPath, () -> {
	            try {
	                return fileStorageService.open(roPath);
	            } catch (IOException ex) {
	                throw new UncheckedIOException(ex);
	            }
	        });
	        resource.setContentType("application/pdf");

	        Anchor link = new Anchor(resource, "View");
	        link.setTarget("_blank");
	        return link;

	    }).setHeader("Uploaded RO").setAutoWidth(true);
	    installmentGrid.addComponentColumn(installment -> {
	        String rfPath = installment.getFundDocument();

	        if (rfPath == null || rfPath.isBlank()) {
	            return new Span("");
	        }

	        if (!fileStorageService.exists(rfPath)) {
	            Span missing = new Span("Missing file");
	            missing.getStyle().set("color", "var(--lumo-error-text-color)");
	            missing.getStyle().set("font-weight", "500");
	            return missing;
	        }

	        StreamResource resource = new StreamResource(rfPath, () -> {
	            try {
	                return fileStorageService.open(rfPath);
	            } catch (IOException ex) {
	                throw new UncheckedIOException(ex);
	            }
	        });
	        resource.setContentType("application/pdf");

	        Anchor link = new Anchor(resource, "View");
	        link.setTarget("_blank");
	        return link;

	    }).setHeader("Fund Receipt").setAutoWidth(true);
	    installmentGrid.addColumn(Installment::getUcLetter).setHeader("UC Letter No").setResizable(true);
	    installmentGrid.addColumn(inst -> inst.getUcDate() != null ? inst.getUcDate().format(dateFormatter) : "")
	            .setHeader("UC Date").setResizable(true).setSortable(true).setAutoWidth(true);

	    // UC Document (filesystem)
	    installmentGrid.addComponentColumn(installment -> {
	        String ucPath = installment.getUcDocument();
	        if (ucPath != null && !ucPath.isBlank()) {

	            String downloadName = ucPath; // or "uc.pdf"

	            StreamResource resource = new StreamResource(downloadName, () -> {
	                try {
	                    return fileStorageService.open(ucPath); // ✅ FIXED: open UC path
	                } catch (IOException ex) {
	                    throw new UncheckedIOException(ex);
	                }
	            });
	            resource.setContentType("application/pdf");

	            Anchor link = new Anchor(resource, "View");
	            link.setTarget("_blank");
	            return link;
	        }
	        return new Span("");
	    }).setHeader("UC Documents").setAutoWidth(true);

	    

	    Button closeButton = new Button("Close", e -> dialog.close());
	    
	    ButtonUtil.applyCloseStyle(closeButton);
	    
	    List<Installment> installments = service.getInstallments(work);
	    installmentGrid.setItems(installments);
	    installmentGrid.setAllRowsVisible(true);

	   

	    dialog.setModal(true);
	    dialog.setCloseOnOutsideClick(false);
	    dialog.setCloseOnEsc(false);

	    dialog.add(installmentGrid);
	    dialog.getFooter().add( closeButton);
	    dialog.open();
	}

	private void showHistoryDialog(Work work) {

	    Dialog dialog = new Dialog();
	    dialog.setWidth("90vw");
	    dialog.addClassName("history-dialog");
	    dialog.setHeaderTitle("History : " + work.getWorkCode() + " - " + work.getWorkName());

	    Grid<ProcessHistory> grid = new Grid<>(ProcessHistory.class, false);

	    List<ProcessHistory> history = service.getProcessHistory(work);

	    // 1️⃣ Serial Number Column
	    grid.addColumn(ph -> history.indexOf(ph) + 1)
	            .setHeader("Sl. No.")
	            .setWidth("90px")
	            .setFlexGrow(0);

	    // 2️⃣ Task
	    grid.addColumn(ph -> ph.getFromStep() != null
	                    ? ph.getFromStep().getStepName()
	                    : "")
	            .setHeader("Task")
	            .setAutoWidth(true);

	    // 3️⃣ Action Performed (Arrow + Text)
	    grid.addComponentColumn(ph -> {

	        Icon icon;
	        if (ph.isReversed()) {
	            icon = VaadinIcon.ARROW_BACKWARD.create();
	            icon.getStyle().set("color", "var(--lumo-error-color)");
	            icon.getElement().setAttribute("title", "Reverse");
	        } else {
	            icon = VaadinIcon.ARROW_FORWARD.create();
	            icon.getStyle().set("color", "var(--lumo-success-color)");
	            icon.getElement().setAttribute("title", "Forward");
	        }
	        icon.setSize("16px");

	        Span text = new Span(ph.getProcessName() != null ? ph.getProcessName() : "");

	        HorizontalLayout layout = new HorizontalLayout(icon, text);
	        layout.getStyle().set("align-items", "center");
	        layout.setSpacing(true);
	        layout.setPadding(false);

	        return layout;

	    }).setHeader("Action Performed").setAutoWidth(true);

	    // 4️⃣ Remarks
	    grid.addColumn(ph -> ph.getRemarks() != null ? ph.getRemarks() : "")
	            .setHeader("Remarks")
	            .setWidth("35%")
	            .setResizable(true);

	    // 5️⃣ Performed By
	    grid.addColumn(ph -> ph.getUser() != null
	                    ? ph.getUser().getProfileName()
	                    : "")
	            .setHeader("Performed By")
	            .setAutoWidth(true);

	    // 6️⃣ Document
	    grid.addComponentColumn(ph -> {

	        String path = ph.getDocument();

	        if (path == null || path.isBlank()) {
	            return new Span("");
	        }

	        if (!fileStorageService.exists(path)) {
	            Span missing = new Span("Missing file");
	            missing.getStyle().set("color", "var(--lumo-error-text-color)");
	            return missing;
	        }

	        StreamResource resource = new StreamResource(path, () -> {
	            try {
	                return fileStorageService.open(path);
	            } catch (IOException ex) {
	                throw new UncheckedIOException(ex);
	            }
	        });
	        resource.setContentType("application/pdf");

	        Anchor link = new Anchor(resource, "View");
	        link.setTarget("_blank");
	        return link;

	    }).setHeader("Document").setAutoWidth(true);

	    // 7️⃣ Date
	    grid.addColumn(ph -> ph.getEnteredOn() != null
	                    ? ph.getEnteredOn().format(timeFormatter)
	                    : "No Date")
	            .setHeader("Action Taken On")
	            .setSortable(true)
	            .setAutoWidth(true);

	    grid.setItems(history);
	    grid.setAllRowsVisible(true);
	    grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

	    Button closeButton = new Button("Close", e -> dialog.close());
	    ButtonUtil.applyCloseStyle(closeButton);

	    dialog.add(grid);
	    dialog.getFooter().add(closeButton);

	    dialog.setModal(true);
	    dialog.setCloseOnOutsideClick(false);
	    dialog.setCloseOnEsc(false);
	    dialog.open();
	}
	
	public void filterGrid() {

		// grid.setItems(service.getFilteredWorks(scheme.getValue(), consti.getValue(),
		// block.getValue(), year.getValue()));
		grid.setItems(service.getFilteredWorksByUser(scheme.getValue(), consti.getValue(), block.getValue(),
				year.getValue()));
		// gridhistory.setItems(service.getWorkHistory());
	}

	private Component getContent() {
		// var grids=new VerticalLayout(grid, gridhistory);
		// grids.setSizeFull();
		HorizontalLayout content = new HorizontalLayout(grid, workform);
		content.setFlexGrow(1, grid);
		content.setFlexGrow(1, workform);
		content.addClassName("content");
		content.setSizeFull();
		return content;
	}

	public void updateGrid() {
		
		grid.setItems(service.getWorks());

	}

	private Component getToolbar() {
		filterText.setPlaceholder("Filter By Work Code, Name or Sanction Number");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateGrid());
		filterText.setWidth("10%");
		expButton.addClickListener(e -> GridExporter.newWithDefaults(grid).open());
		expButton.setIcon(new Icon(VaadinIcon.EXTERNAL_LINK));
		Button addButton = new Button("New Work");
		ButtonUtil.applyNewStyle(addButton);
		//addButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE_O));
		addButton.addClickListener(e -> addWork());
		addButton.setVisible(service.hasAuthorityForStep(loggedUser, "WORK_ENTRY"));
		configureCombos();
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


	public void configureForm() {
		workform = new WorkForm(service, audit, fileStorageService);
		workform.setWidth("40%");
		workform.addListener(WorkForm.SaveEvent.class, this::saveWork);
		workform.addListener(WorkForm.DeleteEvent.class, this::deleteWork);
		workform.addListener(WorkForm.CloseEvent.class, e -> closeEditor());
		workform.addListener(WorkForm.RefreshEvent.class, e -> {
	        updateGrid();
	    });
	}

	public void saveWork(WorkForm.SaveEvent event) {
		long a = event.getWork().getWorkCode();
		service.saveWork(event.getWork());

		updateGrid();
		// updateList();
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
		closeAllAccordions();
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
		Work work = event.getWork();
		audit.saveAudit(work, "Delete Work", "Delete");
		service.deleteWork(work);
		// updateList();
		updateGrid();
		closeEditor();

	}

	

	private void closeEditor() {
		workform.setWork(null);
		workform.setVisible(false);

	}

	private void addWork() {
		// workform.save.setEnabled(true);
		grid.asSingleSelect().clear();
		//openWorkAccordion();
		workform.workSelect.setValue("");
		editWork(new Work());

	}

	private void editWork(Work work) {
	    try {
	        workform.accordion.close();

	        if (work == null) {
	            closeEditor();
	            return;
	        }

	        // ✅ NEW/unsaved work: no DB queries
	        if (work.getWorkId() == 0) {
	            workform.setWork(work);
	            workform.setVisible(true);
	            workform.delete.setEnabled(false);

	            closeAllAccordions();
	            showOnly(workform.workaccordion);
	            applyAccordionVisibilityByAuthority();
	            return;
	        }

	        // ✅ Reload managed Work
	        Work dbWork = service.getWorkById(work.getWorkId());
	        if (dbWork == null) {
	            NotificationUtil.showError("Work not found. Reloading...");
	            UI.getCurrent().getPage().reload();
	            return;
	        }
	        work = dbWork;

	        workform.setWork(work);
	        workform.setVisible(true);

	        // roles
	        workform.delete.setEnabled(isAdmin);

	        // load installments once (safe now because Work is persisted)
	        int instcount = service.getInstallmentCount(work);
	        List<Installment> installments = service.getInstallments(work);

	        // default: reset accordion state
	        closeAllAccordions();

	        // If there are installments, allow admin to open work accordion
	        if (!installments.isEmpty()) {
	            workform.workaccordion.setOpened(isAdmin);
	            workform.workaccordion.setEnabled(isAdmin);
	        }

	        // Current stepCode
	        ProcessFlow pf = work.getProcessflow();
	        String stepCode = (pf != null) ? pf.getStepCode() : null;

	        // -------------- step-specific UI --------------
	        if ("WORK_ENTRY".equals(stepCode)) {

	            // ✅ return-to from HISTORY (must ignore reversed rows in query)
	            
	            showOnly(workform.workaccordion);

	        }else if ("RELEASE_INSTALLMENT".equals(stepCode)) {

	            // ✅ return-to from HISTORY (must ignore reversed rows in query)
	            ProcessFlow returnTo = service.getReturnToStepFromHistory(work);

	            workform.instAction.setItems("Forward", "Return to " + returnTo.getStepName());
	            workform.instAction.setValue("Forward");

	            if (instcount > 0 && installments.size() >= instcount) {
	                BigDecimal lastAmount = installments.get(instcount - 1).getInstallmentAmount();
	                BigDecimal remaining = work.getWorkAmount().subtract(lastAmount);

	                workform.installmentAmount.setValue(remaining);
	                workform.installmentmaster.setText("Installment: " + (instcount + 1));
	                //workform.instAction.setVisible(true);
	            } else {
	                // default first installment suggestion
	                BigDecimal perInst = work.getWorkAmount().divide(
	                        new BigDecimal(work.getNoOfInstallments()),
	                        2,
	                        java.math.RoundingMode.HALF_UP
	                );
	                workform.installmentAmount.setValue(perInst);
	                workform.installmentmaster.setText("Installment: 1");
	                //workform.instAction.setVisible(false);
	            }

	            showOnly(workform.installaccordion);

	        }else if ("GENERATE_RELEASE_ORDER".equals(stepCode)) {
	            showOnly(workform.genroaccordion);
	            ProcessFlow returnTo = service.getReturnToStepFromHistory(work);
	            workform.genroText.setText("Return to " + returnTo.getStepName());
	            
	         
	        } else if ("UPLOAD_RELEASE_ORDER".equals(stepCode)) {

	            showOnly(workform.uproaccordion);

	            // ✅ return-to from HISTORY
	            ProcessFlow returnTo = service.getReturnToStepFromHistory(work);
	            workform.roAction.setItems("Forward", "Return to " + returnTo.getStepName());
	            workform.roAction.setValue("Forward");


	        } else if ("RELEASE_FUNDS".equals(stepCode)) {

	            showOnly(workform.rfaccordion);

	            // ✅ return-to from HISTORY
	            ProcessFlow returnTo = service.getReturnToStepFromHistory(work);
	            workform.rfAction.setItems("Forward", "Return to " + returnTo.getStepName());
	            workform.rfAction.setValue("Forward");

	        } else if ("UPLOAD_UC".equals(stepCode)) {

	            showOnly(workform.ucaccordion);

	            // ✅ return-to from HISTORY
	            ProcessFlow returnTo = service.getReturnToStepFromHistory(work);
	            workform.ucAction.setItems("Forward", "Return to " + returnTo.getStepName());
	            workform.ucAction.setValue("Forward");

	        } else if ("COMPLETED".equals(stepCode)) {
				ProcessFlow returnTo = service.getReturnToStepFromHistory(work);
				workform.complText.setText("Return to " + returnTo.getStepName());
				showOnly(workform.complaccordion);

	        } else {
	            closeAllAccordions();
	        }

	        // -------------- Authority visibility --------------
	        applyAccordionVisibilityByAuthority();

	    } catch (ArithmeticException aE) {
	        NotificationUtil.showError("Invalid installment calculation. Check number of installments.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        NotificationUtil.showError("Something went wrong: " + e.getMessage());
	    }
	}

	private void applyAccordionVisibilityByAuthority() {
	    workform.workaccordion.setVisible(service.hasAuthorityForStep(loggedUser, "WORK_ENTRY"));
	    workform.installaccordion.setVisible(service.hasAuthorityForStep(loggedUser, "RELEASE_INSTALLMENT"));

	    // RO accordion should be visible if user has authority in either RO steps
	    boolean canRo = service.hasAuthorityForStep(loggedUser, "GENERATE_RELEASE_ORDER")
	            || service.hasAuthorityForStep(loggedUser, "UPLOAD_RELEASE_ORDER");
	    workform.uproaccordion.setVisible(canRo);

	    workform.rfaccordion.setVisible(service.hasAuthorityForStep(loggedUser, "RELEASE_FUNDS"));
	    workform.ucaccordion.setVisible(service.hasAuthorityForStep(loggedUser, "UPLOAD_UC"));
	    workform.complaccordion.setVisible(service.hasAuthorityForStep(loggedUser, "COMPLETED"));
	}
	

	private Map<String, AccordionPanel> stepToPanel() {
	    return Map.of(
	        "WORK_ENTRY", workform.workaccordion,
	        "RELEASE_INSTALLMENT", workform.installaccordion,
	        "GENERATE_RELEASE_ORDER", workform.genroaccordion,
	        "UPLOAD_RELEASE_ORDER", workform.uproaccordion,
	        "RELEASE_FUNDS", workform.rfaccordion,
	        "UPLOAD_UC", workform.ucaccordion,
	        "COMPLETED", workform.complaccordion
	    );
	}

	public void openByStepCode(String stepCode) {
	    AccordionPanel p = stepToPanel().get(stepCode);
	    if (p == null) {
	        closeAllAccordions();
	        return;
	    }
	    showOnly(p);
	}
	private List<AccordionPanel> allPanels() {
	    return List.of(
	        workform.workaccordion,
	        workform.installaccordion,
	        workform.uproaccordion,
	        workform.genroaccordion,
	        workform.rfaccordion,
	        workform.ucaccordion,
	        workform.complaccordion
	    );
	}

	private void showOnly(AccordionPanel panelToOpen) {
	    for (AccordionPanel p : allPanels()) {
	        boolean active = (p == panelToOpen);
	        p.setOpened(active);
	        p.setEnabled(active);
	    }
	}

	// If you want "close all"
	private void closeAllAccordions() {
	    for (AccordionPanel p : allPanels()) {
	        p.setOpened(false);
	        p.setEnabled(false);
	    }
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
