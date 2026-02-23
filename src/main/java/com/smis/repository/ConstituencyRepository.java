package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Users;
import com.smis.entity.master.District;

public interface ConstituencyRepository extends JpaRepository<Constituency, Long>{
	
	List<Constituency> findByDistrictOrderByConstituencyLabel(District district);
	List<Constituency> findByDistrictAndInUseOrderByConstituencyLabel(District district, boolean inUse);
	List<Constituency> findByDistrictAndInUseOrderByMasterConstituency_ConstituencyNameAsc(
	        District district, boolean inUse);
	
	List<Constituency> findByDistrictAndInUseOrderByMasterConstituency_ConstituencyNoAsc(
	        District district, boolean inUse);
	
	@Query("""
	        SELECT b
	        FROM Constituency b
	        WHERE b IN (
	              SELECT bu.constituency
	              FROM ConstituencyUser bu
	              WHERE bu.user = :user
	        )
	        ORDER BY b.constituencyLabel ASC
	       """)
	List<Constituency> findConstituenciesByUser(@Param("user") Users user);
	
	
}