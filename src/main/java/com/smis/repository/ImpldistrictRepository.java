package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.Impldistrict;
import com.smis.entity.State;

public interface ImpldistrictRepository extends JpaRepository<Impldistrict, Long>{

	Impldistrict findByDistrictIdOrderByDistrictName(long id);
	Impldistrict findByDistrictCodeAndStateOrderByDistrictNameAsc(long code, State state);
	List <Impldistrict> findAllByOrderByDistrictName();
}
