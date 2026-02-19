package com.smis.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.smis.entity.master.District;
import com.smis.entity.master.MasterYear;

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
public class Year implements Serializable{
	 private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "year_generator")
	@SequenceGenerator(name="year_master_generator", initialValue = 1, sequenceName = "yearmaster_sequence", allocationSize = 1)
	private long id;
	@NotEmpty(message = "Year Label is required")
	private String yearLabel;
	@ManyToOne
	@JoinColumn(name="districtId")
	@NotNull
	private District district;
	private boolean inUse;
	@ManyToOne
	@JoinColumn(name="year_master_id")
	@NotNull
	private MasterYear masterYear;
	private Users updatedBy;
	private LocalDateTime updatedOn;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getYearLabel() {
		return yearLabel;
	}
	public void setYearLabel(String yearLabel) {
		this.yearLabel = yearLabel;
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
	public MasterYear getMasterYear() {
		return masterYear;
	}
	public void setMasterYear(MasterYear masterYear) {
		this.masterYear = masterYear;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
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
	
	
	
}
