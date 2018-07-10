package com.merchant.rest.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Constants {
    public static final List<String> VALID_USERS = Arrays.asList("visa", "bank", "test");
    public static final Map<String, String> USER_PASS_MAP = new HashMap<String, String>(){
        {
            put("visa", "visa");
            put("bank", "bank");
            put("test", "test");
        }
    };
}
