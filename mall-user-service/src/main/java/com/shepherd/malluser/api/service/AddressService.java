package com.shepherd.malluser.api.service;

import com.shepherd.malluser.dto.AddressDTO;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/18 11:04
 */
public interface AddressService {

    List<AddressDTO> getAddressList(Long userId);
}
