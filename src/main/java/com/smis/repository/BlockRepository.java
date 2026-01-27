package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smis.entity.Block;
import com.smis.entity.District;
import com.smis.entity.Users;

public interface BlockRepository extends JpaRepository<Block, Long>{
	
	List<Block> findByDistrict(District district);
	List<Block> findByDistrictAndInUseOrderByBlockNameAsc(District district, boolean inUse);
	
	@Query("""
			SELECT b
			FROM Block b
			WHERE b.inUse = :inUse
			  AND b IN (
			      SELECT bu.block
			      FROM BlockUser bu
			      WHERE bu.user = :user
			  )
			""")
			List<Block> findBlocksByUserAndStatus(@Param("user") Users user,
			                                      @Param("inUse") boolean inUse);
	@Query("SELECT b FROM Block b " +
		       "WHERE b.inUse = :inUse " +
		       "AND b IN (SELECT bu.block FROM BlockUser bu WHERE bu.user = :user)")
		List<Block> findBlocksByUserAndStatus1(@Param("user") Users user, 
		                                      @Param("inUse") boolean inUse);
}
