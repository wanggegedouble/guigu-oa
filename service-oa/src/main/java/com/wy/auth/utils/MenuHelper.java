package com.wy.auth.utils;

import com.atguigu.model.system.SysMenu;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;



/**
 * @Author: Yeman
 * @Date: 2023-03-12-16:44
 * @Description:
 */
@Slf4j
public class MenuHelper {
    public static List<SysMenu> buildTree(List<SysMenu> menuList) {
        // 创建list集合 用于最终数据
        List<SysMenu> tress = new ArrayList<>();
        for (SysMenu sysMenu : menuList) {
            if (sysMenu.getParentId().longValue()==0){
                log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                log.info(sysMenu.toString());
                tress.add(getChildren(sysMenu,menuList));
            }
        }
        return tress;
    }

    /**
     *
     * @param sysMenu 当前菜单
     * @param menuList  树形节点
     * @return
     */
    public static SysMenu getChildren(SysMenu sysMenu,List<SysMenu> menuList){
        sysMenu.setChildren(new ArrayList<SysMenu>());
        for (SysMenu it : menuList) {
            if(sysMenu.getId().longValue() == it.getParentId().longValue()) {

                log.info("<------------->");
                log.info(sysMenu.getChildren().toString());
                if (sysMenu.getChildren() == null) {
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(getChildren(it,menuList));
                log.info("===================================");
                log.info(sysMenu.getChildren().toString());
            }
        }
        log.info("sysMenu:~~~~~~");
        log.info(sysMenu.toString());
        return sysMenu;
    }
}
