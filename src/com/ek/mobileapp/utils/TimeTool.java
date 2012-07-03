package com.ek.mobileapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeTool {
	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat formatDate = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static Calendar calendar = Calendar.getInstance();

	public static int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	public static int getMonth() {
		return calendar.get(Calendar.MONTH);
	}

	public static int getDay() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static String getDateFormatedFromDataPicker(int y, int m, int d) {
		String month;
		String day;
		if (m < 9)
			month = "0" + (m + 1);
		else
			month = (m + 1) + "";
		if (d < 10)
			day = "0" + d;
		else
			day = d + "";
		return y + "-" + month + "-" + day;
	}

	public static Date getCurrentTime() {
		return new Date(System.currentTimeMillis());
	}

	public static String getTimeFormated(Date d) {
		return format.format(d);
	}

	public static String getDateFormated(Date d) {
		return formatDate.format(d);
	}
}
