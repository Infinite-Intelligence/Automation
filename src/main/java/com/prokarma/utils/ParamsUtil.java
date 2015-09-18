


package com.prokarma.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ParamsUtil {
	
	
	/**
     * Replaces the params in a string with corresponding values provided in the 
     * keyvalueparams in the form "_key: value,_key1:value1"
     * 
     * @param requestBody
     * @param keyvalueparams
     * @return
     */
    public static String replaceParams(String requestBody, String keyvalueparams) {
	ParamsUtil putils = new ParamsUtil();
	HashMap<String, String> params = generateRequestParamsMap(keyvalueparams);
	return putils.replaceRequestParameters(requestBody, params);
    }
	

    /**
     * Generates a map of key value parameters from a string in the format:
     * "key:value,key1:value1..." -->
     * 
     * @param params
     * @return
     */
    public static HashMap<String, String> generateRequestParamsMap(String params) {
	HashMap<String, String> paramsMap = new HashMap<String, String>();
	String[] paramSet = params.split(",");
	for (int i = 0; i < paramSet.length; i++) {
	    String[] param = paramSet[i].split(":");
	    if (param[1].equalsIgnoreCase("NIL")) {
		param[1] = "";
	    }
	    if (param[1].startsWith("TempProps[")) {
		Properties tempProps = PropertiesReader.loadPropertyFile(Constants.TEMP_PROP_PATH);
		String keyInProp = param[1].replaceAll(".*\\[|\\].*", "");
		param[1] = tempProps.getProperty(keyInProp).trim();
	    }
	    paramsMap.put(param[0].trim(), param[1].trim());
	}
	return paramsMap;
    }

    /**
     * Replace the placeholder request parameters in the JSON request body with
     * the values in a HashMap.
     * 
     * @param requestBody
     * @param params
     * @return
     */
    private String replaceRequestParameters(String requestBody, HashMap<String, String> params) {
	for (Map.Entry<String, String> entry : params.entrySet()) {
	    requestBody = requestBody.replace(entry.getKey(), entry.getValue());
	}
	return requestBody;
    }

    
}
