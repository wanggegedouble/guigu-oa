package com.wy.auth.service;

import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.AssginRoleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {
    //获取用户角色数据
    Map<String, Object> findRoleDataByUserId(Long userId);
    // 为用户分配角色数据
    void doAssign(AssginRoleVo assginRoleVo);
}
