package com.shepherd.malladvertise.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.malladvertise.api.service.AdvertiseService;
import com.shepherd.malladvertise.dao.AdvertiseDAO;
import com.shepherd.malladvertise.dto.AdvertiseDTO;
import com.shepherd.malladvertise.entity.Advertise;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/1/14 19:51
 */
@Service
public class AdvertiseServiceImpl implements AdvertiseService {
    @Resource
    private AdvertiseDAO advertiseDAO;



    @Override
    public List<AdvertiseDTO> getAdvertiseList(Integer type) {
        LambdaQueryWrapper<Advertise> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Advertise::getIsDelete, CommonConstant.NOT_DEL);
        queryWrapper.eq(Advertise::getType, type);
        List<AdvertiseDTO> advertiseDTOList = advertiseDAO.selectList(queryWrapper).stream().map(advertise ->
                toAdvertiseDTO(advertise)).collect(Collectors.toList());
        return advertiseDTOList;
    }

    AdvertiseDTO toAdvertiseDTO(Advertise advertise) {
        if (advertise == null) {
            return null;
        }
        AdvertiseDTO advertiseDTO = MallBeanUtil.copy(advertise, AdvertiseDTO.class);
        advertiseDTO.setAdvertiseId(advertise.getId());
        return advertiseDTO;
    }

}
