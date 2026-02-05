package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smis.entity.Users;
import com.smis.entity.master.District;

public interface UserRepository extends JpaRepository<Users, Long>{

	Users findByUserName(String username);
	Users findByUserNameAndEnabled(String username, boolean enabled);
	//List<Users> findAll;
	List<Users> findByDistrict(District district);
	List <Users> findByDistrictAndUserNameNot(District dist, String username);
	
	@Query("select Max(c.userId) from Users c ")
	Long findMaxSerial();
}
