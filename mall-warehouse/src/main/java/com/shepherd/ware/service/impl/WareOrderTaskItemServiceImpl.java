package com.shepherd.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shepherd.ware.api.service.WareOrderTaskItemService;
import com.shepherd.ware.dao.WareOrderTaskItemDAO;
import com.shepherd.ware.entity.WareOrderTaskItem;
import org.springframework.stereotype.Service;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/11 16:48
 */
@Service
public class WareOrderTaskItemServiceImpl extends ServiceImpl<WareOrderTaskItemDAO, WareOrderTaskItem> implements WareOrderTaskItemService {
}
