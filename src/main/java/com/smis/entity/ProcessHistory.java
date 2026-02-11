package com.smis.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ProcessHistory implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ph_generator")
	@SequenceGenerator(name = "ph_generator", allocationSize = 1, sequenceName = "ph_seq", initialValue = 1)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "work_id", nullable = false)
	private Work work; // The work item being processed

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private Users user; // The user performing the action

	@ManyToOne
	@JoinColumn(name = "from_step_id")
	private ProcessFlow fromStep;
	@ManyToOne
	@JoinColumn(name = "to_step_id")
	private ProcessFlow toStep;
	
	private String processName;
	private boolean reversed;
	private String remarks; 
	@Column(length = 500)
    private String document;
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
	public ProcessFlow getFromStep() {
		return fromStep;
	}
	public void setFromStep(ProcessFlow fromStep) {
		this.fromStep = fromStep;
	}
	public ProcessFlow getToStep() {
		return toStep;
	}
	public void setToStep(ProcessFlow toStep) {
		this.toStep = toStep;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public boolean isReversed() {
		return reversed;
	}
	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public LocalDateTime getEnteredOn() {
		return enteredOn;
	}
	public void setEnteredOn(LocalDateTime enteredOn) {
		this.enteredOn = enteredOn;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

	
	
}