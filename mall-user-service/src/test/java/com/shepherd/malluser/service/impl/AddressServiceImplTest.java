package com.shepherd.malluser.service.impl;

import com.shepherd.malluser.dto.AddressDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/13 14:39
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AddressServiceImplTest {
    @Resource
    private AddressServiceImpl addressService;

    @Test
    public void test() {
       List<AddressDTO> addressDTOList = addressService.testSpringCache(1l);
        System.out.println(addressDTOList);
    }

}