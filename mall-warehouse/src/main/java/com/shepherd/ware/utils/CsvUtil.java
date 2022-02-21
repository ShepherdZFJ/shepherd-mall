//package com.shepherd.ware.utils;
//
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.Lists;
//import com.opencsv.CSVWriter;
//import com.shepherd.mall.utils.DateUtil;
//import com.shepherd.ware.entity.WareOrderTask;
//import org.apache.commons.io.FileUtils;
//import org.checkerframework.checker.units.qual.C;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * @author fjzheng
// * @version 1.0
// * @date 2021/8/13 15:26
// */
//public class CsvUtil {
//
//    public static void writeCSV() throws IOException {
//        File file = new File("test.csv");
//        CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
////        List<WareOrderTask> list = ImmutableList.of(
////                WareOrderTask.builder().orderId(1l).orderNo("order1111").receiverName("郑方军").createTime(new Date()).build(),
////                WareOrderTask.builder().orderId(2l).orderNo("order2222").receiverName("晚静").createTime(new Date()).build()
////        );
//        List<String[]> dataList = new ArrayList<>();
//        for (int i = 1; i <= 1000000; i++) {
//            List<String> value1 = Lists.newArrayList(String.valueOf(i), UUID.randomUUID().toString(), "郑方军", "郑方军11111", DateUtil.formatDate(new Date(), null), "fgdfkldfkgdlfgldfv");
//            dataList.add(value1.toArray(new String[value1.size()]));
//        }
//        csvWriter.writeAll(dataList);
//        csvWriter.close();
//    }
//
//    public static void testJSON() throws IOException {
//        File file = new File("testJSON.txt");
//        List<Test> list = new ArrayList<>();
//        for (int i = 1; i <=1000000; i++) {
//            Test test = Test.builder().id((long) i).no("orderno-12345678").name("郑方军").date(new Date()).uuid(UUID.randomUUID().toString()).str("好见不见").build();
//            list.add(test);
//        }
//        FileUtils.writeByteArrayToFile(file, JSONObject.toJSONString(list).getBytes(StandardCharsets.UTF_8));
//
//    }
//
//    public static void main(String[] args) throws IOException {
//       // writeCSV();
////        testJSON();
////        Date date = new Date(System.currentTimeMillis());
////        date.setTime(0);
////        System.out.println(date);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Calendar now = Calendar.getInstance();
//        now.setTime(DateUtil.parseDate("2021-08-15 12:23:45", null));
//
//        now.set(Calendar.HOUR, 0);
//
//        now.set(Calendar.MINUTE, 0);
//
//        now.set(Calendar.SECOND, 0);
//
//        System.out.println(sdf.format(now.getTime()));
//
//        now.set(Calendar.HOUR_OF_DAY, 0);
//
//        System.out.println(sdf.format(now.getTime()));
//
//    }
//}
