package com.java_web.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.utils.PasswordUtil;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
        
        // Validation
        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.setAttribute("email", email);
            request.setAttribute("firstName", firstName);
            request.setAttribute("lastName", lastName);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        if (password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters");
            request.setAttribute("email", email);
            request.setAttribute("firstName", firstName);
            request.setAttribute("lastName", lastName);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        try {
            // Check if user already exists
            User existingUser = userDAO.findByEmail(email);
            if (existingUser != null) {
                request.setAttribute("error", "Email already registered");
                request.setAttribute("email", email);
                request.setAttribute("firstName", firstName);
                request.setAttribute("lastName", lastName);
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
            
            // Save user
            Integer userId = userDAO.createUser(newUser);
            
            // Auto login after registration
            newUser.setUserId(userId);
            HttpSession session = request.getSession();
            session.setAttribute("user", newUser);
            session.setAttribute("userId", userId);
            session.setAttribute("userRole", newUser.getRole());
            session.setAttribute("userEmail", email);
            
            // Redirect based on role
            if ("Recruiter".equals(newUser.getRole())) {
                response.sendRedirect(request.getContextPath() + "/employer/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/candidate/profile");
            }
            
        } catch (NoSuchAlgorithmException e) {
            throw new ServletException("Error hashing password", e);
        } catch (Exception e) {
            throw new ServletException("Error during registration", e);
        }
    }
}
