package com.shepherd.malladvertise.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.shepherd.malladvertise.api.service.AdvertiseService;
import com.shepherd.malladvertise.dao.AdvertiseDAO;
import com.shepherd.malladvertise.dto.AdvertiseDTO;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import com.xpand.starter.canal.annotation.UpdateListenPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/1/14 16:29
 */
@CanalEventListener
public class AdvListener {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private AdvertiseService advertiseService;

    //当数据被添加的时候触发
    // CanalEntry.EventType eventType  监听到的操作的类型  INSERT  UPDATE ,DELETE ,CREATE INDEX ,GRAND
    // CanalEntry.RowData rowData 被修改的数据()
 /*   @InsertListenPoint
    public void onEvent(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //do something...

        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            System.out.println(column.getName()+":"+column.getValue());
        }
    }*/
    //当数据被更新的时候触发
//    @UpdateListenPoint
//    public void onEvent1(CanalEntry.RowData rowData) {
//        //do something...
//        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
//        for (CanalEntry.Column column : afterColumnsList) {
//            System.out.println(column.getName()+":"+column.getValue());
//        }
//    }
    // 当数据被删除的时候触发
   /* @DeleteListenPoint
    public void onEvent3(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //do something...
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            System.out.println(column.getName()+":"+column.getValue());
        }
    }*/

    //自定义事件的触发
    // destination = "example" 指定某一个目的地 一定要和配置文件中的目录保持一致
    //schema = "canal-test" 要监听的数据库实例
    //table = {"t_user", "test_table"},   要监听的表
    // eventType = CanalEntry.EventType.UPDATE  要监听的类型
    /*@ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content"}, eventType = {CanalEntry.EventType.UPDATE,CanalEntry.EventType.INSERT,CanalEntry.EventType.DELETE})
    public void onEvent4(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //do something...

        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            System.out.println(column.getName()+":"+column.getValue());
        }
    }*/


    /**
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example", schema = "mall", table = {"advertise"}, eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT, CanalEntry.EventType.DELETE})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String type = getColumnValue(eventType, rowData);
        List<AdvertiseDTO> advertiseDTOList = advertiseService.getAdvertiseList(Integer.valueOf(type));
        stringRedisTemplate.boundValueOps("advertise_" + type).set(JSON.toJSONString(advertiseDTOList));
    }

    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //1.判断更改类型 如果是删除 则需要获取到before的数据
        String type = "";
        if (CanalEntry.EventType.DELETE == eventType) {
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                //column.getName(列的名称   column.getValue() 列对应的值
                if (column.getName().equals("type")) {
                    column.getValue();
                    type = column.getValue();
                    return type;
                }
            }
        } else {
            //2判断是 更新 新增 获取after的数据
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            for (CanalEntry.Column column : afterColumnsList) {
                //column.getName(列的名称   column.getValue() 列对应的值
                if (column.getName().equals("type")) {
                    type = column.getValue();
                    return type;
                }
            }
        }
        //3.返回
        return type;
    }


}
