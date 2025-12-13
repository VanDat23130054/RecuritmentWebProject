package com.java_web.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.java_web.config.GoogleDriveConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for Google Drive operations Handles file upload, download,
 * deletion, and metadata retrieval
 */
public class GoogleDriveService {

    private Drive driveService;
    private String folderId; // ID of the "resumes" folder in your Drive

    /**
     * Constructor - initializes Drive service
     */
    public GoogleDriveService() throws IOException, GeneralSecurityException {
        this.driveService = GoogleDriveConfig.getDriveService();
        // You can set your folder ID here or retrieve it dynamically
        this.folderId = null; // Will upload to root if not set
    }

    /**
     * Constructor with specific folder ID
     *
     * @param folderId Google Drive folder ID where files will be uploaded
     */
    public GoogleDriveService(String folderId) throws IOException, GeneralSecurityException {
        this.driveService = GoogleDriveConfig.getDriveService();
        this.folderId = folderId;
    }

    /**
     * Upload file to Google Drive
     *
     * @param fileContent InputStream of the file content
     * @param fileName Name of the file
     * @param mimeType MIME type of the file (e.g., "application/pdf")
     * @return Google Drive file ID
     * @throws IOException If upload fails
     */
    public String uploadFile(InputStream fileContent, String fileName, String mimeType) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(fileName);

        // Set parent folder if specified
        if (folderId != null) {
            fileMetadata.setParents(java.util.Collections.singletonList(folderId));
        }

        InputStreamContent mediaContent = new InputStreamContent(mimeType, fileContent);

        // Upload file to Drive
        File file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, name, webViewLink, webContentLink")
                .execute();

        // Make file accessible to anyone with the link
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
        driveService.permissions().create(file.getId(), permission).execute();

        System.out.println("File uploaded successfully. ID: " + file.getId());
        return file.getId();
    }

    /**
     * Get shareable download link for file
     *
     * @param fileId Google Drive file ID
     * @return Direct download link
     * @throws IOException If retrieval fails
     */
    public String getFileLink(String fileId) throws IOException {
        File file = driveService.files().get(fileId)
                .setFields("webViewLink, webContentLink")
                .execute();

        // Return direct download link
        String downloadLink = file.getWebContentLink();

        // If webContentLink is null, construct manual download URL
        if (downloadLink == null) {
            downloadLink = "https://drive.google.com/uc?export=download&id=" + fileId;
        }

        return downloadLink;
    }

    /**
     * Get view link for file (opens in browser)
     *
     * @param fileId Google Drive file ID
     * @return View link
     * @throws IOException If retrieval fails
     */
    public String getViewLink(String fileId) throws IOException {
        File file = driveService.files().get(fileId)
                .setFields("webViewLink")
                .execute();
        return file.getWebViewLink();
    }

    /**
     * Get comprehensive file metadata
     *
     * @param fileId Google Drive file ID
     * @return Map containing file metadata
     * @throws IOException If retrieval fails
     */
    public Map<String, String> getFileMetadata(String fileId) throws IOException {
        File file = driveService.files().get(fileId)
                .setFields("id, name, mimeType, size, createdTime, webViewLink, webContentLink")
                .execute();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("id", file.getId());
        metadata.put("name", file.getName());
        metadata.put("mimeType", file.getMimeType());
        metadata.put("size", file.getSize() != null ? file.getSize().toString() : "0");
        metadata.put("createdTime", file.getCreatedTime() != null ? file.getCreatedTime().toString() : "");
        metadata.put("viewLink", file.getWebViewLink());
        metadata.put("downloadLink", file.getWebContentLink() != null
                ? file.getWebContentLink()
                : "https://drive.google.com/uc?export=download&id=" + fileId);

        return metadata;
    }

    /**
     * Delete file from Google Drive
     *
     * @param fileId Google Drive file ID
     * @throws IOException If deletion fails
     */
    public void deleteFile(String fileId) throws IOException {
        driveService.files().delete(fileId).execute();
        System.out.println("File deleted successfully. ID: " + fileId);
    }

    /**
     * Download file content from Google Drive
     *
     * @param fileId Google Drive file ID
     * @return InputStream of file content
     * @throws IOException If download fails
     */
    public InputStream downloadFile(String fileId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        driveService.files().get(fileId)
                .executeMediaAndDownloadTo(outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * Check if file exists
     *
     * @param fileId Google Drive file ID
     * @return true if file exists, false otherwise
     */
    public boolean fileExists(String fileId) {
        try {
            driveService.files().get(fileId).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Update file permissions
     *
     * @param fileId Google Drive file ID
     * @param email Email address to share with (null for public access)
     * @param role Role: "reader", "writer", "commenter"
     * @throws IOException If permission update fails
     */
    public void updatePermissions(String fileId, String email, String role) throws IOException {
        Permission permission = new Permission();

        if (email == null) {
            // Public access
            permission.setType("anyone");
        } else {
            // Specific user
            permission.setType("user");
            permission.setEmailAddress(email);
        }

        permission.setRole(role);
        driveService.permissions().create(fileId, permission).execute();
    }
}
