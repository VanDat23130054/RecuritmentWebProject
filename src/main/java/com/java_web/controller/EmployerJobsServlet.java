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

import org.apache.commons.lang3.StringUtils;

import com.java_web.dao.JobDAO;
import com.java_web.model.auth.User;

@WebServlet("/employer/jobs")
public class EmployerJobsServlet extends HttpServlet {

    private JobDAO jobDAO;

    @Override
    public void init() throws ServletException {
        jobDAO = new JobDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication and role
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?returnUrl=" + request.getRequestURI());
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"Recruiter".equals(user.getRole()) && !"EmployerAdmin".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        // Get recruiterId from session
        Integer recruiterId = (Integer) session.getAttribute("recruiterId");
        if (recruiterId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Recruiter profile not found");
            return;
        }

        try {
            // Get filter parameters
            String statusIdStr = request.getParameter("status");
            String keyword = request.getParameter("keyword");
            String pageStr = request.getParameter("page");
            
            Integer statusId = StringUtils.isNotBlank(statusIdStr) ? Integer.valueOf(statusIdStr) : null;
            int currentPage = StringUtils.isNotBlank(pageStr) ? Integer.parseInt(pageStr) : 1;
            int pageSize = 15;

            // Get jobs with filters
            List<Map<String, Object>> jobs = jobDAO.getRecruiterJobs(
                recruiterId, statusId, keyword, currentPage, pageSize
            );

            // Get total count for pagination
            int totalJobs = jobDAO.getRecruiterJobCount(recruiterId, statusId, keyword);
            int totalPages = (int) Math.ceil((double) totalJobs / pageSize);

            // Get job counts by status for the filter tabs
            int allJobsCount = jobDAO.getRecruiterJobCount(recruiterId, null, null);
            int publishedCount = jobDAO.getRecruiterJobCount(recruiterId, 2, null); // Published = 2
            int draftCount = jobDAO.getRecruiterJobCount(recruiterId, 1, null);     // Draft = 1
            int closedCount = jobDAO.getRecruiterJobCount(recruiterId, 3, null);    // Closed = 3

            // Set attributes for JSP
            request.setAttribute("jobs", jobs);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalJobs", totalJobs);
            request.setAttribute("selectedStatus", statusId);
            request.setAttribute("keyword", keyword);
            request.setAttribute("allJobsCount", allJobsCount);
            request.setAttribute("publishedCount", publishedCount);
            request.setAttribute("draftCount", draftCount);
            request.setAttribute("closedCount", closedCount);

            request.getRequestDispatcher("/WEB-INF/views/employer/jobs.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error loading jobs", e);
        }
    }
}
