package com.merchant.rest.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.merchant.rest.model.RegisterPhoneNumbers;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;

public class AmazonSNSMulti {
	
    public static void main(String[] args) {
        String ACCESS_KEY = "YOUR_ACCESS_KEY";
        String SECRET_KEY = "YOUR_SECRET_KEY";
        String topicName = "merchantSms";
        String message = "Registered as Merchant";
        sendSMSMessage("+6591490096");
        // Populate the list of phoneNumbers
        //List<String> phoneNumbers = null;
        //AmazonSNSClient snsClient = new AmazonSNSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
        // Create SMS Topic
        //String topicArn = createSNSTopic(snsClient, topicName);
        // Subcribe Phone Numbers to Topic
        //subscribeToTopic(snsClient, topicArn, "sms", phoneNumbers);
        // Publish Message to Topic
        //sendSMSMessageToTopic(snsClient, topicArn, message);
        
    }
    
    public static void registerPhoneNumbers(RegisterPhoneNumbers request) {
        String ACCESS_KEY = "YOUR_ACCESS_KEY";
        String SECRET_KEY = "YOUR_SECRET_KEY";
        String topicName = "merchantSms";
		// Populate the list of phoneNumbers
		List<String> phoneNumbers = null;  // Ex: +919384374XX
		AmazonSNSClient snsClient = new AmazonSNSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
		// Create SMS Topic
		String topicArn = createSNSTopic(snsClient, topicName);
		// Subcribe Phone Numbers to Topic
		subscribeToTopic(snsClient, topicArn, "sms", phoneNumbers);
	}
    
    public static String createSNSTopic(AmazonSNSClient snsClient,
                          String topicName) {
        CreateTopicRequest createTopic = new  
                         CreateTopicRequest(topicName);
        CreateTopicResult result =
                 snsClient.createTopic(createTopic);
        return result.getTopicArn();
    }
    
    public static void subscribeToTopic(AmazonSNSClient snsClient, String topicArn,
                                        String protocol, List<String> phoneNumbers) {
        for (String phoneNumber : phoneNumbers) {
            SubscribeRequest subscribe = new SubscribeRequest(topicArn, protocol, phoneNumber);
            snsClient.subscribe(subscribe);
        }
    }
    
    public static String sendSMSMessageToTopic(AmazonSNSClient snsClient, String topicArn, String message) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withTopicArn(topicArn)
                .withMessage(message));
        return result.getMessageId();
    }

	// Send SMS to a Phone Number
	public static void sendSMSMessage(String phoneNumber) {
		String ACCESS_KEY = "AKIAIJSVLSSHLHS4LPOA";
		String SECRET_KEY = "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";
		Map<String, MessageAttributeValue> smsAttributes =
		        new HashMap<String, MessageAttributeValue>();
		smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
		        .withStringValue("MERCHANTREG") //The sender ID shown on the device.
		        .withDataType("String"));
		AmazonSNSClient snsClient = new AmazonSNSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
		String message = "You are Registered as Merchant. To unregister contact +6591490096.";
		 PublishResult result = snsClient.publish(new PublishRequest()
	                .withMessage(message)
	                .withPhoneNumber(phoneNumber)
	                .withMessageAttributes(smsAttributes));
		System.out.println(result); // Prints the message ID.
	}
}
