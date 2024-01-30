package com.mikealbert.accounting.processor.enumeration;

public enum BusinessUnitEnum {
	NONE(null), 
	ADMIN("Admin"),
	FLEET_SOLUTIONS("Fleet Solutions"),
	RENTAL("Rental");
	
	private final String name;
	
	private BusinessUnitEnum(String name) {
		this.name = name;
	}
	
	public static BusinessUnitEnum getEnum(String name) {
		for(BusinessUnitEnum field : values()) {
			if(name == null || name.isBlank()) return BusinessUnitEnum.NONE;
			if(field.getName() != null && field.getName().equals(name)) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unsupported Busines Unit: " + name );
	}

	public String getName() {
		return name;
	}	
	
}

