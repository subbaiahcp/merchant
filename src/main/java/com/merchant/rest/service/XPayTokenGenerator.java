package com.merchant.rest.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;



public class XPayTokenGenerator {

    /**
     * Generate X-Pay-Token as x:+ TimestampUTC + : + SHA256Hash
     * 
     * @param payload
     * @param uri
     * @param apiKey
     * @param sharedSecret
     * @return
     * @throws SignatureException
     */
    public static String generateXpaytoken(String resourcePath, String queryString, String requestBody) throws SignatureException {
        String timestamp = timeStamp();
        String beforeHash = timestamp + resourcePath + queryString + requestBody;
        String hash = hmacSha256Digest(beforeHash);
        String token = "xv2:" + timestamp + ":" + hash;
        return token;
    }


    private static String timeStamp() {
        return String.valueOf(System.currentTimeMillis()/ 1000L);
    }

    private static String hmacSha256Digest(String data)
            throws SignatureException {
        return getDigest("HmacSHA256", VisaProperties.getProperty(Property.SHARED_SECRET), data, true);
    }


    private static String getDigest(String algorithm, String sharedSecret, String data,
            boolean toLower) throws SignatureException {
        try {
            Mac sha256HMAC = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(sharedSecret.getBytes(StandardCharsets.UTF_8), algorithm);
            sha256HMAC.init(secretKey);

            byte[] hashByte = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String hashString = toHex(hashByte);

            return toLower ? hashString.toLowerCase() : hashString;
        } catch (Exception e) {
            throw new SignatureException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }
}
