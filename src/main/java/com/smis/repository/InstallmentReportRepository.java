package com.smis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.smis.entity.InstallmentReportNotes;

import jakarta.transaction.Transactional;

public interface InstallmentReportRepository extends JpaRepository<InstallmentReportNotes, Long>{
	@Modifying
    @Transactional
    @Query("DELETE FROM InstallmentReportNotes d WHERE NOT EXISTS " +
           "(SELECT i FROM Installment i WHERE i.reportNotes = d)")
    void deleteUnreferencedInstallmentReports();
}
