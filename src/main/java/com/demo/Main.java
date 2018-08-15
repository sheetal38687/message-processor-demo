package com.demo;

import com.demo.ItemSales;
import com.demo.vo.Message;

import java.util.List;

public class Main {
	public static void main(String[] args) {

	
		String dataFile = "/testItemdata.csv";
		String testSalesdata = "/testSalesdata.json";

		ItemSales itemSales = ItemSales.getItemSales();

		itemSales.initialize(dataFile);
		List<Message> messages = itemSales.parse(testSalesdata);

		itemSales.finalLogic(messages);

	}

}
