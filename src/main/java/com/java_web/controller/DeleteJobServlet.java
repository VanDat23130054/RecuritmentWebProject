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

import com.java_web.dao.JobDAO;
import com.java_web.model.auth.User;

@WebServlet("/employer/delete-job")
public class DeleteJobServlet extends HttpServlet {

    private JobDAO jobDAO;

    @Override
    public void init() throws ServletException {
        jobDAO = new JobDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Check authentication and role
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\":false,\"message\":\"Not authenticated\"}");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"Recruiter".equals(user.getRole()) && !"EmployerAdmin".equals(user.getRole())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"success\":false,\"message\":\"Access denied\"}");
            return;
        }

        // Get recruiterId from session
        Integer recruiterId = (Integer) session.getAttribute("recruiterId");
        if (recruiterId == null) {
            out.print("{\"success\":false,\"message\":\"Recruiter profile not found\"}");
            return;
        }

        // Get job ID from request
        String jobIdStr = request.getParameter("jobId");
        if (StringUtils.isBlank(jobIdStr)) {
            out.print("{\"success\":false,\"message\":\"Job ID is required\"}");
            return;
        }

        try {
            Integer jobId = Integer.valueOf(jobIdStr);

            // Delete the job (with authorization check in stored procedure)
            boolean success = jobDAO.deleteJob(jobId, recruiterId);

            if (success) {
                out.print("{\"success\":true,\"message\":\"Job deleted successfully\"}");
            } else {
                out.print("{\"success\":false,\"message\":\"Failed to delete job. You may not have permission.\"}");
            }

        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"message\":\"Invalid job ID\"}");
        } catch (SQLException e) {
            String errorMsg = e.getMessage().replace("\"", "'");
            out.print("{\"success\":false,\"message\":\"Database error: " + errorMsg + "\"}");
            e.printStackTrace();
        }
    }
}
