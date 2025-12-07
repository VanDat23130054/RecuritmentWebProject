package com.java_web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.utils.PasswordUtil;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Display login page
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");
        
        try {
            User user = userDAO.findByEmail(email);
            
            if (user == null) {
                request.setAttribute("error", "Invalid email or password");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }
            
            if (!user.isActive()) {
                request.setAttribute("error", "Your account has been deactivated");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }
            
            // Verify password
            if (!PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {
                request.setAttribute("error", "Invalid email or password");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }
            
            // Update last login
            userDAO.updateLastLogin(user.getUserId());
            
            // Create session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("userEmail", user.getEmail());
            
            // Set session timeout (30 minutes default, or longer if remember me)
            if ("on".equals(remember)) {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
            } else {
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
            }
            
            // Redirect based on role
            String redirectUrl = getRedirectUrl(user.getRole(), request);
            response.sendRedirect(redirectUrl);
            
        } catch (Exception e) {
            throw new ServletException("Error during login", e);
        }
    }
    
    private String getRedirectUrl(String role, HttpServletRequest request) {
        String returnUrl = request.getParameter("returnUrl");
        if (returnUrl != null && !returnUrl.isEmpty()) {
            return returnUrl;
        }
        
        switch (role) {
            case "Admin":
                return request.getContextPath() + "/admin/dashboard";
            case "Recruiter":
            case "EmployerAdmin":
                return request.getContextPath() + "/employer/dashboard";
            case "Candidate":
            default:
                return request.getContextPath() + "/candidate/profile";
        }
    }
}
