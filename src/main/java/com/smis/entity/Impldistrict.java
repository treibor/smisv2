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
public class Impldistrict {

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "impl_generator")
	@SequenceGenerator(name="impl_generator", sequenceName = "impl_seq", allocationSize=1)
	private long districtId;
	
	@NotEmpty
	private String districtName;
	private long districtCode;
	@NotEmpty
	private String districtHq;
	@NotEmpty
	private String deputyCommissioner;
	private String deputyCommissionerName;
	@ManyToOne
	@JoinColumn(name="stateId")
	@NotNull
	private State state;
	
	
	public Impldistrict() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public long getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(long districtCode) {
		this.districtCode = districtCode;
	}

	public long getDistrictId() {
		return districtId;
	}
	public void setDistrictId(long districtId) {
		this.districtId = districtId;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getDistrictHq() {
		return districtHq;
	}
	public void setDistrictHq(String districtHq) {
		this.districtHq = districtHq;
	}
	public String getDeputyCommissioner() {
		return deputyCommissioner;
	}
	public void setDeputyCommissioner(String deputyCommissioner) {
		this.deputyCommissioner = deputyCommissioner;
	}
	public String getDeputyCommissionerName() {
		return deputyCommissionerName;
	}
	public void setDeputyCommissionerName(String deputyCommissionerName) {
		this.deputyCommissionerName = deputyCommissionerName;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	
}
