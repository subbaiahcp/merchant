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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.rest.model.CreateAliasRequest;
import com.merchant.rest.model.Request;
import com.merchant.rest.model.ResolveAndPayRequest;
import com.merchant.rest.model.ResolveRequest;
import com.merchant.rest.model.ResolveResponse;
import com.merchant.rest.model.Status;
import com.merchant.rest.model.User;
import com.merchant.rest.service.MethodTypes;
import com.merchant.rest.service.VisaAPIClient;
import com.merchant.rest.utils.Constants;

@Path("/merchant")
public class MerchantService {
	
	private String request;
    private VisaAPIClient visaAPIClient;
    private Status status = new Status();
    private ObjectMapper mapper = new ObjectMapper();

	@POST
	@Path("/oct")
	@Consumes("application/json")
	public Response postMerchantApi(Request request) {
		try{
		enrichOctRequest(request);
		return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON)
				.entity(executeService("visadirect/","fundstransfer/v1/pushfundstransactions/")).build() ;
		}catch(Exception ex){
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
			.entity(status).build();
		}
	}
	
	@POST
	@Path("/create")
	@Consumes("application/json")
	public Response postCreate(CreateAliasRequest request) {
		try{
		enrichCreateAliasRequest(request);
		return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON)
				.entity(executeService("visaaliasdirectory/","v1/manage/createalias/")).build() ;
		}catch(Exception ex){
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
			.entity(status).build();
		}
	}
	
	@POST
	@Path("/resolve")
	@Consumes("application/json")
	public Response postResolve(ResolveRequest request) {
		try{
		enrichResolveRequest(request);
		return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON)
				.entity(executeService("visaaliasdirectory/","v1/resolve/")).build() ;
		}catch(Exception ex){
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
			.entity(status).build();
		}
	}
	
	@POST
	@Path("/resolvepay")
	@Consumes("application/json")
	public Response resolveAndPay(ResolveAndPayRequest request) {
		try{
		ResolveRequest resolveRequest = new ResolveRequest();
		resolveRequest.setAlias(request.getAlias());
		enrichResolveRequest(resolveRequest);
		ResolveResponse resolveResponse = mapper.readValue(executeService("visaaliasdirectory/","v1/resolve/"), ResolveResponse.class);
		Request octRequest = new Request();
		octRequest.setPan(resolveResponse.getRecipientPrimaryAccountNumber());
		octRequest.setAmount(request.getAmount());
		enrichOctRequest(octRequest);
		return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON)
				.entity(executeService("visadirect/","fundstransfer/v1/pushfundstransactions/")).build() ;
		}catch(Exception ex){
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
			.entity(status).build();
		}
	}
	
	@POST
	@Path("/validate/user")
	@Consumes("application/json")
	public Response checkUser(User user) {
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
	
	public void enrichOctRequest(Request request) {
        this.visaAPIClient = new VisaAPIClient();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        this.request = 
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
	
	public void enrichCreateAliasRequest(CreateAliasRequest request) throws JsonProcessingException {
        this.visaAPIClient = new VisaAPIClient();
        this.request = mapper.writeValueAsString(request);
    }
	
	public void enrichResolveRequest(ResolveRequest request) throws JsonProcessingException {
        this.visaAPIClient = new VisaAPIClient();
        request.setBusinessApplicationId("PP");
        this.request = mapper.writeValueAsString(request);
    }

    public String executeService(String baseUri,String resourcePath) throws Exception {
        return this.visaAPIClient.doMutualAuthRequest(baseUri + resourcePath, "performing API call", this.request, MethodTypes.POST, new HashMap<String, String>());
    }
	
}