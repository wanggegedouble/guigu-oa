package com.wy.auth.service;

import com.atguigu.model.system.SysMenu;
import com.atguigu.vo.system.AssginMenuVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author wy
 * @since 2023-03-12
 */
public interface SysMenuService extends IService<SysMenu> {

    // 菜单列表
    List<SysMenu> findMenus();
    /**
     * 删除菜单
     * @param id
     */
    void removeMenuById(Long id);
    /**
     * 查询所有菜单和角色分配的菜单
     * @param roleId
     * @return
     */
    List<SysMenu> findMenuByRoleId(Long roleId);
    /**
     *角色分配菜单
     * @param assginMenuVo
     */
    void doAssign(AssginMenuVo assginMenuVo);

    /**
     * 查询数据库动态构建路由结构，进行显示
     * @param userId
     * @return
     */

    List<RouterVo> findUserMenuListByUserId(@Param("userId") Long userId);

    /**
     * 根据用户id获取用户可以操作按钮列表
     * @param userId
     * @return
     */
    List<String> findUserPermsByUserId(Long userId);
}
