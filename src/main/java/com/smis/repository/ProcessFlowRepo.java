package com.smis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.ProcessFlow;
import java.util.List;


public interface ProcessFlowRepo extends JpaRepository<ProcessFlow, Long>{
	List<ProcessFlow> findAllByOrderByIdAsc();
	ProcessFlow findByStepOrder(int stepOrder);
}
