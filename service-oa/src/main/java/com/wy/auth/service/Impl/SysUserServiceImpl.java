package com.wy.auth.service.Impl;

import com.atguigu.model.system.SysUser;
import com.wy.auth.mapper.SysUserMapper;
import com.wy.auth.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wy
 * @since 2023-03-11
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

}
