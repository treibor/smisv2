package com.smis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.smis.entity.InstallmentDocument;

import jakarta.transaction.Transactional;

public interface InstallmentDocRepository extends JpaRepository<InstallmentDocument, Long>{
	
	@Modifying
    @Transactional
    @Query("DELETE FROM InstallmentDocument d WHERE NOT EXISTS " +
           "(SELECT i FROM Installment i WHERE i.releaseOrder = d OR i.ucDocument = d)")
    void deleteUnreferencedInstallmentDocuments();

}
