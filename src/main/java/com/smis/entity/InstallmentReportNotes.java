package com.smis.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;


@Entity
public class InstallmentReportNotes implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inst_report_generator")
	@SequenceGenerator(name="inst_report_generator", sequenceName = "inst_report_seq", allocationSize=1)
	private long id;

	
	@Column(length=3000)
	private String copyTo;

    @OneToMany(mappedBy = "reportNotes", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Installment> copyTolist;
    
    

    @ManyToOne
	@JoinColumn(name="userId")
	@NotNull
	private Users updatedBy;
	private LocalDateTime updatedOn;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCopyTo() {
		return copyTo;
	}
	public void setCopyTo(String copyTo) {
		this.copyTo = copyTo;
	}
	
	public Users getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Users updatedBy) {
		this.updatedBy = updatedBy;
	}
	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}
	public List<Installment> getCopyTolist() {
		return copyTolist;
	}
	public void setCopyTolist(List<Installment> copyTolist) {
		this.copyTolist = copyTolist;
	}
	
	
	
	
}
