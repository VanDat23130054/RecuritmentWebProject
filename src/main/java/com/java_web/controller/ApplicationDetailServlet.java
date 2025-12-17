package com.java_web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.ApplicationDAO;
import com.java_web.model.dto.ApplicationDetailDTO;

@WebServlet("/employer/applications/detail")
public class ApplicationDetailServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.print("{\"success\": false, \"message\": \"Please login first\"}");
            return;
        }

        // Get recruiter ID - this serves as role check too
        Integer recruiterId = (Integer) session.getAttribute("recruiterId");
        if (recruiterId == null) {
            out.print("{\"success\": false, \"message\": \"Access denied. Only recruiters can view application details.\"}");
            return;
        }

        // Get application ID
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Application ID is required\"}");
            return;
        }

        try {
            int applicationId = Integer.parseInt(idParam);

            // Get application details
            ApplicationDetailDTO application = applicationDAO.getApplicationDetail(applicationId, recruiterId);

            if (application == null) {
                out.print("{\"success\": false, \"message\": \"Application not found\"}");
                return;
            }

            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"application\": {");
            json.append("\"applicationId\": ").append(application.getApplicationId()).append(",");
            json.append("\"candidateName\": \"").append(escapeJson(application.getCandidateName())).append("\",");
            json.append("\"candidateEmail\": \"").append(escapeJson(application.getCandidateEmail())).append("\",");

            String candidateSummary = application.getCandidateSummary();
            if (candidateSummary != null) {
                json.append("\"candidateSummary\": \"").append(escapeJson(candidateSummary)).append("\",");
            } else {
                json.append("\"candidateSummary\": null,");
            }

            String candidateCity = application.getCandidateCity();
            if (candidateCity != null) {
                json.append("\"candidateCity\": \"").append(escapeJson(candidateCity)).append("\",");
            } else {
                json.append("\"candidateCity\": null,");
            }

            json.append("\"jobTitle\": \"").append(escapeJson(application.getJobTitle())).append("\",");
            json.append("\"companyName\": \"").append(escapeJson(application.getCompanyName())).append("\",");

            String coverLetter = application.getCoverLetter();
            if (coverLetter != null) {
                json.append("\"coverLetter\": \"").append(escapeJson(coverLetter)).append("\",");
            } else {
                json.append("\"coverLetter\": null,");
            }

            String source = application.getSource();
            if (source != null) {
                json.append("\"source\": \"").append(escapeJson(source)).append("\",");
            } else {
                json.append("\"source\": null,");
            }

            json.append("\"appliedAt\": \"").append(application.getAppliedAt()).append("\",");
            json.append("\"status\": \"").append(escapeJson(application.getStatus())).append("\",");

            Integer resumeId = application.getResumeId();
            if (resumeId != null) {
                json.append("\"resumeId\": ").append(resumeId).append(",");
            } else {
                json.append("\"resumeId\": null,");
            }

            String resumeFileName = application.getResumeFileName();
            if (resumeFileName != null) {
                json.append("\"resumeFileName\": \"").append(escapeJson(resumeFileName)).append("\",");
            } else {
                json.append("\"resumeFileName\": null,");
            }

            String resumeFileUrl = application.getResumeFileUrl();
            if (resumeFileUrl != null) {
                json.append("\"resumeFileUrl\": \"").append(escapeJson(resumeFileUrl)).append("\"");
            } else {
                json.append("\"resumeFileUrl\": null");
            }

            json.append("}}");

            out.print(json.toString());

        } catch (NumberFormatException e) {
            out.print("{\"success\": false, \"message\": \"Invalid application ID\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"Failed to load application details\"}");
        }
    }

    private String escapeJson(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
