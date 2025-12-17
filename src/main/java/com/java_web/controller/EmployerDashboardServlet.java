package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.dao.CompanyDAO;
import com.java_web.dao.JobDAO;
import com.java_web.dao.RecruiterDAO;
import com.java_web.model.auth.User;
import com.java_web.model.dto.ApplicationStatusStatDTO;
import com.java_web.model.dto.CompanyDetailDTO;
import com.java_web.model.dto.RecentApplicationDTO;
import com.java_web.model.dto.RecruiterDashboardStatsDTO;
import com.java_web.model.dto.RecruiterJobDTO;
import com.java_web.model.employer.Recruiter;

import lombok.extern.slf4j.Slf4j;

@WebServlet("/employer/dashboard")
@Slf4j
public class EmployerDashboardServlet extends HttpServlet {

    private RecruiterDAO recruiterDAO;
    private JobDAO jobDAO;
    private CompanyDAO companyDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        recruiterDAO = new RecruiterDAO();
        jobDAO = new JobDAO();
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
            response.sendRedirect(request.getContextPath() + "/login?returnUrl="
                    + request.getRequestURI());
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
            RecruiterDashboardStatsDTO stats = recruiterDAO.getDashboardStats(recruiter.getRecruiterId());
            log.info("Dashboard stats: {}", stats);

            // Get recruiter's jobs (first 10 for dashboard)
            List<RecruiterJobDTO> recentJobs = jobDAO.getRecruiterJobs(
                    recruiter.getRecruiterId(), null, null, 1, 10);
            log.info("Recent jobs count: {}", recentJobs.size());

            // Get recent applications (last 10)
            List<RecentApplicationDTO> recentApplications = recruiterDAO.getRecentApplications(
                    recruiter.getRecruiterId(), 10);
            log.info("Recent applications count: {}", recentApplications.size());

            // Get application statistics by status
            List<ApplicationStatusStatDTO> applicationStats = recruiterDAO.getApplicationStatsByStatus(
                    recruiter.getRecruiterId());
            log.info("Application stats count: {}", applicationStats.size());

            // Get company information
            CompanyDetailDTO company = null;
            if (recruiter.getCompanyId() != null) {
                company = companyDAO.getCompanyDetail(recruiter.getCompanyId());
                log.info("Company: {}", company != null ? company.getName() : "null");
            }

            // Set attributes for JSP
            request.setAttribute("recruiter", recruiter);
            request.setAttribute("stats", stats);
            request.setAttribute("recentJobs", recentJobs);
            request.setAttribute("recentApplications", recentApplications);
            request.setAttribute("applicationStats", applicationStats);
            request.setAttribute("company", company);
            request.setAttribute("user", user);

            log.info("Forwarding to dashboard.jsp");

            // Forward to dashboard view
            request.getRequestDispatcher("/WEB-INF/views/employer/dashboard.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            log.error("Error loading employer dashboard", e);
            throw new ServletException("Error loading employer dashboard", e);
        }
    }
}
