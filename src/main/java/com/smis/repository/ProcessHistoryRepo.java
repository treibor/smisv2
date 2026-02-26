package com.smis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smis.entity.ProcessFlow;
import com.smis.entity.ProcessHistory;
import com.smis.entity.Users;
import com.smis.entity.Work;


public interface ProcessHistoryRepo extends JpaRepository<ProcessHistory, Long> {
	List<ProcessHistory> findByUser(Users user);

	List<ProcessHistory> findByWork(Work work);

	Optional<ProcessHistory> findTop1ByWorkAndReversedFalseOrderByEnteredOnDesc(Work work);

	@Query("SELECT DISTINCT ph.work FROM ProcessHistory ph WHERE ph.user = :user")
	List<Work> findDistinctWorkByUser(@Param("user") Users user);

	// boolean existsByWorkAndProcessFlowAndUser(Work work, ProcessFlow processFlow,
	// Users user);
	boolean existsByWorkAndFromStepAndUser(Work work, ProcessFlow fromStep, Users user);

	Optional<ProcessHistory> findTopByWorkAndToStepAndReversedFalseOrderByEnteredOnDesc(Work work, ProcessFlow toStep);

	@Query("""
			  SELECT ph
			  FROM ProcessHistory ph
			  WHERE ph.work = :work
			    AND ph.toStep = :toStep
			  ORDER BY ph.enteredOn DESC
			""")
	List<ProcessHistory> findLatestMoveIntoStep(@Param("work") Work work, @Param("toStep") ProcessFlow toStep);
	
	ProcessHistory findTopByWorkOrderByEnteredOnDesc(Work work);
	//ProcessHistory findTopByWorkAndFromStepIsNotNullOrderByEnteredOnDesc(Work work);
}
