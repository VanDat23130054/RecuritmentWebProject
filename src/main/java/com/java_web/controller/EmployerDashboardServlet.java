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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.dao.CompanyDAO;
import com.java_web.dao.RecruiterDAO;
import com.java_web.model.auth.User;
import com.java_web.model.employer.Recruiter;

@WebServlet("/employer/dashboard")
public class EmployerDashboardServlet extends HttpServlet {

    private RecruiterDAO recruiterDAO;
    private CompanyDAO companyDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        recruiterDAO = new RecruiterDAO();
        companyDAO = new CompanyDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?returnUrl=" + 
                                request.getRequestURI());
            return;
        }

        User user = (User) session.getAttribute("user");
        
        // Check if user is a recruiter
        if (!"Recruiter".equals(user.getRole()) && !"EmployerAdmin".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                             "Access denied. This page is only for recruiters.");
            return;
        }

        try {
            // Get recruiter profile
            Recruiter recruiter = recruiterDAO.getRecruiterByUserId(user.getUserId());
            
            if (recruiter == null) {
                // Redirect to complete profile if recruiter profile doesn't exist
                response.sendRedirect(request.getContextPath() + "/employer/setup-profile");
                return;
            }

            // Get dashboard statistics
            Map<String, Object> stats = recruiterDAO.getDashboardStats(recruiter.getRecruiterId());

            // Get recruiter's jobs (first page, 10 items)
            List<Map<String, Object>> recentJobs = recruiterDAO.getRecruiterJobs(
                recruiter.getRecruiterId(), 1, 10);

            // Get recent applications (last 10)
            List<Map<String, Object>> recentApplications = recruiterDAO.getRecentApplications(
                recruiter.getRecruiterId(), 10);

            // Get application statistics by status
            List<Map<String, Object>> applicationStats = recruiterDAO.getApplicationStatsByStatus(
                recruiter.getRecruiterId());

            // Get company information
            Map<String, Object> company = null;
            if (recruiter.getCompanyId() != null) {
                company = companyDAO.getCompanyDetail(recruiter.getCompanyId());
            }

            // Set attributes for JSP
            request.setAttribute("recruiter", recruiter);
            request.setAttribute("stats", stats);
            request.setAttribute("recentJobs", recentJobs);
            request.setAttribute("recentApplications", recentApplications);
            request.setAttribute("applicationStats", applicationStats);
            request.setAttribute("company", company);
            request.setAttribute("user", user);

            // Forward to dashboard view
            request.getRequestDispatcher("/WEB-INF/views/employer/dashboard.jsp")
                   .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error loading employer dashboard", e);
        }
    }
}
