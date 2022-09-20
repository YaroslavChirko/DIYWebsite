package com.diyweb.models;

public enum Cathegory {
	ELECTRONICS(1),
	PAPERCRAFT(2),
	TEXTILE(3),
	WOODWORKING(4),
	PROGRAMMING(5),
	CROCHET(6),
	MISC(0);
	
	private final int numeric;
	
	private Cathegory(int numeric){
		this.numeric = numeric;
	}
	
	public int getNumeric() {
		return numeric;
	}
	
	
}
