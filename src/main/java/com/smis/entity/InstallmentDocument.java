package com.smis.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
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
public class InstallmentDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inst_doc_generator")
	@SequenceGenerator(name="inst_doc_generator", sequenceName = "inst_doc_seq", allocationSize=1)
	private long id;

   
	private byte[] document;

    @OneToMany(mappedBy = "releaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Installment> releaseorderlist;
    
    @OneToMany(mappedBy = "ucDocument", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Installment> ucDocslist;

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

	

	public List<Installment> getReleaseorderlist() {
		return releaseorderlist;
	}

	public void setReleaseorderlist(List<Installment> releaseorderlist) {
		this.releaseorderlist = releaseorderlist;
	}

	public List<Installment> getUcDocslist() {
		return ucDocslist;
	}

	public void setUcDocslist(List<Installment> ucDocslist) {
		this.ucDocslist = ucDocslist;
	}

	public byte[] getDocument() {
		return document;
	}

	public void setDocument(byte[] document) {
		this.document = document;
	} 
    
    
    
}

