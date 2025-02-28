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

@Entity
public class Village implements Serializable{
	 private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "village_generator")
	@SequenceGenerator(name="village_generator", sequenceName = "village_seq", allocationSize=1)
	private long villageId;
	
	@NotEmpty
	private String villageName;
	
	private String villageLabel;
	@ManyToOne
	@JoinColumn(name="blockId")
	private Block block;
	private boolean inUse;
	public long getVillageId() {
		return villageId;
	}
	public void setVillageId(long villageId) {
		this.villageId = villageId;
	}
	
	public String getVillageName() {
		return villageName;
	}
	public void setVillageName(String villageName) {
		this.villageName = villageName;
	}
	public String getVillageLabel() {
		return villageLabel;
	}
	public void setVillageLabel(String villageLabel) {
		this.villageLabel = villageLabel;
	}
	public boolean isInUse() {
		return inUse;
	}
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
}
