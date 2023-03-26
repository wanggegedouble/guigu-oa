package com.wy.auth.service.Impl;

import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysRoleMenu;
import com.atguigu.vo.system.AssginMenuVo;
import com.atguigu.vo.system.MetaVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wy.auth.mapper.SysMenuMapper;
import com.wy.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wy.auth.service.SysRoleMenuService;
import com.wy.auth.utils.MenuHelper;
import com.wy.common.config.exception.GuiguException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author wy
 * @since 2023-03-12
 */
@Slf4j
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService roleMenuService;

    @Override
    public List<SysMenu> findMenus() {
        //查询所有查单数据
        List<SysMenu> menuList = baseMapper.selectList(null);
        log.info("查询所有查单数据");
        log.info(menuList.toString());
        //构建树形结构
        List<SysMenu> returnList =  MenuHelper.buildTree(menuList);
        log.info("tress : ");
        log.info(returnList.toString());
        return returnList;
    }

    @Override
    public void removeMenuById(Long id) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw  new GuiguException(201,"菜单不能删除");
        }
        baseMapper.deleteById(id);
    }

    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        //1 查询所有菜单- 添加条件 status=1
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus,1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapperSysMenu);
        //2 根据roleId查询 角色菜单关系表里面 角色id对应所有的菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> sysRoleMenuList = roleMenuService.list(wrapperSysRoleMenu);
        log.info("根据roleId查询 角色菜单关系表里面 角色id对应所有的菜单id");
        log.info(sysRoleMenuList.toString());

        //3 根据获取菜单id，获取对应菜单对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        //3.1 拿着菜单id 和所有菜单集合里面id进行比较，如果相同封装
        allSysMenuList.forEach(item -> item.setSelect(menuIdList.contains(item.getId())));
        log.info("拿着菜单id 和所有菜单集合里面id进行比较，如果相同封装");
        log.info(allSysMenuList.toString());
        //4 返回规定树形显示格式菜单列表
        return MenuHelper.buildTree(allSysMenuList);
    }

    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
        //1 根据角色id 删除菜单角色表 分配数据
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,assginMenuVo.getRoleId());
        roleMenuService.remove(wrapper);

        //2 从参数里面获取角色新分配菜单id列表，
        // 进行遍历，把每个id数据添加菜单角色表
        List<Long> menuIdList = assginMenuVo.getMenuIdList();
        for(Long menuId:menuIdList) {
            if(StringUtils.isEmpty(menuId)) {
                continue;
            }
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(assginMenuVo.getRoleId());
            roleMenuService.save(sysRoleMenu);
        }
    }
    //4 根据用户id获取用户可以操作菜单列表
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        //1 判断当前用户是否是管理员   userId=1是管理员
        //1.1 如果是管理员，查询所有菜单列表
        if(userId == 1) {
            //查询所有菜单列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            //1.2 如果不是管理员，根据userId查询可以操作菜单列表
            //多表关联查询：用户角色关系表 、 角色菜单关系表、 菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        //2 把查询出来数据列表-构建成框架要求的路由结构
        //使用菜单操作工具类构建树形结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);
        //构建成框架要求的路由结构
        return buildRouter(sysMenuTreeList);
    }

    private List<RouterVo> buildRouter(List<SysMenu> sysMenuTreeList) {
        log.info("sysMenuTreeList");
        log.info(sysMenuTreeList.toString());
        //创建list集合，存储最终数据
        List<RouterVo> routers = new ArrayList<>();
        //sysMenuTreeList遍历
        for(SysMenu menu : sysMenuTreeList) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            //下一层数据部分
            List<SysMenu> children = menu.getChildren();
            //type 为1代表菜单 2为按钮
            if(menu.getType() == 1) {
                //加载出来下面隐藏路由
                List<SysMenu>
                        hiddenMenuList = children.stream()
                        .filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());
                log.info("加载出来下面隐藏路由");
                log.info(hiddenMenuList.toString());
                for(SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    //true 隐藏路由
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else {
                if(!CollectionUtils.isEmpty(children)) {
                    log.info(menu.getName());
                    if(children.size() > 0) {
                        log.info("hahahahahahahahahahahahahahahahah ");
                        router.setAlwaysShow(false);
                    }
                    //递归
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        log.info("构建成框架要求的路由结构");
        log.info(routers.toString());
        return routers;
    }

    private String getRouterPath(SysMenu menu) {
        log.info("menu");
        log.info(menu.toString());
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    //5 根据用户id获取用户可以操作按钮列表
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        //1 判断是否是管理员，如果是管理员，查询所有按钮列表
        List<SysMenu> sysMenuList = null;
        if(userId == 1) {
            //查询所有菜单列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            //2 如果不是管理员，根据userId查询可以操作按钮列表
            //多表关联查询：用户角色关系表 、 角色菜单关系表、 菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
            log.info("根据userId查询可以操作按钮列表");
            log.info(sysMenuList.toString());
        }
        //3 从查询出来的数据里面，获取可以操作按钮值的list集合，返回
        return sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());
    }
}
