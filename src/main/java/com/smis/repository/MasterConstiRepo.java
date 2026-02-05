package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.master.District;
import com.smis.entity.master.MasterConstituency;

public interface MasterConstiRepo extends JpaRepository<MasterConstituency, Long>{
	List<MasterConstituency> findAll();
	List<MasterConstituency> findByDistrict(District district);
}
