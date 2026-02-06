package com.smis.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class BlockUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "buser_generator")
    @SequenceGenerator(name = "buser_generator", allocationSize = 1, sequenceName = "buser_seq", initialValue = 1)
    private long Id;
    
    @ManyToOne
    @JoinColumn(name = "blockId", nullable = false)
    private Block block;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;

    private LocalDateTime assignedDate;
    @ManyToOne
    @JoinColumn(name = "assignedBy", nullable = false)
    private Users assignedBy;
    private String remarks;
    
	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public LocalDateTime getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(LocalDateTime assignedDate) {
		this.assignedDate = assignedDate;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Users getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(Users assignedBy) {
		this.assignedBy = assignedBy;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
    
    
    
	
    
    
    
}
