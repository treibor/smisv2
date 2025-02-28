package com.smis.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class Constituency implements Serializable{
	 private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consti_generator")
	@SequenceGenerator(name="consti_generator", sequenceName = "consti_seq", allocationSize=1)
	private long constituencyId;
	private int constituencyNo;
	
	@NotEmpty
	private String constituencyName;
	
	private String constituencyLabel;
	@NotEmpty
	private String constituencyMLA;
	@ManyToOne
	@JoinColumn(name="districtId")
	@NotNull
	private District district;
	private boolean inUse;
	
	public Constituency() {
		super();
		// TODO Auto-generated constructor stub
	}
	public long getConstituencyId() {
		return constituencyId;
	}
	public void setConstituencyId(long constituencyId) {
		this.constituencyId = constituencyId;
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
	public String getConstituencyLabel() {
		return constituencyLabel;
	}
	public void setConstituencyLabel(String constituencyLabel) {
		this.constituencyLabel = constituencyLabel;
	}
	public String getConstituencyMLA() {
		return constituencyMLA;
	}
	public void setConstituencyMLA(String constituencyMLA) {
		this.constituencyMLA = constituencyMLA;
	}
	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}
	
	
	public boolean isInUse() {
		return inUse;
	}
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	@Override
	public String toString() {
		return "Constituency [constituencyId=" + constituencyId + ", constituencyNo=" + constituencyNo
				+ ", constituencyName=" + constituencyName + ", constituencyLabel=" + constituencyLabel
				+ ", constituencyMLA=" + constituencyMLA + ", district=" + district + "]";
	}
	
	
	
}
