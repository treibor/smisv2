package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.Constituency;
import com.smis.entity.District;
import com.smis.entity.Scheme;

public interface SchemeRepository extends JpaRepository<Scheme, Long>{

	List<Scheme> findByDistrict(District district);
	List<Scheme> findByDistrictAndInUse(District district, boolean inUse);
}
