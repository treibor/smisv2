package com.smis.view;

import com.smis.dbservice.AuditService;
import com.smis.entity.AuditTrail;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@RolesAllowed({"ADMIN", "SUPER"})
@PageTitle("Audit Trail")
@Route(value = "audittrail", layout = MainLayout.class)
public class AuditView extends HorizontalLayout {
	Grid<AuditTrail> auditgrid=new Grid<>(AuditTrail.class);
	AuditService service;
	public AuditView(AuditService service) {
		this.service=service;
		// TODO Auto-generated constructor stub
		setSizeFull();
		
		add(getAuditTab());
	}
	
	public Component getAuditTab() {
		auditgrid.removeAllColumns();
		auditgrid.addColumn(audittrail->audittrail.getAction()).setHeader("Action").setSortable(true).setResizable(true);
		auditgrid.addColumn(audittrail->audittrail.getDetails()).setHeader("Details").setSortable(true).setResizable(true);
		auditgrid.addColumn(audittrail->audittrail.getActionBy()).setHeader("Performed By").setSortable(true).setResizable(true);
		auditgrid.addColumn(audittrail->audittrail.getIpAddress()).setHeader("IP Address").setSortable(true).setResizable(true);
		auditgrid.addColumn(audittrail->audittrail.getActionOn()).setHeader("Date /Time").setSortable(true).setResizable(true);
		auditgrid.setItems(service.getAuditTrail());
		auditgrid.setSizeFull();
		return auditgrid;
	}
}
