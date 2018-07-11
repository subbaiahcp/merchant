package com.merchant.rest.model;

/**
*@author subbaiah
*/
public class CreateAliasRequest {

	private String guid;
	private String recipientFirstName;
	private String recipientMiddleName;
	private String recipientLastName;
	private String address1;
	private String address2;
	private String city;
	private String country;
	private String postalCode;
	private String consentDateTime;
	private String recipientPrimaryAccountNumber;
	private String issuerName;
	private String cardType;
	private String alias;
	private String aliasType;
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getRecipientFirstName() {
		return recipientFirstName;
	}
	public void setRecipientFirstName(String recipientFirstName) {
		this.recipientFirstName = recipientFirstName;
	}
	public String getRecipientMiddleName() {
		return recipientMiddleName;
	}
	public void setRecipientMiddleName(String recipientMiddleName) {
		this.recipientMiddleName = recipientMiddleName;
	}
	public String getRecipientLastName() {
		return recipientLastName;
	}
	public void setRecipientLastName(String recipientLastName) {
		this.recipientLastName = recipientLastName;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
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
	public String getConsentDateTime() {
		return consentDateTime;
	}
	public void setConsentDateTime(String consentDateTime) {
		this.consentDateTime = consentDateTime;
	}
	public String getRecipientPrimaryAccountNumber() {
		return recipientPrimaryAccountNumber;
	}
	public void setRecipientPrimaryAccountNumber(String recipientPrimaryAccountNumber) {
		this.recipientPrimaryAccountNumber = recipientPrimaryAccountNumber;
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
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getAliasType() {
		return aliasType;
	}
	public void setAliasType(String aliasType) {
		this.aliasType = aliasType;
	}
	
}