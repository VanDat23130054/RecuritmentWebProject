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
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.dao.JobDAO;
import com.java_web.dao.SavedJobDAO;
import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.SavedJob;
import com.java_web.model.dto.JobSearchDTO;
import com.java_web.model.dto.JobDetailDTO;

/**
 * Servlet to display a candidate's saved jobs on a separate page
 */
@WebServlet("/candidate/saved-jobs")
public class CandidateSavedJobsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private SavedJobDAO savedJobDAO;
    private JobDAO jobDAO;
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        savedJobDAO = new SavedJobDAO();
        jobDAO = new JobDAO();
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            User user = userDAO.findById(userId);
            if (user == null || !"Candidate".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Load saved jobs and convert to job DTOs
            List<SavedJob> rawSaved = savedJobDAO.getSavedJobsByUser(userId);
            List<JobSearchDTO> jobs = new ArrayList<>();
            if (rawSaved != null && !rawSaved.isEmpty()) {
                for (SavedJob sj : rawSaved) {
                    try {
                        JobDetailDTO detail = jobDAO.getJobDetail(sj.getJobId());
                        if (detail == null) continue;

                        JobSearchDTO job = new JobSearchDTO();
                        job.setJobId(detail.getJobId());
                        job.setTitle(detail.getTitle());
                        job.setSlug(detail.getSlug());
                        job.setCompanyId(detail.getCompanyId());
                        job.setCompanyName(detail.getCompanyName());
                        job.setLogoUrl(detail.getLogoUrl());
                        job.setCityName(detail.getCityName());
                        job.setSalaryMin(detail.getSalaryMin());
                        job.setSalaryMax(detail.getSalaryMax());
                        job.setCurrency(detail.getCurrency());
                        job.setIsFeatured(detail.getIsFeatured());
                        job.setSkills(detail.getSkills());

                        if (detail.getSkills() != null && !detail.getSkills().isEmpty()) {
                            List<Map<String, Object>> skillsList = objectMapper.readValue(
                                    detail.getSkills(), new TypeReference<List<Map<String, Object>>>() {
                                    });
                            job.setSkillsList(skillsList);
                        }

                        job.setIsSaved(Boolean.TRUE);
                        jobs.add(job);
                    } catch (Exception e) {
                        // log and continue
                        e.printStackTrace();
                    }
                }
            }

            request.setAttribute("jobs", jobs);
            request.getRequestDispatcher("/WEB-INF/views/candidate/saved-jobs.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
