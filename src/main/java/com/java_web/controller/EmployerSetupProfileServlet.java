package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.CompanyDAO;
import com.java_web.dao.RecruiterDAO;
import com.java_web.model.auth.User;
import com.java_web.model.employer.Recruiter;

@WebServlet("/employer/setup-profile")
public class EmployerSetupProfileServlet extends HttpServlet {

    private RecruiterDAO recruiterDAO;
    private CompanyDAO companyDAO;

    @Override
    public void init() throws ServletException {
        recruiterDAO = new RecruiterDAO();
        companyDAO = new CompanyDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        
        if (!"Recruiter".equals(user.getRole()) && !"EmployerAdmin".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            // Get top employers for selection
            List<Map<String, Object>> companies = companyDAO.getTopEmployers(50);
            request.setAttribute("companies", companies);
            request.setAttribute("user", user);
            
            request.getRequestDispatcher("/WEB-INF/views/employer/setup-profile.jsp")
                   .forward(request, response);
                   
        } catch (SQLException e) {
            throw new ServletException("Error loading setup page", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        
        String companyIdStr = request.getParameter("companyId");
        String newCompanyName = request.getParameter("newCompanyName");
        String title = request.getParameter("title");
        
        try {
            Integer companyId;
            
            // Create new company or use existing
            if ("new".equals(companyIdStr) && newCompanyName != null && !newCompanyName.trim().isEmpty()) {
                // This would need a CompanyDAO method to create company
                // For now, we'll use the recruiter creation which handles this
                companyId = null; // Will be created by stored procedure
            } else {
                companyId = Integer.parseInt(companyIdStr);
            }
            
            // Create recruiter profile
            Recruiter recruiter = new Recruiter();
            recruiter.setUserId(user.getUserId());
            recruiter.setCompanyId(companyId);
            recruiter.setTitle(title != null && !title.trim().isEmpty() ? title : "Recruiter");
            recruiter.setPrimaryContact(true);
            
            Integer recruiterId = recruiterDAO.createRecruiter(recruiter);
            
            // Update session
            session.setAttribute("recruiterId", recruiterId);
            session.setAttribute("companyId", companyId);
            
            response.sendRedirect(request.getContextPath() + "/employer/dashboard");
            
        } catch (SQLException e) {
            request.setAttribute("error", "Failed to create recruiter profile. Please try again.");
            doGet(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid company selection");
            doGet(request, response);
        }
    }
}
