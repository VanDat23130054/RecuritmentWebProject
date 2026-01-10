package com.java_web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

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
import com.java_web.model.dto.ApplicationListDTO;

@WebServlet("/candidate/applications")
public class CandidateApplicationsServlet extends HttpServlet {

    private ApplicationDAO applicationDAO;
    private CandidateDAO candidateDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        applicationDAO = new ApplicationDAO();
        candidateDAO = new CandidateDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
            if (user == null || !"Candidate".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            Candidate candidate = candidateDAO.getCandidateByUserId(userId);
            if (candidate == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Candidate profile not found");
                return;
            }

            String status = request.getParameter("status");
            String pageStr = request.getParameter("page");
            int page = 1;
            if (StringUtils.isNotBlank(pageStr)) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (NumberFormatException nfe) {
                    page = 1; // fallback to page 1 on bad input
                }
            }
            if (page < 1) {
                page = 1;
            }
            int pageSize = 20;

            // Ensure candidateId exists to avoid NPE when unboxing
            if (candidate.getCandidateId() == null) {
                session.setAttribute("error", "Candidate profile incomplete. Please complete your profile.");
                response.sendRedirect(request.getContextPath() + "/candidate/profile/edit");
                return;
            }

            System.out.println("[DEBUG] Getting applications for candidateId: " + candidate.getCandidateId() + ", status: " + status + ", page: " + page + ", pageSize: " + pageSize);

            List<ApplicationListDTO> applications = applicationDAO.getApplicationsByCandidate(
                    candidate.getCandidateId(), status, page, pageSize);

            System.out.println("[DEBUG] Found " + applications.size() + " applications");
            for (ApplicationListDTO app : applications) {
                System.out.println("[DEBUG] App: " + app.getApplicationId()
                        + " - " + app.getJobTitle()
                        + " - " + app.getStatus()
                        + " - CandidateId: " + app.getCandidateId()
                        + " - RecruiterId: " + app.getRecruiterId()
                        + " - JobId: " + app.getJobId());
            }

            int total = applicationDAO.getApplicationCountByCandidate(candidate.getCandidateId(), status);
            System.out.println("[DEBUG] Total count from DB: " + total);
            int totalPages = (int) Math.ceil((double) total / pageSize);
            if (totalPages < 1) {
                totalPages = 1;
            }

            request.setAttribute("applications", applications);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("selectedStatus", status);

            request.getRequestDispatcher("/WEB-INF/views/candidate/candidate-applications.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error loading candidate applications", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[DEBUG] doPost called for /candidate/applications");

        // Handle withdraw action
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

        System.out.println("[DEBUG] UserId from session: " + userId);

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        try {
            User user = userDAO.findById(userId);
            System.out.println("[DEBUG] User found: " + (user != null ? user.getEmail() : "null"));

            if (user == null || !"Candidate".equals(user.getRole())) {
                if (isAjax) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    try (PrintWriter pw = response.getWriter()) {
                        pw.write("{\"success\":false,\"message\":\"Forbidden\"}");
                    }
                    return;
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }

            Candidate candidate = candidateDAO.getCandidateByUserId(userId);
            System.out.println("[DEBUG] Candidate found: " + (candidate != null ? "CandidateId=" + candidate.getCandidateId() : "null"));

            if (candidate == null) {
                if (isAjax) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    try (PrintWriter pw = response.getWriter()) {
                        pw.write("{\"success\":false,\"message\":\"Candidate profile not found\"}");
                    }
                    return;
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Candidate profile not found");
                    return;
                }
            }

            String action = request.getParameter("action");
            String appIdParam = request.getParameter("applicationId");

            // Debug: print all parameters
            System.out.println("[DEBUG] Action: " + action);
            System.out.println("[DEBUG] ApplicationId param: " + appIdParam);
            System.out.println("[DEBUG] Content-Type: " + request.getContentType());
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                System.out.println("[DEBUG] Param: " + paramName + " = " + request.getParameter(paramName));
            }

            if ("withdraw".equals(action)) {
                String appIdStr = request.getParameter("applicationId");
                if (appIdStr != null && !appIdStr.isEmpty()) {
                    try {
                        Integer appId = Integer.valueOf(appIdStr);
                        Integer candidateId = candidate.getCandidateId();

                        // Debug logging
                        System.out.println("[DEBUG] Withdraw - ApplicationId: " + appId + ", CandidateId: " + candidateId);

                        boolean ok = applicationDAO.withdrawApplication(appId, candidateId);

                        System.out.println("[DEBUG] Withdraw result: " + ok);

                        if (isAjax) {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            try (PrintWriter pw = response.getWriter()) {
                                if (ok) {
                                    // Properly construct JSON for success
                                    String json = String.format("{\"success\":true,\"message\":\"Application withdrawn successfully.\",\"applicationId\":%d,\"newStatus\":\"Withdrawn\"}", appId);
                                    pw.write(json);
                                } else {
                                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                    pw.write("{\"success\":false,\"message\":\"Unable to withdraw application (AppId: " + appId + ", CandidateId: " + candidateId + "). It may already be processed.\"}");
                                }
                            }
                            return;
                        } else {
                            if (ok) {
                                session.setAttribute("message", "Application withdrawn successfully.");
                            } else {
                                session.setAttribute("error", "Unable to withdraw application. It may already be processed.");
                            }
                        }
                    } catch (NumberFormatException nfe) {
                        if (isAjax) {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            try (PrintWriter pw = response.getWriter()) {
                                pw.write("{\"success\":false,\"message\":\"Invalid application id.\"}");
                            }
                            return;
                        } else {
                            session.setAttribute("error", "Invalid application id.");
                        }
                    }
                }
            } else if (isAjax) {
                // Unknown action for AJAX request - return error
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                try (PrintWriter pw = response.getWriter()) {
                    pw.write("{\"success\":false,\"message\":\"Invalid action: " + (action != null ? action : "null") + "\"}");
                }
                return;
            }

            if (!isAjax) {
                response.sendRedirect(request.getContextPath() + "/candidate/applications");
            }

        } catch (SQLException e) {
            if (isAjax) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                try (PrintWriter pw = response.getWriter()) {
                    String msg = e.getMessage() != null
                            ? e.getMessage().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ")
                            : "Database error";
                    pw.write("{\"success\":false,\"message\":\"" + msg + "\"}");
                }
                return;
            }
            throw new ServletException("Error updating application", e);
        }
    }
}
