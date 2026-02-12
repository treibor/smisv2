package com.smis.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Scheme;
import com.smis.entity.Users;
import com.smis.entity.Work;
import com.smis.entity.Year;
import com.smis.entity.master.District;

public interface WorkRepository extends JpaRepository<Work, Long> {
	Work findById(long id);

	List<Work> findByConstituencyAndIsDeletedFalseAndIsRecastedFalse(Constituency constituency);

	List<Work> findBySchemeAndIsDeletedFalseAndIsRecastedFalse(Scheme scheme);

	@Query("select coalesce (Max(c.workCode),0) from Work c where c.district= :district")
	Long findMaxWorkCode(@Param("district") District district);

	@Query("SELECT Distinct(w.workName) FROM Work w")
	List<String> findWorkNamesList();

	@Query("SELECT Distinct(w.sanctionNo) FROM Work w")
	List<String> findSanctionNos();

	// Reports
	@Query("select  c, d, e, f, g, h from Work c join c.constituency d join c.block e join c.scheme f join c.year g join c.district h where  c.district=:district and c.isDeleted=false and c.isRecasted=false and (c.scheme=:scheme or :scheme is null) and (c.year=:year or :year is null) and (c.block=:block or :block is null ) and (c.constituency=:consti or :consti is null ) order by d.constituencyLabel, e.blockLabel, f.schemeLabel, g.yearLabel, c.workCode Desc")
	List<Work> getReportWorks(@Param("scheme") Scheme scheme, @Param("district") District district,
			@Param("year") Year year, @Param("consti") Constituency consti, @Param("block") Block block);

	// Dashboard
	@Query("select  count(*) from Work c  where  c.isDeleted=false and c.isRecasted=false and c.updatedOn between :sdate and :edate")
	int getWorksCountBetweenDates(@Param("sdate") LocalDateTime sdate, @Param("edate") LocalDateTime edate);

	@Query("select  count(*) from Work c where  c.isDeleted=false and c.isRecasted=false")
	int getWorksCount();

	// Find Works
	@Query("""
		    SELECT w
		    FROM Work w
		    WHERE w.isDeleted = false
		      AND w.isRecasted = false
		      AND w.processflow IN (
		            SELECT pfu.processFlow
		            FROM ProcessFlowUser pfu
		            WHERE pfu.user = :user
		      )
		      AND EXISTS (
		            SELECT 1 FROM BlockUser bu
		            WHERE bu.user = :user AND bu.block = w.block
		      )
		      AND EXISTS (
		            SELECT 1 FROM SchemeUser su
		            WHERE su.user = :user AND su.scheme = w.scheme
		      )
		      AND EXISTS (
		            SELECT 1 FROM ConstituencyUser cu
		            WHERE cu.user = :user AND cu.constituency = w.constituency
		      )
		    ORDER BY w.workCode DESC
		""")
		List<Work> findWorksByUser(@Param("user") Users user);
	
	// Works Search
	@Query("SELECT w FROM Work w where  w.isDeleted=false and w.isRecasted=false " + "AND w.processflow IN "
			+ "(SELECT pfu.processFlow FROM ProcessFlowUser pfu WHERE pfu.user = :user) "
			+ "AND w.district = :district "
			+ "AND EXISTS (SELECT 1 FROM BlockUser bu WHERE bu.user = :user AND bu.block = w.block) " + // Block check
			"AND EXISTS (SELECT 1 FROM SchemeUser su WHERE su.user = :user AND su.scheme = w.scheme) " + // Scheme check
			"AND EXISTS (SELECT 1 FROM ConstituencyUser cu WHERE cu.user = :user AND cu.constituency = w.constituency) "
			+ // Consti check
			"AND (CAST(w.workCode AS string) = :searchTerm "
			+ "     OR LOWER(w.workName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
			+ "     OR LOWER(w.sanctionNo) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " + "ORDER BY w.workCode DESC")
	List<Work> findWorksByUserAndSearch(@Param("user") Users user, @Param("district") District district,
			@Param("searchTerm") String searchTerm);

	@Query("""
			    SELECT w
			    FROM Work w
			    WHERE w.isDeleted=false and w.isRecasted=false and w.processflow IN (
			        SELECT pfu.processFlow
			        FROM ProcessFlowUser pfu
			        WHERE pfu.user = :user
			    )
			    AND w.district = :district
			    AND w.block IS NOT NULL

			    AND EXISTS (
			        SELECT 1
			        FROM BlockUser bu
			        WHERE bu.user = :user
			          AND bu.block = w.block
			    )
			    AND EXISTS (
			        SELECT 1
			        FROM SchemeUser su
			        WHERE su.user = :user
			          AND su.scheme = w.scheme
			    )
			    AND EXISTS (
			        SELECT 1
			        FROM ConstituencyUser cu
			        WHERE cu.user = :user
			          AND cu.constituency = w.constituency
			    )

			    AND (:scheme IS NULL OR w.scheme = :scheme)
			    AND (:year   IS NULL OR w.year = :year)
			    AND (:block  IS NULL OR w.block = :block)
			    AND (:consti IS NULL OR w.constituency = :consti)

			    ORDER BY w.workCode DESC
			""")
	List<Work> getFilteredWorksByUser(@Param("user") Users user, @Param("scheme") Scheme scheme,
			@Param("district") District district, @Param("year") Year year, @Param("consti") Constituency consti,
			@Param("block") Block block);

	@Query("SELECT DISTINCT ph.work FROM ProcessHistory ph " + "WHERE ph.user = :user "
			+ "ORDER BY ph.work.workCode DESC")
	List<Work> findWorksByUserFromHistory(@Param("user") Users user);
}
