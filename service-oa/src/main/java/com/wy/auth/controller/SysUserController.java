package com.wy.auth.controller;


import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.SysUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wy.auth.service.SysUserService;
import com.wy.common.config.exception.GuiguException;
import com.wy.common.result.Result;
import com.wy.common.utils.MD5;
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
    public Result updateStatus(@PathVariable Long id,@PathVariable Integer status) {
        userService.updateStatus(id, status);
        return Result.ok().message("修改成功");
    }

    @ApiOperation("更新用户数据")
    @PutMapping("/updateUser")
    public Result updateById(@RequestBody SysUser sysUser) {
        if (!sysUser.getPassword().isEmpty()) {
            String pass_md5 = MD5.encrypt(sysUser.getPassword());
            sysUser.setPassword(pass_md5);
        }
        userService.updateById(sysUser);
        return Result.ok();
    }

    @ApiOperation(value = "添加用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser user) {
        //密码进行加密，使用MD5
        String passwordMD5 = MD5.encrypt(user.getPassword());
        user.setPassword(passwordMD5);
        userService.save(user);
        return Result.ok();
    }

    @ApiOperation(value = "根据用户Id查询")
    @GetMapping("getById/{id}")
    public Result getById(@PathVariable Long id) {
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getId, id);
        List<SysUser> list = userService.list(lqw);
        if (list.size()<0){
            throw new GuiguException(201,"无此用户");
        }
        return Result.ok(list);
    }

    @ApiOperation("删除用户")
    @DeleteMapping("deleteById/{id}")
    public Result deleteById(@PathVariable Long id){
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getId,id);
        SysUser one = userService.getOne(lqw);
        if (one ==null) {
            throw new GuiguException(201,"无此用户");
        }
        boolean is_success = userService.remove(lqw);
        if (is_success) {
            return Result.ok().message("删除成功");
        }
        else {
            return Result.fail().message("删除失败");
        }
    }


}

