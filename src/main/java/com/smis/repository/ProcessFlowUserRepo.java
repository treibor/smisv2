package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.ProcessFlow;
import com.smis.entity.ProcessFlowUser;
import com.smis.entity.Users;


public interface ProcessFlowUserRepo extends JpaRepository<ProcessFlowUser, Long>{
	List<ProcessFlowUser> findByUser(Users user);
	ProcessFlowUser findByUserAndProcessFlow(Users user, ProcessFlow pf);
	boolean existsByUserAndProcessFlow_StepOrder(Users user, int stepOrder);
	boolean existsByUserAndProcessFlow(Users user, ProcessFlow processFlow);
	
}
