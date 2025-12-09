package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.dao.CommonDAO;
import com.java_web.dao.JobDAO;
import com.java_web.dao.SavedJobDAO;
import com.java_web.model.auth.User;
import com.java_web.model.common.City;
import com.java_web.model.common.Skill;

@WebServlet("/jobs")
public class JobListingsServlet extends HttpServlet {

    private JobDAO jobDAO;
    private CommonDAO commonDAO;
    private SavedJobDAO savedJobDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        jobDAO = new JobDAO();
        commonDAO = new CommonDAO();
        savedJobDAO = new SavedJobDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Prevent caching to avoid back button issues
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        try {
            // Get filter parameters
            String keyword = request.getParameter("keyword");
            String cityIdStr = request.getParameter("cityId");
            String skillIdStr = request.getParameter("skillId");
            String employmentTypeStr = request.getParameter("employmentType");
            String seniorityLevelStr = request.getParameter("seniorityLevel");
            String remoteTypeStr = request.getParameter("remoteType");
            String pageStr = request.getParameter("page");
            String sortBy = request.getParameter("sortBy"); // date, relevance, salary
            
            Integer cityId = StringUtils.isNotBlank(cityIdStr) ? Integer.valueOf(cityIdStr) : null;
            Integer skillId = StringUtils.isNotBlank(skillIdStr) ? Integer.valueOf(skillIdStr) : null;
            Integer employmentType = StringUtils.isNotBlank(employmentTypeStr) ? Integer.valueOf(employmentTypeStr) : null;
            Integer seniorityLevel = StringUtils.isNotBlank(seniorityLevelStr) ? Integer.valueOf(seniorityLevelStr) : null;
            Integer remoteType = StringUtils.isNotBlank(remoteTypeStr) ? Integer.valueOf(remoteTypeStr) : null;
            int currentPage = StringUtils.isNotBlank(pageStr) ? Integer.parseInt(pageStr) : 1;
            int pageSize = 20;

            // Get filter data for dropdowns
            List<City> cities = commonDAO.getAllCities();
            List<Skill> skills = commonDAO.getTopSkills(50);
            List<Map<String, String>> employmentTypes = commonDAO.getEmploymentTypes();
            List<Map<String, String>> seniorityLevels = commonDAO.getSeniorityLevels();
            List<Map<String, String>> remoteTypes = commonDAO.getRemoteTypes();
            
            // Search jobs
            List<Map<String, Object>> jobs = jobDAO.searchJobs(keyword, cityId, skillId, currentPage, pageSize);

            // Check if user is logged in to show saved status
            User currentUser = (User) request.getSession().getAttribute("user");
            List<Integer> savedJobIds = new ArrayList<>();
            if (currentUser != null) {
                savedJobIds = savedJobDAO.getSavedJobsByUser(currentUser.getUserId())
                    .stream()
                    .map(sj -> sj.getJobId())
                    .collect(java.util.stream.Collectors.toList());
            }

            // Parse skills JSON for each job and add saved status
            for (Map<String, Object> job : jobs) {
                String skillsJson = (String) job.get("Skills");
                if (StringUtils.isNotBlank(skillsJson)) {
                    List<Map<String, Object>> skillsList = objectMapper.readValue(
                            skillsJson,
                            new TypeReference<List<Map<String, Object>>>() {}
                    );
                    job.put("skillsList", skillsList);
                }
                
                // Add saved status
                Integer jobId = (Integer) job.get("jobId");
                job.put("isSaved", savedJobIds.contains(jobId));
            }

            // Get total count for better pagination
            int totalJobs = jobDAO.getTotalPublishedJobCount();

            // Set attributes
            request.setAttribute("jobs", jobs);
            request.setAttribute("cities", cities);
            request.setAttribute("skills", skills);
            request.setAttribute("employmentTypes", employmentTypes);
            request.setAttribute("seniorityLevels", seniorityLevels);
            request.setAttribute("remoteTypes", remoteTypes);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalJobs", totalJobs);
            request.setAttribute("hasMore", jobs.size() == pageSize);
            request.setAttribute("keyword", keyword);
            request.setAttribute("selectedCityId", cityId);
            request.setAttribute("selectedSkillId", skillId);
            request.setAttribute("selectedEmploymentType", employmentType);
            request.setAttribute("selectedSeniorityLevel", seniorityLevel);
            request.setAttribute("selectedRemoteType", remoteType);
            request.setAttribute("sortBy", sortBy);

            request.getRequestDispatcher("/WEB-INF/views/job/job-listings.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error loading job listings", e);
        }
    }
}
