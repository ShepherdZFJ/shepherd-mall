package com.shepherd.malluser.api.controller;

import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mall.vo.LoginVO;
import com.shepherd.malluser.api.service.AddressService;
import com.shepherd.malluser.dto.AddressDTO;
import com.shepherd.malluser.utils.UserUtil;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/18 11:35
 */
@RestController
@RequestMapping("/api/mall/user/address")
@Api(tags = "地址管理")
@ResponseResultBody
public class AddressController {

    @Resource
    private AddressService addressService;

    @GetMapping
    public List<AddressDTO> getAddressList() {
        LoginVO loginVO = UserUtil.currentUser();
        Long userId = loginVO.getUserId();
        List<AddressDTO> addressList = addressService.getAddressList(userId);
        return addressList;
    }

    @GetMapping("/{id}")
    public AddressDTO getAddressDetail(@PathVariable("id") Long id) {
        AddressDTO addressDetail = addressService.getAddressDetail(id);
        return addressDetail;
    }

}
