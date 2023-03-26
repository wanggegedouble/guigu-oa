package com.wy.auth.service.Impl;

import com.atguigu.model.system.SysUser;
import com.wy.auth.service.SysUserService;
import com.wy.common.config.exception.GuiguException;
import com.wy.security.custom.CustomUser;
import com.wy.security.custom.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @Author: Yeman
 * @Date: 2023-03-26-1:48
 * @Description:
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getUserByUserName(username);
        if (user == null) {
            throw new GuiguException(201,"无此用户");
        }
        if (user.getStatus() ==1){
            throw new GuiguException(201,"用户被禁用");
        }
        return new CustomUser(user, Collections.emptyList());
    }
}
