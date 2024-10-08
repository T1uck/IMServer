package com.xFly.IMServer.common.user.dao;

import com.xFly.IMServer.common.user.domain.entity.ItemConfig;
import com.xFly.IMServer.common.user.mapper.user.ItemConfigMapper;
import com.xFly.IMServer.common.user.service.IItemConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> implements IItemConfigService {

    /**
     * 获取物品类型
     * @param type
     * @return
     */
    public List<ItemConfig> getByType(Integer type) {
        return lambdaQuery().eq(ItemConfig::getType, type).list();
    }
}
