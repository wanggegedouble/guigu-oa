package com.wy.auth.service.Impl;

import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUserRole;
import com.atguigu.vo.system.AssginRoleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wy.auth.mapper.SysRoleMapper;
import com.wy.auth.service.SysRoleService;
import com.wy.auth.service.SysUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysUserRoleService userRoleService;

    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        //1 查询所有角色，返回list集合
        List<SysRole> sysRoles = baseMapper.selectList(null);
        //2 根据user_id 查询用户角色表。查询用户所对应角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> existUserRoleList = userRoleService.list(wrapper);
        //从查询出来的用户id对应角色list集合，获取所有角色id
        List<Long> existRoleIdList =
                existUserRoleList.stream().map(c -> c.getRoleId()).collect(Collectors.toList());
        //3 根据角色Id 找打对应的角色信息 根据角色id 到角色list 集合查找
        List<SysRole> assignRoleList = new ArrayList<>();
        for(SysRole sysRole : sysRoles) {
            //比较
            if(existRoleIdList.contains(sysRole.getId())) {
                assignRoleList.add(sysRole);
            }
        }
        //4 把两部分结果封装成map 返回。
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList", assignRoleList);
       // roleMap.put("allRolesList", sysRoles);
        return roleMap;
    }

    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //把用户之前分配角色数据删除，用户角色关系表里面，根据userid删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assginRoleVo.getUserId());
        userRoleService.remove(wrapper);

        //重新进行分配
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        for(Long roleId:roleIdList) {
            if(StringUtils.isEmpty(roleId)) {
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assginRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            userRoleService.save(sysUserRole);
        }
    }
}
