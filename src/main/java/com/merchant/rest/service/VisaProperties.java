package com.merchant.rest.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VisaProperties {

    static Properties properties;

    static {
        try {
            properties = new Properties();
            InputStream targetStream = VisaProperties.class.getResourceAsStream("/configuration.properties");
                properties.load(targetStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(Property property) {
        return (String) properties.get(property.getValue());
    }
}
