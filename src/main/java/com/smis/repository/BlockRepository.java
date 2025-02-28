package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.Block;
import com.smis.entity.District;
import com.smis.entity.Year;

public interface BlockRepository extends JpaRepository<Block, Long>{
	
	List<Block> findByDistrict(District district);
	List<Block> findByDistrictAndInUseOrderByBlockNameAsc(District district, boolean inUse);
}
