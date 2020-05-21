package com.github.xjs.util.web;

import com.github.xjs.util.StringUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String  origin = request.getHeader("Origin");
        if("null".equals(origin)){
            //说明是本地直接打开的html
            response.setHeader("Access-Control-Allow-Origin", "null");
        }else if(!StringUtil.isEmpty(origin)){
            response.setHeader("Access-Control-Allow-Origin", StringUtil.normalizeOrigin(origin));
        }
        response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        //如果是OPTIONS请求就return 往后执行会到业务代码中 他不带参数会产生异常
        if ("OPTIONS".equals(request.getMethod())) {
            return;
        }
        //第二次就是POST请求 之前设置了跨域就能正常执行代码了
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}