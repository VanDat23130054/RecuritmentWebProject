package com.java_web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter to protect authenticated routes
 * Redirects unauthenticated users to login page
 */
public class AuthenticationFilter implements Filter {

    // Public paths that don't require authentication
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/",
        "/home",
        "/login",
        "/register",
        "/logout",
        "/jobs",
        "/job",
        "/css",
        "/js",
        "/images"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Check if path is public
        boolean isPublicPath = PUBLIC_PATHS.stream()
                .anyMatch(publicPath -> path.startsWith(publicPath));
        
        if (isPublicPath) {
            // Allow access to public paths
            chain.doFilter(request, response);
            return;
        }
        
        // Check authentication
        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        
        if (isLoggedIn) {
            // User is authenticated, proceed
            chain.doFilter(request, response);
        } else {
            // User is not authenticated, redirect to login
            String loginURL = contextPath + "/login";
            
            // Save the original requested URL to redirect after login
            session = httpRequest.getSession(true);
            session.setAttribute("redirectAfterLogin", requestURI);
            
            httpResponse.sendRedirect(loginURL);
        }
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
