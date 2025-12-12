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

@WebServlet("/employer/applications/updateStatus")
public class UpdateApplicationStatusServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

        // Check role
        String role = (String) session.getAttribute("role");
        if (!"Recruiter".equals(role) && !"EmployerAdmin".equals(role)) {
            out.print("{\"success\": false, \"message\": \"Access denied\"}");
            return;
        }

        // Get recruiter ID
        Integer recruiterId = (Integer) session.getAttribute("recruiterId");
        if (recruiterId == null) {
            out.print("{\"success\": false, \"message\": \"Recruiter profile not found\"}");
            return;
        }

        // Get parameters
        String idParam = request.getParameter("applicationId");
        String newStatus = request.getParameter("status");

        if (idParam == null || idParam.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Application ID is required\"}");
            return;
        }

        if (newStatus == null || newStatus.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Status is required\"}");
            return;
        }

        // Validate status
        String[] validStatuses = {"Applied", "Under Review", "Interview Scheduled",
            "Offer Extended", "Rejected", "Withdrawn"};
        boolean isValidStatus = false;
        for (String status : validStatuses) {
            if (status.equals(newStatus)) {
                isValidStatus = true;
                break;
            }
        }

        if (!isValidStatus) {
            out.print("{\"success\": false, \"message\": \"Invalid status value\"}");
            return;
        }

        try {
            int applicationId = Integer.parseInt(idParam);

            // Update status
            boolean success = applicationDAO.updateApplicationStatus(applicationId, recruiterId, newStatus);

            if (success) {
                out.print("{\"success\": true, \"message\": \"Application status updated successfully\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to update application status\"}");
            }

        } catch (NumberFormatException e) {
            out.print("{\"success\": false, \"message\": \"Invalid application ID\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"An error occurred while updating status\"}");
        }
    }
}
