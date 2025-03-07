package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smis.entity.District;
import com.smis.entity.Scheme;
import com.smis.entity.Users;

public interface SchemeRepository extends JpaRepository<Scheme, Long>{

	List<Scheme> findByDistrict(District district);
	List<Scheme> findByDistrictAndInUse(District district, boolean inUse);
	
	@Query("SELECT s FROM Scheme s " +
		       "WHERE s.inUse = :inUse " +
		       "AND s IN (SELECT su.scheme FROM SchemeUser su WHERE su.user = :user)")
		List<Scheme> findSchemesByUserAndStatus(@Param("user") Users user, 
		                                        @Param("inUse") boolean inUse);

}
