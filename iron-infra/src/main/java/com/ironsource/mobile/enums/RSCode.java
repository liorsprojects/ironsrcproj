package com.ironsource.mobile.enums;

//TODO - add this wherever possible
public enum RSCode {

	WALL("W"), IMPRESSION("D"), CLICK("C"), BACK("Q"), CLOSE("-"), INSATLL("+"), REPORT("S"), AI("AI"), ERROR("E");

	private String rsCode;

	private RSCode(String rs) {
		this.rsCode = rs;
	}

	public String getRsCode() {
		return rsCode;
	}

	public static RSCode convert(String rs) {
		for (RSCode rsCode : RSCode.values()) {
			if(rsCode.getRsCode().equals(rs)) {
				return rsCode;
			}
		}
		return null;
	}
	
}
