package com.merchant.rest.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import com.merchant.rest.model.Request;
import com.merchant.rest.model.Status;
import com.merchant.rest.model.User;
import com.merchant.rest.service.MethodTypes;
import com.merchant.rest.service.VisaAPIClient;
import com.merchant.rest.utils.Constants;

@Path("/merchant")
public class JSONService {
	
	String pushFundsRequest;
    VisaAPIClient visaAPIClient;


	@POST
	@Path("/oct")
	@Consumes("application/json")
	public Response createProductInJSON(Request request) {
		try{
		return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON)
				.entity(pushFundsTransactions(request)).build() ;
		}catch(Exception ex){
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
			.entity("Internal Server Error").build();
		}
	}
	
	@POST
	@Path("/validate/user")
	@Consumes("application/json")
	public Response createProductInJSON(User user) {
		Status status = new Status();
		try{
			if(Constants.VALID_USERS.contains(user.getUserName()) && Constants.USER_PASS_MAP.get(user.getUserName()) != null &&
					StringUtils.equalsIgnoreCase(Constants.USER_PASS_MAP.get(user.getUserName()),user.getPass())){
				status.setMessage("success");
				return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON)
						.entity(status).build();
			}else{
				status.setMessage("notfound");
				return Response.status(HttpStatus.NOT_FOUND.value()).type(MediaType.APPLICATION_JSON)
						.entity(status).build();
			}
		}catch(Exception ex){
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
			.entity(status).build();
		}
	}
	
	public void enrichRequest(Request request) {
        this.visaAPIClient = new VisaAPIClient();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        this.pushFundsRequest = 
				"{"
					+ "\"systemsTraceAuditNumber\":350420,"
					+ "\"retrievalReferenceNumber\":\"401010350420\","
					+ "\"localTransactionDateTime\":\""+strDate +"\","
					+ "\"acquiringBin\":409999,\"acquirerCountryCode\":\"101\","
					+ "\"senderAccountNumber\":\"1234567890123456\","
					+ "\"senderCountryCode\":\"USA\","
					+ "\"transactionCurrencyCode\":\"USD\","
					+ "\"senderName\":\"John Smith\","
					+ "\"senderAddress\":\"44 Market St.\","
					+ "\"senderCity\":\"San Francisco\","
					+ "\"senderStateCode\":\"CA\","
					+ "\"recipientName\":\"Adam Smith\","
					+ "\"recipientPrimaryAccountNumber\":\""+request.getPan()+"\","
					+ "\"amount\":\""+request.getAmount()+"\","
					+ "\"businessApplicationId\":\"AA\","
					+ "\"transactionIdentifier\":234234322342343,"
					+ "\"merchantCategoryCode\":6012,"
					+ "\"sourceOfFundsCode\":\"03\","
					+ "\"cardAcceptor\":{"
										+ "\"name\":\"John Smith\","
										+ "\"terminalId\":\"13655392\","
										+ "\"idCode\":\"VMT200911026070\","
										+ "\"address\":{"
														+ "\"state\":\"CA\","
														+ "\"county\":\"081\","
														+ "\"country\":\"USA\","
														+ "\"zipCode\":\"94105\""
											+ "}"
										+ "},"
					+ "\"feeProgramIndicator\":\"123\""
				+ "}";
    }

    public String pushFundsTransactions(Request request) throws Exception {
    	enrichRequest(request);
        String baseUri = "visadirect/";
        String resourcePath = "fundstransfer/v1/pushfundstransactions/";
        return this.visaAPIClient.doMutualAuthRequest(baseUri + resourcePath, "Push Funds Transaction Test", this.pushFundsRequest, MethodTypes.POST, new HashMap<String, String>());
    }
	
}