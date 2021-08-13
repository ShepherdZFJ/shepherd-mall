package com.shepherd.ware.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.opencsv.CSVWriter;
import com.shepherd.ware.entity.WareOrderTask;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/13 15:26
 */
public class CsvUtil {

    public static void writeCSV() throws IOException {
        File file = new File("test.csv");
        CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
//        List<WareOrderTask> list = ImmutableList.of(
//                WareOrderTask.builder().orderId(1l).orderNo("order1111").receiverName("郑方军").createTime(new Date()).build(),
//                WareOrderTask.builder().orderId(2l).orderNo("order2222").receiverName("晚静").createTime(new Date()).build()
//        );
        List<String[]> dataList = new ArrayList<>();
        List<String> value1 = Lists.newArrayList("1", "order111", "郑方军", "2021-08-13 15:46:11");
        List<String> value2 = Lists.newArrayList("2", "order221", "wanjing", "2021-08-13 13:46:11");

        dataList.add(value1.toArray(new String[value1.size()]));
        dataList.add(value2.toArray(new String[value2.size()]));

        csvWriter.writeAll(dataList);
        csvWriter.close();
    }

    public static void main(String[] args) throws IOException {
        writeCSV();
    }
}
