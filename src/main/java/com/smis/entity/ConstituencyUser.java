package com.smis.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ConstituencyUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "constiuser_generator")
    @SequenceGenerator(name = "constiuser_generator", allocationSize = 1, sequenceName = "constiuser_seq", initialValue = 1)
    private long Id;
    
    @ManyToOne
    @JoinColumn(name = "constiId", nullable = false)
    private Constituency constituency;

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

	

	public Constituency getConstituency() {
		return constituency;
	}

	public void setConstituency(Constituency constituency) {
		this.constituency = constituency;
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
