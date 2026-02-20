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


public class MasterBlock implements Serializable{
	 private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blockmaster_generator")
	@SequenceGenerator(name="blockmaster_generator", sequenceName = "blockmaster_seq", allocationSize=1)
	private long blockMasterId;
	@NotEmpty
	private String blockName;
	@ManyToOne
	@JoinColumn(name="districtId")
	@NotNull
	private District district;
	
	
	public long getBlockMasterId() {
		return blockMasterId;
	}
	public void setBlockMasterId(long blockMasterId) {
		this.blockMasterId = blockMasterId;
	}
	
	public String getBlockName() {
		return blockName;
	}
	public void setBlockName(String blockName) {
		this.blockName = blockName;
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
