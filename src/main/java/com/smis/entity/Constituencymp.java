package com.smis.entity;

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
public class Constituencymp {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "constimp_generator")
	@SequenceGenerator(name="constimp_generator", sequenceName = "constimp_seq", allocationSize=1)
	private long constituencyId;
	
	@NotEmpty
	private String constituencyName;
	
	@NotEmpty
	private String constituencyType;
	private String constituencyLabel;
	@NotEmpty
	private String constituencyMp;
	@NotEmpty
	private String address1;
	@NotEmpty
	private String address2;
	@NotEmpty
	private String address3;
	@ManyToOne
	@JoinColumn(name="districtId")
	@NotNull
	private District district;
	private int schemeDuration;
	private boolean inUse;
	public Constituencymp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getConstituencyId() {
		return constituencyId;
	}

	public void setConstituencyId(long constituencyId) {
		this.constituencyId = constituencyId;
	}

	public String getConstituencyName() {
		return constituencyName;
	}

	public void setConstituencyName(String constituencyName) {
		this.constituencyName = constituencyName;
	}

	public String getConstituencyType() {
		return constituencyType;
	}

	public void setConstituencyType(String constituencyType) {
		this.constituencyType = constituencyType;
	}

	public String getConstituencyLabel() {
		return constituencyLabel;
	}

	public void setConstituencyLabel(String constituencyLabel) {
		this.constituencyLabel = constituencyLabel;
	}

	public String getConstituencyMp() {
		return constituencyMp;
	}

	public void setConstituencyMp(String constituencyMp) {
		this.constituencyMp = constituencyMp;
	}

	

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public int getSchemeDuration() {
		return schemeDuration;
	}

	public void setSchemeDuration(int schemeDuration) {
		this.schemeDuration = schemeDuration;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	
	
	
}
