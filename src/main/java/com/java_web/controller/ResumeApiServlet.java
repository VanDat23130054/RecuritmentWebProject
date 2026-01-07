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

import org.apache.commons.lang3.StringUtils;

import com.java_web.dao.CandidateDAO;
import com.java_web.dao.ResumeDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;

/**
 * API Servlet for resume management operations (rename, delete, set primary)
 */
@WebServlet("/api/resume/*")
public class ResumeApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ResumeDAO resumeDAO;
    private CandidateDAO candidateDAO;

    @Override
    public void init() throws ServletException {
        resumeDAO = new ResumeDAO();
        candidateDAO = new CandidateDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\": false, \"message\": \"Please login to manage resumes\"}");
                return;
            }

            User user = (User) session.getAttribute("user");
            if (!"Candidate".equals(user.getRole())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Only candidates can manage resumes\"}");
                return;
            }

            Candidate candidate = candidateDAO.getCandidateByUserId(user.getUserId());
            if (candidate == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Candidate profile not found\"}");
                return;
            }

            String pathInfo = request.getPathInfo(); // e.g., /rename, /delete, /primary
            String action = pathInfo != null ? pathInfo.substring(1) : "";

            String resumeIdStr = request.getParameter("resumeId");
            if (StringUtils.isBlank(resumeIdStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Resume ID is required\"}");
                return;
            }

            int resumeId = Integer.parseInt(resumeIdStr);

            switch (action) {
                case "rename":
                    handleRename(request, response, out, resumeId, candidate.getCandidateId());
                    break;
                case "delete":
                    handleDelete(request, response, out, resumeId, candidate.getCandidateId());
                    break;
                case "primary":
                    handleSetPrimary(request, response, out, resumeId, candidate.getCandidateId());
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"Invalid action\"}");
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid resume ID\"}");
        }
    }

    private void handleRename(HttpServletRequest request, HttpServletResponse response,
            PrintWriter out, int resumeId, int candidateId) throws SQLException {

        String newName = request.getParameter("newName");
        if (StringUtils.isBlank(newName)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"New file name is required\"}");
            return;
        }

        // Sanitize filename - keep extension if exists
        newName = newName.trim();
        if (!newName.contains(".")) {
            // Add .pdf extension if not provided
            newName = newName + ".pdf";
        }

        boolean success = resumeDAO.renameResume(resumeId, newName, candidateId);
        if (success) {
            out.print("{\"success\": true, \"message\": \"Resume renamed successfully\", \"newName\": \"" + escapeJson(newName) + "\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"success\": false, \"message\": \"Resume not found or not owned by you\"}");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response,
            PrintWriter out, int resumeId, int candidateId) throws SQLException {

        boolean success = resumeDAO.deleteResumeSecure(resumeId, candidateId);
        if (success) {
            out.print("{\"success\": true, \"message\": \"Resume deleted successfully\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"success\": false, \"message\": \"Resume not found or not owned by you\"}");
        }
    }

    private void handleSetPrimary(HttpServletRequest request, HttpServletResponse response,
            PrintWriter out, int resumeId, int candidateId) throws SQLException {

        resumeDAO.setPrimaryResume(resumeId, candidateId);
        out.print("{\"success\": true, \"message\": \"Resume set as primary\"}");
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
