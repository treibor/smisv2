package com.smis.view;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.smis.util.StatusBadgeUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import jakarta.annotation.security.RolesAllowed;
import software.xdev.vaadin.grid_exporter.GridExporter;

@PageTitle("History")
@Route(value = "workhistory", layout = MainLayout.class)
@RolesAllowed({ "USER", "SUPER", "ADMIN" })
//@CssImport(value = "../components/vaadin-grid.css", themeFor = "vaadin-grid")
public class WorkViewHistory extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dbservice service;
	@Autowired
	FileStorageService fileStorageService;
	// Grid<Work> grid = new Grid<>(Work.class);
	Grid<Work> gridhistory = new Grid<>(Work.class);
	TextField filterText = new TextField();
	ComboBox<Block> block = new ComboBox<Block>();
	ComboBox<Constituency> consti = new ComboBox<Constituency>();
	ComboBox<Year> year = new ComboBox<Year>();
	ComboBox<Scheme> scheme = new ComboBox<Scheme>();
	Button expButton = new Button("Export");
	// Checkbox displayFilter= new Checkbox("Show More Filters");
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	WorkForm workform;
	boolean isUser;
	boolean isAdmin;
	boolean isSuper;
	// @Autowired
	// private AuditTrail audit;

	public WorkViewHistory(Dbservice service) {
		this.service = service;
		setSizeFull();
		isAdmin = service.hasRole("ADMIN");
		isSuper = service.hasRole("SUPER"); // or SUPER_ADMIN / DIST_ADMIN etc.
		isUser = service.hasRole("USER");

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
		block.setItems(service.getBlocksByUser());
		// block.setClearButtonVisible(true);
		consti.setItems(service.getConstituenciesByUser());
		scheme.setItems(service.getSchemesByUser());
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

	private void configureGridHistory() {
		gridhistory.setSizeFull();
		gridhistory.setColumns("workCode");
		gridhistory.addColumn(work -> work.getWorkName()).setHeader("Name of The Work").setWidth("20%")
				.setResizable(true).setSortable(true);
		gridhistory.addColumn(work -> work.getWorkAmount()).setHeader("Sanc. Amount").setResizable(true)
				.setSortable(true).setAutoWidth(true);
		gridhistory.addColumn(work -> work.getBlock().getBlockLabel()).setAutoWidth(true).setHeader("Block/MB")
				.setSortable(true).setResizable(true);
		gridhistory.addColumn(work -> work.getScheme().getSchemeLabel()).setAutoWidth(true).setHeader("Scheme")
				.setSortable(true).setResizable(true);
		gridhistory
				.addColumn(work -> work.getConstituency().getConstituencyLabel() + "-"
						+ work.getConstituency().getConstituencyMLA())
				.setWidth("20%").setHeader("Constituency").setSortable(true).setResizable(true);
		gridhistory.addColumn(work -> work.getYear().getYearLabel()).setAutoWidth(true).setHeader("Year")
				.setSortable(true).setResizable(true);
		gridhistory.addColumn(work -> work.getSanctionNo()).setHeader("Sanc. No").setResizable(true).setSortable(true)
				.setAutoWidth(true);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		gridhistory.addColumn(
				work -> work.getSanctionDate() != null ? work.getSanctionDate().format(dateFormatter) : "No Date")
				.setHeader("Sanc. Date").setResizable(true).setSortable(true).setAutoWidth(true);
		gridhistory.addColumn(work -> work.getNoOfInstallments()).setHeader("Installments").setResizable(true)
				.setSortable(true).setAutoWidth(true);
		gridhistory.addColumn(work -> work.getProcessflow().getStepName()).setHeader("Current Process")
				.setResizable(true).setSortable(true).setAutoWidth(true);
		gridhistory
				.addComponentColumn(work -> StatusBadgeUtil.workStatusBadge(work.getIsDeleted(), work.getIsRecasted()))
				.setHeader("Status").setAutoWidth(true).setResizable(true).setComparator(work -> {
					if (Boolean.TRUE.equals(work.getIsDeleted()))
						return 2;
					if (Boolean.TRUE.equals(work.getIsRecasted()))
						return 1;
					return 0;
				});
				gridhistory.getHeaderRows().clear();
		gridhistory.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		gridhistory.setClassNameGenerator(work -> {
			String status = work.getWorkStatus();

			if ("Completed".equals(status)) {
				return "high-rating";
			}
			if ("Entered".equals(status)) {
				return "low-rating";
			}
			return null;
		});
		GridContextMenu<Work> contextMenu = new GridContextMenu<>(gridhistory);

		contextMenu.addItem(menuItem(VaadinIcon.EYE, "View Details"),
				event -> event.getItem().ifPresent(this::showInstallmentsDialog));

		contextMenu.addItem(menuItem(VaadinIcon.TIME_BACKWARD, "View History"),
				event -> event.getItem().ifPresent(this::showHistoryDialog));
		

		if (isAdmin) {
		    GridMenuItem<Work> undoItem = contextMenu.addItem(
		        menuItem(VaadinIcon.REFRESH, "Undo Delete"),
		        event -> event.getItem().ifPresent(this::confirmUndoDelete)
		    );
		    undoItem.setVisible(false);
		    contextMenu.addGridContextMenuOpenedListener(e -> {
		        Optional<Work> item = e.getItem();          // ✅ this is the safest
		        undoItem.setVisible(item.isPresent() && item.get().getIsDeleted());
		    });
		}
		if (isAdmin) {
		    GridMenuItem<Work> undoRecast = contextMenu.addItem(
		        menuItem(VaadinIcon.REFRESH, "Undo Recast"),
		        event -> event.getItem().ifPresent(this::confirmUndoRecast)
		    );
		    undoRecast.setVisible(false);
		    contextMenu.addGridContextMenuOpenedListener(e -> {
		        Optional<Work> item = e.getItem();          // ✅ this is the safest
		        undoRecast.setVisible(item.isPresent() && item.get().getIsRecasted());
		    });
		}

	}
	public void undoDelete(Work work, String remarks) {
		ProcessHistory existing=service.getLastPocessStep(work);
		work.setIsDeleted(false);
	    work.setRemarks(remarks);
	    service.saveWork(work);
	    ProcessHistory ph = new ProcessHistory();
	    ph.setWork(work);
	    ph.setEnteredOn(LocalDateTime.now());
	    ph.setProcessName("Undo Delete");
	    ph.setFromStep(existing.getFromStep());
	    ph.setToStep(existing.getToStep()); // optional, nice for audit
	    ph.setUser(service.getLoggedUser());
	    ph.setRemarks(remarks);
	    service.saveProcessHistory(ph);
	    NotificationUtil.showSuccess("Work Reverted to Last Process Successfully");
	    updateGrid();
	}
	public void confirmUndoDelete(Work work) {

		if (work == null)
			return;

		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setHeader("Undo?");

		Paragraph warning = new Paragraph("Are you sure you want to undo delete this item? "
				+ "This Work will be restored to the last Process Flow");

		TextField remarks = new TextField("Remarks");
		remarks.setWidthFull();
		remarks.setRequired(true);
		remarks.setErrorMessage("Remarks is mandatory");

		VerticalLayout layout = new VerticalLayout(warning, remarks);
		layout.setPadding(false);
		layout.setSpacing(true);
		dialog.add(layout);
		dialog.setCancelable(true);
		dialog.setConfirmText("Delete");
		dialog.addConfirmListener(e -> {

			String r = remarks.getValue() == null ? "" : remarks.getValue().trim();

			if (r.isEmpty()) {
				remarks.setInvalid(true);
				NotificationUtil.showError("Please Enter Remarks");
				e.getSource().setOpened(true);
				return; // ✅ keep dialog open
			}
			dialog.close();
			undoDelete(work, r);
			
		});

		dialog.open();
	}
	public void undoRecast(Work work, String remarks) {
		ProcessHistory existing=service.getLastPocessStep(work);
		work.setIsRecasted(false);
	    work.setRemarks(remarks);
	    service.saveWork(work);
	    ProcessHistory ph = new ProcessHistory();
	    ph.setWork(work);
	    ph.setEnteredOn(LocalDateTime.now());
	    ph.setProcessName("Undo Recast");
	    ph.setFromStep(existing.getFromStep());
	    ph.setToStep(existing.getToStep()); // optional, nice for audit
	    ph.setUser(service.getLoggedUser());
	    ph.setRemarks(remarks);
	    service.saveProcessHistory(ph);
	    NotificationUtil.showSuccess("Work Reverted to Last Process Successfully");
	    updateGrid();
	}
	public void confirmUndoRecast(Work work) {

		if (work == null)
			return;

		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setHeader("Undo?");

		Paragraph warning = new Paragraph("Are you sure you want to undo recast this item? "
				+ "This Work will be restored to the last Process Flow");

		TextField remarks = new TextField("Remarks");
		remarks.setWidthFull();
		remarks.setRequired(true);
		remarks.setErrorMessage("Remarks is mandatory");

		VerticalLayout layout = new VerticalLayout(warning, remarks);
		layout.setPadding(false);
		layout.setSpacing(true);
		dialog.add(layout);
		dialog.setCancelable(true);
		dialog.setConfirmText("Delete");
		dialog.addConfirmListener(e -> {

			String r = remarks.getValue() == null ? "" : remarks.getValue().trim();

			if (r.isEmpty()) {
				remarks.setInvalid(true);
				NotificationUtil.showError("Please Enter Remarks");
				e.getSource().setOpened(true);
				return; // ✅ keep dialog open
			}
			dialog.close();
			undoRecast(work, r);
			
		});

		dialog.open();
	}
	private Component menuItem(VaadinIcon icon, String text) {
		Icon i = icon.create();
		i.setSize("16px");

		Span label = new Span(text);

		HorizontalLayout hl = new HorizontalLayout(i, label);
		hl.setSpacing(true);
		hl.setPadding(false);
		hl.setMargin(false);
		hl.setAlignItems(FlexComponent.Alignment.CENTER);

		return hl;
	}
	private void showInstallmentsDialog(Work work) {
		try {
			Dialog dialog = new Dialog();
			dialog.setHeaderTitle(work.getWorkCode() + " - " + work.getWorkName());
			dialog.setWidth("90vw");
			dialog.addClassName("history-dialog");

			Grid<Installment> installmentGrid = new Grid<>(Installment.class, false);
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

			installmentGrid.addColumn(Installment::getInstallmentNo).setHeader("Installment Number").setResizable(true);
			installmentGrid.addColumn(Installment::getInstallmentAmount).setHeader("Amount Released")
					.setResizable(true);
			installmentGrid.addColumn(
					inst -> inst.getInstallmentDate() != null ? inst.getInstallmentDate().format(dateFormatter) : "")
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
			dialog.getFooter().add(closeButton);
			dialog.open();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showHistoryDialog(Work work) {

		Dialog dialog = new Dialog();
		dialog.setWidth("90vw");
		dialog.addClassName("history-dialog");
		dialog.setHeaderTitle("History : " + work.getWorkCode() + " - " + work.getWorkName());

		Grid<ProcessHistory> grid = new Grid<>(ProcessHistory.class, false);

		List<ProcessHistory> history = service.getProcessHistory(work);

		// 1️⃣ Serial Number Column
		grid.addColumn(ph -> history.indexOf(ph) + 1).setHeader("Sl. No.").setWidth("90px").setFlexGrow(0);

		// 2️⃣ Task
		grid.addColumn(ph -> ph.getFromStep() != null ? ph.getFromStep().getStepName() : "").setHeader("Task")
				.setAutoWidth(true);

		// 3️⃣ Action Performed (Arrow + Text)
		grid.addComponentColumn(ph -> {

		    String action = ph.getProcessName() != null ? ph.getProcessName().trim() : "";
		    String actionLower = action.toLowerCase();

		    Icon icon;
		    String title;

		    // --- special actions ---
		    if (actionLower.equals("deleted") || actionLower.equals("delete")) {
		        icon = VaadinIcon.TRASH.create();
		        icon.getStyle().set("color", "var(--lumo-error-color)");
		        title = "Deleted";

		    } else if (actionLower.equals("undo delete") || actionLower.equals("undelete")) {
		        icon = VaadinIcon.ROTATE_LEFT.create(); // or VaadinIcon.UNDO if you prefer
		        icon.getStyle().set("color", "var(--lumo-primary-color)");
		        title = "Undo Delete";

		    }else if (actionLower.equals("recasted") || actionLower.equals("recast")) {
		        icon = VaadinIcon.DEL.create(); // or VaadinIcon.UNDO if you prefer
		        icon.getStyle().set("color", "var(--lumo-primary-color)");
		        title = "Recasted";

		    } else if (actionLower.equals("undo recast") || actionLower.equals("unrecast")) {
		        icon = VaadinIcon.ROTATE_RIGHT.create(); // or VaadinIcon.UNDO if you prefer
		        icon.getStyle().set("color", "var(--lumo-primary-color)");
		        title = "Undo Recast";

		    }else {
		        // --- normal workflow actions ---
		        if (ph.isReversed()) {
		            icon = VaadinIcon.ARROW_BACKWARD.create();
		            icon.getStyle().set("color", "var(--lumo-error-color)");
		            title = "Reverse";
		        } else {
		            icon = VaadinIcon.ARROW_FORWARD.create();
		            icon.getStyle().set("color", "var(--lumo-success-color)");
		            title = "Forward";
		        }
		    }

		    icon.getElement().setAttribute("title", title);
		    icon.setSize("16px");

		    Span text = new Span(action);

		    HorizontalLayout layout = new HorizontalLayout(icon, text);
		    layout.getStyle().set("align-items", "center");
		    layout.setSpacing(true);
		    layout.setPadding(false);

		    return layout;

		}).setHeader("Action Performed").setAutoWidth(true);

		// 4️⃣ Remarks
		grid.addColumn(ph -> ph.getRemarks() != null ? ph.getRemarks() : "").setHeader("Remarks").setWidth("35%")
				.setResizable(true);

		// 5️⃣ Performed By
		grid.addColumn(ph -> ph.getUser() != null ? ph.getUser().getProfileName() : "").setHeader("Performed By")
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
		grid.addColumn(ph -> ph.getEnteredOn() != null ? ph.getEnteredOn().format(timeFormatter) : "No Date")
				.setHeader("Action Taken On").setSortable(true).setAutoWidth(true);

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
		gridhistory.setItems(service.getWorkHistory());
	}

	private Component getContent() {
		// var grids=new VerticalLayout(grid, gridhistory);
		// grids.setSizeFull();
		HorizontalLayout content = new HorizontalLayout(gridhistory);
		content.setFlexGrow(1, gridhistory);
		// content.setFlexGrow(1, workform);
		content.addClassName("content");
		content.setSizeFull();
		return content;
	}

	private Component getToolbar() {
		filterText.setPlaceholder("Filter By Work Code, Name or Sanction Number");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateGrid());
		filterText.setWidth("10%");
		expButton.addClickListener(e -> GridExporter.newWithDefaults(gridhistory).open());
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

	public void updateGrid() {
		gridhistory.setItems(service.getFilteredWorksForHistory(filterText.getValue(), scheme.getValue(),
				consti.getValue(), block.getValue(), year.getValue()));
	}

	
}
