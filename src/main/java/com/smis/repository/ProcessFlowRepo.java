package com.smis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.ProcessFlow;
import java.util.List;


public interface ProcessFlowRepo extends JpaRepository<ProcessFlow, Long>{
	ProcessFlow findByStepOrder(int stepOrder);
}
