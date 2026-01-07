package com.java_web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.ApplicationDAO;
import com.java_web.dao.CandidateDAO;
import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;
import com.java_web.model.dto.ApplicationDetailDTO;

@WebServlet("/candidate/application/detail")
public class CandidateApplicationDetailServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private CandidateDAO candidateDAO = new CandidateDAO();
    private ApplicationDAO applicationDAO = new ApplicationDAO();

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

            // Use ApplicationDAO to get application detail
            ApplicationDetailDTO app = applicationDAO.getApplicationDetailByCandidate(applicationId, candidateId);

            if (app == null) {
                out.print("{\"success\": false, \"message\": \"Application not found\"}");
                return;
            }

            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"application\": {");
            json.append("\"applicationId\": ").append(app.getApplicationId()).append(',');
            json.append("\"jobId\": ").append(app.getJobId()).append(',');
            json.append("\"jobTitle\": \"").append(escapeJson(app.getJobTitle())).append('\"').append(',');
            json.append("\"companyName\": \"").append(escapeJson(app.getCompanyName())).append('\"').append(',');
            json.append("\"appliedAt\": \"").append(app.getAppliedAt()).append('\"').append(',');
            json.append("\"status\": \"").append(escapeJson(app.getStatus())).append('\"').append(',');

            json.append("\"coverLetter\": ");
            if (app.getCoverLetter() != null) {
                json.append('\"').append(escapeJson(app.getCoverLetter())).append('\"').append(',');
            } else {
                json.append("null,");
            }

            json.append("\"resumeFileName\": ");
            if (app.getResumeFileName() != null) {
                json.append('\"').append(escapeJson(app.getResumeFileName())).append('\"').append(',');
            } else {
                json.append("null,");
            }

            json.append("\"resumeFileUrl\": ");
            if (app.getResumeFileUrl() != null) {
                json.append('\"').append(escapeJson(app.getResumeFileUrl())).append('\"').append(',');
            } else {
                json.append("null,");
            }

            json.append("\"recruiterNote\": ");
            if (app.getRecruiterNote() != null) {
                json.append('\"').append(escapeJson(app.getRecruiterNote())).append('\"');
            } else {
                json.append("null");
            }
            json.append("}}");

            out.print(json.toString());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid application ID\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Database error\"}");
        }
    }

    private String escapeJson(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
