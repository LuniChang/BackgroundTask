package com.xu.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by xc
 */
public class TimeTools {
	public static final String[] WeekStrForSys = { "周日", "周一", "周二", "周三",
			"周四", "周五", "周六" };

	/**
	 * 检查两个时间段是否有冲突
	 *
	 * @param startTime1
	 * @param endTime1
	 * @param startTime2
	 * @param endTime2
	 * @return
	 */
	public static boolean haveTimeClash(int startTime1, int endTime1,
			int startTime2, int endTime2) {

		return ((startTime1 >= startTime2 && startTime1 <= endTime2) || (endTime1 >= startTime2 && endTime1 <= endTime2));
	}

	/**
	 * 时间点是否在时间段内
	 *
	 * @param time
	 * @param rgStartTime
	 * @param rgEndTime
	 * @return
	 */
	public static boolean isInRange(int time, int rgStartTime, int rgEndTime) {

		return (rgStartTime <= time && rgEndTime >= time);
	}

	/**
	 * 时间文本化
	 *
	 * @param hour
	 * @param min
	 * @return
	 */
	public static String toHourMinString(int hour, int min) {
		String hourZero = "";
		String minZero = "";
		if (hour == 0) {
			hourZero = "0";
		}
		if (min == 0) {
			minZero = "0";
		}
		return hour + hourZero + " : " + min + minZero;
	}

	/**
	 * 是否在同一天
	 *
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static Boolean isSameDay(long time1, long time2) {
		java.util.GregorianCalendar mGregorianCalendar = new java.util.GregorianCalendar();
		mGregorianCalendar.setTimeInMillis(time1);
		int year = mGregorianCalendar.get(Calendar.YEAR);
		int month = mGregorianCalendar.get(Calendar.MONTH);
		int monthDay = mGregorianCalendar.get(Calendar.MONDAY);
		mGregorianCalendar.setTimeInMillis(time2);
		if (year == mGregorianCalendar.get(Calendar.YEAR)
				&& month == mGregorianCalendar.get(Calendar.MONTH)
				&& monthDay == mGregorianCalendar.get(Calendar.MONDAY)) {
			return true;
		}
		return false;

	}

	/**
	 * 
	 * @param time
	 * @return yyyy_MM_dd
	 */
	public static String longToDateOnDay(long time) {
		// java.util.GregorianCalendar mGregorianCalendar = new
		// java.util.GregorianCalendar();
		// mGregorianCalendar.setTimeInMillis(time);
		//
		// return mGregorianCalendar.get(GregorianCalendar.YEAR) + "_"
		// + mGregorianCalendar.get(GregorianCalendar.MONTH) + "_"
		// + mGregorianCalendar.get(GregorianCalendar.MONDAY);
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd",
					Locale.CHINA);
			Date date = new Date(time);
			return format.format(date);
		} catch (Exception e) {
			return "yyyy_MM_dd";
		}

	}

	
	
	private static SimpleDateFormat format_yyyy_MM_dd = new SimpleDateFormat("yyyy_MM_dd",
			Locale.CHINA);
	/**
	 * 
	 * @param time
	 * @return long
	 */
	public static long dateOnDayToLong(String date) {
		try {

//			SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd",
//					Locale.CHINA);
			return format_yyyy_MM_dd.parse(date).getTime();

		} catch (Exception e) {
			
		}
		return 0l;
		// try {
		// if(date!=null){
		// String dates[]=date.split("_");
		// int year=Integer.valueOf(dates[0]);
		// int month=Integer.valueOf(dates[1]);
		// int mouday=Integer.valueOf(dates[2]);
		// java.util.GregorianCalendar mGregorianCalendar = new
		// java.util.GregorianCalendar();
		// mGregorianCalendar.set(year, month, mouday);
		// return mGregorianCalendar.getTimeInMillis();
		//
		// }
		// }catch (Exception e) {
		//
		// }
		// return 0l;
	}

	
	
	private static  SimpleDateFormat formatlongToDate=null;
	/**
	 * 
	 * @param time
	 * @return yyyy-MM-dd HH:mm:ss:SSSS
	 */
	public static String longToDate(long time) {
		try {
			if(formatlongToDate==null){
				formatlongToDate = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss:SSSS", Locale.CHINA);
			}
			return formatlongToDate.format(time);
		} catch (Exception e) {
			return " yyyy-MM-dd HH:mm:ss:SSSS";
		}

	}
	
	
	
	private static  SimpleDateFormat formatLongToDateNoMS=null;
	/**
	 * 
	 * @param time
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String longToDateNoMS(long time) {
		try {
			if(formatLongToDateNoMS==null){
				formatLongToDateNoMS = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss", Locale.CHINA);
			}
			return formatLongToDateNoMS.format(time);
		} catch (Exception e) {
			return " yyyy-MM-dd HH:mm:ss";
		}

	}

	public static String formatCountTime(long countTime) {
		long countSecond = (countTime >> 3) / 125;

		long leftSecond = countSecond % 60;

		long countMin = countSecond / 60;

		long leftMinutes = countMin % 60;

		long countHour = countMin / 60;

		long lefthours = countHour % 24;

		long leftDay = countHour / 24;
		
		return  leftDay+"天"+lefthours+"时"+leftMinutes+"分";//+leftSecond+"秒";
	}
}
