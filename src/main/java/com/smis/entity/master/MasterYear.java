package com.smis.entity.master;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class MasterYear implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "yearmaster_generator")
	@SequenceGenerator(name = "yearmaster_generator", initialValue = 1, sequenceName = "yearmaster_seq", allocationSize = 1)
	private long yearId;
	@NotEmpty(message = "Year is required")
	private String yearName;
	private String remarks;
	public long getYearId() {
		return yearId;
	}
	public void setYearId(long yearId) {
		this.yearId = yearId;
	}
	public String getYearName() {
		return yearName;
	}
	public void setYearName(String yearName) {
		this.yearName = yearName;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
