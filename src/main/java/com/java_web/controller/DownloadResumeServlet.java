package com.java_web.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.java_web.dao.ResumeDAO;
import com.java_web.service.GoogleDriveService;

/**
 * Servlet for downloading resumes from Google Drive Recruiters can download
 * candidate resumes
 */
@WebServlet("/employer/applications/resume")
public class DownloadResumeServlet extends HttpServlet {

    private GoogleDriveService driveService;
    private ResumeDAO resumeDAO;

    // Set your Google Drive folder ID here
    private static final String DRIVE_FOLDER_ID = "1hPyJDr42I5ILTpnkQkFZJ7VRfLX2E40e";

    @Override
    public void init() throws ServletException {
        try {
            this.driveService = new GoogleDriveService(DRIVE_FOLDER_ID);
            this.resumeDAO = new ResumeDAO();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize Google Drive service", e);
        }
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // For HEAD requests, perform authentication only
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login first");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");
        if (!"Recruiter".equals(userRole) && !"EmployerAdmin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        // For HEAD, just set response OK
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/pdf");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== DownloadResumeServlet: Processing request ===");

        // Check authentication
        HttpSession session = request.getSession(false);
        System.out.println("Session exists: " + (session != null));

        if (session != null) {
            System.out.println("Session ID: " + session.getId());
            System.out.println("UserId in session: " + session.getAttribute("userId"));
            System.out.println("UserRole in session: " + session.getAttribute("userRole"));
            System.out.println("RecruiterId in session: " + session.getAttribute("recruiterId"));
        }

        if (session == null || session.getAttribute("userId") == null) {
            System.err.println("ERROR: No valid session or userId not found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login first");
            return;
        }

        // Check if user is a recruiter or employer admin
        String userRole = (String) session.getAttribute("userRole");
        System.out.println("Checking user role: " + userRole);

        if (!"Recruiter".equals(userRole) && !"EmployerAdmin".equals(userRole)) {
            System.err.println("ERROR: User is not a recruiter. Role: " + userRole);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Only recruiters can download resumes.");
            return;
        }

        System.out.println("Authentication passed for role: " + userRole);

        // Get resume ID parameter
        String resumeIdParam = request.getParameter("id");
        if (resumeIdParam == null || resumeIdParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Resume ID is required");
            return;
        }

        try {
            int resumeId = Integer.parseInt(resumeIdParam);

            // Get resume info from database
            Map<String, Object> resume = resumeDAO.getResume(resumeId);

            if (resume == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resume not found");
                return;
            }

            String driveFileId = (String) resume.get("driveFileId");
            String fileName = (String) resume.get("fileName");

            if (driveFileId == null || driveFileId.trim().isEmpty()) {
                // Fallback for old resumes stored locally
                String fileUrl = (String) resume.get("fileUrl");
                if (fileUrl != null && fileUrl.startsWith("/uploads/")) {
                    // Redirect to local file
                    response.sendRedirect(request.getContextPath() + fileUrl);
                    return;
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resume file not found");
                    return;
                }
            }

            // Check if this is a view request (open in browser) or download
            String viewParam = request.getParameter("view");
            boolean isView = "true".equalsIgnoreCase(viewParam);

            System.out.println("Downloading resume ID: " + resumeId + ", Drive ID: " + driveFileId);

            InputStream fileContent = null;

            try {
                // Download file from Google Drive
                fileContent = driveService.downloadFile(driveFileId);
            } catch (Exception e) {
                System.err.println("Failed to download from Drive: " + e.getMessage());

                // Try fallback to local file if Drive file not found
                String fileUrl = (String) resume.get("fileUrl");
                if (fileUrl != null && fileUrl.startsWith("/uploads/")) {
                    response.sendRedirect(request.getContextPath() + fileUrl);
                    return;
                }

                // If no fallback available, show error
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Resume file not found in Google Drive. File ID: " + driveFileId);
                return;
            }

            // Read the entire file into a byte array first
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileContent.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            fileContent.close();

            byte[] fileData = baos.toByteArray();
            System.out.println("File size: " + fileData.length + " bytes");

            // Determine MIME type based on file extension
            String mimeType = getMimeType(fileName);

            // Set response headers BEFORE writing any data
            response.reset();
            response.setContentType(mimeType);
            response.setContentLength(fileData.length);
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            if (isView) {
                // Open in browser
                response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            } else {
                // Download as attachment
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            }

            // Write file data to response
            OutputStream out = response.getOutputStream();
            out.write(fileData);
            out.flush();
            out.close();

            System.out.println("Resume downloaded successfully: " + fileName);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid resume ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to download resume: " + e.getMessage());
        }
    }

    /**
     * Get MIME type based on file extension
     */
    private String getMimeType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }

        String lowerFileName = fileName.toLowerCase();

        if (lowerFileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerFileName.endsWith(".doc")) {
            return "application/msword";
        } else if (lowerFileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }

        return "application/octet-stream";
    }
}
