package com.shepherd.mall.seckill.api.controller;

import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mall.seckill.api.vo.SeckillVO;
import com.shepherd.mall.seckill.api.service.SeckillService;
import com.shepherd.mall.seckill.dto.SeckillSessionDTO;
import com.shepherd.mall.vo.ResponseVO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/25 23:47
 */
@RestController
@RequestMapping("/api/mall/seckill")
@Api(tags = "秒杀相关接口")
@ResponseResultBody
public class SeckillController {
    @Resource
    private SeckillService seckillService;

    @GetMapping("/session")
    public SeckillSessionDTO getCurrentSeckillSessionInfo() {
        return null;
    }

    @PostMapping("/kill")
    public String kill(@RequestBody SeckillVO seckillVO) {
        String orderNo = seckillService.kill(seckillVO.getKillId(), seckillVO.getKey(), seckillVO.getNum());
        return orderNo;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/test1")
    public ResponseVO<String> test1() {
        return ResponseVO.success("test");
    }


}
