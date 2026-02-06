package com.smis.audit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smis.dbservice.Dbservice;
import com.smis.entity.AuditTrail;
import com.smis.entity.Installment;
import com.smis.entity.Work;
import com.vaadin.flow.server.VaadinRequest;

@Service
public class Audit {
	private static final long serialVersionUID = 1L;

	AuditTrail audit;
	@Autowired
	private Dbservice aservice;
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	private static final int MAX_DETAILS_LENGTH = 1000;
	private static final int MAX_WORK_NAME_LENGTH = 900;

	public Audit(Dbservice service) {
		this.aservice = service;
	}

	public String getRealClientIp() {
		VaadinRequest request = VaadinRequest.getCurrent();
		String xForwardedForHeader = request.getHeader("X-Forwarded-For");
		if (xForwardedForHeader == null || xForwardedForHeader.isEmpty()) {
			return request.getRemoteAddr();
		} else {
			return xForwardedForHeader.split(",")[0].trim();
		}
	}

	public void saveAuditReturn(Work work, String entity, String action) {

		audit = new AuditTrail();
		audit.setAction(action);
		audit.setActionBy(aservice.getLoggedUser());
		audit.setActionOn(LocalDateTime.now());
		audit.setIpAddress(getRealClientIp());
		audit.setProcess(entity);
		String workName = work.getWorkName();
		if (workName.length() > MAX_WORK_NAME_LENGTH) {
			workName = workName.substring(0, MAX_WORK_NAME_LENGTH - 3) + "..."; // Add ellipsis
		}
		String details = work.getWorkCode() + "-" + workName;
		audit.setDetails(details);
		
		aservice.updateAudit(audit);
	}

	public void saveAudit(Work work, String entity, String action) {

		audit = new AuditTrail();
		audit.setAction(action);
		audit.setActionBy(aservice.getLoggedUser());
		audit.setActionOn(LocalDateTime.now());
		audit.setIpAddress(getRealClientIp());
		audit.setProcess(entity);
		String workName = work.getWorkName();
		if (workName.length() > MAX_WORK_NAME_LENGTH) {
			workName = workName.substring(0, MAX_WORK_NAME_LENGTH - 3) + "..."; // Add ellipsis
		}
		String details = work.getWorkCode() + "-" + workName;
		audit.setDetails(details);
		audit.setOtherDetails("Sanction No-" + work.getSanctionNo() + ", Sanction Date-"
				+ work.getSanctionDate().format(dateFormatter) + ", Amount-" + work.getWorkAmount() + ", Installments"
				+ work.getNoOfInstallments() + ", Previous User-" + work.getUpdatedBy().getUserName()
				+ ", Previous Entry Date-" + work.getUpdatedOn().format(timeFormatter) + ", Current Process-"
				+ work.getProcessflow().getStepName() + " /" + work.getBlock().getBlockLabel() + " /"
				+ work.getConstituency().getConstituencyLabel() + " /" + work.getScheme().getSchemeLabel() + " /"
				+ work.getYear().getYearLabel());
		aservice.updateAudit(audit);
	}

	public void saveAudit(Work work, Installment inst, String entity, String action) {

		audit = new AuditTrail();
		audit.setAction(action);
		audit.setActionBy(aservice.getLoggedUser());
		audit.setActionOn(LocalDateTime.now());
		audit.setIpAddress(getRealClientIp());
		audit.setProcess(entity);
		String workName = work.getWorkName();
		if (workName.length() > MAX_WORK_NAME_LENGTH) {
			workName = workName.substring(0, MAX_WORK_NAME_LENGTH - 3) + "..."; // Add ellipsis
		}
		String details = work.getWorkCode() + "-" + workName;
		audit.setDetails(details);
		audit.setOtherDetails("Amount-" + inst.getInstallmentAmount() +", Letter No-" + inst.getInstallmentLetter() + ", Date-"
				+ inst.getInstallmentDate() + ", UC Letter-" + inst.getUcLetter() + ", UC Date-" + inst.getUcDate());
		aservice.updateAudit(audit);
	}

	public void saveLoginAudit(String action, String process, String details, String otherDetails) {
		audit = new AuditTrail();
		audit.setAction(action);
		audit.setProcess(process);
		audit.setOtherDetails(otherDetails);
		audit.setDetails(details);
		audit.setActionOn(LocalDateTime.now());
		audit.setActionBy(aservice.getLoggedUser());
		audit.setIpAddress(getRealClientIp());
		aservice.updateAudit(audit);
	}
}
