package com.demo;

public class Product {
	private String type;
	private long soldOut;
	private Double unitPrice;

	public Product() {
	}

	public Product(String type, long soldOut, Double unitPrice) {
		this.type = type;
		this.soldOut = soldOut;
		this.unitPrice = unitPrice;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getSoldOut() {
		return soldOut;
	}

	public void setSoldOut(long soldOut) {
		this.soldOut = soldOut;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
}
