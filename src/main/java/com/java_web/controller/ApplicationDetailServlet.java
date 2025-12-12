package com.java_web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.ApplicationDAO;

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
            Map<String, Object> application = applicationDAO.getApplicationDetail(applicationId, recruiterId);

            if (application == null) {
                out.print("{\"success\": false, \"message\": \"Application not found\"}");
                return;
            }

            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"application\": {");
            json.append("\"applicationId\": ").append(application.get("applicationId")).append(",");
            json.append("\"candidateName\": \"").append(escapeJson(application.get("candidateName"))).append("\",");
            json.append("\"candidateEmail\": \"").append(escapeJson(application.get("candidateEmail"))).append("\",");

            Object phone = application.get("candidatePhone");
            if (phone != null) {
                json.append("\"candidatePhone\": \"").append(escapeJson(phone)).append("\",");
            } else {
                json.append("\"candidatePhone\": null,");
            }

            json.append("\"jobTitle\": \"").append(escapeJson(application.get("jobTitle"))).append("\",");
            json.append("\"companyName\": \"").append(escapeJson(application.get("companyName"))).append("\",");

            Object coverLetter = application.get("coverLetter");
            if (coverLetter != null) {
                json.append("\"coverLetter\": \"").append(escapeJson(coverLetter)).append("\",");
            } else {
                json.append("\"coverLetter\": null,");
            }

            json.append("\"appliedAt\": \"").append(application.get("appliedAt")).append("\",");
            json.append("\"status\": \"").append(escapeJson(application.get("status"))).append("\"");
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
