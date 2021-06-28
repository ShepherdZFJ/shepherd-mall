package com.shepherd.mallsearch.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mallsearch.api.vo.BrandVO;
import com.shepherd.mallsearch.api.vo.CategoryVO;
import com.shepherd.mallsearch.api.vo.SearchResult;
import com.shepherd.mallsearch.api.vo.SpecVO;
import com.shepherd.mallsearch.api.service.SearchService;
import com.shepherd.mallsearch.config.ElasticSearchConfig;
import com.shepherd.mallsearch.dto.ProductSku;
import com.shepherd.mallsearch.enums.ErrorCodeEnum;
import com.shepherd.mallsearch.param.SearchParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/9 16:04
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    @Resource
    private RestHighLevelClient restHighLevelClient;
    private static final String PRODUCT_INDEX = "product";

    @Override
    public Boolean addProductToEsBatch(List<ProductSku> productSkuList) {
        if (CollectionUtils.isEmpty(productSkuList)) {
            throw new BusinessException(ErrorCodeEnum.UP_PRODUCT_IS_EMPTY.getCode(), ErrorCodeEnum.UP_PRODUCT_IS_EMPTY.getMessage());
        }
        try {
            BulkRequest bulkRequest = new BulkRequest();
            productSkuList.forEach(productSku -> {
                IndexRequest indexRequest = new IndexRequest("product");
                String productStr = JSON.toJSONString(productSku);
                indexRequest.source(productStr, XContentType.JSON);
                bulkRequest.add(indexRequest);
            });
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
            Boolean result = bulkResponse.hasFailures();

            List<BulkItemResponse> bulkItemResponses = Lists.newArrayList(bulkResponse.getItems());
            if (!CollectionUtils.isEmpty(bulkItemResponses)) {
                List<String> bulkItemIds = bulkItemResponses.stream().map(bulkItemResponse -> bulkItemResponse.getId()).collect(Collectors.toList());
                log.info("商品上架完成：{}", bulkItemIds);
            }
            return !result;
        } catch (Exception e) {
            log.error("商品上架失败：{}", e);
        }
        return false;
    }

    @Override
    public SearchResult searchProduct(SearchParam param) {

        try {
            //1、准备检索请求,动态构建出查询需要的DSL语句
            SearchRequest searchRequest = buildSearchRequest(param);
            //2、执行检索请求
            SearchResponse response = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

            //3、分析响应数据，封装成我们需要的格式
            SearchResult result = buildPageInfo(response, param);
            return result;
        } catch (Exception e) {
            log.error("搜索数据发生错误：", e);
            throw new BusinessException(ErrorCodeEnum.SEARCH_ERROR.getCode(), ErrorCodeEnum.SEARCH_ERROR.getMessage());
        }
    }


    /**
     * 构建结果数据
     * 模糊匹配，过滤（按照属性、分类、品牌，价格区间，库存），完成排序、分页、高亮,聚合分析功能
     *
     * @param response
     * @return
     */
    private SearchResult buildPageInfo(SearchResponse response, SearchParam param) {

        SearchResult result = new SearchResult();
        result.setProduct(getProductSkuList(response, param));
        result.setSpecs(getSpec(response));
        result.setBrands(getBrands(response));
        result.setCategoryList(getCategoryList(response));

        //分页信息-页码
        result.setPageNo(param.getPageNo());
        result.setPageSize(param.getPageSize());
        //分页信息、总记录数
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        //分页信息-总页码-计算
        int totalPages = (int) total % param.getPageSize() == 0 ?
                (int) total / param.getPageSize() : ((int) total / param.getPageSize() + 1);
        result.setPages(totalPages);
        return result;

    }

    /**
     * 返回所有查询到的商品，如果有关键字搜索，那么商品名称包含关键字需高亮显示处理
     * @param response
     * @param param
     * @return
     */
    List<ProductSku> getProductSkuList(SearchResponse response, SearchParam param) {
        SearchHits hits = response.getHits();

        List<ProductSku> productSkuList = new ArrayList<>();
        //遍历所有商品信息
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                ProductSku productSku = JSON.parseObject(sourceAsString, ProductSku.class);

                //判断是否按关键字检索，若是就显示高亮，否则不显示
                if (StringUtils.isNotBlank(param.getKeyword())) {
                    //拿到高亮信息显示标题
                    HighlightField name = hit.getHighlightFields().get("name");
                    String str = name.getFragments()[0].string();
                    productSku.setName(str);
                }
                productSkuList.add(productSku);
            }
        }
        return productSkuList;
    }

    /**
     * 返回查询到的所有规格参数，这里的实现方式就是查询时根据spec字段构造聚合查询，然后拿到结构之后对每一个spec进行转换提炼，
     * 最终得到相同规格属性的所有可选值。
     * @param response
     * @return
     */
    List<SpecVO> getSpec(SearchResponse response) {
        List<SpecVO> specs = new ArrayList<>();
        Map<String, Set<String>> specMap = new HashMap<>();
        Set<String> specSet  = new HashSet<>();
        ParsedStringTerms specAgg = response.getAggregations().get("spec_agg");
        for (Terms.Bucket bucket : specAgg.getBuckets()) {
            String key = bucket.getKeyAsString();
            specSet.add(key);
        }
        specSet.forEach(spec -> {
            Map<String, String> map = JSONObject.parseObject(spec, Map.class);
            map.forEach((key, value) -> {
                Set<String> specValues = specMap.get(key);
                if (specValues == null) {
                    specValues = new HashSet<String>();
                }
                specValues.add(value);
                specMap.put(key, specValues);
            });

        });
        specMap.forEach((key, value) -> {
            SpecVO spec = new SpecVO();
            spec.setSpecName(key);
            List<String> list = value.stream().sorted().collect(Collectors.toList());
            spec.setSpecValues(list);
            specs.add(spec);
        });
        return specs;
    }

    /**
     * 当前查询到的商品涉及到的所有品牌信息
     * @param response
     * @return
     */
    List<BrandVO> getBrands(SearchResponse response) {
        List<BrandVO> brands = new ArrayList<>();
        //获取到品牌的聚合
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            BrandVO brandVO = new BrandVO();

            //1、得到品牌的id
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVO.setBrandId(brandId);

            //2、得到品牌的名字
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            brandVO.setBrandName(brandName);

            //3、得到品牌的图片
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            brandVO.setBrandImg(brandImg);

            brands.add(brandVO);
        }
        return brands;
    }

    /**
     * 当前商品涉及到的所有分类信息
     * @param response
     * @return
     */
    List<CategoryVO> getCategoryList(SearchResponse response) {
        List<CategoryVO> categoryList = new ArrayList<>();
        ParsedLongTerms categoryAgg = response.getAggregations().get("category_agg");
        for (Terms.Bucket bucket : categoryAgg.getBuckets()) {
            CategoryVO categoryVO = new CategoryVO();
            //得到分类id
            String keyAsString = bucket.getKeyAsString();
            categoryVO.setCategoryId(Long.parseLong(keyAsString));
            //得到分类名
            ParsedStringTerms categoryNameAgg = bucket.getAggregations().get("category_name_agg");
            String catalogName = categoryNameAgg.getBuckets().get(0).getKeyAsString();
            categoryVO.setCategoryName(catalogName);
            categoryList.add(categoryVO);
        }
        return categoryList;
    }








    /**
     * 准备检索请求
     * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存），排序，分页，高亮，聚合分析
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        // 检索请求构建
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        /**
         * 查询：模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
         */
        //1. 构建 bool-query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //1.1 bool-must 模糊匹配
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", param.getKeyword()));
        }
        if (param.getSpecMap() != null) {
            Map<String, String> specMap = param.getSpecMap();
            specMap.forEach((key, value) -> {
                boolQueryBuilder.must(QueryBuilders.matchQuery("specMap." + key + ".keyword", value));
            });
        }

        //1.2.1 bool-filter categoryId 按照三级分类id查询
        if (param.getCategoryId() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryId", param.getCategoryId()));
        }
        //1.2.2 bool-filter brandId 按照品牌id查询
        if (!CollectionUtils.isEmpty(param.getBrandId())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        //1.2.4 bool-filter hasStock 按照是否有库存查询
        if (null != param.getHasStock()) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }
        //1.2.5 skuPrice bool-filter 按照价格区间查询
        if (StringUtils.isNotBlank(param.getPrice())) {
            //price形式为：1_500或_500或500_
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
            String[] price = param.getPrice().split("_");
            if (price.length == 2) {
                rangeQueryBuilder.gte(price[0]).lte(price[1]);
            } else if (price.length == 1) {
                if (param.getPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(price[1]);
                }
                if (param.getPrice().endsWith("_")) {
                    rangeQueryBuilder.gte(price[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        // 封装所有的查询条件
        searchSourceBuilder.query(boolQueryBuilder);


        /**
         * 排序，分页，高亮
         */
        // 2.1 排序  形式为sort=hotScore_asc/desc
        if (!StringUtils.isEmpty(param.getSort())) {
            String sort = param.getSort();
            // sort=hotScore_asc/desc
            String[] sortFields = sort.split("_");

            SortOrder sortOrder = "asc".equalsIgnoreCase(sortFields[1]) ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(sortFields[0], sortOrder);
        }

        // 2.2 分页 from = (pageNum - 1) * pageSize
        searchSourceBuilder.from((param.getPageNo() - 1) * param.getPageSize());
        searchSourceBuilder.size(param.getPageSize());

        // 2.3 高亮
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("name");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合分析
         */
        //1. 按照品牌进行聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);

        //1.1 品牌的子聚合-品牌名聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        //1.2 品牌的子聚合-品牌图片聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brand_agg);

        //2. 按照分类信息进行聚合
        TermsAggregationBuilder category_agg = AggregationBuilders.terms("category_agg");
        category_agg.field("categoryId").size(20);
        category_agg.subAggregation(AggregationBuilders.terms("category_name_agg").field("categoryName").size(1));
        searchSourceBuilder.aggregation(category_agg);

        TermsAggregationBuilder spec_agg = AggregationBuilders.terms("spec_agg");
        //这里的size要尽量大一点，这样才能拿到所有商品的spec聚合之后信息，才能提炼出所有规格属性和对应的可选项
        spec_agg.field("spec").size(10000);
        searchSourceBuilder.aggregation(spec_agg);

        log.info("构建的DSL语句 {}", searchSourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{PRODUCT_INDEX}, searchSourceBuilder);
        return searchRequest;
    }


}
