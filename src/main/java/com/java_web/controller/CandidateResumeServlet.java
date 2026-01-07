package com.java_web.controller;

import java.io.IOException;
import java.util.List;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.CandidateDAO;
import com.java_web.dao.ResumeDAO;
import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;
import com.java_web.model.dto.ResumeDTO;

@WebServlet("/candidate/resume/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 10 * 1024 * 1024, // 10MB
        maxRequestSize = 20 * 1024 * 1024) // 20MB
public class CandidateResumeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CandidateDAO candidateDAO;
    private UserDAO userDAO;
    private ResumeDAO resumeDAO;

    @Override
    public void init() throws ServletException {
        candidateDAO = new CandidateDAO();
        userDAO = new UserDAO();
        resumeDAO = new ResumeDAO();
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

            Candidate candidate = candidateDAO.getCandidateByUserId(userId);
            List<ResumeDTO> resumes = null;
            if (candidate != null && candidate.getCandidateId() != null) {
                resumes = resumeDAO.getResumesByCandidateId(candidate.getCandidateId());
            }
            request.setAttribute("user", user);
            request.setAttribute("candidate", candidate);
            request.setAttribute("resumes", resumes);

            request.getRequestDispatcher("/WEB-INF/views/candidate/candidate-resume.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Delegate upload to UploadResumeServlet at /candidate/uploadResume
        // Only accept POSTs to /upload path here to keep compatibility
        String pathInfo = request.getPathInfo(); // e.g., /upload
        if (pathInfo != null && pathInfo.startsWith("/upload")) {
            // redirect to canonical upload handler
            request.getRequestDispatcher("/candidate/uploadResume").forward(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}