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

import com.java_web.dao.CommonDAO;
import com.java_web.dao.JobDAO;
import com.java_web.dao.RecruiterDAO;
import com.java_web.model.auth.User;
import com.java_web.model.common.City;
import com.java_web.model.common.Skill;
import com.java_web.model.employer.Recruiter;

@WebServlet("/employer/post-job")
public class PostJobServlet extends HttpServlet {

    private CommonDAO commonDAO;
    private RecruiterDAO recruiterDAO;
    private JobDAO jobDAO;

    @Override
    public void init() throws ServletException {
        commonDAO = new CommonDAO();
        recruiterDAO = new RecruiterDAO();
        jobDAO = new JobDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication and role
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?returnUrl=/employer/post-job");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"Recruiter".equals(user.getRole()) && !"EmployerAdmin".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            // Get form data
            List<City> cities = commonDAO.getAllCities();
            List<Skill> skills = commonDAO.getTopSkills(100);
            List<Map<String, String>> employmentTypes = commonDAO.getEmploymentTypes();
            List<Map<String, String>> seniorityLevels = commonDAO.getSeniorityLevels();
            List<Map<String, String>> remoteTypes = commonDAO.getRemoteTypes();

            request.setAttribute("cities", cities);
            request.setAttribute("skills", skills);
            request.setAttribute("employmentTypes", employmentTypes);
            request.setAttribute("seniorityLevels", seniorityLevels);
            request.setAttribute("remoteTypes", remoteTypes);

            request.getRequestDispatcher("/WEB-INF/views/employer/post-job.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error loading post job form", e);
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

        try {
            // Get recruiter info
            Recruiter recruiter = recruiterDAO.getRecruiterByUserId(user.getUserId());
            if (recruiter == null) {
                request.setAttribute("error", "Recruiter profile not found");
                doGet(request, response);
                return;
            }

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
                doGet(request, response);
                return;
            }

            // Parse parameters
            Integer cityId = Integer.valueOf(cityIdStr);
            Integer employmentType = Integer.valueOf(employmentTypeStr);
            Integer seniorityLevel = StringUtils.isNotBlank(seniorityLevelStr) ? Integer.valueOf(seniorityLevelStr) : null;
            Integer remoteType = StringUtils.isNotBlank(remoteTypeStr) ? Integer.valueOf(remoteTypeStr) : null;
            Integer salaryMin = StringUtils.isNotBlank(salaryMinStr) ? Integer.valueOf(salaryMinStr) : null;
            Integer salaryMax = StringUtils.isNotBlank(salaryMaxStr) ? Integer.valueOf(salaryMaxStr) : null;
            Byte statusId = StringUtils.isNotBlank(statusIdStr) ? Byte.valueOf(statusIdStr) : 2; // Default: Published

            // Create job
            Integer jobId = jobDAO.createJob(
                recruiter.getCompanyId(),
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

            // Add skills if provided
            if (skillIds != null && skillIds.length > 0) {
                for (String skillIdStr : skillIds) {
                    if (StringUtils.isNotBlank(skillIdStr)) {
                        jobDAO.addJobSkill(jobId, Integer.valueOf(skillIdStr));
                    }
                }
            }

            // Redirect to job detail or dashboard
            response.sendRedirect(request.getContextPath() + "/job/" + jobId + "?success=posted");

        } catch (SQLException e) {
            throw new ServletException("Error creating job", e);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid form data");
            try {
                doGet(request, response);
            } catch (Exception ex) {
                throw new ServletException("Error reloading form", ex);
            }
        }
    }
}
