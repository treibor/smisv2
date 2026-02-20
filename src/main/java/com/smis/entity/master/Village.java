package com.smis.entity.master;

import java.io.Serializable;

import com.smis.entity.Block;

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
public class Village implements Serializable{
	 private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "village_generator")
	@SequenceGenerator(name="village_generator", sequenceName = "village_seq", allocationSize=1)
	private long id;
	
	@NotEmpty
	private String villageName;
	
	private String villageLabel;
	@ManyToOne
	@JoinColumn(name="block_master_id")
	@NotNull
	private MasterBlock masterBlock;
	private boolean inUse;
	
	
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
