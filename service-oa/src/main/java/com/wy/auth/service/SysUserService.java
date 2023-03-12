package com.wy.auth.service;

import com.atguigu.model.system.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wy
 * @since 2023-03-11
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);

}
