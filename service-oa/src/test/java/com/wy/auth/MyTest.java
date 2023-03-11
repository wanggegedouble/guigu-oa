package com.wy.auth;

import com.atguigu.model.system.SysRole;
import com.wy.auth.mapper.SysRoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MyTest {

    @Autowired
    private SysRoleMapper mapper;

    @Test
    public void getAll(){
        List<SysRole> list = mapper.selectList(null);
        System.out.println(list);
    }
}
