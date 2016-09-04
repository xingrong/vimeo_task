package org.rong.task.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Data Util class
 * 
 */
public class DateUtil {
	private static String defaultDatePattern = "yyyy-MM-dd HH:mm:ss";

	public static String getDefaultDatePattern() {
		return defaultDatePattern;
	}

	public static String format(Date date) {
		return date == null ? " " : format(date, getDefaultDatePattern());
	}

	public static String format(Date date, String pattern) {
		return date == null ? " " : new SimpleDateFormat(pattern).format(date);
	}

	public static Date parse(String strDate) throws ParseException {
		return (strDate == null || strDate.length() == 0) ? null : parse(
				strDate, getDefaultDatePattern());
	}

	public static Date parse(String strDate, String pattern)
			throws ParseException {
		return (strDate == null || strDate.length() == 0) ? null
				: new SimpleDateFormat(pattern, Locale.US).parse(strDate);
	}

	public static Date addHour(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, n);
		return cal.getTime();
	}

	public static Date getNowHour() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static Date getLastHour() {
		return DateUtil.addHour(getNowHour(), -1);
	}
}