package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smis.entity.ProcessHistory;
import com.smis.entity.Users;
import com.smis.entity.Work;


public interface ProcessHistoryRepo extends JpaRepository<ProcessHistory, Long>{
	List<ProcessHistory> findByUser(Users user);
	List<ProcessHistory> findByWork(Work work);
	
	@Query("SELECT DISTINCT ph.work FROM ProcessHistory ph WHERE ph.user = :user")
	List<Work> findDistinctWorkByUser(@Param("user") Users user);

}
