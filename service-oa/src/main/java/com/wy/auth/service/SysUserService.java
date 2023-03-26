package com.wy.auth.service;

import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wy
 * @since 2023-03-11
 */
public interface SysUserService extends IService<SysUser> {

    //修该用户状态
    void updateStatus(Long id, Integer status);

    SysUser getUserByUserName(String username);
}
