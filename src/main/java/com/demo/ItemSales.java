package com.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.demo.Product;
import com.demo.SalesOperation;
import com.demo.vo.AdjustmentMessage;
import com.demo.vo.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

public class ItemSales {
	private static ItemSales itemSales = new ItemSales();
	private SalesOperation salesOperation;

	private static final long TOTAL_MESSAGE_PASS = 50;

	private ItemSales() {
		this.salesOperation = new SalesOperation();
	}

	public static ItemSales getItemSales() {
		return itemSales;
	}

	public boolean initialize(String stockFile) {
		BufferedReader stockBuffer = null;

		try {
			String stockEntry;
			InputStream inputStream = ItemSales.class.getResourceAsStream(stockFile);

			stockBuffer = new BufferedReader(new InputStreamReader(inputStream));

			while ((stockEntry = stockBuffer.readLine()) != null) {
				boolean productAdded = salesOperation.addProduct(parseStockEntry(stockEntry));

				if (!productAdded) {
					System.out.println(" productAdded failed");
				}
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

		if (productData.length != 4) {
			System.out.println("Too much or too less product data. Please check stock data.");
			return null;
		}

		Product product = null;

		try {
			product = new Product(productData[0], // product type, string value
					Long.valueOf(productData[2]), // sold out units, long value
					Double.valueOf(productData[3])); // unit price, double value
		} catch (NumberFormatException exception) {
			System.out.println(" Please check data.");
		}

		return product;
	}

	public List<Message> parse(String notificationsFile) {
		List<Message> messages = null;
		ObjectMapper mapper = new ObjectMapper();

		try {
			messages = mapper.readValue(new File(ItemSales.class.getResource(notificationsFile).getFile()),
					new TypeReference<List<Message>>() {
					});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return messages;
	}

	public boolean process(List<Message> messages) {
		int processedMessages = 0;
		StringBuilder adjustmentsLog = new StringBuilder();
		for (Message message : messages) {
			boolean recordsUpdated = salesOperation.updateRecords(message);
			if (!recordsUpdated) {
				return false;
			}

			processedMessages++;

			if (message instanceof AdjustmentMessage) {
				adjustmentsLog.append("Product (");
				adjustmentsLog.append(message.getType());
				adjustmentsLog.append(") was adjusted (operation: ");
				adjustmentsLog.append(((AdjustmentMessage) message).getOperationType());
				adjustmentsLog.append(") by a value of ");
				adjustmentsLog.append(message.getSellingPrice());
				adjustmentsLog.append(" at approximately ");
				adjustmentsLog.append(new Date());
				adjustmentsLog.append(".\n");
			}

			if (processedMessages % 10 == 0) {
				System.out.println(
						"\n========================Sales Record ====================================================");
				salesOperation.printSalesReport();
			}

			if (processedMessages == TOTAL_MESSAGE_PASS) {
				System.out.println("\n Max total of " + TOTAL_MESSAGE_PASS + " messages can not be Processed.");
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

		if (adjustmentsLog.length() != 0) {
			System.out.println("\n________________Message Type 3 :- Adjustment Msg Log _________________________");
			System.out.println(adjustmentsLog.toString());
		}

		return true;
	}
}