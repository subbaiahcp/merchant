package com.merchant.rest.model;

import java.util.List;

/**
*@author subbaiah
*/
public class SmsToPhoneNumbers {

	private String from;
    private List<String> to;
    private String message;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public List<String> getTo() {
		return to;
	}
	public void setTo(List<String> to) {
		this.to = to;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}	