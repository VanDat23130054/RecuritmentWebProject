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

import com.java_web.dao.ApplicationDAO;
import com.java_web.dao.CandidateDAO;
import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;

@WebServlet("/candidate/application/edit")
public class CandidateApplicationEditServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private CandidateDAO candidateDAO = new CandidateDAO();
    private ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"Please login to edit application\"}");
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

            String appIdStr = request.getParameter("applicationId");
            String newCover = request.getParameter("coverLetter");

            if (StringUtils.isBlank(appIdStr) || newCover == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Application ID and cover letter are required\"}");
                return;
            }

            int applicationId = Integer.parseInt(appIdStr);

            // Use ApplicationDAO to update cover letter via stored procedure
            boolean updated = applicationDAO.updateApplicationCoverLetter(applicationId, candidateId, newCover);

            if (updated) {
                out.print("{\"success\": true, \"message\": \"Cover letter updated\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print("{\"success\": false, \"message\": \"Unable to update cover letter. It may have been viewed already or does not belong to you.\"}");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid application ID\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}");
        }
    }
}
