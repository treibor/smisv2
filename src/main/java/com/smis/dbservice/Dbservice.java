package com.smis.dbservice;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smis.entity.AuditTrail;
import com.smis.entity.Block;
import com.smis.entity.BlockUser;
import com.smis.entity.Constituency;
import com.smis.entity.ConstituencyUser;
import com.smis.entity.Installment;
import com.smis.entity.InstallmentReportNotes;
import com.smis.entity.ProcessFlow;
import com.smis.entity.ProcessFlowUser;
import com.smis.entity.ProcessHistory;
import com.smis.entity.Scheme;
import com.smis.entity.SchemeUser;
import com.smis.entity.Users;
import com.smis.entity.UsersRoles;
import com.smis.entity.Work;
import com.smis.entity.Year;
import com.smis.entity.master.District;
import com.smis.entity.master.MasterBlock;
import com.smis.entity.master.MasterConstituency;
import com.smis.entity.master.MasterScheme;
import com.smis.entity.master.MasterYear;
import com.smis.entity.master.State;
import com.smis.entity.master.Village;
import com.smis.repository.AuditRepository;
import com.smis.repository.BlockRepository;
import com.smis.repository.BlockUserRepo;
import com.smis.repository.ConstituencyRepository;
import com.smis.repository.ConstituencyUserRepo;
import com.smis.repository.DistrictRepository;
import com.smis.repository.InstallmentReportRepository;
import com.smis.repository.InstallmentRepository;
import com.smis.repository.MasterBlockRepo;
import com.smis.repository.MasterConstiRepo;
import com.smis.repository.MasterSchemeRepo;
import com.smis.repository.MasterYearRepo;
import com.smis.repository.ProcessFlowRepo;
import com.smis.repository.ProcessFlowUserRepo;
import com.smis.repository.ProcessHistoryRepo;
import com.smis.repository.RoleRepository;
import com.smis.repository.SchemeRepository;
import com.smis.repository.SchemeUserRepo;
import com.smis.repository.StateRepository;
import com.smis.repository.UserRepository;
import com.smis.repository.VillageRepository;
import com.smis.repository.WorkRepository;
import com.smis.repository.YearRepository;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;

import jakarta.transaction.Transactional;

@Service
public class Dbservice implements Serializable {
	

	@Autowired
	private AuditService auditservice;
	private static final long serialVersionUID = 1L;
	private final WorkRepository wrepo;
	private final YearRepository yrepo;
	private final SchemeRepository srepo;
	private final ConstituencyRepository crepo;
	private final BlockRepository brepo;
	private final DistrictRepository drepo;
	private final InstallmentRepository irepo;

	private final UserRepository urepo;
	private final StateRepository strepo;
	private final VillageRepository vtrepo;
	private final RoleRepository rolerepo;

	// Notification Notification = new Notification();
	// @Autowired
	private final ProcessFlowRepo pflowrepo;
	private final ProcessFlowUserRepo pflowuserrepo;
	private final ProcessHistoryRepo phistoryrrepo;
	private final BlockUserRepo buserrepo;
	private final ConstituencyUserRepo cuserrepo;
	private final SchemeUserRepo suserrepo;
	// private final InstallmentDocRepository docrepo;
	private final InstallmentReportRepository reportrepo;
	private final MasterConstiRepo mconstirepo;
	private final MasterBlockRepo mblockrepo;
	private final MasterSchemeRepo mschemerepo;
	private final MasterYearRepo myearrepo;

	public Dbservice(StateRepository strepo, UserRepository urepo, WorkRepository workrepo, YearRepository yrepo,
			SchemeRepository srepo, ConstituencyRepository crepo, BlockRepository brepo, DistrictRepository drepo,
			InstallmentReportRepository reportrepo, InstallmentRepository irepo, VillageRepository vrepo,
			RoleRepository rolerepo, ProcessFlowRepo pflowrepo, ProcessFlowUserRepo pflowuserrepo,
			ProcessHistoryRepo phistoryrrepo, BlockUserRepo buserrepo, SchemeUserRepo suserrepo,
			MasterConstiRepo mconstirepo, MasterBlockRepo mblockrepo, MasterSchemeRepo mschemerepo,
			MasterYearRepo myearrepo, ConstituencyUserRepo cuserrepo) {
		this.wrepo = workrepo;
		this.yrepo = yrepo;
		this.srepo = srepo;
		this.crepo = crepo;
		this.brepo = brepo;
		this.drepo = drepo;
		this.irepo = irepo;
		// this.idrepo = idrepo;
		this.urepo = urepo;
		this.strepo = strepo;
		this.vtrepo = vrepo;
		this.rolerepo = rolerepo;

		this.pflowrepo = pflowrepo;
		this.pflowuserrepo = pflowuserrepo;
		this.phistoryrrepo = phistoryrrepo;
		this.buserrepo = buserrepo;
		this.suserrepo = suserrepo;
		this.reportrepo = reportrepo;
		this.mconstirepo = mconstirepo;
		this.mblockrepo = mblockrepo;
		this.mschemerepo = mschemerepo;
		this.myearrepo = myearrepo;
		this.cuserrepo = cuserrepo;
		// this.auditrepo=auditrepo;
	}

	// Roles & Users
	// ___________________________________________________________________________________
	public boolean hasRole(String role) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getAuthorities() == null)
			return false;

		String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;

		return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(roleName::equals);
	}

	public boolean hasAuthorityForStep(Users user, String stepCode) {
		ProcessFlow step = pflowrepo.findByStepCode(stepCode).orElse(null);
		if (step == null)
			return false;

		return pflowuserrepo.existsByUserAndProcessFlow(user, step);
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

		Users user = urepo.findByUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found: " + username);
		}
		return user;
	}

	public boolean isUser() {
		return hasRole("USER");
	}

	public boolean isAdmins() {
		return hasRole("ADMIN");
	}

	public boolean isSuperAdmin() {
		return hasRole("SUPER");
	}

	public Users findByUserName(String username) {
		return urepo.findByUserName(username);
	}

	public List<Users> findUsers() {
		if (isSuperAdmin()) {
			return urepo.findAll();
		} else if (hasRole("ADMIN")) {
			return urepo.findByDistrictAndUserNameNot(getDistrict(), "superadmin");

		} else {
			return urepo.findByDistrictAndUserNameNot(getDistrict(), "superadmin");
		}
	}

	public void saveUser(Users user) {
		try {
			if (user == null) {
				return;
			}
			String action = "Save | Update";
			String process = "User";
			String details = "Id:" + user.getUserId() + " | ProfileName:" + user.getProfileName() + " | UserName:"
					+ user.getUserName();
			String odetails = "Password Changed Date:" + user.getPwdChangedDate() + " | Email :" + user.getEmail()
					+ " | Enabled:" + user.isEnabled();
			urepo.save(user);
			auditservice.saveAudit(action, process, details, odetails);

		} catch (Exception e) {
			Notification.show("Failure :" + e);
			e.printStackTrace();
		}
	}

	public List<Users> getAllUsers() {
		return urepo.findAll();
	}

	public void saveRole(UsersRoles role) {
		try {
			if (role == null) {
				return;
			}
			String action = "Save | Update";
			String process = "Role";
			String details = "Id:" + role.getRoleId() + " | Role:" + role.getRoleName();
			String odetails = "";
			rolerepo.save(role); // Save or update the role
			auditservice.saveAudit(action, process, details, odetails);
		} catch (Exception e) {
			Notification.show("Unable to Save Role. Error: " + e, 5000, Position.TOP_CENTER)
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}

	public void deleteRole(UsersRoles role) {
		try {
			if (role != null) {
				String action = "Delete";
				String process = "Role";
				String details = "Id:" + role.getRoleId() + " | Role:" + role.getRoleName();
				String odetails = "";

				rolerepo.delete(role); // Save or update the role
				auditservice.saveAudit(action, process, details, odetails);
			}
		} catch (Exception e) {
			Notification.show("Unable to Save Role. Error: " + e, 5000, Position.TOP_CENTER)
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}

	// Constituency______________________________________________________________

	public List<MasterConstituency> getMasterConstituencies() {
		return mconstirepo.findByDistrict(getDistrict());
	}

	public List<Constituency> getConstituenciesByUser() {
		if (isSuperAdmin()) {
			return crepo.findAll();
		} else {
			return crepo.findConstituenciesByUser(getLoggedUser());
		}
	}

	public List<Constituency> getAllConstituenciesAndInUse() {
		return crepo.findByDistrictAndInUseOrderByConstituencyLabel(getDistrict(), true);
	}

	public List<Constituency> getAllConstituencies() {
		if (isSuperAdmin()) {
			return crepo.findAll();
		} else
			return crepo.findByDistrictOrderByConstituencyLabel(getDistrict());
	}

	public void saveConstituency(Constituency consti) {
		try {
			if (consti == null) {

				return;
			}
			crepo.save(consti);
			String action = "Save | Update";
			String process = "Constituency";
			String details = "Id:" + consti.getId() + " | Label:" + consti.getConstituencyLabel() + " | MLA:"
					+ consti.getConstituencyMLA();
			String odetails = "Master Id:" + consti.getMasterConstituency().getConstituencymasterId() + " | Name:"
					+ consti.getMasterConstituency().getConstituencyName();
			
			auditservice.saveAudit(action, process, details, odetails);

		} catch (Exception e) {
			Notification.show("Unable to Save Constituency. Error:" + e, 5000, Position.TOP_CENTER);
		}

	}

	public void deleteConstituency(Constituency consti) {
		try {
			String action = "Delete";
			String process = "Constituency";
			String details = "Id:" + consti.getId() + " | Label:" + consti.getConstituencyLabel() + " | MLA:"
					+ consti.getConstituencyMLA();
			String odetails = "Master Id:" + consti.getMasterConstituency().getConstituencymasterId() + " | Name:"
					+ consti.getMasterConstituency().getConstituencyName();
			crepo.delete(consti);
			auditservice.saveAudit(action, process, details, odetails);

		} catch (Exception e) {
			Notification.show("Unable to Delete Constituency " + e, 5000, Position.TOP_CENTER);
		}
	}

	// _____________________________________________________________________________
	public List<MasterBlock> getMasterBlocks() {
		return mblockrepo.findByDistrict(getDistrict());
	}

	public List<Block> getBlocksByUser() {
		if (isSuperAdmin()) {
			return brepo.findAll();
		} else
			return brepo.findBlocksByUser(getLoggedUser());

	}

	public List<Block> getAllBlocksAndInUse() {
		if (isSuperAdmin()) {
			return brepo.findAll();
		} else {
			return brepo.findByDistrictAndInUseOrderByBlockLabelAsc(getDistrict(), true);
		}

	}

	public List<Block> getAllBlocks() {
		if (isSuperAdmin()) {
			return brepo.findAll();
		} else {
			return brepo.findByDistrictOrderByBlockLabelAsc(getDistrict());
		}

	}

	public void saveBlock(Block block) {
		try {
			if (block == null) {

				return;
			}
			brepo.save(block);
			String action = "Save | Update";
			String process = "Block";
			String details = "Id:" + block.getId() + " | Label:" + block.getBlockLabel() + " | Office Head:"
					+ block.getBdoName();
			String odetails = "Master Id:" + block.getMasterBlock().getBlockMasterId() + " | Name:"
					+ block.getMasterBlock().getBlockName();
		
			auditservice.saveAudit(action, process, details, odetails);

		} catch (DataIntegrityViolationException e) {
			Notification.show("Unable to Save Block/MB as It already Exists" + e, 5000, Position.TOP_CENTER);
		}
	}

	public void deleteBlock(Block block) {
		try {
			if (block == null) {
				return;
			}
			String action = "Delete";
			String process = "Block";
			String details = "Id:" + block.getId() + " | Label:" + block.getBlockLabel() + " | Office Head:"
					+ block.getBdoName();
			String odetails = "Master Id:" + block.getMasterBlock().getBlockMasterId() + " | Name:"
					+ block.getMasterBlock().getBlockName();
			brepo.delete(block);
			auditservice.saveAudit(action, process, details, odetails);

		} catch (Exception e) {
			Notification.show("Unable to Delete Constituency " + e, 5000, Position.TOP_CENTER);
		}
	}
	// Schemes____________________________________________________________________________________

	public List<MasterScheme> getMasterSchemes() {
		return mschemerepo.findAll();
	}

	public List<Scheme> getSchemesByUser() {
		if (isSuperAdmin()) {
			return srepo.findAll();
		} else {
			return srepo.findSchemesByUser(getLoggedUser());
		}

	}

	public List<Scheme> getAllSchemesAndInUse() {
		if (isSuperAdmin()) {
			return srepo.findAll();
		} else {
			return srepo.findByDistrictAndInUse(getDistrict(), true);
		}

	}

	public List<Scheme> getAllSchemes() {
		if (isSuperAdmin()) {
			return srepo.findAll();
		} else {
			return srepo.findByDistrict(getDistrict());
		}

	}

	// save & Delete scheme
	public void saveScheme(Scheme scheme) {
		try {
			if (scheme == null) {
				return;
			}
			srepo.save(scheme);
			String action = "Save | Update";
			String process = "Scheme";
			String details = "Id:" + scheme.getId() + " | Label:" + scheme.getSchemeLabel() + " | Dept:"
					+ scheme.getSchemeDept() + " | Duration" + scheme.getSchemeDuration();
			String odetails = "Master Id:" + scheme.getMasterScheme().getSchemeMasterId() + " | Name:"
					+ scheme.getMasterScheme().getSchemeName();
			
			auditservice.saveAudit(action, process, details, odetails);

		} catch (Exception e) {
			Notification.show("Unable to Save Scheme " + e, 5000, Position.TOP_CENTER);
		}

	}

	public void deleteScheme(Scheme scheme) {
		try {
			if (scheme == null) {
				return;
			}
			String action = "Delete";
			String process = "Scheme";
			String details = "Id:" + scheme.getId() + " | Label:" + scheme.getSchemeLabel() + " | Dept:"
					+ scheme.getSchemeDept() + " | Duration" + scheme.getSchemeDuration();
			String odetails = "Master Id:" + scheme.getMasterScheme().getSchemeMasterId() + " | Name:"
					+ scheme.getMasterScheme().getSchemeName();
			srepo.delete(scheme);
			auditservice.saveAudit(action, process, details, odetails);

		} catch (Exception e) {
			Notification.show("Unable to Delete Constituency " + e, 5000, Position.TOP_CENTER);
		}

	}

	// Year______________________________________________________________________________________________
	public List<MasterYear> getMasterYears() {
		return myearrepo.findAll();
	}

	public List<Year> getAllYears() {
		if (isSuperAdmin()) {
			return yrepo.findAll();
		} else {
			return yrepo.findByDistrictAndInUseOrderByYearLabelDesc(getDistrict(), true);
		}

	}

	public List<Year> getAllYearsForAdmin() {
		if (isSuperAdmin()) {
			return yrepo.findAll();
		} else {
			return yrepo.findByDistrict(getDistrict());
		}

	}

	public List<Year> getAllYearsWIthNotInUse() {
		if (isSuperAdmin()) {
			return yrepo.findAll();
		} else {
			// return yrepo.findByDistrict(getDistrict());
			return yrepo.findByDistrictOrderByYearLabelDesc(getDistrict());
		}

	}

	public void saveYear(Year year) {
		if (year == null) {
			return;
		}
		yrepo.save(year);
		String action = "Save | Update";
		String process = "Year";
		String details = "Id:" + year.getId() + " | Label:" + year.getYearLabel();
		String odetails = "Master Id:" + year.getMasterYear().getYearId() + " | Name:"
				+ year.getMasterYear().getYearName();
		
		auditservice.saveAudit(action, process, details, odetails);

	}

	public void deleteYear(Year year) {
		try {
			String action = "Delete";
			String process = "Year";
			String details = "Id:" + year.getId() + " | Label:" + year.getYearLabel();
			String odetails = "Master Id:" + year.getMasterYear().getYearId() + " | Name:"
					+ year.getMasterYear().getYearName();
			yrepo.delete(year);
			auditservice.saveAudit(action, process, details, odetails);

		} catch (Exception e) {
			Notification.show("Unable to Delete Year " + e, 5000, Position.TOP_CENTER);
		}
	}

	// ________________________________________________________________________________________
	public List<Village> getVillage(Block block) {
		return vtrepo.findByMasterBlock(block.getMasterBlock());
		
	}

	public District getDistrict() {
		return drepo.findByDistrictId(getLoggedUser().getDistrict().getDistrictId());
	}

	public State getState(State state) {
		return strepo.findByStateId(state.getStateId());
	}

	public List<UsersRoles> getRoles() {
		return rolerepo.findByUser(getLoggedUser());
	}

	public List<UsersRoles> getRolesByUser(Users username) {
		return rolerepo.findByUser(username);
	}

	public List<String> fetchRolesForSelectedUser(Users user) {
		// Map the UsersRoles objects to a list of role names
		return getRolesByUser(user).stream().map(UsersRoles::getRoleName).collect(Collectors.toList());
	}

	// Installment________________________________________________________________________________________________________

	public int getInstallmentCount(Work work) {
		return irepo.countByWorkAndIsDeletedFalse(work);
	}
	public List<Installment> getAllInstallments() {
		return irepo.findAll();
	}
	public List<Installment> getInstallments(Work work) {
		return irepo.findByWorkAndIsDeletedFalse(work);
	}

	public List<Installment> getFilteredInstallments(Scheme scheme, Constituency consti, List<ProcessFlow> pflows,
			Block block, Year year, int inst) {
		return irepo.getFilteredInstallment(scheme, consti, pflows, block, getDistrict(), year, inst);
	}

	public Installment getInstallmentByWorkAndNo(int insallment, Work work) {
		return irepo.getInstallmentByNoAndWork(insallment, work);
	}

	public List<Installment> getInstallmentForReport(Scheme scheme, Year year, Constituency consti, Block block) {
		return irepo.getReportData(scheme, getDistrict(), year, consti, block);
	}

	public void saveInstallment(Installment install) {
		try {
			if (install == null) {
				return;
			}
			irepo.save(install);
			long id=install.getInstallmentId();
			String action = "Save | Update";
			String process = "Installment";
			String details = "Id:" + id + " | Inst No: " + install.getInstallmentNo()
					+ " | Work Code:" + install.getWork().getWorkCode();
			String odetails = "Amount:" + install.getInstallmentAmount() + " | Letter:"
					+ install.getInstallmentLetter();
			
			auditservice.saveAudit(action, process, details, odetails);
		} catch (Exception e) {
			Notification.show("Unable to Save Installment. Error:" + e, 5000, Position.TOP_CENTER);
		}
	}

	@Transactional
	public void markLatestInstallmentDeletedIfExists(Work work) {
		Optional<Installment> optionalInstallment = irepo.findTopByWorkAndIsDeletedFalseOrderByInstallmentNoDesc(work);
		if (optionalInstallment.isEmpty()) {
			return; // Nothing to delete
		}
		
		Installment last = optionalInstallment.get();
		irepo.save(last);
		long id=last.getInstallmentId();
		String action = "Soft Delete";
		String process = "Installment";
		String details = "Id:" + id + " | Inst No: " + last.getInstallmentNo() + " | Work Code:"
				+ last.getWork().getWorkCode();
		String odetails = "Amount:" + last.getInstallmentAmount() + " | Letter:" + last.getInstallmentLetter();
		last.setDeleted(true);
		
		auditservice.saveAudit(action, process, details, odetails);
	}
	
	//Copy To Notes of Release Order
	public void saveInstallmentReport(InstallmentReportNotes ipn) {
		if(ipn==null) {
			return;
		}
		try {
			reportrepo.save(ipn);
			//No need for Audit
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Works
	// Queries___________________________________________________________________________________________________
	public List<Work> getWorks() {
		if (isSuperAdmin()) {
			return wrepo.findAll();
		} else {
			return wrepo.findWorksByUser(getLoggedUser());
		}
	}
	public List<String> getWorkNamesList() {
		return wrepo.findWorkNamesList();
	}

	public List<String> getSanctionNos() {
		return wrepo.findSanctionNos();
	}
	public Work getWorkById(long id) {
		return wrepo.findById(id);
	}

	// Inbox
	public List<Work> getFilteredWorksByUser(Scheme scheme, Constituency consti, Block block, Year year) {
		try {
			return wrepo.getFilteredWorksByUser(getLoggedUser(), scheme, getDistrict(), year, consti, block);
		} catch (Exception e) {

			return Collections.emptyList();

		}
	}

	// History
	public List<Work> getFilteredWorksAndSearch(String searchTerm, Scheme scheme, Constituency consti, Block block,
			Year year) {
		try {
			return wrepo.findWorksEverProcessedByUserAndSearch(getLoggedUser(), scheme, getDistrict(), year, consti,
					block, searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	public List<Work> getWorkHistory() {
		try {
			return wrepo.findWorksByUserFromHistory(getLoggedUser());
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	public List<Work> getReportWorks(Scheme scheme, Constituency consti, Block block, Year year) {
		try {
			return wrepo.getReportWorks(scheme, getDistrict(), year, consti, block);
		} catch (Exception e) {

			return Collections.emptyList();

		}
	}

	public long getWorkCode() {
		return wrepo.findMaxWorkCode(getDistrict());
	}

	public long getWorkCountByConstituency(Constituency consti) {
		return wrepo.findByConstituencyAndIsDeletedFalseAndIsRecastedFalse(consti).size();
	}

	public long getWorkCountByScheme(Scheme scheme) {
		return wrepo.findBySchemeAndIsDeletedFalseAndIsRecastedFalse(scheme).size();
	}

	public void saveWork(Work work) {
		try {
			if (work == null) {
				return;
			}
			String action = "Save | Update";
			String process = "Work";
			wrepo.save(work);
			auditservice.saveAudit(work, process, action);
		} catch (Exception e) {

			Notification.show("Unable to Save Work. Error:" + e, 5000, Position.TOP_CENTER);
		}
	}

	public void deleteWork(Work work) {
		// irepo.deleteByWork(work);
		try {
			String action = "Soft Delete";
			String process = "Work";
			wrepo.save(work);
			auditservice.saveAudit(work, process, action);
			Notification.show("Deleted Successfully", 5000, Position.TOP_CENTER)
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
		} catch (Exception e) {
			Notification.show("Unable to Delete Work. Error:" + e, 5000, Position.TOP_CENTER);
		}
	}

//____________________________________________________________________________________________
	
	// save & Delete state
	public void saveState(State state) {
		try {
			if (state == null) {

				return;
			}
			strepo.save(state);
		} catch (Exception e) {
			Notification.show("Unable to Save State" + e, 5000, Position.TOP_CENTER);
		}
	}

	public void deleteState(State state) {
		try {
			strepo.delete(state);
		} catch (Exception e) {
			Notification.show("Unable to Delete Constituency " + e, 5000, Position.TOP_CENTER);
		}

	}

	// save & Delete district
	public void saveDistrict(District dist) {
		if (dist == null) {
			return;
		}
		drepo.save(dist);
	}

	public long getMaxDistrictCode(State state) {
		return drepo.findMaxDistrictCode(state);
	}

	

	public List<District> getAllDistricts(State state) {
		return drepo.findByState(state);
	}

	public List<District> getAllDistrictsOfAllStates() {
		return drepo.findAll();
	}

	public List<State> getAllStates() {
		return strepo.findAll();
	}

	//ProcessFlow & History___________________________________________________________________________________

	public ProcessFlow getStepByCode(String code) {
		return pflowrepo.findByStepCode(code)
				.orElseThrow(() -> new IllegalStateException("Missing ProcessFlow stepCode=" + code));
	}

	public List<ProcessFlow> getAllProcessFlow() {
		// return pflowrepo.findAll();
		return pflowrepo.findAllByOrderByIdAsc();
	}

	public ProcessFlow getProcessFlowByOrder(int a) {
		return pflowrepo.findByStepOrder(a);
	}

	public ProcessFlow getReturnToStepFromHistory(Work work) {
		ProcessFlow current = work.getProcessflow();

		return phistoryrrepo.findTopByWorkAndToStepAndReversedFalseOrderByEnteredOnDesc(work, current)
				.map(ProcessHistory::getFromStep)
				.orElseThrow(() -> new IllegalStateException("No previous forward step found from history."));
	}

	public ProcessFlow getPrevStepFromHistoryX(Work work) {
		ProcessFlow current = work.getProcessflow();

		return phistoryrrepo.findTopByWorkAndToStepAndReversedFalseOrderByEnteredOnDesc(work, current)
				.map(ProcessHistory::getFromStep).orElseThrow(
						() -> new IllegalStateException("No history found for move into " + current.getStepName()));
	}
	public ProcessFlow getPrevStepFromHistory(Work work) {
	    ProcessFlow current = work.getProcessflow();
	    if (current == null) {
	        throw new IllegalStateException("Work has no current process flow.");
	    }

	    if ("RELEASE_INSTALLMENT".equals(current.getStepCode())) {

	        boolean hasActiveInstallments = irepo.existsByWorkAndIsDeletedFalse(work);

	        if (!hasActiveInstallments) {
	            return pflowrepo.findByStepCode("WORK_ENTRY")
	                    .orElseThrow(() -> new IllegalStateException("ProcessFlow WORK_ENTRY not found"));
	        }

	        // ✅ for installment 2+ return the real previous step from history (e.g., UPLOAD_UC)
	        return phistoryrrepo
	                .findTopByWorkAndToStepAndReversedFalseOrderByEnteredOnDesc(work, current)
	                .map(ProcessHistory::getFromStep)
	                .orElseThrow(() ->
	                        new IllegalStateException("No history found for move into " + current.getStepName()));
	    }

	    return phistoryrrepo
	            .findTopByWorkAndToStepAndReversedFalseOrderByEnteredOnDesc(work, current)
	            .map(ProcessHistory::getFromStep)
	            .orElseThrow(() ->
	                    new IllegalStateException("No history found for move into " + current.getStepName()));
	}
	public void saveProcessHistory(ProcessHistory pfh) {
		if(pfh==null) {
			return;
		}
		
		try {
			phistoryrrepo.save(pfh);
			long id=pfh.getId();
			String action = "Save | Update";
			String process = "Process History";
			String details = "Id:" + id +" | Process:" + pfh.getProcessName();
			String odetails = "From:"+pfh.getFromStep().getStepName()+" | To:"+pfh.getToStep().getStepName();
			
			auditservice.saveAudit(action, process, details, odetails);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public List<ProcessHistory> getProcessHistory() {
		try {
			return phistoryrrepo.findByUser(getLoggedUser());
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	//ProsessFlowUser______________________________________________________________
	public List<ProcessFlowUser> getProcessFlowUser(Users user) {
		return pflowuserrepo.findByUser(user);
	}
	
	public void saveProcessFlowUser(ProcessFlowUser pfu) {
		if(pfu==null) {
			return;
		}
		
		try {
			pflowuserrepo.save(pfu);
			long id=pfu.getId();
			String action = "Save | Update";
			String process = "ProcessFlow User";
			String details = "Id:" + id + " | Process: " + pfu.getProcessFlow().getStepName();
			String odetails = "";
			
			auditservice.saveAudit(action, process, details, odetails);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void deleteProcessFlowUser(ProcessFlowUser pfu) {
		if(pfu==null) {
			return;
		}
		
		try {
			long id=pfu.getId();
			String action = "Delete";
			String process = "ProcessFlow User";
			String details = "Id:" + id + " | Process: " + pfu.getProcessFlow().getStepName();
			String odetails = "";
			pflowuserrepo.delete(pfu);
			auditservice.saveAudit(action, process, details, odetails);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public ProcessFlowUser getProcessFlowUser(Users user, ProcessFlow pfu) {
		return pflowuserrepo.findByUserAndProcessFlow(user, pfu);
	}

	public boolean hasAuthorityForStepR(Users user, int stepOrder) {
		return pflowuserrepo.existsByUserAndProcessFlow_StepOrder(user, stepOrder);
	}

	
	public ProcessFlow findReturnTarget(Work work) {
		ProcessHistory lastForward = phistoryrrepo.findTop1ByWorkAndReversedFalseOrderByEnteredOnDesc(work)
				.orElseThrow(() -> new IllegalStateException("No forward history found"));
		return lastForward.getFromStep(); // exact path taken
	}

	public boolean processHistoryExists(Work work, ProcessFlow processFlow, Users user) {
		return true;
	}

	public List<ProcessHistory> getProcessHistory(Work work) {
		try {
			return phistoryrrepo.findByWork(work);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	//BlockUser_____________________________________________________________________
	
	public BlockUser getBlockUser(Users user, Block block) {
		return buserrepo.findByUserAndBlock(user, block);
	}

	public List<BlockUser> getBlockUser(Users user) {
		return buserrepo.findByUser(user);
	}

	public void saveBlockUser(BlockUser bu) {
		
		if(bu==null) {
			return;
		}
		long id=bu.getId();
		try {
			String action = "Save | Update";
			String process = "Block User";
			String details = "Id:" + id + " | Block: " + bu.getBlock().getBlockLabel();
			String odetails = "";
			buserrepo.save(bu);
			auditservice.saveAudit(action, process, details, odetails);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteBlockUser(BlockUser bu) {
		if(bu==null) {
			return;
		}
		long id=bu.getId();
		try {
			String action = "Delete";
			String process = "Block User";
			String details = "Id:" + id + " | Block: " + bu.getBlock().getBlockLabel();
			String odetails = "";
			buserrepo.delete(bu);
			auditservice.saveAudit(action, process, details, odetails);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//_Constituency User_____________________________________________________________
	public ConstituencyUser getConstituencyUser(Users user, Constituency consti) {
		return cuserrepo.findByUserAndConstituency(user, consti);
	}

	public List<ConstituencyUser> getConstituencyUser(Users user) {
		
		return cuserrepo.findByUser(user);
	}

	public void saveConstituencyUser(ConstituencyUser bu) {
		if(bu==null) {
			return;
		}
		long id=bu.getId();
		try {
			String action = "Save | Update";
			String process = "Constituency User";
			String details = "Id:" + id + " | Constituency: " + bu.getConstituency().getConstituencyLabel();
			String odetails = "";
			cuserrepo.save(bu);
			auditservice.saveAudit(action, process, details, odetails);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void deleteConstituencyUser(ConstituencyUser su) {
		if(su==null) {
			return;
		}
		long id=su.getId();
		try {
			String action = "Delete";
			String process = "Constituency User";
			String details = "Id:" + id + " | Constituency: " + su.getConstituency().getConstituencyLabel();
			String odetails = "";
			cuserrepo.delete(su);
			auditservice.saveAudit(action, process, details, odetails);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//Scheme User____________________________________________________
	
	public SchemeUser getSchemeUser(Users user, Scheme sch) {
		return suserrepo.findByUserAndScheme(user, sch);
	}

	public List<SchemeUser> getSchemeUser(Users user) {
		return suserrepo.findByUser(user);
	}

	public void saveSchemeUser(SchemeUser bu) {
		if(bu==null) {
			return;
		}
		long id=bu.getId();
		try {
			String action = "Save | Update";
			String process = "Scheme User";
			String details = "Id:" + id + " | Constituency: " + bu.getScheme().getSchemeLabel();
			String odetails = "";
			suserrepo.save(bu);
			auditservice.saveAudit(action, process, details, odetails);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void deleteSchemeUser(SchemeUser bu) {
		if(bu==null) {
			return;
		}
		long id=bu.getId();
		try {
			String action = "Delete";
			String process = "Scheme User";
			String details = "Id:" + id + " | Constituency: " + bu.getScheme().getSchemeLabel();
			String odetails = "";
			suserrepo.delete(bu);
			auditservice.saveAudit(action, process, details, odetails);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

//______________________________________________________________________________	

	
}
