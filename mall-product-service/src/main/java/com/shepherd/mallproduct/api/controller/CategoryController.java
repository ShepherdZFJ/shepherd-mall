package com.shepherd.mallproduct.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallproduct.api.service.CategoryService;
import com.shepherd.mallproduct.api.vo.CategoryVO;
import com.shepherd.mallproduct.dto.CategoryDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/10/11 23:40
 */
@RestController
@ResponseResultBody
@RequestMapping("/api/mall/product/category")
@Api("商品类目相关接口")
public class CategoryController {
    @Resource
    private CategoryService categoryService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping
    @ApiOperation("添加商品类目")
    public void addCategory(@RequestBody CategoryVO categoryVO) {
        categoryService.addCategory(MallBeanUtil.copy(categoryVO, CategoryDTO.class));
    }

    @GetMapping
    @ApiOperation("获取商品类目列表")
    public List<CategoryDTO> getCategoryList() {
        List<CategoryDTO> categoryList = categoryService.getCategoryList();
        return categoryList;
    }


    @PutMapping
    @ApiOperation("更新类目")
    public void updateCategory(@RequestBody CategoryVO categoryVO) {
        CategoryDTO categoryDTO = MallBeanUtil.copy(categoryVO, CategoryDTO.class);
        categoryService.updateCategory(categoryDTO);
    }

    @PutMapping("/batch")
    @ApiOperation("批量更新类目")
    public void updateCategoryBatch(@RequestBody CategoryVO categoryVO) {
        categoryService.updateCategory(categoryVO.getCategoryDTOList());
    }

    @DeleteMapping
    @ApiOperation("删除类目(批量)")
    public void delBatch(@RequestBody CategoryVO categoryVO) {
        categoryService.delBatch(categoryVO.getCategoryIds());
    }

    @GetMapping("/{categoryId}")
    @ApiOperation("获取类目详情")
    public CategoryDTO getCategoryDetail(@PathVariable("categoryId") Long categoryId) {
        return categoryService.getCategoryDetail(categoryId);
    }

    @GetMapping("/test")
    @ApiOperation("test")
    public List<CategoryDTO> test() {
        return categoryService.test();
    }


    @GetMapping(value = "/hello")
    public String hello() {

        //1、获取一把锁，只要锁的名字一样，就是同一把锁
        RLock rLock = redissonClient.getLock("my-lock");

        //2、加锁
        rLock.lock();      //阻塞式等待。默认加的锁都是30s
        //1）、锁的自动续期，如果业务超长，运行期间自动锁上新的30s。不用担心业务时间长，锁自动过期被删掉
        //2）、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认会在30s内自动过期，不会产生死锁问题
        // myLock.lock(10,TimeUnit.SECONDS);   //10秒钟自动解锁,自动解锁时间一定要大于业务执行时间
        //问题：在锁时间到了以后，不会自动续期
        //1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是 我们制定的时间
        //2、如果我们指定锁的超时时间，就使用 lockWatchdogTimeout = 30 * 1000 【看门狗默认时间】
        //只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】,每隔10秒都会自动的再次续期，续成30秒
        // internalLockLeaseTime 【看门狗时间】 / 3， 10s
        try {
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //3、解锁  假设解锁代码没有运行，Redisson会不会出现死锁
            System.out.println("释放锁..." + Thread.currentThread().getId());
            rLock.unlock();
        }

        return "hello";
    }


    /**
     * 保证一定能读到最新数据，修改期间，写锁是一个排它锁（互斥锁、独享锁），读锁是一个共享锁
     * 写锁没释放读锁必须等待
     * 读 + 读 ：相当于无锁，并发读，只会在Redis中记录好，所有当前的读锁。他们都会同时加锁成功
     * 写 + 读 ：必须等待写锁释放
     * 写 + 写 ：阻塞方式
     * 读 + 写 ：有读锁。写也需要等待
     * 只要有读或者写的存都必须等待
     * @return
     */

    @GetMapping(value = "/write")
    @ResponseBody
    public String writeValue() {
        String s = "";
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.writeLock();
        try {
            //1、改数据加写锁，读数据加读锁
            rLock.lock();
            s = UUID.randomUUID().toString();
            ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            ops.set("writeValue",s);
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }


    @GetMapping(value = "/read")
    @ResponseBody
    public String readValue() {
        String s = "";
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        //加读锁
        RLock rLock = readWriteLock.readLock();
        try {
            rLock.lock();
            ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            s = ops.get("writeValue");
            try { TimeUnit.SECONDS.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }

        return s;
    }


    /**
     * 车库停车
     * 3车位
     * 信号量也可以做分布式限流
     */
    @GetMapping(value = "/park")
    @ResponseBody
    public String park() throws InterruptedException {

        RSemaphore park = redissonClient.getSemaphore("park");
        park.acquire();     //获取一个信号、获取一个值,占一个车位
        boolean flag = park.tryAcquire();
        if (flag) {
            //执行业务
        } else {
            return "error";
        }

        return "ok=>" + flag;
    }

    @GetMapping(value = "/go")
    @ResponseBody
    public String go() {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();     //释放一个车位
        return "ok";
    }


    /**
     * 放假、锁门
     * 1班没人了
     * 5个班，全部走完，我们才可以锁大门
     * 分布式闭锁
     */

    @GetMapping(value = "/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {

        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();       //等待闭锁完成

        return "放假了...";
    }

    @GetMapping(value = "/gogogo/{id}")
    @ResponseBody
    public String gogogo(@PathVariable("id") Long id) {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();       //计数-1

        return id + "班的人都走了...";
    }

}
