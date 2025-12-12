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

import com.java_web.dao.ApplicationDAO;
import com.java_web.dao.JobDAO;
import com.java_web.model.auth.User;

@WebServlet("/employer/applications")
public class EmployerApplicationsServlet extends HttpServlet {

    private ApplicationDAO applicationDAO;
    private JobDAO jobDAO;

    @Override
    public void init() throws ServletException {
        applicationDAO = new ApplicationDAO();
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
            String jobIdStr = request.getParameter("jobId");
            String status = request.getParameter("status");
            String keyword = request.getParameter("keyword");
            String pageStr = request.getParameter("page");

            Integer jobId = StringUtils.isNotBlank(jobIdStr) ? Integer.valueOf(jobIdStr) : null;
            int currentPage = StringUtils.isNotBlank(pageStr) ? Integer.parseInt(pageStr) : 1;
            int pageSize = 20;

            // Get applications with filters
            List<Map<String, Object>> applications = applicationDAO.getApplicationsByRecruiter(
                    recruiterId, jobId, status, keyword, currentPage, pageSize
            );

            // Get total count for pagination
            int totalApplications = applicationDAO.getApplicationCountByRecruiter(
                    recruiterId, jobId, status, keyword
            );
            int totalPages = (int) Math.ceil((double) totalApplications / pageSize);

            // Get application counts by status for filter tabs
            Map<String, Integer> statusCounts = applicationDAO.getApplicationStatusCounts(recruiterId);
            int allCount = statusCounts.getOrDefault("all", 0);
            int appliedCount = statusCounts.getOrDefault("applied", 0);
            int underReviewCount = statusCounts.getOrDefault("underReview", 0);
            int interviewCount = statusCounts.getOrDefault("interview", 0);
            int rejectedCount = statusCounts.getOrDefault("rejected", 0);

            // Get recruiter's jobs for job filter dropdown
            List<Map<String, Object>> recruiterJobs = jobDAO.getRecruiterJobs(
                    recruiterId, null, null, 1, 100
            );

            // Set attributes for JSP
            request.setAttribute("applications", applications);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalApplications", totalApplications);
            request.setAttribute("selectedJobId", jobId);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("keyword", keyword);
            request.setAttribute("allCount", allCount);
            request.setAttribute("appliedCount", appliedCount);
            request.setAttribute("underReviewCount", underReviewCount);
            request.setAttribute("interviewCount", interviewCount);
            request.setAttribute("rejectedCount", rejectedCount);
            request.setAttribute("recruiterJobs", recruiterJobs);

            request.getRequestDispatcher("/WEB-INF/views/employer/applications.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error loading applications", e);
        }
    }
}
