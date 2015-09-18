package com.prokarma.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    /**
     * Get the current date and time.
     * 
     * @return returns the current date and time in the format: "dd-MM-yyyy_HH_mm_ss"
     */
    public static String getCurrentTimeStamp() {
	SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss");
	Date now = new Date();
	return sdfDate.format(now);
	
    }
}