package com.java_web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter to handle CORS (Cross-Origin Resource Sharing)
 * Useful for API endpoints that need to be accessed from different origins
 */
public class CORSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Allow all origins (in production, specify allowed origins)
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        
        // Allow specific HTTP methods
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        
        // Allow specific headers
        httpResponse.setHeader("Access-Control-Allow-Headers", 
                "Content-Type, Authorization, X-Requested-With");
        
        // Allow credentials
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        
        // Cache preflight response for 1 hour
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        
        // Handle preflight OPTIONS request
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
