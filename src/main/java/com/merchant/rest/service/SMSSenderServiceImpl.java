//package com.merchant.rest.service;
//
//import com.amazonaws.services.sns.AmazonSNS;
//import com.amazonaws.services.sns.model.*;
//import com.google.common.base.Strings;
//import com.merchant.rest.model.SmsToPhoneNumbers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.text.MessageFormat;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Pattern;
//
//import static java.util.stream.Collectors.toList;
// 
//@Service
//public class SMSSenderServiceImpl {
// 
//    private static final String MESSAGE_DEFAULT = "Default SMS message.";
// 
//    @Value("${aws.sns.SMSType}")
//    private String smsType;
//    @Value("${aws.sns.phoneNumberRegex}")
//    private String phoneNumberRegex;
//    @Value("${aws.sns.senderIDRegex}")
//    private String senderIDRegex;
//    @Value("${aws.sns.senderID}")
//    private String senderID;
//
//    private AmazonSNS snsClient;
// 
//    @Autowired
//    public SMSSenderServiceImpl(AmazonSNS snsClient) {
//        this.snsClient = snsClient;
//    }
// 
// 
//    public boolean sendSMSMessage(final SmsToPhoneNumbers smsDTO) {
//        List<String> phoneNumbers = smsDTO.getTo();
//       try {
//            if(!CollectionUtils.isEmpty(phoneNumbers)) {
// 
//                // Filter valid phone numbers and prevent no duplicated. 
//                phoneNumbers = phoneNumbers.stream()
//                     .filter(p -> isValidPhoneNumber(p)).distinct().collect(toList());
// 
//                // Prepare for SNS Client environment like MessageAttributeValue default.
//                Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
//                //Map<String, MessageAttributeValue> smsAttributes = setSmsAttributes(smsDTO);
//                 
//                // Send SMS message for each phone numbers.
//                phoneNumbers.forEach(p -> sendSMSMessage(snsClient,
//                        smsMessageBuilder(smsDTO), p, smsAttributes));
//            }
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }
// 
//    private String smsMessageBuilder(final SmsToPhoneNumbers smsDTO) {
//        final StringBuilder builder = new StringBuilder();
//        String message = smsDTO.getMessage();
// 
//        if(Strings.isNullOrEmpty(message)) {
//            builder.append(MessageFormat.format("{0}. ", MESSAGE_DEFAULT));
//        } else {
//            builder.append(MessageFormat.format("{0}.",message));
//        }
//        //TODO Put more message content here
// 
//        return builder.toString();
//    }
// 
//    private void sendSMSMessage(AmazonSNS snsClient, String message,
//                               String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
//        PublishResult result = snsClient.publish(new PublishRequest()
//                .withMessage(message)
//                .withPhoneNumber(phoneNumber)
//                .withMessageAttributes(smsAttributes));
//    }
// 
////   private Map<String, MessageAttributeValue> setSmsAttributes(ShareCampDTO shareCampDTO) {
////        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
//// 
////        senderID = Strings.isNullOrEmpty(senderID) ?
////                genSenderID(shareCampDTO.getFrom()) : senderID;
////        // According to Amazon, SenderID must be 1-11 alpha-numeric characters
////       smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
////                .withStringValue(senderID)
////                .withDataType("String"));
//// 
////        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
////                .withStringValue(smsType)
////                .withDataType("String"));
////        return smsAttributes;
////    }
// 
//    /**
//     *  E.164 is a standard for the phone number structure used for international telecommunication.
//     *  Phone numbers that follow this format can have a maximum of 15 digits, and they are prefixed
//     *  with the plus character (+) and the country code. For example,
//     *  a U.S. phone number in E.164 format would appear as +1XXX5550100.
//     * @See at http://docs.aws.amazon.com/sns/latest/dg/sms_publish-to-phone.html
//     * @see <a href="http://docs.aws.amazon.com/sns/latest/dg/sms_publish-to-phone.html">
//     *  Sending an SMS Message</a>
//     * @param phoneNumber String
//     * @return true if valid, otherwise false.
//     */
//    private boolean isValidPhoneNumber(String phoneNumber) {
//        return Pattern.matches(phoneNumberRegex, phoneNumber);
//    }
// 
//    /**
//     * Because Amazon SNS requires that SenderID must be 1-11 alpha-numeric characters.
//     * So we try to generate a correct SenderID.
//     * @see  <a href="http://docs.aws.amazon.com/sns/latest/dg/sms_supported-countries.html"></a>
//     */
//    private String genSenderID(String phoneNumber) {
//        String sid = phoneNumber.replaceAll(senderIDRegex, "");
//        return sid.length() > 11 ? sid.substring(sid.length() - 11, sid.length()) : sid;
//    }
//}