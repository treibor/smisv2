package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.Constituency;
import com.smis.entity.Constituencymp;
import com.smis.entity.District;
import com.smis.entity.Year;

public interface ConstituencympRepository extends JpaRepository<Constituencymp, Long>{
	
	List<Constituencymp> findByDistrict(District district);
	//List<Constituencymp> findByDistrictAndInUse(District district, boolean inUse);
	List<Constituencymp> findByDistrictAndInUseOrderByConstituencyTypeAsc(District district, boolean inUse);
}
