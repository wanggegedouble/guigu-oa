package com.wy.security.filter;

/**
 * @Author: Yeman
 * @Date: 2023-03-26-14:38
 * @Description:
 */

import com.wy.common.jwt.JwtHelper;
import com.wy.common.result.ResponseUtil;
import com.wy.common.result.Result;
import com.wy.common.result.ResultCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;


/**
 * <p>
 * 认证解析token过滤器
 * </p>
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.info("uri:"+request.getRequestURI());
        //如果是登录接口，直接放行
        if("/controller/index/login".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if(null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (!StringUtils.isEmpty(token)) {
            String userName = JwtHelper.getUsername(token);
            if (!StringUtils.isEmpty(userName)){
                return new UsernamePasswordAuthenticationToken(userName,null, Collections.emptyList());
            }
        }
        return null;
    }
}
