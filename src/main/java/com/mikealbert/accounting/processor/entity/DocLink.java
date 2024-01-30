package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the DOC_LINKS database table.
 * 
 */
@Entity
@Table(name="DOC_LINKS")
public class DocLink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DocLinkPK id;

    @JoinColumn(name = "PARENT_DOC_ID", referencedColumnName = "DOC_ID", insertable=false, updatable=false)	
    @ManyToOne
	private Doc parentDoc;
    
    @JoinColumn(name = "CHILD_DOC_ID", referencedColumnName = "DOC_ID", insertable=false, updatable=false)	
    @ManyToOne
	private Doc childDoc;

	public DocLink() {
	}

	public DocLinkPK getId() {
		return this.id;
	}

	public void setId(DocLinkPK id) {
		this.id = id;
	}

	public Doc getChildDoc() {
		return childDoc;
	}

	public void setChildDoc(Doc childDoc) {
		this.childDoc = childDoc;
	}

}