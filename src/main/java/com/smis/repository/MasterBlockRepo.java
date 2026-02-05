package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.master.District;
import com.smis.entity.master.MasterBlock;

public interface MasterBlockRepo extends JpaRepository<MasterBlock, Long> {
	List<MasterBlock> findAll();
	List<MasterBlock> findByDistrict(District district);
}
