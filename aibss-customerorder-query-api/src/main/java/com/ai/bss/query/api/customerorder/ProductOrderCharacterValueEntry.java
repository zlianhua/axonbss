package com.ai.bss.query.api.customerorder;

import java.util.Set;

import javax.persistence.Entity;

import com.ai.bss.query.api.product.AbstractProductCharacterValue;

@Entity
public class ProductOrderCharacterValueEntry extends AbstractProductCharacterValue{
	public ProductOrderCharacterValueEntry(){}
	private String action;
	private long asisVersion;
	
	//updated after archived
	private long tobeVersion;
	
	public long getAsisVersion() {
		return asisVersion;
	}

	public void setAsisVersion(long asisVersion) {
		this.asisVersion = asisVersion;
	}

	public long getTobeVersion() {
		return tobeVersion;
	}

	public void setTobeVersion(long tobeVersion) {
		this.tobeVersion = tobeVersion;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
