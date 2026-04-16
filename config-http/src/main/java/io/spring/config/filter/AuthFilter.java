package io.spring.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.config.response.ApiResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(1)
public class AuthFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/auth/login",
            "/auth/logout"
    );
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        
        // 检查是否在排除列表中
        if (isExcluded(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 验证用户是否登录
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write(objectMapper.writeValueAsString(
                    ApiResponse.error(401, "请先登录")
            ));
            return;
        }
        
        // 继续过滤链
        chain.doFilter(request, response);
    }
    
    private boolean isExcluded(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}
