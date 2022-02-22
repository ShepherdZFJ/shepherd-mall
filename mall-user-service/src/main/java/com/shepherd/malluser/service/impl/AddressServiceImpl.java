package com.shepherd.malluser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.malluser.api.service.AddressService;
import com.shepherd.malluser.dao.AddressDAO;
import com.shepherd.malluser.dto.AddressDTO;
import com.shepherd.malluser.entity.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/18 11:24
 */
@Service
@Slf4j
public class AddressServiceImpl implements AddressService {
    @Resource
    private AddressDAO addressDAO;


    @Override
    public List<AddressDTO> getAddressList(Long userId) {
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, userId);
        queryWrapper.eq(Address::getIsDelete, CommonConstant.NOT_DEL);
        List<AddressDTO> addressDTOList = addressDAO.selectList(queryWrapper).parallelStream().map(address -> toAddressDTO(address)).collect(Collectors.toList());
        return addressDTOList;
    }

    @Override
    public AddressDTO getAddressDetail(Long id) {
        Address address = addressDAO.selectById(id);
        return toAddressDTO(address);
    }

    AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        AddressDTO addressDTO = MallBeanUtil.copy(address, AddressDTO.class);
        return addressDTO;
    }
}
