package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.AuditTrail;


public interface AuditRepository extends JpaRepository<AuditTrail, Long> {
	List<AuditTrail> findAllByOrderByIdDesc();
}
