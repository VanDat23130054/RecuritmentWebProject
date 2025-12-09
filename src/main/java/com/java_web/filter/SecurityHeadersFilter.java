package com.java_web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter to add security headers to all responses
 * Protects against common web vulnerabilities
 */
public class SecurityHeadersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Prevent clickjacking attacks
        httpResponse.setHeader("X-Frame-Options", "DENY");
        
        // Enable XSS protection in browsers
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Prevent MIME type sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        
        // Referrer policy
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Content Security Policy (adjust based on your needs)
        // This is a basic policy - customize for your application
        String csp = "default-src 'self'; " +
                     "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                     "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://fonts.googleapis.com; " +
                     "font-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://fonts.gstatic.com; " +
                     "img-src 'self' data: https:; " +
                     "connect-src 'self';";
        httpResponse.setHeader("Content-Security-Policy", csp);
        
        // Permissions Policy (formerly Feature-Policy)
        httpResponse.setHeader("Permissions-Policy", 
                "geolocation=(), microphone=(), camera=()");
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
