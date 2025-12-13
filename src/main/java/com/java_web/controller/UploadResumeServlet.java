package com.java_web.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.java_web.dao.ResumeDAO;
import com.java_web.service.GoogleDriveService;

/**
 * Servlet for handling resume uploads to Google Drive Candidates can upload
 * their resumes which are stored in Google Drive
 */
@WebServlet("/candidate/uploadResume")
@MultipartConfig(
        maxFileSize = 10 * 1024 * 1024, // 10 MB max file size
        maxRequestSize = 20 * 1024 * 1024, // 20 MB max request size
        fileSizeThreshold = 5 * 1024 * 1024 // 5 MB threshold for temp storage
)
public class UploadResumeServlet extends HttpServlet {

    private GoogleDriveService driveService;
    private ResumeDAO resumeDAO;

    // Set your Google Drive folder ID here (the "resumes" folder)
    // You can get this from your Google Drive folder URL
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Forward to upload page
        request.getRequestDispatcher("/WEB-INF/views/candidate/uploadResume.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        // Check if user is a candidate
        String userRole = (String) session.getAttribute("userRole");
        if (!"Candidate".equals(userRole)) {
            response.sendRedirect(request.getContextPath() + "/home?error=notCandidate");
            return;
        }

        // Get userId and lookup the actual CandidateId from Candidates table
        Integer userId = (Integer) session.getAttribute("userId");
        Integer candidateId = null;

        try {
            candidateId = resumeDAO.getCandidateIdByUserId(userId);
            if (candidateId == null) {
                response.sendRedirect(request.getContextPath() + "/candidate/uploadResume?error=candidateProfileNotFound");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/candidate/uploadResume?error=databaseError");
            return;
        }

        try {
            // Get uploaded file
            Part filePart = request.getPart("resume");
            if (filePart == null || filePart.getSize() == 0) {
                response.sendRedirect(request.getContextPath() + "/candidate/uploadResume?error=noFile");
                return;
            }

            String fileName = getFileName(filePart);
            String mimeType = filePart.getContentType();

            // Validate file type
            if (!isValidFileType(mimeType, fileName)) {
                response.sendRedirect(request.getContextPath() + "/candidate/uploadResume?error=invalidFileType");
                return;
            }

            // Generate unique filename to avoid conflicts
            String uniqueFileName = generateUniqueFileName(fileName, candidateId);

            System.out.println("Uploading file: " + uniqueFileName + " (Type: " + mimeType + ")");

            // Upload to Google Drive
            String driveFileId = driveService.uploadFile(
                    filePart.getInputStream(),
                    uniqueFileName,
                    mimeType
            );

            System.out.println("File uploaded to Drive. ID: " + driveFileId);

            // Get download link
            String downloadLink = driveService.getFileLink(driveFileId);

            System.out.println("Download link: " + downloadLink);

            // Save to database
            int resumeId = resumeDAO.saveResume(candidateId, driveFileId, uniqueFileName, downloadLink);

            System.out.println("Resume saved to database. ID: " + resumeId);

            // Redirect with success message
            response.sendRedirect(request.getContextPath() + "/candidate/uploadResume?success=resumeUploaded");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/candidate/uploadResume?error=uploadFailed&message=" + e.getMessage());
        }
    }

    /**
     * Extract filename from Part header
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "resume.pdf";
    }

    /**
     * Validate file type
     */
    private boolean isValidFileType(String mimeType, String fileName) {
        if (mimeType == null) {
            return false;
        }

        // Check MIME type
        if (mimeType.equals("application/pdf")
                || mimeType.equals("application/msword")
                || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return true;
        }

        // Also check file extension as fallback
        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(".pdf")
                || lowerFileName.endsWith(".doc")
                || lowerFileName.endsWith(".docx");
    }

    /**
     * Generate unique filename with candidate ID and timestamp
     */
    private String generateUniqueFileName(String originalFileName, int candidateId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String extension = "";

        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }

        return "resume_candidate" + candidateId + "_" + timestamp + extension;
    }
}
