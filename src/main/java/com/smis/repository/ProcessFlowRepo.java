package com.smis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.ProcessFlow;
import com.smis.entity.Users;


public interface ProcessFlowRepo extends JpaRepository<ProcessFlow, Long>{
	List<ProcessFlow> findAllByOrderByIdAsc();
	ProcessFlow findByStepOrder(int stepOrder);
	Optional<ProcessFlow> findByStepCode(String stepCode);
	//boolean existsByUserAndProcessFlow(Users user, ProcessFlow processFlow);
}
