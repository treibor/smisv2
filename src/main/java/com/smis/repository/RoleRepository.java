package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.Users;
import com.smis.entity.UsersRoles;

public interface RoleRepository extends JpaRepository<UsersRoles, Long>{
	List<UsersRoles> findByUser(Users user);
}
