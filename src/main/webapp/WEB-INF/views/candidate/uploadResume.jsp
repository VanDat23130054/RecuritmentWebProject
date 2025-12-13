<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload Resume - Recruitment Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="container my-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card shadow">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">
                            <i class="fas fa-cloud-upload-alt me-2"></i>Upload Resume to Google Drive
                        </h4>
                    </div>
                    <div class="card-body">
                        <!-- Alert Messages -->
                        <c:if test="${param.success == 'resumeUploaded'}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="fas fa-check-circle me-2"></i>
                                Resume uploaded successfully to Google Drive!
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <c:if test="${param.error == 'uploadFailed'}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-circle me-2"></i>
                                Failed to upload resume. Please try again.
                                <c:if test="${not empty param.message}">
                                    <br><small>${param.message}</small>
                                </c:if>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <c:if test="${param.error == 'noFile'}">
                            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                Please select a file to upload.
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <c:if test="${param.error == 'invalidFileType'}">
                            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                Invalid file type. Please upload PDF, DOC, or DOCX files only.
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <!-- Upload Form -->
                        <form action="${pageContext.request.contextPath}/candidate/uploadResume" 
                              method="post" 
                              enctype="multipart/form-data"
                              id="uploadForm">
                            
                            <div class="mb-4">
                                <label for="resume" class="form-label">
                                    <strong>Select Resume File</strong>
                                </label>
                                <input type="file" 
                                       class="form-control form-control-lg" 
                                       id="resume" 
                                       name="resume"
                                       accept=".pdf,.doc,.docx"
                                       required>
                                <div class="form-text">
                                    <i class="fas fa-info-circle me-1"></i>
                                    Max file size: 10MB. Accepted formats: PDF, DOC, DOCX
                                </div>
                                <div id="fileInfo" class="mt-2"></div>
                            </div>

                            <div class="alert alert-info">
                                <i class="fas fa-cloud me-2"></i>
                                <strong>Google Drive Storage:</strong> Your resume will be securely stored in Google Drive and will be accessible to recruiters when you apply for jobs.
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary btn-lg" id="submitBtn">
                                    <i class="fas fa-cloud-upload-alt me-2"></i>Upload to Google Drive
                                </button>
                                <a href="${pageContext.request.contextPath}/candidate/profile" class="btn btn-outline-secondary">
                                    <i class="fas fa-arrow-left me-2"></i>Back to Profile
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Instructions Card -->
                <div class="card shadow mt-4">
                    <div class="card-header">
                        <h5 class="mb-0">
                            <i class="fas fa-question-circle me-2"></i>Upload Instructions
                        </h5>
                    </div>
                    <div class="card-body">
                        <ul class="list-unstyled mb-0">
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                Your resume should be in PDF, DOC, or DOCX format
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                File size should not exceed 10MB
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                Use a professional filename (e.g., "John_Doe_Resume.pdf")
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                Your resume will be stored securely in Google Drive
                            </li>
                            <li class="mb-0">
                                <i class="fas fa-check text-success me-2"></i>
                                Recruiters will be able to download your resume when reviewing applications
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="../common/footer.jsp" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Show file info when selected
        document.getElementById('resume').addEventListener('change', function(e) {
            const file = e.target.files[0];
            const fileInfo = document.getElementById('fileInfo');
            
            if (file) {
                const fileSize = (file.size / 1024 / 1024).toFixed(2); // Convert to MB
                const fileType = file.type || 'Unknown';
                
                let icon = 'fa-file';
                if (fileType.includes('pdf')) icon = 'fa-file-pdf text-danger';
                else if (fileType.includes('word') || fileType.includes('document')) icon = 'fa-file-word text-primary';
                
                fileInfo.innerHTML = `
                    <div class="alert alert-light border">
                        <i class="fas ${icon} me-2"></i>
                        <strong>Selected:</strong> ${file.name} (${fileSize} MB)
                    </div>
                `;

                // Validate file size
                if (file.size > 10 * 1024 * 1024) {
                    fileInfo.innerHTML += `
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            File size exceeds 10MB limit!
                        </div>
                    `;
                    document.getElementById('submitBtn').disabled = true;
                } else {
                    document.getElementById('submitBtn').disabled = false;
                }
            }
        });

        // Show loading state on submit
        document.getElementById('uploadForm').addEventListener('submit', function() {
            const submitBtn = document.getElementById('submitBtn');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Uploading...';
        });
    </script>
</body>
</html>
