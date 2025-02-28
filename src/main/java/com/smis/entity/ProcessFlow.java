package com.smis.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
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
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "process_generator")
    @SequenceGenerator(name = "process_generator", allocationSize = 1, sequenceName = "process_seq", initialValue = 1)
    private long processFlowId;

    private String stepName;
    private int stepOrder;

    @OneToMany(mappedBy = "processFlow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProcessFlowUser> assignedUsers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "nextStepId")
    private ProcessFlow nextStep;

	
    
    public long getProcessFlowId() {
		return processFlowId;
	}

	public void setProcessFlowId(long processFlowId) {
		this.processFlowId = processFlowId;
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
    
    
    
}