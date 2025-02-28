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
	
	public int getWorksCount() {
		return workrepo.getWorksCount();
	}
	
	public int getInstallmentCount() {
		return instrepo.getInstallmentCount();
	}
	public int getMonthData(){
		//List<Integer> yearData = new ArrayList<>();
		//int yeardata;
		//String month="";
		int year = Year.now().getValue();
		int currentMonth = YearMonth.now().getMonthValue();

        // Calculate the previous month
        int previousMonth = currentMonth - 1;

        // Adjust for wrap-around from January to December
        if (previousMonth == 0) {
            previousMonth = 12;
            year=year-1;
        }

        // Get the Month enum for the previous month
        Month monthEnum = Month.of(previousMonth);

        // Get the month name from the Month enum
        String monthName = monthEnum.name();

        // Format the month name to proper case (e.g., "January" instead of "JANUARY")
        monthName = monthName.charAt(0) + monthName.substring(1).toLowerCase();

        // Print the formatted month name
        //System.out.println("Previous month name: " + monthName);

        // Format the month number as a two-digit string with leading zero if necessary
        String month = (previousMonth < 10) ? "0" + previousMonth : String.valueOf(previousMonth);

        // Print the formatted month number
        //System.out.println("Formatted previous month number: " + formattedMonthNumber);
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate startdate = LocalDate.parse("01/"+month+"/" + year, df);
		LocalDate enddate = LocalDate.parse("31/"+month+"/" + year, df);
		int count = workrepo.getWorksCountBetweenDates(startdate, enddate);
		
		return count;
	}
	public int getCurrentMonthData(){
		//List<Integer> yearData = new ArrayList<>();
		//int yeardata;
		//String month="";
		int year = Year.now().getValue();
		int currentMonth = YearMonth.now().getMonthValue();

        

        // Get the Month enum for the previous month
        Month monthEnum = Month.of(currentMonth);

        // Get the month name from the Month enum
        String monthName = monthEnum.name();

        // Format the month name to proper case (e.g., "January" instead of "JANUARY")
        monthName = monthName.charAt(0) + monthName.substring(1).toLowerCase();

        // Print the formatted month name
        //System.out.println("Previous month name: " + monthName);

        // Format the month number as a two-digit string with leading zero if necessary
        String month = (currentMonth < 10) ? "0" + currentMonth : String.valueOf(currentMonth);

        // Print the formatted month number
        //System.out.println("Formatted previous month number: " + formattedMonthNumber);
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate startdate = LocalDate.parse("01/"+month+"/" + year, df);
		LocalDate enddate = LocalDate.parse("31/"+month+"/" + year, df);
		int count=workrepo.getWorksCountBetweenDates(startdate, enddate);
		return count;
	}
}
