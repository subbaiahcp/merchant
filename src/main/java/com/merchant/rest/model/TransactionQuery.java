package com.merchant.rest.model;

public class TransactionQuery {

	private String transactionIdentifier;
	private String amount;

	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}

	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

}