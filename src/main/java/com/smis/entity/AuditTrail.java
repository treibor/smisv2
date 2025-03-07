package com.smis.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
@Entity
@Table(name="AuditTrail")
public class AuditTrail implements Serializable {
	 private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_generator")
	@SequenceGenerator(name="audit_generator", sequenceName = "audit_seq", allocationSize=1)
	private long id;
	private String action;
	private String ipAddress;
	 @ManyToOne
	    @JoinColumn(name = "userId", nullable = false)
	    private Users actionBy;
	private LocalDateTime actionOn;
	@Column(length = 1000)
	private String details;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public LocalDateTime getActionOn() {
		return actionOn;
	}
	public void setActionOn(LocalDateTime actionOn) {
		this.actionOn = actionOn;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public Users getActionBy() {
		return actionBy;
	}
	public void setActionBy(Users actionBy) {
		this.actionBy = actionBy;
	}
	
	
}
