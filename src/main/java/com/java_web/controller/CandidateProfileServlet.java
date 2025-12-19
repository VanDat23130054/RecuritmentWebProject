package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.CandidateDAO;
import com.java_web.dao.SavedJobDAO;
import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;

/**
 * Servlet implementation class CandidateProfileServlet
 */
@WebServlet("/candidate/profile/*")
public class CandidateProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private CandidateDAO candidateDAO;
    private SavedJobDAO savedJobDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        candidateDAO = new CandidateDAO();
        savedJobDAO = new SavedJobDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Only serve candidate users
            if (!"Candidate".equals(user.getRole())) {
                // Option: Redirect to an appropriate page (home) or show forbidden
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "This page is only for candidates.");
                return;
            }

            request.setAttribute("user", user);

            Candidate candidate = candidateDAO.getCandidateByUserId(userId);
            request.setAttribute("candidate", candidate);

            request.setAttribute("savedJobs", savedJobDAO.getSavedJobsByUser(userId));

            request.getRequestDispatcher("/WEB-INF/views/candidate/candidate-profile.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
