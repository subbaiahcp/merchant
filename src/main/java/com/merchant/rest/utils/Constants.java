package com.merchant.rest.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author subbaiah
 */
public class Constants {
	public static final String SUPPLIER = "Subbaiah beverage supplier";
	public static final Map<String, String> USER_PASS_MAP = new HashMap<String, String>() {
		{
			put("visas", "visas");
			put("visam", "visam");
		}
	};
	public static final Map<String, String> USER_TYPE = new HashMap<String, String>() {
		{
			put("visas", "supplier");
			put("visam", "merchant");
		}
	};
}
