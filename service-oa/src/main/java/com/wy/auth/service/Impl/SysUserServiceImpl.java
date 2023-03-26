package com.wy.auth.service.Impl;

import com.atguigu.model.system.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wy.auth.mapper.SysUserMapper;
import com.wy.auth.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wy.common.config.exception.GuiguException;
import com.wy.security.custom.CustomUser;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser sysUser = baseMapper.selectById(id);
        if (sysUser == null) {
            throw new GuiguException(201,"无此用户");
        }
        sysUser.setStatus(status);
        baseMapper.updateById(sysUser);
    }

    @Override
    public SysUser getUserByUserName(String username) {
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getUsername,username);
        return baseMapper.selectOne(lqw);
    }


}
