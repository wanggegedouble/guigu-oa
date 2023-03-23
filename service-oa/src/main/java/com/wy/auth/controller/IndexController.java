package com.wy.auth.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.LoginVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wy.auth.service.SysMenuService;
import com.wy.auth.service.SysUserService;
import com.wy.common.config.exception.GuiguException;
import com.wy.common.jwt.JwtHelper;
import com.wy.common.result.Result;
import com.wy.common.utils.MD5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Yeman
 * @Date: 2023-03-20-16:30
 * @Description:
 */
@Api(tags = "登录接口管理")
@RestController
@RequestMapping("controller/index")
@Slf4j
public class IndexController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysMenuService menuService;


    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo){
        log.info("username"+loginVo.getUsername());
        String userName = loginVo.getUsername();
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getUsername,userName);
        SysUser sysUser = userService.getOne(lqw);
        if (sysUser == null) {
            throw new GuiguException(201,"无此用户");
        }
        String pass_input = loginVo.getPassword();
        String pass_db = sysUser.getPassword();
        String pass_md5 = MD5.encrypt(pass_input);
        //String pass_md5 = DigestUtil.md5Hex(pass_input);
        if (!pass_md5.equals(pass_db)) {
            throw new  GuiguException(201,"密码错误");
        }
        // 0 可用
        if (sysUser.getStatus().intValue() == 1){
            throw new  GuiguException(201,"用户被禁止使用");
        }
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }

    @ApiOperation("info")
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        //1 从请求头获取用户信息（获取请求头token字符串）
        String token = request.getHeader("token");
        //2 从token字符串获取用户id 或者 用户名称
        Long userId = JwtHelper.getUserId(token);
        //Long userId =1L;
        log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        log.info(token+"");

        //3 根据用户id查询数据库，把用户信息获取出来
        SysUser sysUser = userService.getById(userId);

        //4 根据用户id获取用户可以操作菜单列表
        //查询数据库动态构建路由结构，进行显示
        List<RouterVo> routerList = menuService.findUserMenuListByUserId(userId);
        log.info("根据用户id获取用户可以操作菜单列表");
        log.info(routerList.toString());

        //5 根据用户id获取用户可以操作按钮列表
        List<String> permsList = menuService.findUserPermsByUserId(userId);
        log.info("根据用户id获取用户可以操作按钮列表");
        log.info(permsList.toString());
        //6 返回相应的数据
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",sysUser.getUsername());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        //返回用户可以操作菜单
        map.put("routers",routerList);
        //返回用户可以操作按钮
        map.put("buttons",permsList);
        return Result.ok(map);
    }

    @ApiOperation("退出")
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
