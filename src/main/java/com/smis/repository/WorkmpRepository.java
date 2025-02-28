package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Constituencymp;
import com.smis.entity.District;
import com.smis.entity.Impldistrict;
import com.smis.entity.Scheme;
import com.smis.entity.Work;
import com.smis.entity.Workmp;
import com.smis.entity.Year;


@Repository
public interface WorkmpRepository extends JpaRepository<Workmp, Long> {
	
	List<Workmp> findByDistrictOrderByWorkCodeDesc(District district);
	
	@Query("select coalesce (Max(c.workCode),0) from Workmp c where c.district= :district")
	Long findMaxWorkCode(@Param ("district") District district);
	
	/*
	@Query("select  c, d, e,f,g, h, i  from Workmp c join c.installment d join c.year e  join c.scheme f join c.constituency g join c.district h join c.block i where c.block=:block and c.district=:district and c.scheme=:scheme  and c.constituency=:consti and c.scheme=:scheme and c.year=:year and d.installmentNo=:installment order by c.workCode ASC")
	List<Work> getFilteredWorks(@Param("scheme") Scheme scheme, @Param("consti") Constituency consti,  @Param ("block") Block block ,  @Param ("district") District district, @Param ("year") Year year, @Param ("installment") int installment);
	*/
	@Query("select  c from Workmp c where  c.district=:district  and c.year=:year ")
	List<Workmp> getWorksForCalculation(@Param("district") District district, @Param("year") Year year);
	
	
	@Query("select  c from Workmp c where  c.district=:district and (c.implDistrict=:impldistrict or :impldistrict is null or :impldistrict=0) and (c.year=:year or :year is null or :year=0) and  (c.constituencymp=:consti or :consti is null or :consti=0) order by c.workCode Desc")
	List<Workmp> getFilteredWorks( @Param("district") District district,@Param("impldistrict") Impldistrict impldistrict, @Param("year") Year year,@Param("consti") Constituencymp consti);
	
	
	@Query("select  c, d, e, f from Workmp c join c.constituencymp  d join c.implDistrict e join c.year f where  c.district=:district and (c.implDistrict=:impldistrict or :impldistrict is null or :impldistrict=0) and (c.year=:year or :year is null or :year=0) and  (c.constituencymp=:consti or :consti is null or :consti=0) order by d.constituencyName, e.districtName, f.yearName, c.workCode Asc")
	List<Workmp> getFilteredWorksForReport( @Param("district") District district,@Param("impldistrict") Impldistrict impldistrict, @Param("year") Year year,@Param("consti") Constituencymp consti);
	
	String a1="select c from Workmp c where c.district= :district and(";
	String a2="lower(c.workName) like lower(concat('%', :searchTerm, '%')) ";
	String a3="or lower(c.sanctionNo) like lower(concat('%', :searchTerm, '%'))) order by c.workCode";
	@Query(a1+a2+a3)
	List<Workmp> search(@Param("searchTerm") String searchTerm, @Param("district") District district);
}
