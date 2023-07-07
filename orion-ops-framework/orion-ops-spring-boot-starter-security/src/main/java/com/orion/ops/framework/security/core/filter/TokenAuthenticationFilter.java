package com.orion.ops.framework.security.core.filter;

import com.orion.lang.utils.Strings;
import com.orion.ops.framework.common.constant.ErrorCode;
import com.orion.ops.framework.common.security.LoginUser;
import com.orion.ops.framework.security.core.service.SecurityFrameworkService;
import com.orion.ops.framework.security.core.utils.SecurityUtils;
import com.orion.web.servlet.web.Servlets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证过滤器
 * 验证 token 有效后将其加入上下文
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/7/6 18:39
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityFrameworkService securityFrameworkService;

    public TokenAuthenticationFilter(SecurityFrameworkService securityFrameworkService) {
        this.securityFrameworkService = securityFrameworkService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            // 获取请求头 token
            String token = SecurityUtils.obtainAuthorization(request);
            if (!Strings.isBlank(token)) {
                // 通过 token 获取用户信息
                LoginUser loginUser = securityFrameworkService.getUserByToken(token);
                // 设置上下文
                if (loginUser != null) {
                    SecurityUtils.setLoginUser(loginUser, request);
                }
            }
        } catch (Exception e) {
            log.error("TokenAuthenticationFilter.doFilterInternal error", e);
            Servlets.writeHttpWrapper(response, ErrorCode.INTERNAL_SERVER_ERROR.getWrapper());
            return;
        }
        // 继续执行
        chain.doFilter(request, response);
    }

}
