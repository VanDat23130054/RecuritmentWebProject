package com.java_web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.config.DB;
import com.java_web.dao.CandidateDAO;
import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;

@WebServlet("/candidate/application/detail")
public class CandidateApplicationDetailServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private CandidateDAO candidateDAO = new CandidateDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"Please login to view application\"}");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        try {
            User user = userDAO.findById(userId);
            if (user == null || !"Candidate".equals(user.getRole())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Candidates only\"}");
                return;
            }

            Integer candidateId = (Integer) session.getAttribute("candidateId");
            if (candidateId == null) {
                Candidate cand = candidateDAO.getCandidateByUserId(userId);
                if (cand != null) {
                    candidateId = cand.getCandidateId();
                    session.setAttribute("candidateId", candidateId);
                }
            }

            if (candidateId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Candidate profile not found\"}");
                return;
            }

            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Application ID required\"}");
                return;
            }

            int applicationId = Integer.parseInt(idParam);

            // Query application details directly (no DAO/DTO changes)
            String sql = "SELECT a.ApplicationId, a.JobId, j.Title AS jobTitle, e.Name AS companyName, a.AppliedAt, a.Status, a.CoverLetter, r.FileName AS resumeFileName, r.FileUrl AS resumeFileUrl, a.RecruiterNote "
                       + "FROM candidate.Applications a "
                       + "LEFT JOIN employer.Jobs j ON a.JobId = j.JobId "
                       + "LEFT JOIN employer.Companies e ON j.CompanyID = e.CompanyID "
                       + "LEFT JOIN candidate.Resumes r ON a.ResumeId = r.ResumeId "
                       + "WHERE a.ApplicationId = ? AND a.CandidateId = ?";

            try (Connection conn = DB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, applicationId);
                ps.setInt(2, candidateId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        out.print("{\"success\": false, \"message\": \"Application not found\"}");
                        return;
                    }

                    String jobTitle = rs.getString("jobTitle");
                    String companyName = rs.getString("companyName");
                    java.sql.Timestamp appliedAt = rs.getTimestamp("AppliedAt");
                    String status = rs.getString("Status");
                    String coverLetter = rs.getString("CoverLetter");
                    String resumeFileName = rs.getString("resumeFileName");
                    String resumeFileUrl = rs.getString("resumeFileUrl");
                    String recruiterNote = null;
                    try {
                        recruiterNote = rs.getString("RecruiterNote");
                    } catch (Exception ex) {
                        // Column might not exist; ignore and continue
                        recruiterNote = null;
                    }

                    StringBuilder json = new StringBuilder();
                    json.append("{\"success\": true, \"application\": {");
                    json.append("\"applicationId\": ").append(rs.getInt("ApplicationId")).append(',');
                    json.append("\"jobId\": ").append(rs.getInt("JobId")).append(',');
                    json.append("\"jobTitle\": \"").append(escapeJson(jobTitle)).append('\"').append(',');
                    json.append("\"companyName\": \"").append(escapeJson(companyName)).append('\"').append(',');
                    json.append("\"appliedAt\": \"").append(appliedAt).append('\"').append(',');
                    json.append("\"status\": \"").append(escapeJson(status)).append('\"').append(',');

                    json.append("\"coverLetter\": ");
                    if (coverLetter != null) {
                        json.append('\"').append(escapeJson(coverLetter)).append('\"').append(',');
                    } else {
                        json.append("null,");
                    }

                    json.append("\"resumeFileName\": ");
                    if (resumeFileName != null) {
                        json.append('\"').append(escapeJson(resumeFileName)).append('\"').append(',');
                    } else {
                        json.append("null,");
                    }

                    json.append("\"resumeFileUrl\": ");
                    if (resumeFileUrl != null) {
                        json.append('\"').append(escapeJson(resumeFileUrl)).append('\"').append(',');
                    } else {
                        json.append("null,");
                    }

                    json.append("\"recruiterNote\": ");
                    if (recruiterNote != null) {
                        json.append('\"').append(escapeJson(recruiterNote)).append('\"');
                    } else {
                        json.append("null");
                    }
                    json.append("}}");

                    out.print(json.toString());
                }
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid application ID\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Database error\"}");
        }
    }

    private String escapeJson(Object value) {
        if (value == null) return "";
        return value.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}