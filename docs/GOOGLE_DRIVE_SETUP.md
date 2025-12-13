# Google Drive Integration Setup Guide

## Overview

This guide will help you set up Google Drive integration for resume storage in your recruitment application. Candidates will upload resumes to your Google Drive folder, and recruiters can download them.

---

## Prerequisites Completed ✅

- [x] Maven dependencies added to pom.xml
- [x] GoogleDriveConfig.java created
- [x] GoogleDriveService.java created
- [x] ResumeDAO.java created
- [x] UploadResumeServlet.java created (mapped to `/candidate/uploadResume`)
- [x] DownloadResumeServlet.java created (mapped to `/employer/applications/resume`)
- [x] uploadResume.jsp created for candidate UI
- [x] Database migration script created

---

## Step 1: Google Cloud Console Setup

### 1.1 Create Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click **"Select a project"** → **"New Project"**
3. Project name: `RecruitmentWebProject` (or your choice)
4. Click **"Create"**
5. Wait for project creation (30-60 seconds)

### 1.2 Enable Google Drive API

1. In the search bar, type **"Google Drive API"**
2. Click on **"Google Drive API"** from results
3. Click **"Enable"** button
4. Wait for activation (15-30 seconds)

### 1.3 Create OAuth 2.0 Credentials

1. In the left sidebar, click **"Credentials"**
2. Click **"+ CREATE CREDENTIALS"** → **"OAuth client ID"**
3. If prompted, configure OAuth consent screen:
   - User Type: **External**
   - App name: `Recruitment Platform`
   - User support email: Your email
   - Developer contact: Your email
   - Click **"Save and Continue"**
   - Scopes: Click **"Add or Remove Scopes"**
   - Filter for: `Google Drive API`
   - Select: `.../auth/drive.file` (manage files created by this app)
   - Click **"Update"** → **"Save and Continue"**
   - Test users: Add your Gmail address
   - Click **"Save and Continue"**
4. Back to Credentials page:
   - Click **"+ CREATE CREDENTIALS"** → **"OAuth client ID"**
   - Application type: **Web application**
   - Name: `RecruitmentWebApp`
   - Authorized redirect URIs:
     - Click **"+ ADD URI"**
     - Enter: `http://localhost:8888/Callback`
     - Click **"+ ADD URI"**
     - Enter: `http://localhost:8080/Callback` (if using Tomcat default port)
   - Click **"Create"**

### 1.4 Download Credentials

1. You'll see a popup with Client ID and Client Secret
2. Click **"DOWNLOAD JSON"**
3. Rename the downloaded file to exactly: `credentials.json`
4. Move `credentials.json` to: `src/main/resources/credentials.json`

**Important:** Add to `.gitignore`:

```
src/main/resources/credentials.json
tokens/
```

---

## Step 2: Database Schema Update

### 2.1 Run SQL Migration Script

1. Open SQL Server Management Studio (SSMS)
2. Connect to your database server
3. Open the file: `database/add_drive_file_id_column.sql`
4. Execute the script (F5)
5. Verify output: "DriveFileId column added successfully"

### 2.2 Verify Column Addition

Run this query:

```sql
SELECT TOP 5
    ResumeId,
    CandidateId,
    FileName,
    FileUrl,
    DriveFileId,  -- New column
    UploadedAt
FROM candidate.Resumes
ORDER BY UploadedAt DESC;
```

---

## Step 3: Web.xml Configuration (Already Set Up)

Verify your `WEB-INF/web.xml` has these servlet mappings:

```xml
<!-- Upload Resume Servlet -->
<servlet>
    <servlet-name>UploadResumeServlet</servlet-name>
    <servlet-class>com.java_web.controller.UploadResumeServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>UploadResumeServlet</servlet-name>
    <url-pattern>/candidate/uploadResume</url-pattern>
</servlet-mapping>

<!-- Download Resume Servlet -->
<servlet>
    <servlet-name>DownloadResumeServlet</servlet-name>
    <servlet-class>com.java_web.controller.DownloadResumeServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>DownloadResumeServlet</servlet-name>
    <url-pattern>/employer/applications/resume</url-pattern>
</servlet-mapping>
```

---

## Step 4: First Run and OAuth Authorization

### 4.1 Build and Deploy

```bash
# Clean and build project
mvn clean package

# Deploy to your application server (Tomcat/etc)
```

### 4.2 First Upload (OAuth Flow)

1. Start your application server
2. Login as a **candidate**
3. Navigate to: `http://localhost:8080/YourAppContext/candidate/uploadResume`
4. Select a test resume file (PDF, DOC, or DOCX)
5. Click **"Upload to Google Drive"**

**First-time OAuth flow will happen:**

- Your browser will open automatically
- Google sign-in page will appear
- Select your Google account (use the account that owns the Drive folder)
- Review permissions: "See, edit, create, and delete only the specific Google Drive files you use with this app"
- Click **"Allow"**
- You'll see "Received verification code. You may now close this window."
- Close the OAuth window
- Upload will proceed automatically

### 4.3 Verify Authorization

After first authorization:

- A `tokens/` folder will be created in your project root
- Contains `StoredCredential` file (authorization token)
- Future uploads won't require OAuth (token is reused)

---

## Step 5: Testing the Integration

### 5.1 Test Candidate Upload

1. Login as candidate
2. Go to upload resume page
3. Upload a test resume (e.g., `test_resume.pdf`)
4. Check success message: "Resume uploaded successfully to Google Drive!"

### 5.2 Verify in Google Drive

1. Go to [Google Drive](https://drive.google.com/)
2. Navigate to your "resumes" folder (ID: `1hIylPx9P9fllTJphOKkF7J7VRfLX9F4Db`)
3. You should see the uploaded file with timestamp in filename
4. Example: `1234_resume_2025_01_26_10_30_45.pdf`

### 5.3 Verify in Database

```sql
SELECT TOP 5
    ResumeId,
    CandidateId,
    FileName,
    DriveFileId,  -- Should have value like '1ABcdEFghIJklMNopQRstUVwxYZ'
    UploadedAt
FROM candidate.Resumes
ORDER BY UploadedAt DESC;
```

### 5.4 Test Recruiter Download

1. Login as employer/recruiter
2. Go to Applications page: `employer/applications`
3. Click "View Details" on an application with a resume
4. In the modal, you should see resume file name
5. Click the resume download link
6. File should download from Google Drive

**Test download servlet directly:**

```
http://localhost:8080/YourAppContext/employer/applications/resume?id=1
```

Replace `1` with actual ResumeId from database.

---

## Step 6: Migrate Existing Resumes (Optional)

If you have existing resumes in `/uploads/resumes/`, create a migration script:

### 6.1 Java Migration Tool (Create this file)

```java
// MigrateResumesToDrive.java
public class MigrateResumesToDrive {
    public static void main(String[] args) throws Exception {
        // 1. Get all resumes with null DriveFileId
        // 2. For each resume:
        //    - Read local file from FileUrl
        //    - Upload to Google Drive
        //    - Update DriveFileId in database
        //    - Delete local file (optional)
    }
}
```

### 6.2 Manual Migration

Or manually:

1. Download each resume from `/uploads/resumes/`
2. Upload via the candidate upload form
3. Update database to link old application records to new resume

---

## Step 7: Integration with Existing Pages

### 7.1 Add Upload Link to Candidate Profile

In your candidate profile/dashboard JSP, add:

```jsp
<a href="${pageContext.request.contextPath}/candidate/uploadResume"
   class="btn btn-primary">
    <i class="fas fa-cloud-upload-alt"></i> Upload Resume to Google Drive
</a>
```

### 7.2 Verify Application Modal Downloads

Your `dashboard.jsp` and `applications.jsp` already have:

```javascript
function downloadResume(resumeId, fileName) {
  window.location.href = `${pageContext.request.contextPath}/employer/applications/resume?id=${resumeId}`;
}
```

This will now download from Google Drive automatically.

---

## Troubleshooting

### Issue: "credentials.json not found"

- **Solution:** Make sure `credentials.json` is in `src/main/resources/`
- Rebuild project: `mvn clean package`

### Issue: "Redirect URI mismatch"

- **Solution:** Verify in Google Cloud Console:
  - Credentials → Your OAuth Client → Authorized redirect URIs
  - Must include: `http://localhost:8888/Callback`

### Issue: "Access denied" during OAuth

- **Solution:**
  - Check OAuth consent screen has your email as test user
  - Re-authorize by deleting `tokens/` folder and uploading again

### Issue: "File not found in Drive"

- **Solution:**
  - Check folder ID in `GoogleDriveService.java` matches your Drive folder
  - Current folder ID: `1hIylPx9P9fllTJphOKkF7J7VRfLX9F4Db`
  - Verify you have write permissions to this folder

### Issue: Download shows 404

- **Solution:**
  - Check `DriveFileId` is not null in database
  - Verify file exists in Google Drive
  - Check recruiter session authentication

### Issue: "File size too large"

- **Solution:**
  - Upload servlet has 10MB limit
  - Increase in `@MultipartConfig` if needed:
    ```java
    @MultipartConfig(
        maxFileSize = 20 * 1024 * 1024,  // 20MB
        maxRequestSize = 25 * 1024 * 1024
    )
    ```

---

## Security Considerations

### OAuth Token Storage

- `tokens/` folder stores OAuth refresh tokens
- **Never commit to Git**
- Add to `.gitignore`
- Secure folder permissions on production server

### File Access Permissions

- Files are uploaded with public read access
- Anyone with link can view (required for download servlet)
- Consider implementing signed URLs for production

### Candidate Authorization

- Upload servlet checks `session.getAttribute("candidateId")`
- Ensure proper session management
- Verify candidate can only upload for their own profile

### Recruiter Authorization

- Download servlet checks recruiter session
- Verify recruiter has access to application
- Log all resume downloads for audit trail

---

## Production Deployment Checklist

- [ ] OAuth consent screen set to "External" and verified
- [ ] Add production domain to Authorized redirect URIs
- [ ] Secure `tokens/` folder with proper permissions (chmod 700)
- [ ] Never expose `credentials.json` in version control
- [ ] Implement logging for uploads/downloads
- [ ] Set up error alerting for Drive API failures
- [ ] Consider implementing virus scanning on uploads
- [ ] Monitor Drive API quota usage
- [ ] Backup Drive folder regularly
- [ ] Document recovery procedure for Drive outages

---

## API Quota Limits

Google Drive API free tier:

- **Queries per day:** 1,000,000,000
- **Queries per 100 seconds per user:** 1,000
- **Queries per 100 seconds:** 10,000

For typical recruitment app with 100 uploads/day and 500 downloads/day, you're well within limits.

---

## Support and Documentation

- **Google Drive API Docs:** https://developers.google.com/drive/api/v3/about-sdk
- **OAuth 2.0 Guide:** https://developers.google.com/identity/protocols/oauth2
- **Java Client Library:** https://developers.google.com/api-client-library/java

---

## Summary

Your Google Drive integration is now ready! Here's what you have:

✅ **Backend:** Complete Java implementation
✅ **Frontend:** Upload UI for candidates  
✅ **Download:** Recruiters can download from Drive
✅ **Database:** Schema updated with DriveFileId column
✅ **Security:** OAuth 2.0 authentication

**Next Steps:**

1. Set up Google Cloud Console and download credentials.json
2. Run database migration script
3. Test the upload flow (first time triggers OAuth)
4. Verify files appear in your Google Drive
5. Test recruiter download functionality

**That's it!** Your recruitment platform now uses Google Drive for secure cloud resume storage.
