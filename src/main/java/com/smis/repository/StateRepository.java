package com.smis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

//import com.smis.entity.District;
import com.smis.entity.State;

public interface StateRepository extends JpaRepository<State, Long>{

	//District findByDistrictId(long id);
	State findByStateId(long id);
}
