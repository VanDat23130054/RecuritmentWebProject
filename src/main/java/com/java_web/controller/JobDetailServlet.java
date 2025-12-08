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
import com.java_web.dao.CompanyDAO;
import com.java_web.dao.JobDAO;

@WebServlet("/job/*")
public class JobDetailServlet extends HttpServlet {

    private JobDAO jobDAO;
    private CompanyDAO companyDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        jobDAO = new JobDAO();
        companyDAO = new CompanyDAO();
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
            // Get job ID from URL path
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendRedirect(request.getContextPath() + "/jobs");
                return;
            }

            String jobIdStr = pathInfo.substring(1);
            Integer jobId = Integer.valueOf(jobIdStr);

            // Get job details
            Map<String, Object> job = jobDAO.getJobDetail(jobId);
            
            if (job == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Job not found");
                return;
            }

            // Parse skills JSON
            String skillsJson = (String) job.get("skills");
            if (StringUtils.isNotBlank(skillsJson)) {
                List<Map<String, Object>> skillsList = objectMapper.readValue(
                        skillsJson,
                        new TypeReference<List<Map<String, Object>>>() {}
                );
                job.put("skillsList", skillsList);
            }

            // Get company info
            Integer companyId = (Integer) job.get("companyId");
            Map<String, Object> company = companyDAO.getCompanyDetail(companyId);
            
            // Get related jobs
            List<Map<String, Object>> relatedJobs = jobDAO.getRelatedJobs(jobId, companyId, 5);

            request.setAttribute("job", job);
            request.setAttribute("company", company);
            request.setAttribute("relatedJobs", relatedJobs);

            request.getRequestDispatcher("/WEB-INF/views/job/job-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid job ID");
        } catch (SQLException e) {
            throw new ServletException("Error loading job details", e);
        }
    }
}
