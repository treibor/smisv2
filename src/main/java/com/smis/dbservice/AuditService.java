package com.smis.dbservice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smis.entity.AuditTrail;
import com.smis.entity.Installment;
import com.smis.entity.Users;
import com.smis.entity.Work;
import com.smis.repository.AuditRepository;
import com.smis.repository.UserRepository;
import com.vaadin.flow.server.VaadinRequest;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuditService {
	private UserRepository uRepo;
	private AuditRepository aRepo;
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	// private static final int MAX_DETAILS_LENGTH = 1000;
	private static final int MAX_WORK_NAME_LENGTH = 900;

	public AuditService(AuditRepository aRepo, UserRepository uRepo) {
		this.aRepo = aRepo;
		this.uRepo = uRepo;
	}

	public void updateAudit(AuditTrail entity) {
		aRepo.save(entity);
	}

	public List<AuditTrail> getAuditTrail() {
		return aRepo.findAllByOrderByIdDesc();
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

	public String getRealClientIp(HttpServletRequest request) {
		if (request == null)
			return "N/A";

		String xff = request.getHeader("X-Forwarded-For");
		if (xff == null || xff.isBlank()) {
			return request.getRemoteAddr();
		}
		return xff.split(",")[0].trim();
	}

	public void saveAuditReturn(Work work, String entity, String action) {
		AuditTrail audit;
		audit = new AuditTrail();
		audit.setAction(action);
		audit.setActionBy(getLoggedUser());
		audit.setActionOn(LocalDateTime.now());
		audit.setIpAddress(getRealClientIp());
		audit.setDistrict(getLoggedUser().getDistrict());
		audit.setProcess(entity);
		String workName = work.getWorkName();
		if (workName.length() > MAX_WORK_NAME_LENGTH) {
			workName = workName.substring(0, MAX_WORK_NAME_LENGTH - 3) + "..."; // Add ellipsis
		}
		String details = work.getWorkCode() + "-" + workName;
		audit.setDetails(details);

		updateAudit(audit);
	}

	public void saveAudit(Work work, String process, String action) {
		AuditTrail audit;
		audit = new AuditTrail();
		audit.setAction(action);
		audit.setActionBy(getLoggedUser());
		audit.setActionOn(LocalDateTime.now());
		audit.setIpAddress(getRealClientIp());
		audit.setProcess(process);
		audit.setDistrict(getLoggedUser().getDistrict());
		String workName = work.getWorkName();
		if (workName.length() > MAX_WORK_NAME_LENGTH) {
			workName = workName.substring(0, MAX_WORK_NAME_LENGTH - 3) + "..."; // Add ellipsis
		}
		String details = "Id:" + work.getWorkId() + " | WorkCode:" + work.getWorkCode() + " | WorkName:" + workName;
		audit.setDetails(details);
		audit.setOtherDetails("Sanction No:" + work.getSanctionNo() + " | Sanction Date:"
				+ work.getSanctionDate().format(dateFormatter) + " | Amount:" + work.getWorkAmount() + " |Installments"
				+ work.getNoOfInstallments() + " | " + work.getConstituency().getConstituencyLabel() + " | "
				+ work.getScheme().getSchemeLabel() + " | " + work.getYear().getYearLabel());
		updateAudit(audit);
	}

	public void saveAudit(Work work, Installment inst, String entity, String action) {
		AuditTrail audit;
		audit = new AuditTrail();
		audit.setAction(action);
		audit.setActionBy(getLoggedUser());
		audit.setActionOn(LocalDateTime.now());
		audit.setIpAddress(getRealClientIp());
		audit.setProcess(entity);
		audit.setDistrict(getLoggedUser().getDistrict());
		String workName = work.getWorkName();
		if (workName.length() > MAX_WORK_NAME_LENGTH) {
			workName = workName.substring(0, MAX_WORK_NAME_LENGTH - 3) + "..."; // Add ellipsis
		}
		String details = "Id:" + work.getWorkId() + " | WorkCode:" + work.getWorkCode() + " | WorkName:" + workName;
		audit.setDetails(details);
		audit.setOtherDetails("Amount-" + inst.getInstallmentAmount() + ", Letter No-" + inst.getInstallmentLetter()
				+ ", Date-" + inst.getInstallmentDate() + ", UC Letter-" + inst.getUcLetter() + ", UC Date-"
				+ inst.getUcDate());
		updateAudit(audit);
	}

	public void saveAudit(String action, String process, String details, String otherDetails) {
		AuditTrail audit;
		audit = new AuditTrail();
		audit.setAction(action);
		audit.setProcess(process);
		audit.setOtherDetails(otherDetails);
		audit.setDetails(details);
		audit.setActionOn(LocalDateTime.now());
		audit.setActionBy(getLoggedUser());
		audit.setIpAddress(getRealClientIp());
		audit.setDistrict(getLoggedUser().getDistrict());
		updateAudit(audit);
	}

	public void saveAuthAudit(String action, String process, String username, String odetails, String ip) {
		AuditTrail audit = new AuditTrail();
		audit.setAction(action); // LOGIN_SUCCESS / LOGIN_FAIL / LOGOUT / EXPIRED
		audit.setProcess(process);
		audit.setDetails("User=" + username);
		audit.setOtherDetails(odetails);
		audit.setIpAddress(ip);
		audit.setActionOn(LocalDateTime.now());

		// don't call aservice.getLoggedUser() here.
		updateAudit(audit);
	}

	public Users getLoggedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// No auth or not authenticated
		if (auth == null || !auth.isAuthenticated()) {
			return null; // or throw IllegalStateException if you prefer
		}

		// IMPORTANT: anonymous is considered "authenticated" in Spring
		if (auth instanceof AnonymousAuthenticationToken) {
			return null;
		}

		Object principal = auth.getPrincipal();

		// Resolve username safely
		String username;
		if (principal instanceof UserDetails ud) {
			username = ud.getUsername();
		} else if (principal instanceof String s) {
			// This covers cases like "anonymousUser"
			if ("anonymousUser".equalsIgnoreCase(s)) {
				return null;
			}
			username = s;
		} else {
			// Unknown principal type
			return null;
		}

		Users user = uRepo.findByUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found: " + username);
		}
		return user;
	}
}
