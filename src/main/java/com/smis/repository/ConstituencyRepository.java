package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.Constituency;
import com.smis.entity.master.District;

public interface ConstituencyRepository extends JpaRepository<Constituency, Long>{
	
	
	List<Constituency> findByDistrict(District district);
	List<Constituency> findByDistrictAndInUseOrderByMasterConstituency_ConstituencyNameAsc(
	        District district, boolean inUse);
	
	List<Constituency> findByDistrictAndInUseOrderByMasterConstituency_ConstituencyNoAsc(
	        District district, boolean inUse);
	
	
	
	
}