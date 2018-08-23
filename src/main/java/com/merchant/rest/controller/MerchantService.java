package com.merchant.rest.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.rest.model.CreateAliasRequest;
import com.merchant.rest.model.RegisterPhoneNumbers;
import com.merchant.rest.model.Request;
import com.merchant.rest.model.ResolveAndPayRequest;
import com.merchant.rest.model.ResolveRequest;
import com.merchant.rest.model.ResolveResponse;
import com.merchant.rest.model.SmsToPhoneNumber;
import com.merchant.rest.model.Status;
import com.merchant.rest.model.TransactionQuery;
import com.merchant.rest.model.User;
import com.merchant.rest.model.UserLoginResponse;
import com.merchant.rest.service.MethodTypes;
import com.merchant.rest.service.VisaAPIClient;
import com.merchant.rest.utils.AmazonSNSMulti;
import com.merchant.rest.utils.Constants;

/**
 * @author subbaiah
 */
@Path("/merchant")
public class MerchantService {

	private String request;
	private VisaAPIClient visaAPIClient;
	private Status status = new Status();
	private ObjectMapper mapper = new ObjectMapper();
	private static List<String> transcationIdentifiers = new ArrayList<String>();
	private static Map<String, String> transcationIdNameMap = new HashMap<String, String>();

	@POST
	@Path("/oct")
	@Consumes("application/json")
	public Response postMerchantApi(Request request) {
		try {
			enrichOctRequest(request);
			String response = executeService("visadirect/", "fundstransfer/v1/pushfundstransactions/",
					MethodTypes.POST);
			JSONObject object = (JSONObject) JSONValue.parse(response);
			transcationIdentifiers.add(object.get("transactionIdentifier").toString());
			return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON).entity(response).build();
		} catch (Exception ex) {
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
					.entity(status).build();
		}
	}

	@POST
	@Path("/create")
	@Consumes("application/json")
	public Response postCreate(CreateAliasRequest request) {
		try {
			enrichCreateAliasRequest(request);
			return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON)
					.entity(executeService("visaaliasdirectory/", "v1/manage/createalias/", MethodTypes.POST)).build();
		} catch (Exception ex) {
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
					.entity(status).build();
		}
	}

	@POST
	@Path("/resolve")
	@Consumes("application/json")
	public Response postResolve(ResolveRequest request) {
		try {
			enrichResolveRequest(request);
			return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON)
					.entity(executeService("visaaliasdirectory/", "v1/resolve/", MethodTypes.POST)).build();
		} catch (Exception ex) {
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
					.entity(status).build();
		}
	}
	
	@POST
	@Path("/register/sms")
	@Consumes("application/json")
	public Response registerSmsService(RegisterPhoneNumbers request) {
		try {
			AmazonSNSMulti.registerPhoneNumbers(request);
			return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception ex) {
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
					.entity(status).build();
		}
	}

	@POST
	@Path("/send/sms")
	@Consumes("application/json")
	public Response sendSmsService(SmsToPhoneNumber smsToPhoneNumbers) {
		try {
			AmazonSNSMulti.sendSMSMessage(smsToPhoneNumbers.getTo());
			return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception ex) {
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
					.entity(status).build();
		}
	}

	@POST
	@Path("/resolvepay")
	@Consumes("application/json")
	public Response resolveAndPay(ResolveAndPayRequest request) {
		try {
			ResolveRequest resolveRequest = new ResolveRequest();
			resolveRequest.setAlias(request.getAlias());
			enrichResolveRequest(resolveRequest);
			ResolveResponse resolveResponse = mapper.readValue(
					executeService("visaaliasdirectory/", "v1/resolve/", MethodTypes.POST), ResolveResponse.class);
			Request octRequest = new Request();
			octRequest.setPan(resolveResponse.getRecipientPrimaryAccountNumber());
			octRequest.setAmount(request.getAmount());
			enrichOctRequest(octRequest);
			String response = executeService("visadirect/", "fundstransfer/v1/pushfundstransactions/",
					MethodTypes.POST);
			this.visaAPIClient.doGooglePostNotification("", getGoogleNotifyMessage(octRequest.getAmount()),
					MethodTypes.GET);
			JSONObject object = (JSONObject) JSONValue.parse(response);
			transcationIdentifiers.add(object.get("transactionIdentifier").toString());
			transcationIdNameMap.put(object.get("transactionIdentifier").toString(), request.getName());
			Response finalResponse = Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON)
					.entity(response).build();

			return finalResponse;
		} catch (Exception ex) {
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
					.entity(status).build();
		}
	}

	@GET
	@Path("/transactiondata")
	@Consumes("application/json")
	public Response getTransactionData() {
		List<TransactionQuery> transactionQuery = new ArrayList<TransactionQuery>();
		try {
			for (String transcationIdentifier : transcationIdentifiers) {
				this.visaAPIClient = new VisaAPIClient();
				String response = executeService("visadirect/",
						"v1/transactionquery?acquiringBIN=409999&transactionIdentifier=" + transcationIdentifier,
						MethodTypes.GET);
				JSONArray jsonArray = new JSONArray(response);
				TransactionQuery transactionQ = new TransactionQuery();
				for (int i = 0; i < jsonArray.length(); i++) {
					org.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
					transactionQ.setTransactionIdentifier(jsonObject.optString("transactionIdentifier"));
					transactionQ.setAmount(jsonObject.optString("amount"));
					if(StringUtils.isBlank(transcationIdNameMap.get(transactionQ.getTransactionIdentifier()))){
						transactionQ.setName(Constants.SUPPLIER);
					}else{
					    transactionQ.setName(transcationIdNameMap.get(transactionQ.getTransactionIdentifier()));
					}
				}
				transactionQuery.add(transactionQ);
			}
			if (transactionQuery.isEmpty()) {
				status.setMessage("notfound");
				return Response.status(HttpStatus.NOT_FOUND.value()).type(MediaType.APPLICATION_JSON).entity(status)
						.build();
			} else {
				return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON)
						.entity(mapper.writeValueAsString(transactionQuery)).build();
			}
		} catch (Exception ex) {
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
					.entity(status).build();
		}
	}

	@POST
	@Path("/validate/user")
	@Consumes("application/json")
	public Response checkUser(User user) {
		try {
			UserLoginResponse userLoginResponse = new UserLoginResponse();
			if (Constants.USER_PASS_MAP.keySet().contains(user.getUserName())
					&& Constants.USER_PASS_MAP.get(user.getUserName()) != null
					&& StringUtils.equalsIgnoreCase(Constants.USER_PASS_MAP.get(user.getUserName()), user.getPass())) {
				userLoginResponse.setStatus("success");
				userLoginResponse.setType(Constants.USER_TYPE.get(user.getUserName()));
				return Response.status(HttpStatus.OK.value()).type(MediaType.APPLICATION_JSON).entity(userLoginResponse).build();
			} else {
				status.setMessage("notfound");
				return Response.status(HttpStatus.NOT_FOUND.value()).type(MediaType.APPLICATION_JSON).entity(status)
						.build();
			}
		} catch (Exception ex) {
			status.setMessage("Internal Server Error");
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).type(MediaType.APPLICATION_JSON)
					.entity(status).build();
		}
	}

	public String getGoogleNotifyMessage(String amount) {
		String message = "{ \"to\" : \"/topics/payments\", " + "\"notification\" : "
				+ "{ \"body\" : \"Payment Recieved $" + amount + "\",\"title\" : \"Doogle Biz\"}}";
		return message;
	}

	public void enrichOctRequest(Request request) {
		this.visaAPIClient = new VisaAPIClient();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		this.request = "{" + "\"systemsTraceAuditNumber\":350420," + "\"retrievalReferenceNumber\":\"401010350420\","
				+ "\"localTransactionDateTime\":\"" + strDate + "\","
				+ "\"acquiringBin\":409999,\"acquirerCountryCode\":\"101\","
				+ "\"senderAccountNumber\":\"1234567890123456\"," + "\"senderCountryCode\":\"USA\","
				+ "\"transactionCurrencyCode\":\"USD\"," + "\"senderName\":\"John Smith\","
				+ "\"senderAddress\":\"44 Market St.\"," + "\"senderCity\":\"San Francisco\","
				+ "\"senderStateCode\":\"CA\"," + "\"recipientName\":\"Adam Smith\","
				+ "\"recipientPrimaryAccountNumber\":\"" + request.getPan() + "\"," + "\"amount\":\""
				+ request.getAmount() + "\"," + "\"businessApplicationId\":\"AA\","
				+ "\"transactionIdentifier\":234234322342343," + "\"merchantCategoryCode\":6012,"
				+ "\"sourceOfFundsCode\":\"03\"," + "\"cardAcceptor\":{" + "\"name\":\"John Smith\","
				+ "\"terminalId\":\"13655392\"," + "\"idCode\":\"VMT200911026070\"," + "\"address\":{"
				+ "\"state\":\"CA\"," + "\"county\":\"081\"," + "\"country\":\"USA\"," + "\"zipCode\":\"94105\"" + "}"
				+ "}," + "\"feeProgramIndicator\":\"123\"" + "}";
	}

	public void enrichCreateAliasRequest(CreateAliasRequest request) throws JsonProcessingException {
		request.setAddress1("Street 1");
		request.setAddress2("Region 1");
		request.setAliasType("01");
		request.setCardType("Visa Classic");
		request.setCity("Nairobi");
		request.setConsentDateTime("2018-03-01 01:02:03");
		request.setCountry("KE");
		request.setIssuerName("Test Bank 1");
		request.setPostalCode("00111");
		request.setRecipientFirstName("Jamie");
		request.setRecipientMiddleName("M");
		request.setRecipientLastName("Bakari");
		request.setRecipientPrimaryAccountNumber("4895142232120006");
		request.setGuid(
				"574f4b6a4c2b704766306f300099515a789092348832455975343" + RandomStringUtils.randomAlphanumeric(10));
		this.visaAPIClient = new VisaAPIClient();
		this.request = mapper.writeValueAsString(request);
	}

	public void enrichResolveRequest(ResolveRequest request) throws JsonProcessingException {
		this.visaAPIClient = new VisaAPIClient();
		request.setBusinessApplicationId("PP");
		this.request = mapper.writeValueAsString(request);
	}

	public String executeService(String baseUri, String resourcePath, MethodTypes methodTypes) throws Exception {
		return this.visaAPIClient.doMutualAuthRequest(baseUri + resourcePath, "performing API call", this.request,
				methodTypes, new HashMap<String, String>());
	}

}