package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.ProcessFlow;
import com.smis.entity.Users;
import com.smis.entity.Work;


public interface ProcessFlowRepo extends JpaRepository<ProcessFlow, Long>{
	List<ProcessFlow> findAllByOrderByIdAsc();
	ProcessFlow findByStepOrder(int stepOrder);
	
	
}
