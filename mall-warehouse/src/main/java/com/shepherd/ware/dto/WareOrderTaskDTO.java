package com.shepherd.ware.dto;

import com.shepherd.ware.entity.WareOrderTask;
import com.shepherd.ware.entity.WareOrderTaskItem;
import lombok.Data;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/11 17:27
 */
@Data
public class WareOrderTaskDTO extends WareOrderTask {
    private List<WareOrderTaskItem> wareOrderTaskItems;
}
