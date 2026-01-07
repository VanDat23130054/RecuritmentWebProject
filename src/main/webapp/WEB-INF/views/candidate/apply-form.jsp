<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Apply - ${job.title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-dashboard.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css"/>
    <style>
        .apply-container {
            max-width: 900px;
            margin: 0 auto;
        }
        .job-preview-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 16px;
            padding: 2rem;
            margin-bottom: 2rem;
            position: relative;
            overflow: hidden;
        }
        .job-preview-card::before {
            content: '';
            position: absolute;
            top: -50%;
            right: -50%;
            width: 100%;
            height: 200%;
            background: rgba(255,255,255,0.1);
            transform: rotate(30deg);
        }
        .job-preview-card .company-logo {
            width: 70px;
            height: 70px;
            background: white;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2rem;
            color: #667eea;
            font-weight: bold;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }
        .job-preview-card .company-logo img {
            width: 100%;
            height: 100%;
            object-fit: contain;
            border-radius: 12px;
        }
        .job-preview-card h2 {
            font-size: 1.75rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }
        .job-preview-card .company-name {
            font-size: 1.1rem;
            opacity: 0.9;
        }
        .job-meta-tag {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            background: rgba(255,255,255,0.2);
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.85rem;
            margin-right: 8px;
            margin-top: 8px;
        }
        .application-form-card {
            background: white;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
            overflow: hidden;
        }
        .application-form-card .card-header {
            background: #f8f9fa;
            padding: 1.5rem 2rem;
            border-bottom: 1px solid #eee;
        }
        .application-form-card .card-header h3 {
            margin: 0;
            font-weight: 600;
            color: #333;
        }
        .application-form-card .card-body {
            padding: 2rem;
        }
        .resume-option {
            border: 2px solid #e9ecef;
            border-radius: 12px;
            padding: 1rem 1.25rem;
            margin-bottom: 0.75rem;
            cursor: pointer;
            transition: all 0.2s ease;
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        .resume-option:hover {
            border-color: #667eea;
            background: #f8f9ff;
        }
        .resume-option.selected {
            border-color: #667eea;
            background: linear-gradient(135deg, rgba(102,126,234,0.1) 0%, rgba(118,75,162,0.1) 100%);
        }
        .resume-option input[type="radio"] {
            width: 20px;
            height: 20px;
            accent-color: #667eea;
        }
        .resume-option .resume-icon {
            width: 45px;
            height: 45px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1.2rem;
        }
        .resume-option .resume-info {
            flex: 1;
        }
        .resume-option .resume-name {
            font-weight: 600;
            color: #333;
        }
        .resume-option .resume-date {
            font-size: 0.85rem;
            color: #6c757d;
        }
        .resume-option .primary-badge {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 0.75rem;
            font-weight: 600;
        }
        .cover-letter-textarea {
            border: 2px solid #e9ecef;
            border-radius: 12px;
            padding: 1rem;
            min-height: 180px;
            transition: border-color 0.2s ease;
        }
        .cover-letter-textarea:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102,126,234,0.15);
        }
        .btn-submit-application {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            padding: 14px 32px;
            font-size: 1.1rem;
            font-weight: 600;
            border-radius: 12px;
            transition: all 0.3s ease;
        }
        .btn-submit-application:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102,126,234,0.4);
        }
        .btn-back {
            padding: 14px 24px;
            border-radius: 12px;
            font-weight: 500;
        }
        .upload-resume-cta {
            background: linear-gradient(135deg, rgba(102,126,234,0.1) 0%, rgba(118,75,162,0.1) 100%);
            border: 2px dashed #667eea;
            border-radius: 12px;
            padding: 2rem;
            text-align: center;
        }
        .upload-resume-cta i {
            font-size: 2.5rem;
            color: #667eea;
            margin-bottom: 1rem;
        }
        .section-label {
            font-weight: 600;
            color: #333;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        .section-label i {
            color: #667eea;
        }
        .char-counter {
            font-size: 0.85rem;
            color: #6c757d;
            text-align: right;
            margin-top: 0.5rem;
        }
        .tips-card {
            background: #fff8e6;
            border: 1px solid #ffeeba;
            border-radius: 12px;
            padding: 1.25rem;
            margin-top: 1.5rem;
        }
        .tips-card h6 {
            color: #856404;
            font-weight: 600;
            margin-bottom: 0.75rem;
        }
        .tips-card ul {
            margin: 0;
            padding-left: 1.25rem;
            color: #856404;
        }
        .tips-card li {
            margin-bottom: 0.35rem;
        }
    </style>
</head>
<body>
<jsp:include page="../common/header.jsp" />

<div class="dashboard-container">
    <jsp:include page="_sidebar.jsp" />
    <main class="dashboard-main">
        <div class="apply-container py-4">
            
            <!-- Job Preview Card -->
            <div class="job-preview-card">
                <div class="d-flex align-items-start gap-3 position-relative" style="z-index: 1;">
                    <div class="company-logo">
                        <c:choose>
                            <c:when test="${not empty job.logoUrl}">
                                <img src="${job.logoUrl}" alt="${job.companyName}">
                            </c:when>
                            <c:otherwise>
                                ${job.companyName.substring(0,1).toUpperCase()}
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flex-grow-1">
                        <h2>${job.title}</h2>
                        <div class="company-name">
                            <i class="fas fa-building me-1"></i> ${job.companyName}
                        </div>
                        <div class="mt-3">
                            <c:if test="${not empty job.cityName}">
                                <span class="job-meta-tag">
                                    <i class="fas fa-map-marker-alt"></i> ${job.cityName}
                                </span>
                            </c:if>
                            <c:if test="${not empty job.employmentType}">
                                <span class="job-meta-tag">
                                    <i class="fas fa-briefcase"></i> ${job.employmentType}
                                </span>
                            </c:if>
                            <c:if test="${not empty job.remoteType}">
                                <span class="job-meta-tag">
                                    <i class="fas fa-laptop-house"></i> ${job.remoteType}
                                </span>
                            </c:if>
                            <c:if test="${job.salaryMin != null || job.salaryMax != null}">
                                <span class="job-meta-tag">
                                    <i class="fas fa-dollar-sign"></i>
                                    <c:if test="${job.salaryMin != null}">
                                        <fmt:formatNumber value="${job.salaryMin}" pattern="#,###"/>
                                    </c:if>
                                    <c:if test="${job.salaryMin != null && job.salaryMax != null}"> - </c:if>
                                    <c:if test="${job.salaryMax != null}">
                                        <fmt:formatNumber value="${job.salaryMax}" pattern="#,###"/>
                                    </c:if>
                                    <c:if test="${not empty job.currency}"> ${job.currency}</c:if>
                                </span>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Application Form -->
            <div class="application-form-card">
                <div class="card-header">
                    <h3><i class="fas fa-file-alt me-2 text-primary"></i>Submit Your Application</h3>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/apply/${jobId}" id="applyForm">
                        
                        <!-- Resume Selection -->
                        <div class="mb-4">
                            <label class="section-label">
                                <i class="fas fa-file-pdf"></i> Select Your Resume
                            </label>
                            
                            <c:choose>
                                <c:when test="${empty resumes}">
                                    <div class="upload-resume-cta">
                                        <i class="fas fa-cloud-upload-alt d-block"></i>
                                        <h5>No Resume Found</h5>
                                        <p class="text-muted mb-3">Upload your resume to increase your chances of getting hired</p>
                                        <a href="${pageContext.request.contextPath}/candidate/uploadResume" class="btn btn-primary">
                                            <i class="fas fa-upload me-2"></i>Upload Resume Now
                                        </a>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="r" items="${resumes}" varStatus="status">
                                        <label class="resume-option ${r.isPrimary ? 'selected' : ''}" for="resume-${r.resumeId}">
                                            <input type="radio" name="resumeId" id="resume-${r.resumeId}" 
                                                   value="${r.resumeId}" ${r.isPrimary ? 'checked' : ''}>
                                            <div class="resume-icon">
                                                <i class="fas fa-file-pdf"></i>
                                            </div>
                                            <div class="resume-info">
                                                <div class="resume-name">${r.fileName}</div>
                                                <div class="resume-date">
                                                    Uploaded: <fmt:formatDate value="${r.uploadedAt}" pattern="MMM dd, yyyy"/>
                                                </div>
                                            </div>
                                            <c:if test="${r.isPrimary}">
                                                <span class="primary-badge"><i class="fas fa-star me-1"></i>Primary</span>
                                            </c:if>
                                        </label>
                                    </c:forEach>
                                    
                                    <label class="resume-option" for="resume-none">
                                        <input type="radio" name="resumeId" id="resume-none" value="">
                                        <div class="resume-icon" style="background: #6c757d;">
                                            <i class="fas fa-times"></i>
                                        </div>
                                        <div class="resume-info">
                                            <div class="resume-name">Apply without resume</div>
                                            <div class="resume-date">Not recommended - most employers prefer resumes</div>
                                        </div>
                                    </label>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Cover Letter -->
                        <div class="mb-4">
                            <label class="section-label">
                                <i class="fas fa-envelope-open-text"></i> Cover Letter <span class="text-muted fw-normal">(Optional)</span>
                            </label>
                            <textarea name="coverLetter" id="coverLetter" class="form-control cover-letter-textarea" 
                                      rows="6" maxlength="2000"
                                      placeholder="Dear Hiring Manager,

I am excited to apply for this position. Here's why I would be a great fit for your team...

• Highlight your relevant experience
• Mention specific skills that match the job requirements
• Show enthusiasm for the company and role

I look forward to the opportunity to discuss how I can contribute to your team.

Best regards"></textarea>
                            <div class="char-counter">
                                <span id="charCount">0</span> / 2000 characters
                            </div>
                            
                            <div class="tips-card">
                                <h6><i class="fas fa-lightbulb me-2"></i>Tips for a Great Cover Letter</h6>
                                <ul>
                                    <li>Keep it concise - aim for 150-300 words</li>
                                    <li>Mention the specific job title and company name</li>
                                    <li>Highlight 2-3 relevant achievements or skills</li>
                                    <li>Show you've researched the company</li>
                                </ul>
                            </div>
                        </div>

                        <!-- Action Buttons -->
                        <div class="d-flex gap-3 mt-4">
                            <a href="${pageContext.request.contextPath}/job/${jobId}" class="btn btn-outline-secondary btn-back">
                                <i class="fas fa-arrow-left me-2"></i>Back to Job
                            </a>
                            <button type="submit" class="btn btn-primary btn-submit-application flex-grow-1" id="submitBtn">
                                <i class="fas fa-paper-plane me-2"></i>Submit Application
                            </button>
                        </div>
                    </form>
                </div>
            </div>
            
        </div>
    </main>
</div>

<jsp:include page="../common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Character counter for cover letter
    const coverLetter = document.getElementById('coverLetter');
    const charCount = document.getElementById('charCount');
    
    if (coverLetter && charCount) {
        coverLetter.addEventListener('input', function() {
            charCount.textContent = this.value.length;
        });
    }
    
    // Resume option selection styling
    document.querySelectorAll('.resume-option input[type="radio"]').forEach(radio => {
        radio.addEventListener('change', function() {
            document.querySelectorAll('.resume-option').forEach(opt => opt.classList.remove('selected'));
            if (this.checked) {
                this.closest('.resume-option').classList.add('selected');
            }
        });
    });
    
    // Form submission loading state
    document.getElementById('applyForm').addEventListener('submit', function() {
        const btn = document.getElementById('submitBtn');
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Submitting...';
    });
</script>
</body>
</html>
