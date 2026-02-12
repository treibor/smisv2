package com.smis.dbservice;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.smis.security.SecurityService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;

import jakarta.transaction.Transactional;

@Service
public class Dbservice implements Serializable{
	/**
	 * 
	 */
	@Autowired
	SecurityService securityService;
	@Autowired
	private AuditRepository auditrepo; 
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
	
	//Notification Notification = new Notification();
	//@Autowired
	private final ProcessFlowRepo pflowrepo;
	private final ProcessFlowUserRepo pflowuserrepo;
	private final ProcessHistoryRepo phistoryrrepo;
	private final BlockUserRepo buserrepo;
	private final ConstituencyUserRepo cuserrepo;
	private final SchemeUserRepo suserrepo;
	//private final InstallmentDocRepository docrepo;
	private final InstallmentReportRepository reportrepo;
	private final MasterConstiRepo mconstirepo;
	private final MasterBlockRepo mblockrepo;
	private final MasterSchemeRepo mschemerepo;
	private final MasterYearRepo myearrepo;
	public Dbservice(StateRepository strepo, UserRepository urepo, WorkRepository workrepo, YearRepository yrepo,
			SchemeRepository srepo, ConstituencyRepository crepo, BlockRepository brepo, DistrictRepository drepo,InstallmentReportRepository reportrepo,
			InstallmentRepository irepo,  VillageRepository vrepo, RoleRepository rolerepo, 
			ProcessFlowRepo pflowrepo,ProcessFlowUserRepo pflowuserrepo,ProcessHistoryRepo phistoryrrepo,BlockUserRepo buserrepo,SchemeUserRepo suserrepo,
			MasterConstiRepo mconstirepo, MasterBlockRepo mblockrepo, MasterSchemeRepo mschemerepo,MasterYearRepo myearrepo,ConstituencyUserRepo cuserrepo) {
		this.wrepo = workrepo;
		this.yrepo = yrepo;
		this.srepo = srepo;
		this.crepo = crepo;
		this.brepo = brepo;
		this.drepo = drepo;
		this.irepo = irepo;
		//this.idrepo = idrepo;
		this.urepo = urepo;
		this.strepo = strepo;
		this.vtrepo = vrepo;
		this.rolerepo=rolerepo;
		
		this.pflowrepo=pflowrepo;
		this.pflowuserrepo=pflowuserrepo;
		this.phistoryrrepo=phistoryrrepo;
		this.buserrepo=buserrepo;
		this.suserrepo=suserrepo;
		this.reportrepo=reportrepo;
		this.mconstirepo=mconstirepo;
		this.mblockrepo=mblockrepo;
		this.mschemerepo=mschemerepo;
		this.myearrepo=myearrepo;
		this.cuserrepo=cuserrepo;
	}
	//Roles & Users
	//___________________________________________________________________________________
	public boolean hasRole(String role) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth == null || auth.getAuthorities() == null) return false;

	    String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;

	    return auth.getAuthorities().stream()
	            .map(GrantedAuthority::getAuthority)
	            .anyMatch(roleName::equals);
	}
	
	
	public boolean hasAuthorityForStep(Users user, String stepCode) {
	    ProcessFlow step = pflowrepo.findByStepCode(stepCode)
	            .orElse(null);
	    if (step == null) return false;

	    return pflowuserrepo.existsByUserAndProcessFlow(user, step);
	}
	public Users getLoggedUser() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth == null || !auth.isAuthenticated()) {
	        throw new IllegalStateException("No authenticated user");
	    }

	    String username = auth.getName();
	    Users user = urepo.findByUserName(username);

	    if (user == null) {
	        throw new UsernameNotFoundException("User not found: " + username);
	    }

	    return user;
	}
	//________________________________________________________________________
	
	// Constituency______________________________________________________________
	
	public List<MasterConstituency> getMasterConstituencies() {
		return mconstirepo.findByDistrict(getDistrict());
	}
	public List<Constituency> getAllConstituencies() {
		if (isSuperAdmin()) {
			return crepo.findAll();
		} else {
			return crepo.findConstituenciesByUserAndStatus(getLoggedUser(), true);
		}
	}
	
	public List<Constituency> getAllConstituenciesWIthNotInUse() {
		if (isSuperAdmin()) {
			return crepo.findAll();
		} else if (isAdmin()){
			return crepo.findByDistrictAndInUseOrderByConstituencyLabel(getDistrict(), true);
		}else {
			//return crepo.findByDistrict(getDistrict());
			return crepo.findConstituenciesByUserAndStatus(getLoggedUser(), true);
		}
	}
	
	public void saveConstituency(Constituency consti) {
		try {
			if (consti == null) {

				return;
			}
			crepo.save(consti);
		} catch (Exception e) {
			Notification.show("Unable to Save Constituency. Error:" + e, 5000, Position.TOP_CENTER);
		}

	}

	public void deleteConstituency(Constituency consti) {
		try {
			crepo.delete(consti);
		} catch (Exception e) {
			Notification.show("Unable to Delete Constituency " + e, 5000, Position.TOP_CENTER);
		}
	}
	

	//_____________________________________________________________________________
	public List<MasterBlock> getMasterBlocks() {
		return mblockrepo.findByDistrict(getDistrict());
	}
	public List<Block> getAllBlocks(boolean inUse) {
		if (isSuperAdmin()) {
			return brepo.findAll();
		} 
		else if (isAdmin()){
			if (inUse == true) {
				return brepo.findByDistrictAndInUseOrderByBlockLabelAsc(getDistrict(), inUse);
			} else {
				return brepo.findByDistrictOrderByBlockLabelAsc(getDistrict());
			}
		}
		else {
			return brepo.findBlocksByUserAndStatus(getLoggedUser(), inUse);
		}

	}
	//Schemes____________________________________________________________________________________
	
	public List<MasterScheme> getMasterSchemes() {
		return mschemerepo.findAll();
	}
	
	public List<Scheme> getAllSchemes() {
		if (isSuperAdmin()) {
			return srepo.findAll();
		} else if (hasRole("ADMIN")) {
			return srepo.findByDistrictAndInUse(getDistrict(), true);
		} else {
			return srepo.findSchemesByUserAndStatus(getLoggedUser(), true);
		}

	}

	public List<Scheme> getAllSchemesWIthNotInUse() {
		if (isSuperAdmin()) {
			return srepo.findAll();
		} else {
			return srepo.findByDistrict(getDistrict());
		}

	}
	
	//Year______________________________________________________________________________________________
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

	public List<Year> getAllYearsWIthNotInUse() {
		if (isSuperAdmin()) {
			return yrepo.findAll();
		} else {
			//return yrepo.findByDistrict(getDistrict());
			return yrepo.findByDistrictOrderByYearLabelDesc(getDistrict());
		}

	}
	
	//_______________________________________________________________________________________________________
	
	public void updateAudit(AuditTrail entity) {
		auditrepo.save(entity);
	}
	
	public List<AuditTrail> getAuditTrail() {
		return auditrepo.findAllByOrderByIdDesc();
	}
	// Development Phase only
	public List<Village> getVillage(Block block) {
		return vtrepo.findByBlock(block);
	}

	public District getDistrict() {
		return drepo.findByDistrictId(getLoggedUser().getDistrict().getDistrictId());
	}

	public State getState(State state) {
		return strepo.findByStateId(state.getStateId());
	}
	public List<UsersRoles> getRoles(){
		return rolerepo.findByUser(getLoggedUser());
	}
	public List<UsersRoles> getRolesByUser(Users username){
		return rolerepo.findByUser(username);
	}
	public List<String> fetchRolesForSelectedUser(Users user) {
	   	    // Map the UsersRoles objects to a list of role names
	    return getRolesByUser(user).stream()
	                    .map(UsersRoles::getRoleName)
	                    .collect(Collectors.toList());
	}
	
	public boolean isUser() {
		return hasRole("USER");
	}

	public boolean isAdmin() {
		return hasRole("ADMIN");
	}

	public boolean isSuperAdmin() { 
		return hasRole("SUPER");
	}
	 
	// Users
	public Users findUser(String username) {
		return urepo.findByUserName(username);
	}
	public List<Users> findUsers() {
		if(isSuperAdmin()) {
			return urepo.findAll();
		}else if(hasRole("ADMIN")){
			return urepo.findByDistrictAndUserNameNot(getDistrict(), "superadmin");
			
		}else {
			return urepo.findByDistrictAndUserNameNot(getDistrict(), "superadmin");
		}
	}
	public List<Users> findUsersByDistrictAndUserNameNot(District district, String username) {
		return urepo.findByDistrictAndUserNameNot(district, username);
	}

	/*
	 * public Users getLoggedUser() { String username = getloggeduser(); Users user
	 * = urepo.findByUserName(username); if (user == null) {
	 * securityService.logout(); } return user; }
	 */
	public Users getLoggedUserold() {//old method replaced by above
		Users loggeduser=urepo.findByUserName(getloggeduser());
		if(loggeduser!=null) {
			return urepo.findByUserName(getloggeduser());
		}else {
			securityService.logout();
			return null;
		}
	}
	public Users getUser(String user) {
		return urepo.findByUserName(user);
	}
	public String getloggeduser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
	}

	public void saveUser(Users user) {
		if (user == null) {
			Notification.show("Fail Fail Fail-7734");
			return;
		}
		urepo.save(user);

	}

	public long findMaxUserSerial() {

		try {

			return urepo.findMaxSerial();

		} catch (NullPointerException e) {

			return (long) 0;

		}
	}

	public List<Users> getAllUsers() {
		return urepo.findAll();
	}

	

	// Installment Service
	@Transactional
	public void markLatestInstallmentDeletedIfExists(Work work) {

	    Optional<Installment> optionalInstallment =
	            irepo.findTopByWorkAndIsDeletedFalseOrderByInstallmentNoDesc(work);

	    if (optionalInstallment.isEmpty()) {
	        return; // Nothing to delete
	    }

	    Installment last = optionalInstallment.get();

	    last.setDeleted(true);
	    irepo.save(last);
	}
	public int getInstallmentCount(Work work) {
		return irepo.countByWorkAndIsDeletedFalse(work);
	}
	
	public List<Installment> getInstallments(Work work) {
		return irepo.findByWorkAndIsDeletedFalse(work);
	}

	public List<Installment> getFilteredInstallments(Scheme scheme, Constituency consti, List<ProcessFlow> pflows,
			Block block, Year year, int installment) {

		return irepo.getFilteredInstallment(scheme, consti, pflows, block, getDistrict(), year, installment);
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
		} catch (Exception e) {
			Notification.show("Unable to Save Installment. Error:" + e, 5000, Position.TOP_CENTER);
		}
	}



	// Works Queries
	public List<Work> getWorks() {
		if (isSuperAdmin()) {
			return wrepo.findAll();
		}else {
			return wrepo.findWorksByUser(getLoggedUser());
		}
    }
	
	public Work getWorkById(long id) {
		return wrepo.findById(id);
	}
	
	
	
	
	public List<Work> getFilteredWorksAndSearch(String searchTerm) {
		try {
			return wrepo.findWorksByUserAndSearch(getLoggedUser(), getDistrict(),searchTerm);
		} catch (Exception e) {
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
	
	public List<Work> getFilteredWorksByUser(Scheme scheme, Constituency consti, Block block, Year year) {
		try {
			return wrepo.getFilteredWorksByUser(getLoggedUser(),scheme, getDistrict(), year, consti, block);
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
			wrepo.save(work);
		} catch (Exception e) {

			Notification.show("Unable to Save Work. Error:" + e, 5000, Position.TOP_CENTER);
		}
	}

	public void deleteWork(Work work) {
		// irepo.deleteByWork(work);
		try {
			wrepo.delete(work);
			Notification.show("Deleted Successfully", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		} catch (Exception e) {
			Notification.show("Unable to Delete Work. Error:" + e, 5000, Position.TOP_CENTER);
		}
	}

	// save & Delete Constituency
	

	// save & Delete Year
	public void saveYear(Year year) {
		if (year == null) {

			return;
		}
		yrepo.save(year);
	}

	public void deleteYear(Year year) {
		try {
			yrepo.delete(year);
		} catch (Exception e) {
			Notification.show("Unable to Delete Year " + e, 5000, Position.TOP_CENTER);
		}
	}

	// save & Delete scheme
	public void saveScheme(Scheme scheme) {
		try {
			if (scheme == null) {

				return;
			}
			srepo.save(scheme);
		} catch (Exception e) {
			Notification.show("Unable to Save Scheme " + e, 5000, Position.TOP_CENTER);
		}

	}

	public void deleteScheme(Scheme scheme) {
		try {
			srepo.delete(scheme);
		} catch (Exception e) {
			Notification.show("Unable to Delete Constituency " + e, 5000, Position.TOP_CENTER);
		}

	}

	// save & Delete blocks
	public void saveBlock(Block block) {
		try {
			if (block == null) {

				return;
			}
			brepo.save(block);
		} catch (DataIntegrityViolationException e) {
			Notification.show("Unable to Save Block/MB as It already Exists" + e, 5000, Position.TOP_CENTER);
		}
	}

	public void deleteBlock(Block block) {
		try {
			brepo.delete(block);
		} catch (Exception e) {
			Notification.show("Unable to Delete Constituency " + e, 5000, Position.TOP_CENTER);
		}
	}

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

	
	public List<String> getWorkNamesList(){
		return wrepo.findWorkNamesList();
	}
	public List<String> getSanctionNos(){
		return wrepo.findSanctionNos();
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

	public List<Installment> getAllInstallments() {
		return irepo.findAll();
	}

	
	public void saveRole(UsersRoles role) {
	    try {
	        if (role != null) {
	            rolerepo.save(role); // Save or update the role
	        }
	    } catch (Exception e) {
	        Notification.show("Unable to Save Role. Error: " + e, 5000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	    }
	}
	public void deleteRole(UsersRoles role) {
	    try {
	        if (role != null) {
	            rolerepo.delete(role); // Save or update the role
	        }
	    } catch (Exception e) {
	        Notification.show("Unable to Save Role. Error: " + e, 5000, Position.TOP_CENTER)
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	    }
	}
	public ProcessFlow getStepByCode(String code) {
	    return pflowrepo.findByStepCode(code)
	        .orElseThrow(() -> new IllegalStateException("Missing ProcessFlow stepCode=" + code));
	}
	public List<ProcessFlow> getAllProcessFlow() {
		//return pflowrepo.findAll();
		return pflowrepo.findAllByOrderByIdAsc();
	}
	public ProcessFlow getProcessFlowByOrder(int a) {
		return pflowrepo.findByStepOrder(a);
	}
	public ProcessFlow getReturnToStepFromHistory(Work work) {
		ProcessFlow current = work.getProcessflow();

	    return phistoryrrepo
	        .findTopByWorkAndToStepAndReversedFalseOrderByEnteredOnDesc(work, current)
	        .map(ProcessHistory::getFromStep)
	        .orElseThrow(() -> new IllegalStateException("No previous forward step found from history."));
	}
	public ProcessFlow getPrevStepFromHistory(Work work) {
	    ProcessFlow current = work.getProcessflow();

	    return phistoryrrepo
	        .findTopByWorkAndToStepAndReversedFalseOrderByEnteredOnDesc(work, current)
	        .map(ProcessHistory::getFromStep)
	        .orElseThrow(() -> new IllegalStateException(
	            "No history found for move into " + current.getStepName()
	        ));
	}
	public void saveProcessFlow(ProcessFlow processflow) {
		pflowrepo.save(processflow);
	}
	
	
	public List<ProcessFlowUser> getProcessFlowUser(Users user) {
		return pflowuserrepo.findByUser(user);
	}
	public void saveProcessFlowUser(ProcessFlowUser pfu) {
		pflowuserrepo.save(pfu);
	}

	public ProcessFlowUser getProcessFlowUser(Users user, ProcessFlow pfu) {
		return pflowuserrepo.findByUserAndProcessFlow(user, pfu);
	}

	public boolean hasAuthorityForStepR(Users user, int stepOrder) {
	    return pflowuserrepo.existsByUserAndProcessFlow_StepOrder(user, stepOrder);
	}
	public void deleteProcessFlowUser(ProcessFlowUser pfu) {
		pflowuserrepo.delete(pfu);
	}
	public BlockUser getBlockUser(Users user, Block block) {
		return buserrepo.findByUserAndBlock(user, block);
	}
	public List<BlockUser> getBlockUser(Users user) {
		return buserrepo.findByUser(user);
	}
	public void saveBlockUser(BlockUser bu) {
		buserrepo.save(bu);
	}
	public void deleteBlockUser(BlockUser su) {
		buserrepo.delete(su);
	}
	public ConstituencyUser getConstituencyUser(Users user, Constituency consti) {
		return cuserrepo.findByUserAndConstituency(user, consti);
	}
	public List<ConstituencyUser> getConstituencyUser(Users user) {
		return cuserrepo.findByUser(user);
	}
	public void saveConstituencyUser(ConstituencyUser bu) {
		cuserrepo.save(bu);
	}
	public void deleteConstituencyUser(ConstituencyUser su) {
		cuserrepo.delete(su);
	}
	public SchemeUser getSchemeUser(Users user, Scheme sch) {
		return suserrepo.findByUserAndScheme(user, sch);
	}
	public List<SchemeUser> getSchemeUser(Users user) {
		return suserrepo.findByUser(user);
	}
	public void saveSchemeUser(SchemeUser bu) {
		suserrepo.save(bu);
	}
	public void deleteSchemeUser(SchemeUser su) {
		suserrepo.delete(su);
	}
	public void saveProcessHistory(ProcessHistory pfh) {
		phistoryrrepo.save(pfh);
	}
	public List<ProcessHistory> getProcessHistory() {
		try {
			return phistoryrrepo.findByUser(getLoggedUser());
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	public ProcessFlow findReturnTarget(Work work) {
	    ProcessHistory lastForward = phistoryrrepo.findTop1ByWorkAndReversedFalseOrderByEnteredOnDesc(work)
	        .orElseThrow(() -> new IllegalStateException("No forward history found"));
	    return lastForward.getFromStep(); // exact path taken
	}
	
	public boolean processHistoryExists(Work work, ProcessFlow processFlow, Users user) {
	    //return phistoryrrepo.existsByWorkAndProcessFlowAndUser(work, processFlow, user);
		return true;
	}
	public List<ProcessHistory> getProcessHistory(Work work) {
		try {
			return phistoryrrepo.findByWork(work);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	public void saveInstallmentReport(InstallmentReportNotes ipn) {
		reportrepo.save(ipn);
	}
}
