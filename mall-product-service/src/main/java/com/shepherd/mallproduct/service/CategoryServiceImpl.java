package com.shepherd.mallproduct.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallproduct.api.service.CategoryService;
import com.shepherd.mallproduct.cache.CompositeCache;
import com.shepherd.mallproduct.dao.CategoryDAO;
import com.shepherd.mallproduct.dto.CategoryDTO;
import com.shepherd.mallproduct.entity.Category;
import com.shepherd.mallproduct.query.CategoryQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 17:27
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private CompositeCache compositeCache;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;

    private Lock lock = new ReentrantLock();

    private final static String CATEGORY_CACHE = "category_cache";

    /**
     * 缓存失效的问题三大常见问题：
     * 1.缓存穿透：查询一个不存在的key，这样缓存就永远不命中，去数据库查询。解决方案：缓存空数据，或者使用布隆过滤器
     * 2.缓存击穿：大量并发进来同时查询一个正好过期的数据。解决方案：加锁 ? 默认是无加锁的;使用sync = true来解决击穿问题
     * 3.缓存雪崩：大量的key同时过期。解决：加随机时间。加上过期时间
     */


    /**
     * 缓存经典场景伪代码：
     * value = cache.load(key);//从缓存加载数据
     *     If(value == null){
     *        value = db.load(key);//从数据库加载数据
     *        cache.put(key,value);//保存到 cache 中
     *     }
     *     return value;
     * @return
     */
    @Override
    public List<CategoryDTO> test() {
        //解决单机模式下缓存击穿的问题：通过加单机版的并发锁：加本地锁：synchronized, juc的lock
        String categoryJson = stringRedisTemplate.opsForValue().get(CATEGORY_CACHE);
        if (StringUtils.isBlank(categoryJson)) {
            List<CategoryDTO> categoryDTOList = getCategoryTreeWithRedissonLock();
            categoryJson = JSON.toJSONString(categoryDTOList);
            stringRedisTemplate.opsForValue().set(CATEGORY_CACHE, categoryJson, 5, TimeUnit.MINUTES);
            return categoryDTOList;
        }
        log.info("<=======通过redis缓存查询商品分类数据=======>");
        return JSONObject.parseArray(categoryJson, CategoryDTO.class);
    }

    /**
     * 使用synchronized关键字加锁
     * @return
     */
    List<CategoryDTO> getCategoryTreeWithSynchronized() {
        synchronized (this) {
            String categoryJson = stringRedisTemplate.opsForValue().get(CATEGORY_CACHE);
            if (StringUtils.isBlank(categoryJson)) {
                List<CategoryDTO> categoryDTOList = getList();
                List<CategoryDTO> list = new ArrayList<>();
                listToTree(categoryDTOList, list);
                categoryJson = JSON.toJSONString(categoryDTOList);
                stringRedisTemplate.opsForValue().set(CATEGORY_CACHE, categoryJson, 5, TimeUnit.MINUTES);
            }
            return JSONObject.parseArray(categoryJson, CategoryDTO.class);
        }
    }

    /**
     * 使用JUC加锁
     * @return
     */
    List<CategoryDTO> getCategoryTreeWithLock() {
        lock.lock();
        try {
            String categoryJson = stringRedisTemplate.opsForValue().get(CATEGORY_CACHE);
            if (StringUtils.isBlank(categoryJson)) {
                List<CategoryDTO> categoryDTOList = getList();
                List<CategoryDTO> list = new ArrayList<>();
                listToTree(categoryDTOList, list);
                categoryJson = JSON.toJSONString(categoryDTOList);
                stringRedisTemplate.opsForValue().set(CATEGORY_CACHE, categoryJson, 5, TimeUnit.MINUTES);
            }
            return JSONObject.parseArray(categoryJson, CategoryDTO.class);
        } catch (Exception e) {
            log.error("发生异常：", e);
        } finally {
          lock.unlock();
        }
        return null;
    }

    /**
     * 使用redis实现分布式锁，使用命令set key value [EX seconds] [NX|XX]，但有如下问题：
     * 1、加锁和设置过期时间必须是原子性的，不然有可能加锁之后还没有执行到设置过期时间代码时服务不可用，锁一直不释放，造成死锁
     * 2、主动删除key，即解锁需要注意：如果在key设置的过期时间之前删除key那么没问题，测试业务执行完正常解锁，但是如果删除key在过期之后就有问题了，
     * 此时当前线程的锁已经因为过期自动解锁，另外的请求线程拿到锁，所以这时候删除的不是当前线程的锁，
     * 解决方案：利用CAS原理，删除之前先比较value值是不是之前存放进去的，这里为了保证每次的value都不一样，使用uuid生成value，但是这时候有一种情况就是
     * 你去拿value的时候锁没有过期，此时拿到value和传入的一样，但是当你刚刚获取完value之后锁就过期了，其他请求线程拿到锁，然后你再根据传入的值
     * 和获取的value值去删除锁，这时候删除的是其他请求线程的锁，造成问题，所以利用CAS原理值比较和删除锁必须是原子性操作
     * @return
     */
    List<CategoryDTO> getCategoryTreeWithRedisLock() {

        //1、占分布式锁。去redis占坑 设置过期时间必须和加锁是同步的，保证原子性（避免死锁）
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            log.info("获取分布式锁成功...");
            List<CategoryDTO> categoryDTOList = null;
            try {
                String categoryJson = stringRedisTemplate.opsForValue().get(CATEGORY_CACHE);
                if (StringUtils.isBlank(categoryJson)) {
                    //加锁成功...，并且redis还没有数据库，执行业务
                    categoryDTOList = getCategoryTree();
                    stringRedisTemplate.opsForValue().set(CATEGORY_CACHE, JSON.toJSONString(categoryDTOList), 5, TimeUnit.MINUTES);
                } else {
                    categoryDTOList = JSON.parseArray(categoryJson, CategoryDTO.class);
                }
            } finally {
                // lua 脚本解锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 删除锁
                stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList("lock"), uuid);
            }
            //先去redis查询下保证当前的锁是自己的
            //获取值对比，对比成功删除=原子性 lua脚本解锁
            // String lockValue = stringRedisTemplate.opsForValue().get("lock");
            // if (uuid.equals(lockValue)) {
            //     //删除我自己的锁
            //     stringRedisTemplate.delete("lock");
            // }
            return categoryDTOList;
        } else {
            log.info("获取分布式锁失败...等待重试...");
            //加锁失败...重试机制
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                log.error("redis分布式锁发生错误", e);
            }
            return tryAgainWithTime();
        }
    }

    List<CategoryDTO> tryAgainWithTime() {
        long startTime = System.currentTimeMillis();
        while(true) {
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 1000) {
                throw new BusinessException("获取数据超时，请重试");
            }
            String categoryJson = stringRedisTemplate.opsForValue().get(CATEGORY_CACHE);
            if (StringUtils.isBlank(categoryJson)) {
                continue;
            }
           List<CategoryDTO> categoryDTOList = JSON.parseArray(categoryJson, CategoryDTO.class);
            return categoryDTOList;
        }
    }

    List<CategoryDTO> tryAgainWithFor() {
        for (int i = 0; i < 5; i++) {
            String categoryJson = stringRedisTemplate.opsForValue().get(CATEGORY_CACHE);
            if (StringUtils.isBlank(categoryJson)) {
                continue;
            }
            List<CategoryDTO> categoryDTOList = JSON.parseArray(categoryJson, CategoryDTO.class);
            return categoryDTOList;
        }
        throw new BusinessException("获取数据超时，请重试");
    }

    /**
     * 通过redisson实现分布式锁
     * @return
     */
    List<CategoryDTO> getCategoryTreeWithRedissonLock() {
        // 1. 获取一把锁
        RLock rLock = redissonClient.getLock("category-lock");
        // 2. 加锁, 阻塞式等待
        rLock.lock();
        try {
            String categoryJson = stringRedisTemplate.opsForValue().get(CATEGORY_CACHE);
            if (StringUtils.isBlank(categoryJson)) {
                List<CategoryDTO> categoryDTOList = getList();
                List<CategoryDTO> list = new ArrayList<>();
                listToTree(categoryDTOList, list);
                categoryJson = JSON.toJSONString(categoryDTOList);
                stringRedisTemplate.opsForValue().set(CATEGORY_CACHE, categoryJson, 5, TimeUnit.MINUTES);
            }
            return JSONObject.parseArray(categoryJson, CategoryDTO.class);
        } catch (Exception e) {
            throw new BusinessException("redisson分布式锁发生错误");
        } finally {
            // 3. 解锁 假设解锁代码没有运行，Redisson 会出现死锁吗？（不会）
            rLock.unlock();
        }

    }








    @Override
    public List<CategoryDTO> getCategoryList() {
        List<CategoryDTO> categoryDTOList = compositeCache.get("category_cache", () -> getCategoryTree());
        if (CollectionUtils.isEmpty(categoryDTOList)) {
            categoryDTOList = getCategoryTree();
        }
        return categoryDTOList;
    }

    List<CategoryDTO> getCategoryTree() {
        List<CategoryDTO> categoryDTOList = getList();
        List<CategoryDTO> list = new ArrayList<>();
        listToTree(categoryDTOList, list);
        return list;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addCategory(CategoryDTO categoryDTO) {
        Category category = MallBeanUtil.copy(categoryDTO, Category.class);
        category.setIsDelete(CommonConstant.NOT_DEL);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        category.setStatus(1);
        int insert = categoryDAO.insert(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delBatch(List<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return false;
        }
        UpdateWrapper<Category> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", categoryIds);
        updateWrapper.set("is_delete", CommonConstant.DEL);
        updateWrapper.set("update_time", new Date());
        int update = categoryDAO.update(new Category(), updateWrapper);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategory(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return false;
        }
        update(categoryDTO);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategory(List<CategoryDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        list.forEach(categoryDTO -> {
            update(categoryDTO);
        });
        return true;
    }

    @Override
    public CategoryDTO getCategory(Long categoryId) {
        return null;
    }

    @Override
    public List<Long> getCategoryIds(Long categoryId) {
        List<CategoryDTO> categoryDTOList = getList();
        List<Long> ids = new ArrayList<>();
        ids.add(categoryId);
        getIds(categoryDTOList, categoryId, ids);
        return ids;
    }

    @Override
    public CategoryDTO getCategoryDetail(Long categoryId) {
        Category category = categoryDAO.selectById(categoryId);
        return toCategoryDTO(category);
    }

    @Override
    public List<CategoryDTO> getCategoryList(List<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Category::getId, categoryIds);
        List<CategoryDTO> categoryDTOList = categoryDAO.selectList(queryWrapper).stream().map(category -> toCategoryDTO(category)).collect(Collectors.toList());
        return categoryDTOList;
    }


    private List<Long> getIds(List<CategoryDTO>categoryDTOList, Long categoryId, List<Long> ids) {
        if (CollectionUtils.isEmpty(categoryDTOList)) {
            return null;
        }
        for (CategoryDTO categoryDTO: categoryDTOList) {
            if (Objects.equals(categoryDTO.getParentId(), categoryId)) {
                ids.add(categoryDTO.getCategoryId());
                getIds(categoryDTOList, categoryDTO.getCategoryId(), ids);
            }
        }
        return ids;
    }

    private void update(CategoryDTO categoryDTO) {
        Category category = MallBeanUtil.copy(categoryDTO, Category.class);
        category.setId(categoryDTO.getCategoryId());
        int i = categoryDAO.updateById(category);
    }

    //只获取类目列表，不转树结构
    private List<CategoryDTO> getList() {
        log.info("<=======通过数据库查询商品分类数据=======>");
        QueryWrapper<Category>queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", CommonConstant.NOT_DEL);
        List<Category> categoryList = categoryDAO.selectList(queryWrapper);
        List<CategoryDTO> categoryDTOList = categoryList.stream().map(category -> toCategoryDTO(category)).collect(Collectors.toList());
        return categoryDTOList;
    }

    private List<CategoryDTO> listToTree(List<CategoryDTO> categoryDTOList, List<CategoryDTO> list) {
        for (CategoryDTO categoryDTO :categoryDTOList) {
            if (categoryDTO.getParentId() == null) {
                list.add(categoryDTO);
            }
            for (CategoryDTO node: categoryDTOList) {
                if (Objects.equals(node.getParentId(), categoryDTO.getCategoryId())) {
                    if (categoryDTO.getNodeList() == null) {
                        categoryDTO.setNodeList(new ArrayList<>());
                    }
                    categoryDTO.getNodeList().add(node);
                }
            }
        }
        return list;
    }


    private CategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDTO categoryDTO = MallBeanUtil.copy(category, CategoryDTO.class);
        categoryDTO.setCategoryId(category.getId());
        return categoryDTO;
    }
}
