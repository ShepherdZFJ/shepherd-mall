package com.shepherd.mall.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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



}
