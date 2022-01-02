package com.shepherd.malluser.spel;


import com.shepherd.malluser.entity.Address;
import com.shepherd.malluser.spel.aspectj.LogRecord;
import org.springframework.stereotype.Component;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/16 17:52
 */
@Component
public class TestExpression {
    @DistributeExceptionHandler(attachmentId = "修改了订单的配送员：从“#address.province”, 修改到“#address.detailAddress”")
    public void test(Address address){
        String city = address.getCity();
        int length = city.length();
        System.out.println(address);
    }

    public Long getAddressId(Address address) {
        return address.getId();
    }

    @LogRecord(success = "成功了", fail = "失败了", bizNo = "#address.id", detail = "修改了订单的配送员：从“{queryOldUser{#request.deliveryOrderNo()}}”, 修改到“{deveryUser{#request.userId}}")
    public void testAspectj(Address address) {
        address.setIsDefault(1);
    }

}
