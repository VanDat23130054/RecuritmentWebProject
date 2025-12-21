package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.CandidateDAO;
import com.java_web.dao.SavedJobDAO;
import com.java_web.dao.UserDAO;
import com.java_web.dao.JobDAO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.model.dto.JobSearchDTO;
import com.java_web.model.dto.JobDetailDTO;
import com.java_web.model.candidate.SavedJob;

import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class CandidateProfileServlet
 */
@WebServlet("/candidate/profile/*")
public class CandidateProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private CandidateDAO candidateDAO;
    private SavedJobDAO savedJobDAO;
    private JobDAO jobDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        candidateDAO = new CandidateDAO();
        savedJobDAO = new SavedJobDAO();
        jobDAO = new JobDAO();
        objectMapper = new ObjectMapper();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Only serve candidate users
            if (!"Candidate".equals(user.getRole())) {
                // Option: Redirect to an appropriate page (home) or show forbidden
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "This page is only for candidates.");
                return;
            }

            request.setAttribute("user", user);

            Candidate candidate = candidateDAO.getCandidateByUserId(userId);
            request.setAttribute("candidate", candidate);

            // Determine path: profile view or edit form
            String pathInfo = request.getPathInfo(); // e.g. /edit or /
            if (pathInfo != null && pathInfo.startsWith("/edit")) {
                // Show edit form
                request.getRequestDispatcher("/WEB-INF/views/candidate/candidate-edit.jsp").forward(request, response);
                return;
            }

            // Load saved jobs and map to job DTOs so they can render with job-card markup
            List<SavedJob> rawSaved = savedJobDAO.getSavedJobsByUser(userId);
            List<JobSearchDTO> savedJobListings = new ArrayList<>();
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
                                    detail.getSkills(), new TypeReference<List<Map<String, Object>>>() {});
                            job.setSkillsList(skillsList);
                        }

                        job.setIsSaved(Boolean.TRUE);
                        savedJobListings.add(job);
                    } catch (Exception e) {
                        // Ignore failures for individual saved items
                        e.printStackTrace();
                    }
                }
            }

            request.setAttribute("savedJobs", savedJobListings);

            request.getRequestDispatcher("/WEB-INF/views/candidate/candidate-profile.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle updates from edit form
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
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "This action is only for candidate users.");
                return;
            }

            // Read form fields
            String fullName = request.getParameter("fullName");
            String headline = request.getParameter("headline");
            String summary = request.getParameter("summary");
            String yearsStr = request.getParameter("yearsOfExperience");
            String cityIdStr = request.getParameter("cityId");
            String countryIdStr = request.getParameter("countryId");
            String avatarUrl = request.getParameter("avatarUrl");
            String publicProfileStr = request.getParameter("publicProfile");

            Candidate candidate = candidateDAO.getCandidateByUserId(userId);
            if (candidate == null) {
                candidate = new Candidate();
                candidate.setUserId(userId);
            }

            candidate.setFullName(fullName);
            candidate.setHeadline(headline);
            candidate.setSummary(summary);

            if (yearsStr != null && !yearsStr.isEmpty()) {
                try {
                    candidate.setYearsOfExperience(Double.valueOf(yearsStr));
                } catch (NumberFormatException nfe) {
                    candidate.setYearsOfExperience(null);
                }
            } else {
                candidate.setYearsOfExperience(null);
            }

            if (cityIdStr != null && !cityIdStr.isEmpty()) {
                try {
                    candidate.setCityId(Integer.valueOf(cityIdStr));
                } catch (NumberFormatException nfe) {
                    candidate.setCityId(null);
                }
            } else {
                candidate.setCityId(null);
            }

            if (countryIdStr != null && !countryIdStr.isEmpty()) {
                try {
                    candidate.setCountryId(Integer.valueOf(countryIdStr));
                } catch (NumberFormatException nfe) {
                    candidate.setCountryId(null);
                }
            } else {
                candidate.setCountryId(null);
            }

            candidate.setAvatarUrl(avatarUrl);
            candidate.setPublicProfile("on".equals(publicProfileStr));

            boolean ok = candidateDAO.updateCandidateByUserId(userId, candidate);

            if (ok) {
                // Redirect back to profile view
                response.sendRedirect(request.getContextPath() + "/candidate/profile");
            } else {
                request.setAttribute("error", "Unable to update profile. Please try again.");
                request.setAttribute("candidate", candidate);
                request.getRequestDispatcher("/WEB-INF/views/candidate/candidate-edit.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}