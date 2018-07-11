package com.merchant.rest.model;

public class ResolveAndPayRequest {

	private String alias;
	private String businessApplicationId;
	private String amount;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getBusinessApplicationId() {
		return businessApplicationId;
	}

	public void setBusinessApplicationId(String businessApplicationId) {
		this.businessApplicationId = businessApplicationId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

}