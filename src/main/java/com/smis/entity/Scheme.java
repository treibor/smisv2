package com.smis.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.smis.entity.master.District;
import com.smis.entity.master.MasterBlock;
import com.smis.entity.master.MasterScheme;

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
public class Scheme implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scheme_generator")
	@SequenceGenerator(name="scheme_generator", initialValue = 1, sequenceName = "scheme_sequence", allocationSize = 1)
	private long id;

	@NotEmpty(message = "Scheme full name is required")
	private String schemeNameLong;
	@NotEmpty(message = "Label is required")
	private String schemeLabel;
	@Digits(integer=12, fraction=2)
	private BigDecimal schemeAllocation;
	@NotEmpty(message = "Dept name is required")
	private String schemeDept;
	private String schemeDeptLong;
	private int schemeDuration;
	@ManyToOne
	@JoinColumn(name="districtId")
	@NotNull
	private District district;
	private int schemeReport;
	private boolean inUse;
	@ManyToOne
	@JoinColumn(name="scheme_master_id")
	@NotNull(message = "Please Select The Scheme")
	private MasterScheme masterScheme;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSchemeNameLong() {
		return schemeNameLong;
	}
	public void setSchemeNameLong(String schemeNameLong) {
		this.schemeNameLong = schemeNameLong;
	}
	public String getSchemeLabel() {
		return schemeLabel;
	}
	public void setSchemeLabel(String schemeLabel) {
		this.schemeLabel = schemeLabel;
	}
	public BigDecimal getSchemeAllocation() {
		return schemeAllocation;
	}
	public void setSchemeAllocation(BigDecimal schemeAllocation) {
		this.schemeAllocation = schemeAllocation;
	}
	public String getSchemeDept() {
		return schemeDept;
	}
	public void setSchemeDept(String schemeDept) {
		this.schemeDept = schemeDept;
	}
	public String getSchemeDeptLong() {
		return schemeDeptLong;
	}
	public void setSchemeDeptLong(String schemeDeptLong) {
		this.schemeDeptLong = schemeDeptLong;
	}
	public int getSchemeDuration() {
		return schemeDuration;
	}
	public void setSchemeDuration(int schemeDuration) {
		this.schemeDuration = schemeDuration;
	}
	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}
	public int getSchemeReport() {
		return schemeReport;
	}
	public void setSchemeReport(int schemeReport) {
		this.schemeReport = schemeReport;
	}
	public boolean isInUse() {
		return inUse;
	}
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	public MasterScheme getMasterScheme() {
		return masterScheme;
	}
	public void setMasterScheme(MasterScheme masterScheme) {
		this.masterScheme = masterScheme;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
}
