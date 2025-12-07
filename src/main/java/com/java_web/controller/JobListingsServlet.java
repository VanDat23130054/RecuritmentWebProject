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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.dao.CommonDAO;
import com.java_web.dao.JobDAO;
import com.java_web.model.common.City;
import com.java_web.model.common.Skill;

@WebServlet("/jobs")
public class JobListingsServlet extends HttpServlet {

    private JobDAO jobDAO;
    private CommonDAO commonDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        jobDAO = new JobDAO();
        commonDAO = new CommonDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get filter parameters
            String keyword = request.getParameter("keyword");
            String cityIdStr = request.getParameter("cityId");
            String skillIdStr = request.getParameter("skillId");
            String pageStr = request.getParameter("page");
            String sortBy = request.getParameter("sortBy"); // date, relevance, salary
            
            Integer cityId = StringUtils.isNotBlank(cityIdStr) ? Integer.valueOf(cityIdStr) : null;
            Integer skillId = StringUtils.isNotBlank(skillIdStr) ? Integer.valueOf(skillIdStr) : null;
            int currentPage = StringUtils.isNotBlank(pageStr) ? Integer.parseInt(pageStr) : 1;
            int pageSize = 20;

            // Get filter data for dropdowns
            List<City> cities = commonDAO.getAllCities();
            List<Skill> skills = commonDAO.getTopSkills(50);
            
            // Search jobs
            List<Map<String, Object>> jobs = jobDAO.searchJobs(keyword, cityId, skillId, currentPage, pageSize);

            // Parse skills JSON for each job
            for (Map<String, Object> job : jobs) {
                String skillsJson = (String) job.get("Skills");
                if (StringUtils.isNotBlank(skillsJson)) {
                    List<Map<String, Object>> skillsList = objectMapper.readValue(
                            skillsJson,
                            new TypeReference<List<Map<String, Object>>>() {}
                    );
                    job.put("skillsList", skillsList);
                }
            }

            // Set attributes
            request.setAttribute("jobs", jobs);
            request.setAttribute("cities", cities);
            request.setAttribute("skills", skills);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("hasMore", jobs.size() == pageSize);
            request.setAttribute("keyword", keyword);
            request.setAttribute("selectedCityId", cityId);
            request.setAttribute("selectedSkillId", skillId);
            request.setAttribute("sortBy", sortBy);

            request.getRequestDispatcher("/WEB-INF/views/job/job-listings.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error loading job listings", e);
        }
    }
}
