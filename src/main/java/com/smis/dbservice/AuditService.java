package com.smis.dbservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smis.entity.AuditTrail;
import com.smis.repository.AuditRepository;

@Service
public class AuditService {
	@Autowired
	private AuditRepository auditrepo; 
	
	
	public void updateAudit(AuditTrail entity) {
		auditrepo.save(entity);
	}
	
	public List<AuditTrail> getAuditTrail() {
		return auditrepo.findAllByOrderByIdDesc();
	}
	
}
