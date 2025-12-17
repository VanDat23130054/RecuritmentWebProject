package com.java_web.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.java_web.dao.RecruiterDAO;
import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.utils.PasswordUtil;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private RecruiterDAO recruiterDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        recruiterDAO = new RecruiterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String role = request.getParameter("role"); // Candidate or Recruiter
        
        // Recruiter-specific fields
        String companyName = request.getParameter("companyName");
        String recruiterTitle = request.getParameter("recruiterTitle");
        
        // Validation
        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            setFormAttributes(request, email, firstName, lastName, role, companyName, recruiterTitle);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        if (password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters");
            setFormAttributes(request, email, firstName, lastName, role, companyName, recruiterTitle);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        // Validate recruiter-specific fields
        if ("Recruiter".equals(role)) {
            if (StringUtils.isBlank(companyName)) {
                request.setAttribute("error", "Company name is required for recruiters");
                setFormAttributes(request, email, firstName, lastName, role, companyName, recruiterTitle);
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
                return;
            }
        }
        
        try {
            // Check if user already exists
            User existingUser = userDAO.findByEmail(email);
            if (existingUser != null) {
                request.setAttribute("error", "Email already registered");
                setFormAttributes(request, email, firstName, lastName, role, companyName, recruiterTitle);
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
                return;
            }
            
            // Create new user
            User newUser = new User();
            newUser.setEmail(email);
            
            // Hash password
            byte[] salt = PasswordUtil.generateSalt();
            byte[] passwordHash = PasswordUtil.hashPassword(password, salt);
            
            newUser.setSalt(salt);
            newUser.setPasswordHash(passwordHash);
            newUser.setRole(role != null && role.equals("Recruiter") ? "Recruiter" : "Candidate");
            newUser.setActive(true);
            newUser.setEmailConfirmed(false);
            
            // Prepare full name
            String fullName = firstName + " " + lastName;
            
            // Register user with profile
            Integer userId;
            Integer profileId;
            Integer companyId = null;
            
            if ("Recruiter".equals(newUser.getRole())) {
                // Use combined registration for recruiter
                Object[] result = userDAO.registerUser(
                    email, 
                    passwordHash, 
                    salt, 
                    "Recruiter", 
                    fullName, 
                    companyName, 
                    StringUtils.isNotBlank(recruiterTitle) ? recruiterTitle : "Recruiter"
                );
                userId = (Integer) result[0];
                profileId = (Integer) result[1];
                companyId = (Integer) result[2];
            } else {
                // Use combined registration for candidate
                Object[] result = userDAO.registerUser(
                    email, 
                    passwordHash, 
                    salt, 
                    "Candidate", 
                    fullName, 
                    null, 
                    null
                );
                userId = (Integer) result[0];
                profileId = (Integer) result[1];
            }
            
            // Auto login after registration
            newUser.setUserId(userId);
            HttpSession session = request.getSession();
            session.setAttribute("user", newUser);
            session.setAttribute("userId", userId);
            session.setAttribute("userRole", newUser.getRole());
            session.setAttribute("userEmail", email);
            
            if ("Recruiter".equals(newUser.getRole())) {
                session.setAttribute("companyId", companyId);
                session.setAttribute("recruiterId", profileId);
            } else {
                session.setAttribute("candidateId", profileId);
            }
            
            // Redirect based on role
            if ("Recruiter".equals(newUser.getRole())) {
                response.sendRedirect(request.getContextPath() + "/employer/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
            
        } catch (NoSuchAlgorithmException e) {
            throw new ServletException("Error hashing password", e);
        } catch (SQLException e) {
            // Check for duplicate email constraint
            if (e.getMessage().contains("unique") || e.getMessage().contains("duplicate")) {
                request.setAttribute("error", "Email already registered");
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
            }
            setFormAttributes(request, email, firstName, lastName, role, companyName, recruiterTitle);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
    
    private void setFormAttributes(HttpServletRequest request, String email, String firstName, 
                                   String lastName, String role, String companyName, String recruiterTitle) {
        request.setAttribute("email", email);
        request.setAttribute("firstName", firstName);
        request.setAttribute("lastName", lastName);
        request.setAttribute("role", role);
        request.setAttribute("companyName", companyName);
        request.setAttribute("recruiterTitle", recruiterTitle);
    }
}
