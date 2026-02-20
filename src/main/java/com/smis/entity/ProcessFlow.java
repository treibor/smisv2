package com.smis.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ProcessFlow implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "p_flow_generator")
    @SequenceGenerator(name = "p_flow_generator", allocationSize = 1, sequenceName = "p_flow_seq", initialValue = 1)
    private long id;

    private String stepName;
    private int stepOrder;
    
    @OneToMany(mappedBy = "processFlow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProcessFlowUser> assignedUsers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "nextStepId")
    private ProcessFlow nextStep;

    @Column(name="step_code", nullable=false, unique=true)
    private String stepCode;

   
	
    
   


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public int getStepOrder() {
		return stepOrder;
	}

	public void setStepOrder(int stepOrder) {
		this.stepOrder = stepOrder;
	}

	public List<ProcessFlowUser> getAssignedUsers() {
		return assignedUsers;
	}

	public void setAssignedUsers(List<ProcessFlowUser> assignedUsers) {
		this.assignedUsers = assignedUsers;
	}

	public ProcessFlow getNextStep() {
		return nextStep;
	}

	public void setNextStep(ProcessFlow nextStep) {
		this.nextStep = nextStep;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getStepCode() {
		return stepCode;
	}

	public void setStepCode(String stepCode) {
		this.stepCode = stepCode;
	}

	

	
    
    
}