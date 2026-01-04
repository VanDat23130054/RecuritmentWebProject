package com.java_web.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.config.DB;
import com.java_web.dao.CandidateDAO;
import com.java_web.dao.JobDAO;
import com.java_web.dao.ResumeDAO;
import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;
import com.java_web.model.dto.JobDetailDTO;
import com.java_web.model.dto.ResumeDTO;

@WebServlet("/apply/*")
public class ApplyJobServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private CandidateDAO candidateDAO = new CandidateDAO();
    private JobDAO jobDAO = new JobDAO();
    private ResumeDAO resumeDAO = new ResumeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        try {
            User user = userDAO.findById(userId);
            if (user == null || !"Candidate".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Candidates only");
                return;
            }

            String pathInfo = request.getPathInfo(); // /{jobId}
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.sendRedirect(request.getContextPath() + "/jobs");
                return;
            }

            int jobId;
            try {
                jobId = Integer.parseInt(pathInfo.substring(1));
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/jobs");
                return;
            }

            // Ensure job exists
            JobDetailDTO job = jobDAO.getJobDetail(jobId);
            if (job == null) {
                response.sendRedirect(request.getContextPath() + "/jobs?error=jobNotFound");
                return;
            }

            // Resolve candidateId
            Integer candidateId = (Integer) session.getAttribute("candidateId");
            if (candidateId == null) {
                Candidate cand = candidateDAO.getCandidateByUserId(userId);
                if (cand != null) {
                    candidateId = cand.getCandidateId();
                    session.setAttribute("candidateId", candidateId);
                }
            }

            if (candidateId == null) {
                response.sendRedirect(request.getContextPath() + "/candidate/profile?error=candidateProfileNotFound");
                return;
            }

            // Check if already applied
            String checkSql = "SELECT COUNT(*) AS cnt FROM candidate.Applications WHERE JobId = ? AND CandidateId = ?";
            try (Connection conn = DB.getConnection(); PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, jobId);
                ps.setInt(2, candidateId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt("cnt") > 0) {
                        // Already applied - redirect back with info
                        response.sendRedirect(request.getContextPath() + "/job/" + jobId + "?error=alreadyApplied");
                        return;
                    }
                }
            }

            // Load candidate resumes to allow selecting one
            List<ResumeDTO> resumes = resumeDAO.getResumesByCandidateId(candidateId);

            request.setAttribute("job", job);
            request.setAttribute("resumes", resumes);
            request.setAttribute("jobId", jobId);

            request.getRequestDispatcher("/WEB-INF/views/candidate/apply-form.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Failed to prepare application form", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        try {
            User user = userDAO.findById(userId);
            if (user == null || !"Candidate".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Candidates only");
                return;
            }

            String pathInfo = request.getPathInfo(); // /{jobId}
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.sendRedirect(request.getContextPath() + "/jobs");
                return;
            }

            int jobId;
            try {
                jobId = Integer.parseInt(pathInfo.substring(1));
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/jobs");
                return;
            }

            // Resolve candidateId
            Integer candidateId = (Integer) session.getAttribute("candidateId");
            if (candidateId == null) {
                Candidate cand = candidateDAO.getCandidateByUserId(userId);
                if (cand != null) {
                    candidateId = cand.getCandidateId();
                    session.setAttribute("candidateId", candidateId);
                }
            }

            if (candidateId == null) {
                response.sendRedirect(request.getContextPath() + "/candidate/profile?error=candidateProfileNotFound");
                return;
            }

            // Read form params
            String resumeIdStr = request.getParameter("resumeId");
            String coverLetter = request.getParameter("coverLetter");

            Integer resumeId = null;
            if (resumeIdStr != null && !resumeIdStr.trim().isEmpty()) {
                try {
                    resumeId = Integer.parseInt(resumeIdStr);
                } catch (NumberFormatException e) {
                    resumeId = null;
                }
            }

            // Check duplicate again
            String checkSql = "SELECT COUNT(*) AS cnt FROM candidate.Applications WHERE JobId = ? AND CandidateId = ?";
            try (Connection conn = DB.getConnection(); PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, jobId);
                ps.setInt(2, candidateId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt("cnt") > 0) {
                        response.sendRedirect(request.getContextPath() + "/job/" + jobId + "?error=alreadyApplied");
                        return;
                    }
                }
            }

            // Insert application
            String insertSql = "INSERT INTO candidate.Applications (JobId, CandidateId, ResumeId, CoverLetter, Source, AppliedAt, Status) VALUES (?, ?, ?, ?, ?, GETDATE(), 'Applied')";
            try (Connection conn = DB.getConnection(); PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, jobId);
                ps.setInt(2, candidateId);
                if (resumeId != null) {
                    ps.setInt(3, resumeId);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                if (coverLetter != null && !coverLetter.trim().isEmpty()) {
                    ps.setString(4, coverLetter.trim());
                } else {
                    ps.setNull(4, java.sql.Types.NVARCHAR);
                }
                ps.setString(5, "Website");

                int updated = ps.executeUpdate();
                if (updated > 0) {
                    response.sendRedirect(request.getContextPath() + "/job/" + jobId + "?success=applied");
                } else {
                    response.sendRedirect(request.getContextPath() + "/job/" + jobId + "?error=applyFailed");
                }
            }

        } catch (SQLException e) {
            throw new ServletException("Failed to apply for job", e);
        }
    }
}