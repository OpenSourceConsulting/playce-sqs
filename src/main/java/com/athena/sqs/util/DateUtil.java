package com.athena.sqs.util;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateUtil
 * 
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 *
 */
public class DateUtil {

	public static final String LOG_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
	
	public static String getTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(d);
	}

	public static String getTime(String format) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}


	public static Date string2Date(String s, String format) {
		if( s == null ) return null;
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		date = sdf.parse(s, new ParsePosition(0));
		return date;
	}

}
