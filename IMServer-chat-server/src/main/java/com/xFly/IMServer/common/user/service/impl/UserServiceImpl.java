package com.xFly.IMServer.common.user.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xFly.IMServer.common.common.annotation.RedissonLock;
import com.xFly.IMServer.common.common.event.BlackUserEvent;
import com.xFly.IMServer.common.common.event.UserRegisterEvent;
import com.xFly.IMServer.common.common.utils.AssertUtil;
import com.xFly.IMServer.common.user.dao.BlackDao;
import com.xFly.IMServer.common.user.dao.ItemConfigDao;
import com.xFly.IMServer.common.user.domain.entity.Black;
import com.xFly.IMServer.common.user.domain.entity.ItemConfig;
import com.xFly.IMServer.common.user.domain.entity.UserBackpack;
import com.xFly.IMServer.common.user.domain.enums.BlackTypeEnum;
import com.xFly.IMServer.common.user.domain.enums.ItemEnum;
import com.xFly.IMServer.common.user.dao.UserBackpackDao;
import com.xFly.IMServer.common.user.dao.UserDao;
import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.domain.enums.ItemTypeEnum;
import com.xFly.IMServer.common.user.domain.enums.RoleEnum;
import com.xFly.IMServer.common.user.domain.vo.Req.BlackUserReq;
import com.xFly.IMServer.common.user.domain.vo.Req.ModifyNameReq;
import com.xFly.IMServer.common.user.domain.vo.Req.WearingBadgeReq;
import com.xFly.IMServer.common.user.domain.vo.Resp.BadgeResp;
import com.xFly.IMServer.common.user.domain.vo.Resp.UserInfoResp;
import com.xFly.IMServer.common.user.mapper.user.UserMapper;
import com.xFly.IMServer.common.user.service.IRoleService;
import com.xFly.IMServer.common.user.service.UserService;
import com.xFly.IMServer.common.user.service.adapter.UserAdapter;
import com.xFly.IMServer.common.user.service.cache.ItemCache;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService {

    @Resource
    private UserDao userDao;
    @Resource
    private BlackDao blackDao;
    @Resource
    private UserBackpackDao userBackpackDao;
    @Resource
    private ItemCache itemCache;
    @Resource
    private ItemConfigDao itemConfigDao;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private IRoleService roleService;

    /**
     * 用户注册,需要获取id
     * @param user
     */
    @Override
    public void register(User user) {
        userDao.save(user);
        // 发放物品
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this,user));
    }

    /**
     * 获取用户信息
     * @param uid
     * @return
     */
    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfoResp(user,countByValidItemId);
    }

    /**
     * 修改用户名
     * @param uid
     * @param req
     */
    @Override
    @Transactional
    @RedissonLock(key = "#uid")
    public void modifyName(Long uid, ModifyNameReq req) {
        // 判断名字是否重复
        String newName = req.getName();
        User oldUser = userDao.getByName(newName);
        AssertUtil.isEmpty(oldUser,"名字已经被强占了，请换一个哦~");
        // 判断改名卡够不够
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid,ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(firstValidItem,"改名卡次数不够了，等待后续活动哦~");
        // 使用改名卡
        boolean userSuccess = userBackpackDao.invalidItem(firstValidItem.getId());
        if (userSuccess) {
            // 改名
            userDao.modifyName(uid,newName);
        }
    }

    /**
     * 获取徽章列表
     * @param uid
     * @return
     */
    @Override
    public List<BadgeResp> badges(Long uid) {
        // 查询所有徽章
        List<ItemConfig> itemConfigList = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        // 查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid,itemConfigList.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        // 查询用户佩戴的徽章
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigList,backpacks,user);
    }

    @Override
    public void wearingBadge(Long uid, WearingBadgeReq req) {
        //确保有这个徽章
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, req.getBadgeId());
        AssertUtil.isNotEmpty(firstValidItem, "您没有这个徽章哦，快去达成条件获取吧");
        //确保物品类型是徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "该徽章不可佩戴");
        //佩戴徽章
        userDao.wearingBadge(uid, req.getBadgeId());
    }

    /**
     * 判断用户是否有权限
     * @param uid
     */
    @Override
    public void hasPower(Long uid) {
        boolean hasPower = roleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasPower,"您没有权限哦~");
    }

    /**
     * 拉黑目标用户
     * @param req 目标用户信息
     */
    @Override
    public void black(BlackUserReq req) {
        // 获取目标用户uid
        Long uid = req.getUid();
        // 将目标用户uid加入黑名单
        Black black = new Black();
        black.setTarget(uid.toString());
        black.setType(BlackTypeEnum.UID.getType());
        blackDao.save(black);
        // 将目标用户创建ip和登陆ip拉入黑名单
        User user = userDao.getById(uid);
        blackIp(user.getIpInfo().getCreateIp());
        blackIp(user.getIpInfo().getUpdateIp());
        // 监听事件，将拉黑消息发送
        applicationEventPublisher.publishEvent(new BlackUserEvent(this, user));
    }

    /**
     * 拉黑ip
     * @param ip
     */
    private void blackIp(String ip) {
        if (StringUtil.isBlank(ip)) {
            return;
        }
        // 将目标ip加入黑名单
        try {
            Black black = new Black();
            black.setTarget(ip);
            black.setType(BlackTypeEnum.IP.getType());
            blackDao.save(black);
        } catch (Exception e) {
            log.error("拉黑ip失败",e);
            log.error("duplicate black ip:{}",ip);
        }
    }
}
