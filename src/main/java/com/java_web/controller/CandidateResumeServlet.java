package com.java_web.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.java_web.dao.CandidateDAO;
import com.java_web.dao.UserDAO;
import com.java_web.model.auth.User;
import com.java_web.model.candidate.Candidate;

@WebServlet("/candidate/resume/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 10 * 1024 * 1024, // 10MB
        maxRequestSize = 20 * 1024 * 1024) // 20MB
public class CandidateResumeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CandidateDAO candidateDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        candidateDAO = new CandidateDAO();
        userDAO = new UserDAO();
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
            request.setAttribute("user", user);
            request.setAttribute("candidate", candidate);

            request.getRequestDispatcher("/WEB-INF/views/candidate/candidate-resume.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle upload at /candidate/resume/upload
        String pathInfo = request.getPathInfo(); // e.g., /upload
        if (pathInfo != null && pathInfo.startsWith("/upload")) {
            handleUpload(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void handleUpload(HttpServletRequest request, HttpServletResponse response)
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

            Part resumePart = request.getPart("resumeFile");
            if (resumePart == null || resumePart.getSize() == 0) {
                request.setAttribute("error", "No file uploaded");
                doGet(request, response);
                return;
            }

            String submittedName = Path.of(resumePart.getSubmittedFileName()).getFileName().toString();
            String ext = "";
            int i = submittedName.lastIndexOf('.');
            if (i > 0) ext = submittedName.substring(i);

            // prepare uploads dir under webapp (not ideal for prod, but simple)
            String uploadsDir = request.getServletContext().getRealPath("/uploads/resumes");
            File uploads = new File(uploadsDir);
            if (!uploads.exists()) uploads.mkdirs();

            String fileName = "resume_user_" + userId + ext;
            Path target = uploads.toPath().resolve(fileName);

            try (InputStream in = resumePart.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            String publicUrl = request.getContextPath() + "/uploads/resumes/" + fileName;

            // Persist resume url - assumes CandidateDAO has updateResumeUrl method
            candidateDAO.updateResumeUrlByUserId(userId, "/uploads/resumes/" + fileName);

            response.sendRedirect(request.getContextPath() + "/candidate/resume");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}