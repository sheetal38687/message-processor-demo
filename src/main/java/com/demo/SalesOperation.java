package com.demo;

import com.demo.OperationType;

import com.demo.Product;
import com.demo.vo.DetailedMessage;
import com.demo.vo.AdjustmentMessage;
import com.demo.vo.Message;

import java.util.*;

public class SalesOperation {
	Map<Product, List<SalesVO>> records;

	public SalesOperation() {
		this.records = new HashMap<>();
	}

	public SalesOperation(Map<Product, List<SalesVO>> records) {
		this.records = records;
	}

	public Map<Product, List<SalesVO>> getRecords() {
		return records;
	}

	public boolean addProduct(Product product) {
		if (product == null) {
			return false;
		}

		records.put(product, new ArrayList<>());
		return true;
	}

	public boolean updateRecords(Message message) {
		if (message == null) {
			System.out.println("Invalid sales record.");
			return false;
		}

		Product product = findProduct(message.getType());
		if (product == null) {
			System.out.println("Invalid sales record.");
			return false;
		}

		List<SalesVO> transactions = records.get(product);

		if (message instanceof AdjustmentMessage) {
			transactions = adjustSalesVOs(transactions, message);
		} else if (message instanceof DetailedMessage) {
			transactions = addNewSalesVOs(transactions, message);
		} else {
			
			transactions.add(new SalesVO(message.getSellingPrice()));
		}

		records.put(product, transactions);
		return true;
	}

	private Product findProduct(String productType) {
		Set<Product> products = records.keySet();

		for (Product product : products) {
			if (productType.equals(product.getType())) {
				return product;
			}
		}

		return null;
	}

	private List<SalesVO> adjustSalesVOs(List<SalesVO> transactions, Message message) {
		OperationType operationType = ((AdjustmentMessage) message).getOperationType();

		switch (operationType) {
		case ADD:
			for (SalesVO transaction : transactions) {
				transaction.setSalesValue(transaction.getSalesValue() + message.getSellingPrice());
				transaction.setSalesStatus(SalesStatus.ADJUSTED);
			}
			break;
		case MULTIPLY:
			for (SalesVO transaction : transactions) {
				transaction.setSalesValue(transaction.getSalesValue() * message.getSellingPrice());
				transaction.setSalesStatus(SalesStatus.ADJUSTED);
			}
			break;
		case SUBTRACT:
			for (SalesVO transaction : transactions) {
				if (transaction.getSalesValue() < message.getSellingPrice()) {
					System.out.println("loss detected Product type: " + message.getType() + ", Existing value: "
							+ transaction.getSalesValue() + ", Selling price: " + message.getSellingPrice()
							+ ", Adjustment operation: SUBTRACT.");
				}

				transaction.setSalesValue(transaction.getSalesValue() - message.getSellingPrice());
				transaction.setSalesStatus(SalesStatus.ADJUSTED);
			}
			break;
		default:
			break;
		}

		return transactions;
	}

	private List<SalesVO> addNewSalesVOs(List<SalesVO> transactions, Message message) {
		double price = message.getSellingPrice();
		long transactionsCount = ((DetailedMessage) message).getOccurCount();

		

		if (transactionsCount <= 0) {
			return transactions;
		}

		for (long i = 0; i < transactionsCount; i++) {
			transactions.add(new SalesVO(price));
		}

		return transactions;
	}

	
	public void printSalesReport() {
		for (Map.Entry<Product, List<SalesVO>> record : records.entrySet()) {
			System.out.println("item type: " + record.getKey().getType() + ", Total item sold out: "
					+ record.getValue().size() + ", Revenue :- " + getRevenueForProduct(record.getValue()));
		}
	}

	private double getRevenueForProduct(List<SalesVO> transactions) {
		double revenueGenerated = 0;

		for (SalesVO transaction : transactions) {
			revenueGenerated += transaction.getSalesValue();
		}

		return revenueGenerated;
	}
}
