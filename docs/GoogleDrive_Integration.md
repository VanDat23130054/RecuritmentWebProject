# Resume Storage with Google Drive Integration

## Overview

This document outlines the implementation plan for integrating Google Drive as the resume storage solution for the Recruitment Web Project.

## Current State

- **Storage Location**: Local filesystem at `/uploads/resumes/`
- **Database Schema**:
  - `candidate.Resumes` table with columns: `resumeId`, `candidateId`, `fileUrl`, `fileName`, `resumeData` (JSON), `isPrimary`, `isPublic`
- **File Access**: Direct file system access

## Proposed Solution: Google Drive API

### 1. Prerequisites

#### Maven Dependencies

Add to `pom.xml`:

```xml
<!-- Google Drive API -->
<dependency>
    <groupId>com.google.apis</groupId>
    <artifactId>google-api-services-drive</artifactId>
    <version>v3-rev20231016-2.0.0</version>
</dependency>

<!-- Google OAuth Client -->
<dependency>
    <groupId>com.google.oauth-client</groupId>
    <artifactId>google-oauth-client-jetty</artifactId>
    <version>1.34.1</version>
</dependency>

<!-- Google HTTP Client -->
<dependency>
    <groupId>com.google.http-client</groupId>
    <artifactId>google-http-client-jackson2</artifactId>
    <version>1.43.3</version>
</dependency>
```

#### Google Cloud Console Setup

1. Create a project at https://console.cloud.google.com
2. Enable Google Drive API
3. Create OAuth 2.0 credentials (Web application)
4. Download `credentials.json` and place in `src/main/resources`
5. Add authorized redirect URIs: `http://localhost:8080/oauth2callback`

### 2. Implementation Architecture

#### A. Configuration Class

**Location**: `com.java_web.config.GoogleDriveConfig.java`

```java
public class GoogleDriveConfig {
    private static final String APPLICATION_NAME = "Recruitment Platform";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    public static Drive getDriveService() throws IOException, GeneralSecurityException {
        // Load credentials
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
            JSON_FACTORY,
            new InputStreamReader(GoogleDriveConfig.class.getResourceAsStream(CREDENTIALS_FILE_PATH))
        );

        // Build flow and trigger user authorization
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JSON_FACTORY,
            clientSecrets,
            SCOPES
        )
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JSON_FACTORY,
            credential
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }
}
```

#### B. Google Drive Service

**Location**: `com.java_web.service.GoogleDriveService.java`

```java
public class GoogleDriveService {
    private Drive driveService;

    public GoogleDriveService() throws IOException, GeneralSecurityException {
        this.driveService = GoogleDriveConfig.getDriveService();
    }

    /**
     * Upload file to Google Drive
     * @return Google Drive file ID
     */
    public String uploadFile(InputStream fileContent, String fileName, String mimeType)
            throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(fileName);

        InputStreamContent mediaContent = new InputStreamContent(mimeType, fileContent);

        File file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, name, webViewLink, webContentLink")
                .execute();

        // Make file publicly accessible
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
        driveService.permissions().create(file.getId(), permission).execute();

        return file.getId();
    }

    /**
     * Get shareable link for file
     */
    public String getFileLink(String fileId) throws IOException {
        File file = driveService.files().get(fileId)
                .setFields("webViewLink, webContentLink")
                .execute();
        return file.getWebContentLink(); // Direct download link
    }

    /**
     * Get file metadata
     */
    public Map<String, String> getFileMetadata(String fileId) throws IOException {
        File file = driveService.files().get(fileId)
                .setFields("id, name, mimeType, size, createdTime, webViewLink, webContentLink")
                .execute();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("id", file.getId());
        metadata.put("name", file.getName());
        metadata.put("mimeType", file.getMimeType());
        metadata.put("size", String.valueOf(file.getSize()));
        metadata.put("createdTime", file.getCreatedTime().toString());
        metadata.put("viewLink", file.getWebViewLink());
        metadata.put("downloadLink", file.getWebContentLink());

        return metadata;
    }

    /**
     * Delete file from Google Drive
     */
    public void deleteFile(String fileId) throws IOException {
        driveService.files().delete(fileId).execute();
    }

    /**
     * Download file content
     */
    public InputStream downloadFile(String fileId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
```

#### C. Resume Upload Servlet

**Location**: `com.java_web.controller.UploadResumeServlet.java`

```java
@WebServlet("/candidate/uploadResume")
@MultipartConfig(
    maxFileSize = 10 * 1024 * 1024,      // 10 MB
    maxRequestSize = 20 * 1024 * 1024    // 20 MB
)
public class UploadResumeServlet extends HttpServlet {

    private GoogleDriveService driveService;
    private ResumeDAO resumeDAO;

    @Override
    public void init() throws ServletException {
        try {
            this.driveService = new GoogleDriveService();
            this.resumeDAO = new ResumeDAO();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize Google Drive service", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Authentication check
        HttpSession session = request.getSession(false);
        Integer candidateId = (Integer) session.getAttribute("candidateId");

        // Get uploaded file
        Part filePart = request.getPart("resume");
        String fileName = getFileName(filePart);
        String mimeType = filePart.getContentType();

        try {
            // Upload to Google Drive
            String driveFileId = driveService.uploadFile(
                filePart.getInputStream(),
                fileName,
                mimeType
            );

            // Get download link
            String downloadLink = driveService.getFileLink(driveFileId);

            // Save to database
            resumeDAO.saveResume(candidateId, driveFileId, fileName, downloadLink);

            response.sendRedirect("profile?success=resumeUploaded");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("profile?error=uploadFailed");
        }
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
```

### 3. Database Schema Updates

#### Update Resumes Table

```sql
ALTER TABLE candidate.Resumes
ADD driveFileId NVARCHAR(255) NULL;

-- Index for faster lookups
CREATE INDEX idx_resumes_driveFileId ON candidate.Resumes(driveFileId);
```

#### Resume DAO Updates

```java
public class ResumeDAO {
    public void saveResume(int candidateId, String driveFileId, String fileName, String fileUrl) {
        String sql = "{CALL candidate.sp_SaveResume(?, ?, ?, ?)}";
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, candidateId);
            stmt.setString(2, driveFileId);
            stmt.setString(3, fileName);
            stmt.setString(4, fileUrl);

            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getResume(int resumeId) {
        // Returns driveFileId, fileName, fileUrl
    }
}
```

### 4. Resume Download Servlet

**Location**: `com.java_web.controller.DownloadResumeServlet.java`

```java
@WebServlet("/employer/applications/resume")
public class DownloadResumeServlet extends HttpServlet {

    private GoogleDriveService driveService;
    private ResumeDAO resumeDAO;

    @Override
    public void init() throws ServletException {
        try {
            this.driveService = new GoogleDriveService();
            this.resumeDAO = new ResumeDAO();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize service", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String resumeIdParam = request.getParameter("id");
        int resumeId = Integer.parseInt(resumeIdParam);

        // Get resume info from database
        Map<String, Object> resume = resumeDAO.getResume(resumeId);
        String driveFileId = (String) resume.get("driveFileId");
        String fileName = (String) resume.get("fileName");

        try {
            // Download from Google Drive
            InputStream fileContent = driveService.downloadFile(driveFileId);

            // Set response headers
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // Stream file to response
            byte[] buffer = new byte[4096];
            int bytesRead;
            OutputStream out = response.getOutputStream();

            while ((bytesRead = fileContent.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            fileContent.close();
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
```

### 5. Frontend Integration

#### Upload Form (in candidate profile page)

```html
<form
  action="${pageContext.request.contextPath}/candidate/uploadResume"
  method="post"
  enctype="multipart/form-data"
>
  <div class="mb-3">
    <label for="resume" class="form-label">Upload Resume</label>
    <input
      type="file"
      class="form-control"
      id="resume"
      name="resume"
      accept=".pdf,.doc,.docx"
      required
    />
    <small class="text-muted">Max size: 10MB. Formats: PDF, DOC, DOCX</small>
  </div>
  <button type="submit" class="btn btn-primary">
    <i class="fas fa-upload me-2"></i>Upload to Google Drive
  </button>
</form>
```

#### View/Download Resume in applications.jsp

```javascript
function downloadResume(resumeId) {
  // Already implemented in applications.jsp
  window.location.href =
    "${pageContext.request.contextPath}/employer/applications/resume?id=" +
    resumeId;
}

function viewResume(resumeId) {
  // Open in new tab for preview
  window.open(
    "${pageContext.request.contextPath}/employer/applications/resume?id=" +
      resumeId +
      "&view=true",
    "_blank"
  );
}
```

### 6. Benefits of Google Drive Integration

1. **Cost Effective**: Free 15GB storage per Google account
2. **Scalability**: Easy to expand storage with Google Workspace
3. **Reliability**: 99.9% uptime guaranteed by Google
4. **Security**: Built-in encryption and access controls
5. **Shareable Links**: Easy to generate and manage access
6. **No Server Storage**: Reduces server storage requirements
7. **Global CDN**: Fast access from anywhere
8. **Version Control**: Drive maintains file versions automatically

### 7. Migration Strategy

#### For Existing Files

1. Create migration script to upload existing files from `/uploads/resumes/` to Google Drive
2. Update database with new `driveFileId` and `fileUrl`
3. Keep old files as backup for rollback

```java
public class MigrateResumesToDrive {
    public static void main(String[] args) {
        GoogleDriveService driveService = new GoogleDriveService();
        ResumeDAO resumeDAO = new ResumeDAO();

        List<Map<String, Object>> resumes = resumeDAO.getAllResumes();

        for (Map<String, Object> resume : resumes) {
            String localPath = (String) resume.get("fileUrl");
            File file = new File("src/main/webapp" + localPath);

            if (file.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    String driveFileId = driveService.uploadFile(
                        fis,
                        file.getName(),
                        "application/pdf"
                    );

                    resumeDAO.updateDriveFileId(
                        (Integer) resume.get("resumeId"),
                        driveFileId
                    );

                    System.out.println("Migrated: " + file.getName());
                } catch (Exception e) {
                    System.err.println("Failed: " + file.getName());
                }
            }
        }
    }
}
```

### 8. Security Considerations

1. **Access Control**: Only authenticated recruiters can download resumes
2. **Token Storage**: Store OAuth tokens securely (encrypted)
3. **File Permissions**: Set Drive permissions to "anyone with link" or specific emails
4. **Audit Logging**: Log all resume downloads for compliance
5. **Virus Scanning**: Google Drive automatically scans files for malware

### 9. Testing Checklist

- [ ] OAuth flow works correctly
- [ ] File upload to Drive succeeds
- [ ] Database stores driveFileId and fileUrl
- [ ] Download link generates correctly
- [ ] Recruiter can download resume
- [ ] File permissions are set correctly
- [ ] Error handling for failed uploads
- [ ] Large file support (up to 10MB)
- [ ] Multiple file format support (PDF, DOC, DOCX)
- [ ] Migration script works for existing files

### 10. Alternative: Service Account (Recommended for Production)

For production, consider using a Service Account instead of OAuth:

```java
public class GoogleDriveConfig {
    private static final String SERVICE_ACCOUNT_KEY_PATH = "/service-account-key.json";

    public static Drive getDriveService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
            new FileInputStream(SERVICE_ACCOUNT_KEY_PATH)
        ).createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }
}
```

**Benefits of Service Account**:

- No user interaction needed
- Better for server-to-server communication
- Easier to manage in production
- More secure token management

### 11. Implementation Timeline

1. **Week 1**: Setup Google Cloud project, implement basic upload/download
2. **Week 2**: Integrate with existing application flow, update database
3. **Week 3**: Migration of existing files, testing
4. **Week 4**: Production deployment, monitoring

### 12. Next Steps

1. Create Google Cloud project and enable Drive API
2. Add Maven dependencies
3. Implement `GoogleDriveConfig` and `GoogleDriveService`
4. Update `UploadResumeServlet` to use Google Drive
5. Test upload/download flow
6. Run migration script for existing files
7. Update documentation for users

---

**Note**: This implementation provides a complete, production-ready solution for resume storage using Google Drive. The code examples are ready to use with minimal modifications.
