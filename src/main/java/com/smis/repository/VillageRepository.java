package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.Block;
import com.smis.entity.District;
import com.smis.entity.Village;

public interface VillageRepository extends JpaRepository<Village, Long>{
	List<Village> findByBlock(Block block);
}
