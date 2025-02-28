package com.smis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.ProcessHistory;

public interface ProcessHistoryRepo extends JpaRepository<ProcessHistory, Long>{

}
