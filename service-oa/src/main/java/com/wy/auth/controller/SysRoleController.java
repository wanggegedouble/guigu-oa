package com.wy.auth.controller;

import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wy.auth.service.SysRoleService;
import com.wy.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色管理接口")
@RequestMapping("/controller/sysRole")
@RestController
@Slf4j
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("查询所有角色")
    @GetMapping("/findAll")
    public Result findAll() {
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }

    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable(value = "page") Integer page,
                                @PathVariable(value = "limit") Integer limit,
                                SysRoleQueryVo sysRoleQueryVo){
        Page<SysRole> pageParam = new Page<>(page,limit);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
       String roleName = sysRoleQueryVo.getRoleName();
        if (!StringUtils.isEmpty(roleName)){
            wrapper.like(SysRole::getRoleName,roleName);
        }
        IPage<SysRole> sysRolePage = sysRoleService.page(pageParam, wrapper);
        return Result.ok(sysRolePage);
    }

    @ApiOperation("添加角色")
    @GetMapping("/save")
    public Result save(SysRole sysRole){
        boolean is_success = sysRoleService.save(sysRole);
        if (is_success){
            Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation("根据id查询")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        SysRole sysRoleOne = sysRoleService.getById(id);
        return Result.ok(sysRoleOne);
    }

    @ApiOperation("修改角色")
    @PutMapping("/update")
    public Result update(SysRole sysRole){
        boolean is_success = sysRoleService.updateById(sysRole);
        if (is_success){
            Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation("根据Id删除")
    @DeleteMapping("delete/{id}")
    public Result deleteById(@PathVariable Long id){
        boolean is_success = sysRoleService.removeById(id);
        if (is_success){
            return Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation("批量删除")
    @DeleteMapping("batchDelete")
    public Result batchDelete(@PathVariable List<Long> idList){
        boolean is_success = sysRoleService.removeByIds(idList);
        if (is_success){
            return Result.ok();
        }
        return Result.fail();
    }
}
