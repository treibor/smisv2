package com.smis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smis.entity.Block;
import com.smis.entity.Constituency;
import com.smis.entity.Constituencymp;
import com.smis.entity.District;
import com.smis.entity.Impldistrict;
import com.smis.entity.Installment;
import com.smis.entity.Installmentmp;
import com.smis.entity.Scheme;
//import com.smis.entity.Work;
import com.smis.entity.Workmp;
import com.smis.entity.Year;

public interface InstallmentmpRepository extends JpaRepository<Installmentmp, Long>{
	List<Installmentmp> findByWorkmp(Workmp workmp);
	List<Installmentmp> findByWorkmpAndInstallmentNo(Workmp work, int inst_no);
	List<Installmentmp> deleteByWorkmp(Workmp workmp);
	int countByWorkmp(Workmp workmp);
	
	
	
	@Query("select  c, d, e, g, h  from Installmentmp c join c.workmp d join d.year e  join d.constituencymp g join d.district h  where d.constituencymp=:consti and d.district=:district   and  d.year=:year and c.installmentNo=:installment and d.implDistrict=:impldistrict order by d.workCode ASC")
	List<Installmentmp> getFilteredInstallment( @Param("consti") Constituencymp consti,    @Param ("district") District district, @Param ("year") Year year, @Param ("installment") int installment,  @Param ("impldistrict") Impldistrict impldistrict);
	
	
	@Query("select  c, d, e, g, h  from Installmentmp c join c.workmp d join d.year e  join d.constituencymp g join d.district h  where  c.installmentNo=:installment and c.workmp=:workmp")
	Installmentmp getInstallmentByNoAndWork( int installment,  @Param ("workmp") Workmp workmp);
	
	/*
	@Query("select  c from Installmentmp c join c.workmp d")
	List<Installmentmp> getFilteredInstallment( @Param("consti") Constituencymp consti,    @Param ("district") District district, @Param ("year") Year year, @Param ("installment") int installment,  @Param ("impldistrict") District impldistrict);
	*/
	@Query("select  a, c,  e, f, g, h from Installmentmp a join a.workmp c join c.constituencymp d join c.implDistrict e join c.year f join c.district g join g.state h   where  c.district=:district  and (c.year=:year or :year is null or :year=0)  and (c.constituencymp=:consti or :consti is null or :consti=0) and (c.implDistrict=:impldistrict or :impldistrict is null or :consti=0) order by d.constituencyName, f.yearName, c.workCode, a.installmentNo ASC")
	List<Installmentmp> getReportData(@Param("district") District district, @Param("year") Year year,@Param("consti") Constituencymp consti, @Param("impldistrict")Impldistrict impldistrict);
	
}
