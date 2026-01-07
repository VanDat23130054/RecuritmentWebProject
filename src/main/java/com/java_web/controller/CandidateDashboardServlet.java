package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.ApplicationDAO;
import com.java_web.dao.CandidateDAO;
import com.java_web.dao.SavedJobDAO;
import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;
import com.java_web.model.candidate.SavedJob;
import com.java_web.model.dto.ApplicationListDTO;

/**
 * Servlet for the candidate dashboard page
 */
@WebServlet("/candidate/dashboard")
public class CandidateDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private CandidateDAO candidateDAO;
    private UserDAO userDAO;
    private ApplicationDAO applicationDAO;
    private SavedJobDAO savedJobDAO;

    @Override
    public void init() throws ServletException {
        candidateDAO = new CandidateDAO();
        userDAO = new UserDAO();
        applicationDAO = new ApplicationDAO();
        savedJobDAO = new SavedJobDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        try {
            User user = userDAO.findById(userId);
            if (user == null || !"Candidate".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Candidates only");
                return;
            }

            // Get candidate profile
            Candidate candidate = candidateDAO.getCandidateByUserId(userId);
            request.setAttribute("candidate", candidate);

            // Get statistics
            Map<String, Object> stats = new HashMap<>();

            if (candidate != null && candidate.getCandidateId() != null) {
                // Get all applications for counting (use large page size to get all)
                List<ApplicationListDTO> applications = applicationDAO.getApplicationsByCandidate(
                        candidate.getCandidateId(), null, 1, 1000);
                int totalApplications = applications != null ? applications.size() : 0;

                // Count by status
                int underReviewCount = 0;
                int interviewCount = 0;
                int rejectedCount = 0;

                if (applications != null) {
                    for (ApplicationListDTO app : applications) {
                        String status = app.getStatus();
                        if ("Under Review".equalsIgnoreCase(status) || "Pending".equalsIgnoreCase(status)) {
                            underReviewCount++;
                        } else if ("Interview".equalsIgnoreCase(status) || "Interviewed".equalsIgnoreCase(status)) {
                            interviewCount++;
                        } else if ("Rejected".equalsIgnoreCase(status)) {
                            rejectedCount++;
                        }
                    }
                }

                stats.put("totalApplications", totalApplications);
                stats.put("underReviewCount", underReviewCount);
                stats.put("interviewCount", interviewCount);
                stats.put("rejectedCount", rejectedCount);

                // Get saved jobs count
                List<SavedJob> savedJobs = savedJobDAO.getSavedJobsByUser(userId);
                stats.put("savedJobsCount", savedJobs != null ? savedJobs.size() : 0);

                // Recent applications (last 5)
                if (applications != null && applications.size() > 5) {
                    request.setAttribute("recentApplications", applications.subList(0, 5));
                } else {
                    request.setAttribute("recentApplications", applications);
                }
            } else {
                stats.put("totalApplications", 0);
                stats.put("underReviewCount", 0);
                stats.put("interviewCount", 0);
                stats.put("rejectedCount", 0);
                stats.put("savedJobsCount", 0);
            }

            request.setAttribute("stats", stats);
            request.getRequestDispatcher("/WEB-INF/views/candidate/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }
    }
}
