package com.xFly.IMServer.common.user.dao;

import com.xFly.IMServer.common.user.domain.entity.Role;
import com.xFly.IMServer.common.user.mapper.user.RoleMapper;
import com.xFly.IMServer.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role>{

}
