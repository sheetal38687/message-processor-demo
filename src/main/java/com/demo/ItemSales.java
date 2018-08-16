package com.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.demo.Product;
import com.demo.SalesOperation;
import com.demo.vo.AdjustmentMessage;
import com.demo.vo.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

public class ItemSales {
	private static ItemSales itemSales = new ItemSales();
	private SalesOperation salesOperation;

	private static final long MAX_MESSAGE_ALLOW = 50;

	private ItemSales() {
		this.salesOperation = new SalesOperation();
	}

	public static ItemSales getItemSales() {
		return itemSales;
	}

	public boolean initialize(String dataFile) {
		BufferedReader stockBuffer = null;

		try {
			String dataEntry;
			InputStream inputStream = ItemSales.class.getResourceAsStream(dataFile);

			stockBuffer = new BufferedReader(new InputStreamReader(inputStream));

			while ((dataEntry = stockBuffer.readLine()) != null) {
				Product p=parseStockEntry(dataEntry);
				salesOperation.addProduct(p);

				
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			if (stockBuffer != null) {
				try {
					stockBuffer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	private Product parseStockEntry(String stockEntry) {
		if (stockEntry == null) {
			return null;
		}

		String[] productData = stockEntry.split("\\s*,\\s*");

		Product product = null;

		try {
			product = new Product(productData[0]); // product
		} catch (NumberFormatException exception) {
			System.out.println(" Please check data.");
		}

		return product;
	}

	public List<Message> parse(String testSalesdata) {
		List<Message> messages = null;
		ObjectMapper mapper = new ObjectMapper();

		try {
			messages = mapper.readValue(new File(ItemSales.class.getResource(testSalesdata).getFile()),
					new TypeReference<List<Message>>() {
					});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return messages;
	}

	public boolean finalLogic(List<Message> messages) {
		
		StringBuilder aLog = new StringBuilder();
		int proMsg = 0;
		for (Message message : messages) {
			boolean recordsUpdated = salesOperation.updateRecords(message);
			if (!recordsUpdated) {
				return false;
			}

			proMsg++;
           
			if (message instanceof AdjustmentMessage) {
				aLog.append("Product (");
				aLog.append(message.getType());
				aLog.append(") was adjusted (operation: ");
				aLog.append(((AdjustmentMessage) message).getOperationType());
				aLog.append(") by a value of ");
				aLog.append(message.getSellingPrice());
				aLog.append(" at approximately ");
				aLog.append(new Date());
				aLog.append(".\n");
			}

			if (proMsg % 10 == 0) {
				//After every 10th message received your application should log a report detailing the number of sales of each product and their total value.
				System.out.println("\n Message:"+(proMsg-10)+" to "+proMsg+" Comments:- After every 10th message received your application should log a report detailing the number of sales of each product and their total value.");
				
				salesOperation.printSalesReport();
			}

			if (proMsg == MAX_MESSAGE_ALLOW) {
				System.out.println("\n  50 messages DONE. After 50 messages your application should log that it is pausing, stop accepting new messagesd.");
				break;
			}
		}

		/*
		 * printing one last time because any processed and recorded "reminder" (14
		 * notifications % 10 = 4 not reflected otherwise) sales won't get displayed on
		 * console otherwise.
		 */
		System.out.println("\n=========================Final Sales Record ==========================================");
		salesOperation.printSalesReport();

		if (aLog.length() != 0) {
			System.out.println("\n________________Message Type 3 :- Adjustment Msg Log _________________________");
			System.out.println(aLog.toString());
		}

		return true;
	}
}