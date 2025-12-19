package com.java_web.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.model.auth.User;

/**
 * Authorization filter that restricts access to URLs based on user roles. This
 * filter checks if the logged-in user has the required role to access specific
 * paths.
 */
@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    private static final Map<String, List<String>> ROLE_ACCESS_MAP = new HashMap<>();

    static {

        ROLE_ACCESS_MAP.put("/admin", Arrays.asList("Admin"));

        ROLE_ACCESS_MAP.put("/employer/dashboard", Arrays.asList("Recruiter", "EmployerAdmin"));
        ROLE_ACCESS_MAP.put("/employer/jobs", Arrays.asList("Recruiter", "EmployerAdmin"));
        ROLE_ACCESS_MAP.put("/employer/post-job", Arrays.asList("Recruiter", "EmployerAdmin"));
        ROLE_ACCESS_MAP.put("/employer/edit-job", Arrays.asList("Recruiter", "EmployerAdmin"));
        ROLE_ACCESS_MAP.put("/employer/delete-job", Arrays.asList("Recruiter", "EmployerAdmin"));
        ROLE_ACCESS_MAP.put("/employer/applications", Arrays.asList("Recruiter", "EmployerAdmin"));
        ROLE_ACCESS_MAP.put("/employer/company-profile", Arrays.asList("Recruiter", "EmployerAdmin"));
        ROLE_ACCESS_MAP.put("/employer/graph", Arrays.asList("Recruiter", "EmployerAdmin"));
        ROLE_ACCESS_MAP.put("/employer/setup-profile", Arrays.asList("Recruiter", "EmployerAdmin"));

        ROLE_ACCESS_MAP.put("/candidate/dashboard", Arrays.asList("Candidate"));
        ROLE_ACCESS_MAP.put("/candidate/profile", Arrays.asList("Candidate"));
        ROLE_ACCESS_MAP.put("/candidate/applications", Arrays.asList("Candidate"));
        ROLE_ACCESS_MAP.put("/candidate/saved-jobs", Arrays.asList("Candidate"));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {

            if (requiresAuthentication(path)) {
                httpResponse.sendRedirect(contextPath + "/login?returnUrl=" + requestURI);
                return;
            }

            chain.doFilter(request, response);
            return;
        }

        String userRole = user.getRole();

        if (requiresRoleCheck(path)) {
            if (!hasAccess(path, userRole)) {

                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Access denied. You don't have permission to access this resource.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Check if the path is a public resource that doesn't require
     * authentication.
     */
    private boolean isPublicResource(String path) {
        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/uploads/")
                || path.equals("/")
                || path.equals("/home")
                || path.equals("/login")
                || path.equals("/register")
                || path.equals("/logout")
                || path.equals("/jobs")
                || path.equals("/job-detail")
                || path.equals("/companies")
                || path.equals("/company-detail")
                || path.equals("/about")
                || path.equals("/contact")
                || path.startsWith("/api/public/");
    }

    /**
     * Check if the path requires authentication.
     */
    private boolean requiresAuthentication(String path) {
        return path.startsWith("/employer/")
                || path.startsWith("/candidate/")
                || path.startsWith("/admin/")
                || path.startsWith("/profile/");
    }

    /**
     * Check if the path requires role-based access control.
     */
    private boolean requiresRoleCheck(String path) {

        for (String protectedPath : ROLE_ACCESS_MAP.keySet()) {
            if (path.startsWith(protectedPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user has access to the given path based on their role.
     */
    private boolean hasAccess(String path, String userRole) {

        String matchedPath = null;
        for (String protectedPath : ROLE_ACCESS_MAP.keySet()) {
            if (path.startsWith(protectedPath)) {
                if (matchedPath == null || protectedPath.length() > matchedPath.length()) {
                    matchedPath = protectedPath;
                }
            }
        }

        if (matchedPath == null) {
            return true;
        }

        List<String> allowedRoles = ROLE_ACCESS_MAP.get(matchedPath);
        return allowedRoles.contains(userRole);
    }

    @Override
    public void destroy() {

    }
}
