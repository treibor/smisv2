package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.Constituency;
import com.smis.entity.ConstituencyUser;
import com.smis.entity.Users;

public interface ConstituencyUserRepo extends JpaRepository<ConstituencyUser, Long> {
	ConstituencyUser findByUserAndConstituency(Users user, Constituency consti);
	List<ConstituencyUser> findByUser(Users user);
}
