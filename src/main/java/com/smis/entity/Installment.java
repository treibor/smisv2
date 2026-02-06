package com.smis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

@Entity 
public class Installment implements Serializable {
	 private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,  generator= "inst_generator")
	@SequenceGenerator(name="inst_generator", allocationSize = 1, sequenceName = "inst_seq", initialValue = 1)
	private long installmentId;
	private int installmentNo;
	@Digits(integer=10, fraction=2)
	private BigDecimal installmentAmountPrev;
	@Digits(integer=10, fraction=2)
	private BigDecimal installmentAmount;
	private String installmentLetter;
	private String installmentCheque;
	private LocalDate installmentDate;
	private String installmentLabel;
	private String ucLetter;
	private LocalDate ucDate;
	
	private LocalDateTime enteredOn;
	@ManyToOne
	@JoinColumn(name="userId")
	@NotNull
	private Users enteredBy;
	
	
	
	@ManyToOne 
	@JoinColumn(name="reportNotes", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private InstallmentReportNotes reportNotes;
	
	
	@ManyToOne 
	@JoinColumn(name="workId", referencedColumnName = "workId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@NotNull
	private Work work;
	
	@Column(length = 500)
    private String releaseOrder;
	
	@Column(length = 500)
    private String ucDocument;

	public long getInstallmentId() {
		return installmentId;
	}

	public void setInstallmentId(long installmentId) {
		this.installmentId = installmentId;
	}

	public int getInstallmentNo() {
		return installmentNo;
	}

	public void setInstallmentNo(int installmentNo) {
		this.installmentNo = installmentNo;
	}

	public BigDecimal getInstallmentAmountPrev() {
		return installmentAmountPrev;
	}

	public void setInstallmentAmountPrev(BigDecimal installmentAmountPrev) {
		this.installmentAmountPrev = installmentAmountPrev;
	}

	public BigDecimal getInstallmentAmount() {
		return installmentAmount;
	}

	public void setInstallmentAmount(BigDecimal installmentAmount) {
		this.installmentAmount = installmentAmount;
	}

	public String getInstallmentLetter() {
		return installmentLetter;
	}

	public void setInstallmentLetter(String installmentLetter) {
		this.installmentLetter = installmentLetter;
	}

	public String getInstallmentCheque() {
		return installmentCheque;
	}

	public void setInstallmentCheque(String installmentCheque) {
		this.installmentCheque = installmentCheque;
	}

	public LocalDate getInstallmentDate() {
		return installmentDate;
	}

	public void setInstallmentDate(LocalDate installmentDate) {
		this.installmentDate = installmentDate;
	}

	public String getInstallmentLabel() {
		return installmentLabel;
	}

	public void setInstallmentLabel(String installmentLabel) {
		this.installmentLabel = installmentLabel;
	}

	public String getUcLetter() {
		return ucLetter;
	}

	public void setUcLetter(String ucLetter) {
		this.ucLetter = ucLetter;
	}

	public LocalDate getUcDate() {
		return ucDate;
	}

	public void setUcDate(LocalDate ucDate) {
		this.ucDate = ucDate;
	}

	public LocalDateTime getEnteredOn() {
		return enteredOn;
	}

	public void setEnteredOn(LocalDateTime enteredOn) {
		this.enteredOn = enteredOn;
	}

	public Users getEnteredBy() {
		return enteredBy;
	}

	public void setEnteredBy(Users enteredBy) {
		this.enteredBy = enteredBy;
	}

	public InstallmentReportNotes getReportNotes() {
		return reportNotes;
	}

	public void setReportNotes(InstallmentReportNotes reportNotes) {
		this.reportNotes = reportNotes;
	}

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	public String getReleaseOrder() {
		return releaseOrder;
	}

	public void setReleaseOrder(String releaseOrder) {
		this.releaseOrder = releaseOrder;
	}

	public String getUcDocument() {
		return ucDocument;
	}

	public void setUcDocument(String ucDocument) {
		this.ucDocument = ucDocument;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	


	
}
