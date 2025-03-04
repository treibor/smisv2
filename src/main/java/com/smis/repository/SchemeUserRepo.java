package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.BlockUser;
import com.smis.entity.Scheme;
import com.smis.entity.SchemeUser;
import com.smis.entity.Users;



public interface SchemeUserRepo extends JpaRepository<SchemeUser, Long>{
	SchemeUser findByUserAndScheme(Users user, Scheme scheme);
	List<SchemeUser> findByUser(Users user);
}
