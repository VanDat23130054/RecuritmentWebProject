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

import com.java_web.dao.ApplicationDAO;
import com.java_web.dao.JobDAO;
import com.java_web.model.auth.User;
import com.java_web.model.dto.ApplicationFunnelDTO;
import com.java_web.model.dto.ApplicationStatusCountsDTO;
import com.java_web.model.dto.JobPerformanceDTO;
import com.java_web.model.dto.TimelineDataDTO;

@WebServlet("/employer/graph")
public class EmployerGraphServlet extends HttpServlet {

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

        Integer recruiterId = (Integer) session.getAttribute("recruiterId");
        if (recruiterId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Recruiter profile not found");
            return;
        }

        try {
            // Get application status distribution
            ApplicationStatusCountsDTO statusCounts = applicationDAO.getApplicationStatusCounts(recruiterId);
            request.setAttribute("statusCounts", statusCounts);

            // Get applications over time (last 30 days)
            List<TimelineDataDTO> applicationsTimeline = applicationDAO.getApplicationsTimeline(recruiterId, 30);
            request.setAttribute("applicationsTimeline", applicationsTimeline);

            // Get job performance metrics
            List<JobPerformanceDTO> jobPerformance = jobDAO.getJobPerformanceMetrics(recruiterId);
            request.setAttribute("jobPerformance", jobPerformance);

            // Get application funnel data
            ApplicationFunnelDTO funnelData = applicationDAO.getApplicationFunnel(recruiterId);
            request.setAttribute("funnelData", funnelData);

            request.getRequestDispatcher("/WEB-INF/views/employer/graph.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error loading graph data", e);
        }
    }
}
