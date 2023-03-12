package com.wy.auth.controller;


import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.SysUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wy.auth.service.SysUserService;
import com.wy.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wy
 * @since 2023-03-11
 */
@RestController
@RequestMapping("controller/SysUser")
@Api(tags = "用户管理接口")
@Slf4j
public class SysUserController {
    @Autowired
    private SysUserService userService;

    @ApiOperation("查询所有用户")
    @GetMapping("/findUsers")
    public Result findUsers(){
        List<SysUser> users = userService.list();
        return Result.ok(users);
    }

    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryUser(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysUserQueryVo sysUserQueryVo){
        Page<SysUser> pageParam = new Page<>(page,limit);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        String userName = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();

        if (!StringUtils.isEmpty(userName)){
            wrapper.like(SysUser::getUsername,userName);
        }
        if (!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge(SysUser::getCreateTime,createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)){
            wrapper.le(SysUser::getCreateTime,createTimeEnd);
        }
        IPage<SysUser> sysUserPage = userService.page(pageParam,wrapper);
        return Result.ok(sysUserPage);
    }

    @ApiOperation("修改用户状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id,@PathVariable Integer status){
        userService.updateStatus(id,status);
        return Result.ok();
    }

    @ApiOperation("更新用户数据")
    @PutMapping("/updateUser")
    public Result updateById(@RequestBody SysUser sysUser) {
        userService.updateById(sysUser);
        return Result.ok();
    }


}

