package com.smis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class Workmp implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,  generator= "workmp_generator")
	@SequenceGenerator(name="workmp_generator", allocationSize = 1, sequenceName = "workmp_seq", initialValue = 1)
	private long workId;
	@Column(unique=false)
	private long workCode;
	@NotEmpty
	@Column(length=1000)
	private String workName;
	@Digits(integer=10, fraction=2)
	private BigDecimal workAmount;
	@NotNull
	private int priority;
	private int noOfInstallments;
	@NotEmpty
	private String sanctionNo;
	@NotNull
	private LocalDate sanctionDate;
	//private long workCode;
	private String workStatus;
	@NotEmpty
	private String workLocation;
	private String workLabel;
	@ManyToOne
	//@JoinColumn(name="districtId")
	@NotNull
	private Impldistrict implDistrict;
	
	private String implAgency;
	@NotEmpty
	private String implHead;
	@NotEmpty
	private String implAddress;
	@ManyToOne
	@JoinColumn(name="yearId")
	@NotNull
	private Year year;
	@ManyToOne
	@JoinColumn(name="constituencyId")
	@NotNull
	private Constituencymp constituencymp;
	@ManyToOne
	//@JoinColumn(name="districtId",insertable = false, updatable = false)
	@NotNull
	private District district;
	
	private String enteredBy;
	private LocalDate enteredOn;
	public long getWorkId() {
		return workId;
	}
	public void setWorkId(long workId) {
		this.workId = workId;
	}
	public long getWorkCode() {
		return workCode;
	}
	public void setWorkCode(long workCode) {
		this.workCode = workCode;
	}
	public String getWorkName() {
		return workName;
	}
	public void setWorkName(String workName) {
		this.workName = workName;
	}
	public BigDecimal getWorkAmount() {
		return workAmount;
	}
	public void setWorkAmount(BigDecimal workAmount) {
		this.workAmount = workAmount;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getNoOfInstallments() {
		return noOfInstallments;
	}
	public void setNoOfInstallments(int noOfInstallments) {
		this.noOfInstallments = noOfInstallments;
	}
	public String getSanctionNo() {
		return sanctionNo;
	}
	public void setSanctionNo(String sanctionNo) {
		this.sanctionNo = sanctionNo;
	}
	public LocalDate getSanctionDate() {
		return sanctionDate;
	}
	public void setSanctionDate(LocalDate sanctionDate) {
		this.sanctionDate = sanctionDate;
	}
	public String getWorkStatus() {
		return workStatus;
	}
	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}
	public String getWorkLocation() {
		return workLocation;
	}
	public void setWorkLocation(String workLocation) {
		this.workLocation = workLocation;
	}
	public String getWorkLabel() {
		return workLabel;
	}
	public void setWorkLabel(String workLabel) {
		this.workLabel = workLabel;
	}
	
	public Impldistrict getImplDistrict() {
		return implDistrict;
	}
	public void setImplDistrict(Impldistrict implDistrict) {
		this.implDistrict = implDistrict;
	}
	public String getImplAgency() {
		return implAgency;
	}
	public void setImplAgency(String implAgency) {
		this.implAgency = implAgency;
	}
	public String getImplHead() {
		return implHead;
	}
	public void setImplHead(String implHead) {
		this.implHead = implHead;
	}
	public String getImplAddress() {
		return implAddress;
	}
	public void setImplAddress(String implAddress) {
		this.implAddress = implAddress;
	}
	public Year getYear() {
		return year;
	}
	public void setYear(Year year) {
		this.year = year;
	}
	public Constituencymp getConstituencymp() {
		return constituencymp;
	}
	public void setConstituencymp(Constituencymp constituencymp) {
		this.constituencymp = constituencymp;
	}
	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}
	public String getEnteredBy() {
		return enteredBy;
	}
	public void setEnteredBy(String enteredBy) {
		this.enteredBy = enteredBy;
	}
	public LocalDate getEnteredOn() {
		return enteredOn;
	}
	public void setEnteredOn(LocalDate enteredOn) {
		this.enteredOn = enteredOn;
	}
	
	
	
	
}
