package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;

public class CacheVO implements Serializable{
	private static final long serialVersionUID = -8233175788319679432L;
	
	String name;
	
	public CacheVO(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Cache [name=" + name + "]";
	}
		
}
