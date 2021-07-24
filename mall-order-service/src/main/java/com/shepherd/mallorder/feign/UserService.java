package com.shepherd.mallorder.feign;

import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallorder.dto.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/20 19:06
 */
@FeignClient(name="${micro-server.mall-user}",path = "/api/mall/user")
public interface UserService {

    @GetMapping("/address")
    ResponseVO<List<Address>> getUserAddressList();

    @GetMapping("/integration")
    ResponseVO<Integer> getUserIntegration();

    @GetMapping("/address/{id}")
    ResponseVO<Address> getAddressDetail(@PathVariable("id") Long id);
}
