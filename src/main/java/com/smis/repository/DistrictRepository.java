package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smis.entity.District;
import com.smis.entity.State;

public interface DistrictRepository extends JpaRepository<District, Long>{
	
	@Query("select coalesce (Max(c.districtCode),0) from District c where c.state= :state")
	Long findMaxDistrictCode(@Param ("state") State state);
	
	District findByDistrictId(long id);
	List<District> findByState(State state);
}
