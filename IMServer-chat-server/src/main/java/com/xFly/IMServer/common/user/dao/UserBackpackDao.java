package com.xFly.IMServer.common.user.dao;

import com.xFly.IMServer.common.common.domain.enums.YesOrNoEnum;
import com.xFly.IMServer.common.user.domain.entity.UserBackpack;
import com.xFly.IMServer.common.user.mapper.user.UserBackpackMapper;
import com.xFly.IMServer.common.user.service.IUserBackpackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {

    /**
     * 获取改名卡数量
     * @param uid
     * @param itemId
     */
    public Integer getCountByValidItemId(Long uid, Long itemId) {
        return lambdaQuery().eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .count();
    }

    /**
     * 根据物品id获取一张
     * @param uid
     * @param id
     * @return
     */
    public UserBackpack getFirstValidItem(Long uid, Long id) {
        return lambdaQuery().eq(UserBackpack::getUid,uid)
                .eq(UserBackpack::getItemId,id)
                .eq(UserBackpack::getStatus,YesOrNoEnum.NO.getStatus())
                .last("limit 1")
                .one();
    }

    /**
     * 使用物品
     * @param id
     * @return
     */
    public boolean invalidItem(Long id) {
        return lambdaUpdate()
                .eq(UserBackpack::getId, id)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .set(UserBackpack::getStatus,YesOrNoEnum.YES.getStatus())
                .update();
    }

    /**
     * 获取用户拥有的徽章
     * @param uid
     * @param itemIds
     * @return
     */
    public List<UserBackpack> getByItemIds(Long uid, List<Long> itemIds) {
        return lambdaQuery().eq(UserBackpack::getId,uid)
                .in(UserBackpack::getItemId,itemIds)
                .eq(UserBackpack::getStatus,YesOrNoEnum.NO.getStatus())
                .list();
    }

    /**
     *
     * @param idempotent
     * @return
     */
    public UserBackpack getByIdp(String idempotent) {
        return lambdaQuery().eq(UserBackpack::getIdempotent,idempotent).one();
    }
}
