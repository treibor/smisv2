package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.Block;
import com.smis.entity.master.District;
import com.smis.entity.master.MasterBlock;
import com.smis.entity.master.Village;

public interface VillageRepository extends JpaRepository<Village, Long>{
	List<Village> findByMasterBlock(MasterBlock masterBlock);
}
