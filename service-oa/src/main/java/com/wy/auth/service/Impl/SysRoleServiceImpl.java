package com.wy.auth.service.Impl;

import com.atguigu.model.system.SysRole;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wy.auth.mapper.SysRoleMapper;
import com.wy.auth.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}
