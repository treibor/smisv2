package com.smis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Installment;
import com.smis.entity.ProcessFlow;
import com.smis.entity.Scheme;
import com.smis.entity.Work;
import com.smis.entity.Year;
import com.smis.entity.master.District;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {
	List<Installment> findByWorkAndIsDeletedFalse(Work work);
	int countByWorkAndIsDeletedFalse(Work work);
	int countByIsDeletedFalse();
	Optional<Installment> findTopByWorkAndIsDeletedFalseOrderByInstallmentNoDesc(Work work);

	@Query("""
		    SELECT i
		    FROM Installment i
		    WHERE i.work.scheme = :scheme
		      AND i.work.isDeleted = false
		      AND i.work.constituency = :consti
		      AND i.work.processflow IN :processflows
		      AND i.work.block = :block
		      AND i.work.district = :district
		      AND i.work.year = :year
		      AND i.isDeleted = false
		      AND i.installmentNo = :installment
		      AND :installment = (
		            SELECT MAX(i2.installmentNo)
		            FROM Installment i2
		            WHERE i2.work = i.work
		              AND i2.isDeleted = false
		      )
		""")
		List<Installment> getFilteredInstallment(
		        @Param("scheme") Scheme scheme,
		        @Param("consti") Constituency consti,
		        @Param("processflows") List<ProcessFlow> processflows,
		        @Param("block") Block block,
		        @Param("district") District district,
		        @Param("year") Year year,
		        @Param("installment") int installment
		);
	

	@Query("select  c, d, e, g, h  from Installment c join c.work d join d.year e  join d.constituency g join d.district h  where  c.installmentNo=:installment and c.work=:work and c.isDeleted=false")
	Installment getInstallmentByNoAndWork(int installment, @Param("work") Work work);

	@Query("select  a, c, d, e, f, g from Installment a join a.work c join c.constituency d join c.scheme e join c.year f join c.block g  where  c.isDeleted=false and c.district=:district and (c.scheme=:scheme or :scheme is null ) and (c.year=:year or :year is null ) and (c.block=:block or :block is null ) and (c.constituency=:consti or :consti is null ) order by d.constituencyLabel, g.blockLabel, e.schemeLabel, f.yearLabel, c.workCode, a.installmentNo ASC")
	List<Installment> getReportData(@Param("scheme") Scheme scheme, @Param("district") District district,
			@Param("year") Year year, @Param("consti") Constituency consti, @Param("block") Block block);


	
}
