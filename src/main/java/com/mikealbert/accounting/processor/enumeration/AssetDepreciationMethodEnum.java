package com.mikealbert.accounting.processor.enumeration;

public enum AssetDepreciationMethodEnum {
	STRAIGHT_LINE("Straight Line"),
	NON_DEPRECIATING("Non-Depreciating");
	
	private String name;

	AssetDepreciationMethodEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
