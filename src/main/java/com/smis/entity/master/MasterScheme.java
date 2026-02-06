package com.smis.entity.master;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class MasterScheme implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "schememaster_generator")
	@SequenceGenerator(name="schememaster_generator", initialValue = 1, sequenceName = "schememaster_seq", allocationSize = 1)
	private long schemeMasterId;
	@NotEmpty(message = "Scheme code is required")
	private String schemeCode;
	
	@NotEmpty(message = "Scheme name is required")
	private String schemeName;

	public long getSchemeMasterId() {
		return schemeMasterId;
	}

	public void setSchemeMasterId(long schemeMasterId) {
		this.schemeMasterId = schemeMasterId;
	}

	public String getSchemeCode() {
		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}

	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	
}
