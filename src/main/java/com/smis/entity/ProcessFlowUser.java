package com.smis.entity;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ProcessFlowUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "pfu_generator")
    @SequenceGenerator(name = "pfu_generator", allocationSize = 1, sequenceName = "pfu_seq", initialValue = 1)
    private long Id;

    @ManyToOne
    @JoinColumn(name = "processFlowId", nullable = false)
    private ProcessFlow processFlow;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;

    private LocalDate assignedDate;

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public ProcessFlow getProcessFlow() {
		return processFlow;
	}

	public void setProcessFlow(ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public LocalDate getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(LocalDate assignedDate) {
		this.assignedDate = assignedDate;
	}

    
    
    
	
    
    
    
}
