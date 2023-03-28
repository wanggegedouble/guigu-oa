package com.wy.auth.service.Impl;

import com.atguigu.model.system.SysUser;
import com.wy.auth.service.SysMenuService;
import com.wy.auth.service.SysUserService;
import com.wy.common.config.exception.GuiguException;
import com.wy.security.custom.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yeman
 * @Date: 2023-03-26-1:48
 * @Description:
 */
@Service()
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysMenuService menuService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getUserByUserName(username);
        if (user == null) {
            throw new GuiguException(201,"无此用户");
        }
        if (user.getStatus() == 1){
            throw new GuiguException(201,"用户被禁用");
        }
        // 根据用户id 查询用户权限
        List<String> userPermsList = menuService.findUserPermsByUserId(user.getId());
        // 创建List集合 封装最终的权限数据
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        for (String perms : userPermsList) {
            authList.add(new SimpleGrantedAuthority(perms.trim()));
        }
        return new CustomUser(user,authList);
    }
}
