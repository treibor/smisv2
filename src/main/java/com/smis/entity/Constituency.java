package com.smis.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.smis.entity.master.District;
import com.smis.entity.master.MasterConstituency;

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
	private long id;
	@NotEmpty(message = "Constituency Label is required")
	private String constituencyLabel;
	@NotEmpty(message = "MLA name is required")
	private String constituencyMLA;
	@ManyToOne
	@JoinColumn(name="constituency_master_id")
	@NotNull(message = "Select Constituency")
	private MasterConstituency masterConstituency;
	private boolean inUse;
	@ManyToOne
	@JoinColumn(name="districtId")
	@NotNull
	private District district;
	@ManyToOne
	@JoinColumn(name = "updated_by")
	private Users updatedBy;
	private LocalDateTime updatedOn;
	public Users getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Users updatedBy) {
		this.updatedBy = updatedBy;
	}

	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Constituency() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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



	public MasterConstituency getMasterConstituency() {
		return masterConstituency;
	}

	public void setMasterConstituency(MasterConstituency masterConstituency) {
		this.masterConstituency = masterConstituency;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}
	

	
	
}
