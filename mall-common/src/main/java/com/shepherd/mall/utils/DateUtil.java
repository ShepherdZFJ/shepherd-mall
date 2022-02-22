package com.shepherd.mall.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/23 17:05
 */
public class DateUtil {
    private static final String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(Date date, String format) {
        if (StringUtils.isBlank(format)) {
            format = FULL_DATE_FORMAT;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.of("Asia/Shanghai")));
    }


    public static Date parseDate(String strDate, String format) {
        try {
            if (StringUtils.isBlank(format)) {
                format = FULL_DATE_FORMAT;
            }
            SimpleDateFormat sf = new SimpleDateFormat(format);
            return sf.parse(strDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String timeStampToStr(Long timestamp) {
        if (null == timestamp) {
            return null;
        }
        return dateTimeFormatter.format(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("Asia/Shanghai")));
    }

    /**
     * 加n天后的日期
     *
     * @param date
     * @param n
     * @return
     */
    public static Date addDays(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, n);
        return cal.getTime();
    }

    /**
     * 获取当天开始时间的 00:00:00 做起始时间
     * @return
     */

    public static String getStartTime() {
        LocalDate now = LocalDate.now();
        LocalDateTime time = now.atTime(LocalTime.MIN);
        String format = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }

    //当前天数+2 23:59:59..
    public static String getEndTime(Integer day) {
        LocalDate now = LocalDate.now();
        LocalDateTime time = now.plusDays(day).atTime(LocalTime.MAX);
        String format = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }

    public static void main(String[] args) {
//        String startTime = getStartTime();
//        System.out.println(startTime);
        String endTime = getEndTime(2);
        System.out.println(endTime);

    }


}
