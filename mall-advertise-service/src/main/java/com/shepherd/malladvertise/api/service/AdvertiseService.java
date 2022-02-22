package com.shepherd.malladvertise.api.service;

import com.shepherd.malladvertise.dto.AdvertiseDTO;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/1/14 19:42
 */
public interface AdvertiseService {
    List<AdvertiseDTO> getAdvertiseList(Integer type);
}
