package com.demo;

import java.util.Date;

public class SalesVO {
	private double salesValue;
	private SalesStatus salesStatus;
	private Date salesDate;

	public SalesVO() {
		this.salesStatus = SalesStatus.NOT_ADJUSTED;
		this.salesDate = new Date();
	}

	public SalesVO(double value) {
		this.salesValue = value;
		this.salesStatus = SalesStatus.NOT_ADJUSTED;
		this.salesDate = new Date();
	}

	public double getSalesValue() {
		return salesValue;
	}

	public void setSalesValue(double salesValue) {
		this.salesValue = salesValue;
	}

	public SalesStatus getSalesStatus() {
		return salesStatus;
	}

	public void setSalesStatus(SalesStatus salesStatus) {
		this.salesStatus = salesStatus;
	}

	public Date getSalesDate() {
		return salesDate;
	}

	public void setSalesDate(Date salesDate) {
		this.salesDate = salesDate;
	}

}
