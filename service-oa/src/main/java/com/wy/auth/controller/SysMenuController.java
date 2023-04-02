package com.wy.auth.controller;


import com.atguigu.model.system.SysMenu;
import com.atguigu.vo.system.AssginMenuVo;
import com.wy.auth.service.SysMenuService;
import com.wy.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author wy
 * @since 2023-03-12
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/controller/SysMenu")
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    @ApiOperation("菜单列表")
    @GetMapping("findMeus")
    public Result findMenus(){
       List<SysMenu> menuList = menuService.findMenus();
        return Result.ok(menuList);
    }

    @ApiOperation("删除菜单")
    @DeleteMapping("deleteMenu/{id}")
    public Result deleteMenuById(@PathVariable Long id) {
        menuService.removeMenuById(id);
        return Result.ok();
    }

    
    //查询所有菜单和角色分配的菜单
    @ApiOperation("查询所有菜单和角色分配的菜单")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId) {
        List<SysMenu> list = menuService.findMenuByRoleId(roleId);
        return Result.ok(list);
    }

    @ApiOperation("角色分配菜单")
    @PostMapping("doAssign")
    public Result doAssign(@RequestBody AssginMenuVo assginMenuVo) {
        menuService.doAssign(assginMenuVo);
        return Result.ok();
    }


    @ApiOperation(value = "新增菜单")
    @PostMapping("save")
    public Result save(@RequestBody SysMenu sysMenu) {
        menuService.save(sysMenu);
        return Result.ok();
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping("update")
    public Result updateById(@RequestBody SysMenu sysMenu) {
        menuService.updateById(sysMenu);
        return Result.ok();
    }

}

