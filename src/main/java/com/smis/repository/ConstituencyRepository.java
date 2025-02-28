package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.Constituency;
import com.smis.entity.Constituencymp;
import com.smis.entity.District;
import com.smis.entity.Year;

public interface ConstituencyRepository extends JpaRepository<Constituency, Long>{
	
	List<Constituency> findByDistrict(District district);
	//List<Constituency> findByDistrictAndInUse(District district, boolean inUse);
	List<Constituency> findByDistrictAndInUseOrderByConstituencyNameAsc(District district, boolean inUse);
	List<Constituency> findByDistrictAndInUseOrderByConstituencyNoAsc(District district, boolean inUse);
}
