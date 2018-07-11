package com.merchant.rest.model;

public class ResolveResponse {
	
	private String recipientPrimaryAccountNumber;
	private String recipientName;
	private String issuerName;
	private String cardType;
	private String city;
	private String country;
	private String postalCode;

	public String getRecipientPrimaryAccountNumber() {
		return recipientPrimaryAccountNumber;
	}

	public void setRecipientPrimaryAccountNumber(String recipientPrimaryAccountNumber) {
		this.recipientPrimaryAccountNumber = recipientPrimaryAccountNumber;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

}