package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.java_web.dao.CommonDAO;
import com.java_web.dao.JobDAO;
import com.java_web.model.auth.User;
import com.java_web.model.common.City;
import com.java_web.model.common.Skill;

@WebServlet("/employer/edit-job/*")
public class EditJobServlet extends HttpServlet {

    private CommonDAO commonDAO;
    private JobDAO jobDAO;

    @Override
    public void init() throws ServletException {
        commonDAO = new CommonDAO();
        jobDAO = new JobDAO();
    }

    /**
     * Helper method to reload form data for the edit job page
     */
    private void reloadFormData(HttpServletRequest request, Integer jobId, Integer recruiterId) 
            throws SQLException {
        // Reload job data
        Map<String, Object> job = jobDAO.getJobForEdit(jobId, recruiterId);
        if (job == null) {
            throw new SQLException("Job not found or access denied");
        }
        
        // Load all form data
        List<City> cities = commonDAO.getAllCities();
        List<Skill> skills = commonDAO.getTopSkills(100);
        List<Map<String, String>> employmentTypes = commonDAO.getEmploymentTypes();
        List<Map<String, String>> seniorityLevels = commonDAO.getSeniorityLevels();
        List<Map<String, String>> remoteTypes = commonDAO.getRemoteTypes();
        
        // Format expiresAt for date input
        if (job.get("expiresAt") != null) {
            Timestamp expiresAt = (Timestamp) job.get("expiresAt");
            LocalDate expiresDate = expiresAt.toLocalDateTime().toLocalDate();
            job.put("expiresAtFormatted", expiresDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        request.setAttribute("job", job);
        request.setAttribute("cities", cities);
        request.setAttribute("skills", skills);
        request.setAttribute("employmentTypes", employmentTypes);
        request.setAttribute("seniorityLevels", seniorityLevels);
        request.setAttribute("remoteTypes", remoteTypes);
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

        // Get job ID from URL path
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required");
            return;
        }

        try {
            Integer jobId = Integer.valueOf(pathInfo.substring(1));
            
            // Load job and form data using helper method
            reloadFormData(request, jobId, recruiterId);

            request.getRequestDispatcher("/WEB-INF/views/employer/edit-job.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid job ID");
        } catch (SQLException e) {
            throw new ServletException("Error loading job for edit", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication and role
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

        // Get recruiterId from session
        Integer recruiterId = (Integer) session.getAttribute("recruiterId");
        if (recruiterId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Recruiter profile not found");
            return;
        }

        // Get job ID from URL path
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required");
            return;
        }

        try {
            Integer jobId = Integer.valueOf(pathInfo.substring(1));

            // Get form parameters
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String requirements = request.getParameter("requirements");
            String benefits = request.getParameter("benefits");
            String cityIdStr = request.getParameter("cityId");
            String employmentTypeStr = request.getParameter("employmentType");
            String seniorityLevelStr = request.getParameter("seniorityLevel");
            String remoteTypeStr = request.getParameter("remoteType");
            String salaryMinStr = request.getParameter("salaryMin");
            String salaryMaxStr = request.getParameter("salaryMax");
            String currency = request.getParameter("currency");
            String[] skillIds = request.getParameterValues("skillIds[]");
            String expiresAtStr = request.getParameter("expiresAt");
            String statusIdStr = request.getParameter("statusId");

            // Validate required fields
            if (StringUtils.isBlank(title) || StringUtils.isBlank(description) || 
                StringUtils.isBlank(cityIdStr) || StringUtils.isBlank(employmentTypeStr)) {
                request.setAttribute("error", "Please fill in all required fields");
                reloadFormData(request, jobId, recruiterId);
                request.getRequestDispatcher("/WEB-INF/views/employer/edit-job.jsp").forward(request, response);
                return;
            }

            // Parse parameters
            Integer cityId = Integer.valueOf(cityIdStr);
            Integer employmentType = Integer.valueOf(employmentTypeStr);
            Integer seniorityLevel = StringUtils.isNotBlank(seniorityLevelStr) ? Integer.valueOf(seniorityLevelStr) : null;
            Integer remoteType = StringUtils.isNotBlank(remoteTypeStr) ? Integer.valueOf(remoteTypeStr) : null;
            Double salaryMin = StringUtils.isNotBlank(salaryMinStr) ? Double.valueOf(salaryMinStr) : null;
            Double salaryMax = StringUtils.isNotBlank(salaryMaxStr) ? Double.valueOf(salaryMaxStr) : null;
            Byte statusId = StringUtils.isNotBlank(statusIdStr) ? Byte.valueOf(statusIdStr) : 2; // Default: Published

            // Update job
            boolean success = jobDAO.updateJob(
                jobId,
                recruiterId,
                title,
                description,
                requirements,
                benefits,
                cityId,
                employmentType,
                seniorityLevel,
                remoteType,
                salaryMin,
                salaryMax,
                currency,
                expiresAtStr,
                statusId
            );

            if (!success) {
                request.setAttribute("error", "Failed to update job");
                reloadFormData(request, jobId, recruiterId);
                request.getRequestDispatcher("/WEB-INF/views/employer/edit-job.jsp").forward(request, response);
                return;
            }

            // Update skills: remove all and add new ones
            jobDAO.removeAllJobSkills(jobId);
            if (skillIds != null && skillIds.length > 0) {
                for (String skillIdStr : skillIds) {
                    if (StringUtils.isNotBlank(skillIdStr)) {
                        jobDAO.addJobSkill(jobId, Integer.valueOf(skillIdStr));
                    }
                }
            }

            // Redirect to job detail or dashboard
            response.sendRedirect(request.getContextPath() + "/job/" + jobId + "?success=updated");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid form data: " + e.getMessage());
            // Get job ID from URL for reloading the form
            Integer jobId = Integer.valueOf(pathInfo.substring(1));
            
            try {
                reloadFormData(request, jobId, recruiterId);
                request.getRequestDispatcher("/WEB-INF/views/employer/edit-job.jsp").forward(request, response);
            } catch (Exception ex) {
                throw new ServletException("Error reloading form", ex);
            }
        } catch (SQLException e) {
            throw new ServletException("Error updating job", e);
        }
    }
}
