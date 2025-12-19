package com.java_web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java_web.model.auth.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter to log all HTTP requests with authentication status and timing
 * information
 */
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("LoggingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Skip logging for static resources and favicon
        if (shouldSkipLogging(path)) {
            chain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();

        String method = httpRequest.getMethod();
        String queryString = httpRequest.getQueryString();
        String clientIP = getClientIP(httpRequest);

        // Get authentication info
        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        String authInfo = getAuthInfo(user);

        String fullURL = queryString != null ? requestURI + "?" + queryString : requestURI;

        logger.info(">> {} {} [{}] from {}", method, fullURL, authInfo, clientIP);

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = httpResponse.getStatus();

            // Add meaningful status descriptions
            String statusDescription = getStatusDescription(status);

            if (status >= 400) {
                logger.warn("<< {} {} [{}] - {} {} - {}ms",
                        method, requestURI, authInfo, status, statusDescription, duration);
            } else if (status == 302 || status == 301) {
                String redirectLocation = httpResponse.getHeader("Location");
                logger.info("<< {} {} [{}] - {} {} -> {} - {}ms",
                        method, requestURI, authInfo, status, statusDescription, redirectLocation, duration);
            } else {
                logger.info("<< {} {} [{}] - {} - {}ms",
                        method, requestURI, authInfo, status, duration);
            }
        }
    }

    /**
     * Skip logging for static resources to reduce noise
     */
    private boolean shouldSkipLogging(String path) {
        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/uploads/")
                || path.equals("/favicon.ico")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".gif")
                || path.endsWith(".ico");
    }

    /**
     * Get authentication information for logging
     */
    private String getAuthInfo(User user) {
        if (user == null) {
            return "Guest";
        }
        return String.format("%s:%s", user.getRole(), user.getEmail());
    }

    /**
     * Get human-readable status description
     */
    private String getStatusDescription(int status) {
        switch (status) {
            case 200:
                return "OK";
            case 201:
                return "Created";
            case 301:
                return "Moved Permanently";
            case 302:
                return "Redirect";
            case 304:
                return "Not Modified";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            case 502:
                return "Bad Gateway";
            case 503:
                return "Service Unavailable";
            default:
                return "";
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public void destroy() {
        logger.info("LoggingFilter destroyed");
    }
}
