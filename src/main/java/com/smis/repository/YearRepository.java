package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.District;
import com.smis.entity.Year;

public interface YearRepository extends JpaRepository<Year, Long>{

	
	List<Year> findByDistrictAndInUseOrderByYearNameAsc(District district, boolean inUse);
	List<Year> findByDistrictAndInUseOrderByYearNameDesc(District district, boolean inUse);
	List<Year> findByDistrict(District district);
	List<Year> findByDistrictOrderByYearNameDesc(District district);
}
