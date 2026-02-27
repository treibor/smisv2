package com.smis.dbservice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smis.repository.InstallmentRepository;
import com.smis.repository.WorkRepository;

@Service
public class DashboardService {
	@Autowired
	WorkRepository workrepo;
	@Autowired
	InstallmentRepository instrepo;
	
	
	
	public int getMonthData() {

	    YearMonth previousMonth = YearMonth.now().minusMonths(1);

	    LocalDateTime start = previousMonth
	            .atDay(1)
	            .atStartOfDay();

	    LocalDateTime endExclusive = previousMonth
	            .plusMonths(1)
	            .atDay(1)
	            .atStartOfDay();

	    return workrepo.getWorksCountBetweenDates(start, endExclusive);
	}
	public int getCurrentMonthData() {

	    YearMonth currentYearMonth = YearMonth.now();

	    LocalDateTime startDateTime = currentYearMonth
	            .atDay(1)
	            .atStartOfDay();

	    LocalDateTime endDateTime = currentYearMonth
	            .atEndOfMonth()
	            .atTime(23, 59, 59);

	    return workrepo.getWorksCountBetweenDates(startDateTime, endDateTime);
	}
}
