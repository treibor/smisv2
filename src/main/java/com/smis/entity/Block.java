package com.smis.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.smis.entity.master.District;
import com.smis.entity.master.MasterBlock;
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


public class Block implements Serializable{
	 private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "block_generator")
	@SequenceGenerator(name="block_generator", sequenceName = "block_seq", allocationSize=1)
	private long id;
	@NotEmpty
	private String blockLabel;
	@NotEmpty
	private String bdoName;
	private boolean inUse;
	@ManyToOne
	@JoinColumn(name="block_master_id")
	@NotNull(message = "Select Block")
	private MasterBlock masterBlock;
	@ManyToOne
	@JoinColumn(name="districtId")
	@NotNull
	private District district;
	@ManyToOne
	@JoinColumn(name = "updated_by")
	private Users updatedBy;
	private LocalDateTime updatedOn;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getBlockLabel() {
		return blockLabel;
	}
	public void setBlockLabel(String blockLabel) {
		this.blockLabel = blockLabel;
	}
	public String getBdoName() {
		return bdoName;
	}
	public void setBdoName(String bdoName) {
		this.bdoName = bdoName;
	}
	public boolean isInUse() {
		return inUse;
	}
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	public MasterBlock getMasterBlock() {
		return masterBlock;
	}
	public void setMasterBlock(MasterBlock masterBlock) {
		this.masterBlock = masterBlock;
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
