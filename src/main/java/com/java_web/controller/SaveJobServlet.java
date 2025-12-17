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

import com.java_web.dao.SavedJobDAO;
import com.java_web.model.auth.User;

@WebServlet("/api/save-job")
public class SaveJobServlet extends HttpServlet {

    private SavedJobDAO savedJobDAO;

    @Override
    public void init() throws ServletException {
        savedJobDAO = new SavedJobDAO();
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
                out.print("{\"success\": false, \"message\": \"Please login to save jobs\"}");
                return;
            }

            User user = (User) session.getAttribute("user");
            
            // Check if user is a candidate - only candidates can save jobs
            if (!"Candidate".equals(user.getRole())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"message\": \"Only candidates can save jobs\"}");
                return;
            }
            
            String jobIdStr = request.getParameter("jobId");
            String action = request.getParameter("action"); // "save" or "unsave"

            if (StringUtils.isBlank(jobIdStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Job ID is required\"}");
                return;
            }

            int jobId = Integer.parseInt(jobIdStr);
            boolean success;

            if ("unsave".equals(action)) {
                success = savedJobDAO.unsaveJob(user.getUserId(), jobId);
            } else {
                success = savedJobDAO.saveJob(user.getUserId(), jobId);
            }

            if (success) {
                out.print("{\"success\": true, \"message\": \"" + 
                         ("unsave".equals(action) ? "Job removed from saved list" : "Job saved successfully") + 
                         "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Failed to " + 
                         ("unsave".equals(action) ? "unsave" : "save") + " job\"}");
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid job ID\"}");
        }
    }
}
