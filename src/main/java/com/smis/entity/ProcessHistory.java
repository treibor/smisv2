package com.smis.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ProcessHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "work_id", nullable = false)
    private Work work; // The work item being processed

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // The user performing the action

    @ManyToOne
    @JoinColumn(name = "process_flow_id", nullable = false)
    private ProcessFlow processFlow; // The process step executed

    //private String action; // e.g., "Work Created", "Amount Released", "UC Entered"

    private LocalDateTime enteredOn; // When the action occurred

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public ProcessFlow getProcessFlow() {
		return processFlow;
	}

	public void setProcessFlow(ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}

	public LocalDateTime getEnteredOn() {
		return enteredOn;
	}

	public void setEnteredOn(LocalDateTime enteredOn) {
		this.enteredOn = enteredOn;
	}

    
}