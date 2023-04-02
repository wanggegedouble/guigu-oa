package com.wy.security.filter;

/**
 * @Author: Yeman
 * @Date: 2023-03-26-14:38
 * @Description:
 */

import com.alibaba.fastjson.JSON;
import com.wy.common.jwt.JwtHelper;
import com.wy.common.result.ResponseUtil;
import com.wy.common.result.Result;
import com.wy.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * <p>
 * 认证解析token过滤器
 * </p>
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.info("url:"+request.getRequestURI());
        //如果是登录接口，直接放行
        if("/controller/index/login".equals(request.getRequestURI())) {
            log.info("如果是登录接口，直接放行");
            filterChain.doFilter(request, response);
            return;
        }
        log.info("非登录接口");
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication == null){
            log.info("authentication: "+authentication);
        }
        if(null != authentication) {
            log.info("执行 1");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            log.info("执行 2");
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        log.info("request:\n"+request);
        String token = request.getHeader("token");
        log.info("token");
        log.info(token);
        if (!StringUtils.isEmpty(token)) {
            log.info("token 不为空");
            String userName = JwtHelper.getUsername(token);
            if (!StringUtils.isEmpty(userName)){
                // 通过userName 从Redis获取用户权限
                String authString = (String) redisTemplate.opsForValue().get(userName);
                if (!StringUtils.isEmpty(authString)) {
                    List<Map> mapList = JSON.parseArray(authString, Map.class);
                    log.info("mapList: ");
                    log.info(mapList.toString());
                    List<SimpleGrantedAuthority> authList = new ArrayList<>();
                    for (Map map : mapList) {
                        String authority = (String)map.get("authority");
                        authList.add(new SimpleGrantedAuthority(authority));
                    }
                    return new UsernamePasswordAuthenticationToken(userName,null,authList);
                }else {
                    return new UsernamePasswordAuthenticationToken(userName,null, new ArrayList<>());

                }
            }
        }
        return null;
    }
}
