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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.dao.ApplicationDAO;
import com.java_web.dao.CandidateDAO;
import com.java_web.dao.CompanyDAO;
import com.java_web.dao.JobDAO;
import com.java_web.dao.SavedJobDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;
import com.java_web.model.dto.CompanyDetailDTO;
import com.java_web.model.dto.JobDetailDTO;
import com.java_web.model.dto.RelatedJobDTO;

@WebServlet("/job/*")
public class JobDetailServlet extends HttpServlet {

    private JobDAO jobDAO;
    private CompanyDAO companyDAO;
    private SavedJobDAO savedJobDAO;
    private ApplicationDAO applicationDAO;
    private CandidateDAO candidateDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        jobDAO = new JobDAO();
        companyDAO = new CompanyDAO();
        savedJobDAO = new SavedJobDAO();
        applicationDAO = new ApplicationDAO();
        candidateDAO = new CandidateDAO();
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
            JobDetailDTO job = jobDAO.getJobDetail(jobId);

            if (job == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Job not found");
                return;
            }

            // Parse skills JSON
            String skillsJson = job.getSkills();
            if (StringUtils.isNotBlank(skillsJson)) {
                List<Map<String, Object>> skillsList = objectMapper.readValue(
                        skillsJson,
                        new TypeReference<List<Map<String, Object>>>() {
                }
                );
                job.setSkillsList(skillsList);
            }

            // Get company info
            Integer companyId = job.getCompanyId();
            CompanyDetailDTO company = companyDAO.getCompanyDetail(companyId);

            // Get related jobs
            List<RelatedJobDTO> relatedJobs = jobDAO.getRelatedJobs(jobId, companyId, 5);

            // Check if job is saved for logged-in candidate
            HttpSession session = request.getSession(false);
            boolean isAlreadyApplied = false;
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                if ("Candidate".equals(user.getRole())) {
                    boolean isSaved = savedJobDAO.isJobSaved(user.getUserId(), jobId);
                    job.setIsSaved(isSaved);

                    // Check if candidate already applied
                    Integer userId = user.getUserId();
                    Candidate candidate = candidateDAO.getCandidateByUserId(userId);
                    if (candidate != null) {
                        isAlreadyApplied = applicationDAO.hasApplied(candidate.getCandidateId(), jobId);
                    }
                } else {
                    job.setIsSaved(false);
                }
            } else {
                job.setIsSaved(false);
            }

            request.setAttribute("job", job);
            request.setAttribute("company", company);
            request.setAttribute("relatedJobs", relatedJobs);
            request.setAttribute("isAlreadyApplied", isAlreadyApplied);

            request.getRequestDispatcher("/WEB-INF/views/job/job-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid job ID");
        } catch (SQLException e) {
            throw new ServletException("Error loading job details", e);
        }
    }
}
