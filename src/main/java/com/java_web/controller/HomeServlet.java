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
import com.java_web.dao.CompanyDAO;
import com.java_web.dao.JobDAO;
import com.java_web.model.common.City;
import com.java_web.model.common.Skill;

@WebServlet({"/", "/home", "/index"})
public class HomeServlet extends HttpServlet {

    private JobDAO jobDAO;
    private CompanyDAO companyDAO;
    private CommonDAO commonDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        jobDAO = new JobDAO();
        companyDAO = new CompanyDAO();
        commonDAO = new CommonDAO();
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
            // Get cities for dropdown
            List<City> cities = commonDAO.getAllCities();
            request.setAttribute("cities", cities);

            // Get top skills for suggestions
            List<Skill> topSkills = commonDAO.getTopSkills(8);
            request.setAttribute("topSkills", topSkills);

            // Get total job count
            int totalJobs = jobDAO.getTotalPublishedJobCount();
            request.setAttribute("totalJobs", totalJobs);

            // Get featured/recent jobs for homepage
            String keyword = request.getParameter("keyword");
            String cityIdStr = request.getParameter("cityId");
            Integer cityId = (StringUtils.isNotBlank(cityIdStr))
                    ? Integer.valueOf(cityIdStr) : null;

            List<Map<String, Object>> jobs = jobDAO.searchJobs(keyword, cityId, null, 1, 20);

            // Parse skills JSON for each job
            for (Map<String, Object> job : jobs) {
                String skillsJson = (String) job.get("Skills");
                if (StringUtils.isNotBlank(skillsJson)) {
                    List<Map<String, Object>> skillsList = objectMapper.readValue(
                            skillsJson,
                            new TypeReference<List<Map<String, Object>>>() {
                    }
                    );
                    job.put("skillsList", skillsList);
                }
            }

            request.setAttribute("jobs", jobs);

            // Get top employers
            List<Map<String, Object>> topEmployers = companyDAO.getTopEmployers(6);

            // Parse top skills JSON for each company
            for (Map<String, Object> company : topEmployers) {
                String skillsJson = (String) company.get("TopSkills");
                if (StringUtils.isNotBlank(skillsJson)) {
                    List<Map<String, Object>> skillsList = objectMapper.readValue(
                            skillsJson,
                            new TypeReference<List<Map<String, Object>>>() {
                    }
                    );
                    company.put("topSkillsList", skillsList);
                }
            }

            request.setAttribute("topEmployers", topEmployers);

            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);

        } catch (IOException | NumberFormatException | SQLException | ServletException e) {
            throw new ServletException("Error loading homepage", e);
        }
    }
}
