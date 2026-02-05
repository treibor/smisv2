package com.smis.entity.master;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity

public class MasterConstituency implements Serializable{
	 private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "constimaster_generator")
	@SequenceGenerator(name="constimaster_generator", sequenceName = "constimaster_seq", allocationSize=1)
	private long constituencymasterId;
	@Min(value = 1, message = "Constituency number must be at least 1")
	private int constituencyNo;
	@NotEmpty(message = "Constituency name is required")
	private String constituencyName;
	@ManyToOne
	@JoinColumn(name="districtId")
	@NotNull
	private District district;
	
	
	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}
	public long getConstituencymasterId() {
		return constituencymasterId;
	}
	public void setConstituencymasterId(long constituencymasterId) {
		this.constituencymasterId = constituencymasterId;
	}
	public int getConstituencyNo() {
		return constituencyNo;
	}
	public void setConstituencyNo(int constituencyNo) {
		this.constituencyNo = constituencyNo;
	}
	public String getConstituencyName() {
		return constituencyName;
	}
	public void setConstituencyName(String constituencyName) {
		this.constituencyName = constituencyName;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
