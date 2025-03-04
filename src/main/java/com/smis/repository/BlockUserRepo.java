package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smis.entity.Block;
import com.smis.entity.BlockUser;
import com.smis.entity.Users;



public interface BlockUserRepo extends JpaRepository<BlockUser, Long>{
	BlockUser findByUserAndBlock(Users user, Block block);
	List<BlockUser> findByUser(Users user);
}
