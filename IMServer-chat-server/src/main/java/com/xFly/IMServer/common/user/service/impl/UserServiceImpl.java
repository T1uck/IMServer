package com.xFly.IMServer.common.user.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xFly.IMServer.common.common.utils.AssertUtil;
import com.xFly.IMServer.common.user.domain.entity.UserBackpack;
import com.xFly.IMServer.common.user.domain.enums.ItemEnum;
import com.xFly.IMServer.common.user.dao.UserBackpackDao;
import com.xFly.IMServer.common.user.dao.UserDao;
import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.domain.vo.Req.ModifyNameReq;
import com.xFly.IMServer.common.user.domain.vo.Resp.UserInfoResp;
import com.xFly.IMServer.common.user.mapper.user.UserMapper;
import com.xFly.IMServer.common.user.service.UserService;
import com.xFly.IMServer.common.user.service.adapter.UserAdapter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService {

    @Resource
    private UserDao userDao;
    @Resource
    private UserBackpackDao userBackpackDao;
    /**
     * 用户注册,需要获取id
     * @param user
     */
    @Override
    public void register(User user) {
        userDao.save(user);
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
}
