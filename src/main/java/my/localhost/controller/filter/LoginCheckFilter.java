package my.localhost.controller.filter;

import lombok.extern.slf4j.Slf4j;
import my.localhost.common.BaseContext;
import my.localhost.common.R;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    private final static String[] whiteList = {"/api", "/js", "/login", "/plugins", "/styles", "favicon.ico", "/emailCode", "/register"};
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        String uri = httpRequest.getRequestURI();
        log.info("拦截请求：" + uri);

        Long id = null;
        // 判断登录哪里
        if (httpRequest.getSession().getAttribute("user") != null) {
            id = (Long) httpRequest.getSession().getAttribute("user");
        } else {
            id = (Long) httpRequest.getSession().getAttribute("employee");
        }

        if (id != null) {
            // 向登录后请求的线程变量中，添加ID
            BaseContext.setId(id);
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (this.checkWhiteList(uri)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            httpResponse.getWriter().write(R.error("NOTLOGIN").toString());
        }
    }

    @Override
    public void destroy() {

    }

    private boolean checkWhiteList(String uri) {
        for (String s : whiteList) {
            if (uri.contains(s)) {
                return true;
            }
        }
        return false;
    }
}
