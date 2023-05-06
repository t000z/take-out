package my.localhost.controller.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import my.localhost.common.CustomHttpServletRequestWrapper;
import my.localhost.common.R;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = "/employee")
public class AuthorizationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        CustomHttpServletRequestWrapper httpRequest = new CustomHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        JSONObject json = JSON.parseObject(httpRequest.getBody());

        log.info("权限检查: " + httpRequest.getRequestURI());

        Long empId = (Long) httpRequest.getSession().getAttribute("employee");
        if (empId == 1 || !httpRequest.getMethod().equals("PUT")) {
            filterChain.doFilter(httpRequest, servletResponse);
        } else if (json.get("status") != null) {
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.getWriter().write(R.error("信息更新失败").toString());
        } else {
            filterChain.doFilter(httpRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
