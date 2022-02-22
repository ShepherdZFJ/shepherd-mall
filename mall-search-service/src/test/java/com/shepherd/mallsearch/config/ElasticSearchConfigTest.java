package com.shepherd.mallsearch.config;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/4/27 1:21
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticSearchConfigTest {
    @Resource
    private RestHighLevelClient client;

    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        // json 字符串
        indexRequest.source("{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}", XContentType.JSON);

        // KV 键值对
        // indexRequest.source("username", "zhangsan", "age", 12, "address", "sz");

        // 同步执行
        client.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
    }

    /**
     * 检索地址中带有 大悦城 的人员年龄分布和平均薪资
     */
    @Test
    public void searchData() throws IOException {
        // 1. 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定 DSL 检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 1.1 构建检索条件 address 包含 大悦城
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "大悦城"));
        // 1.2 按照年龄值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);
        // 1.3 计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);

        System.out.println("检索条件：" + searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);


        // 2. 执行检索, 获得响应
        SearchResponse searchResponse = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

        // 3. 分析结果
        // 3.1 获取所有查到的记录
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            // 数据字符串
            String jsonString = hit.getSourceAsString();
            System.out.println(jsonString);
            // 可以通过 json 转换成实体类对象
            // Account account = JSON.parseObject(jsonString, Account.class);
        }

        // 3.2 获取检索的分析信息(聚合数据等)
        Aggregations aggregations = searchResponse.getAggregations();
        // for (Aggregation aggregation : aggregations.asList()) {
        //     System.out.println("当前聚合名：" + aggregation.getName());
        // }
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄：" + keyAsString + " 岁的有 " + bucket.getDocCount() + " 人");
        }

        Avg balanceAvg1 = aggregations.get("balanceAvg");
        System.out.println("平均薪资: " + balanceAvg1.getValue());
    }

}