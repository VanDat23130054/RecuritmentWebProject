<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${job.title} - ${job.companyName} | JobHunter</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome 6 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css"/>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container job-detail-container">
        <div class="job-detail-main">
            <!-- Job Header -->
            <div class="job-header">
                <div class="job-header-content">
                    <img src="${job.logoUrl}" alt="${job.companyName}" class="company-logo-large">
                    <div class="job-title-section">
                        <h1>${job.title}</h1>
                        <p class="company-name">
                            <i class="fas fa-building"></i> ${job.companyName}
                        </p>
                        <div class="job-meta-tags">
                            <span class="meta-tag">
                                <i class="fas fa-map-marker-alt"></i> ${job.cityName}
                            </span>
                            <c:if test="${not empty job.employmentType}">
                                <span class="meta-tag">
                                    <i class="fas fa-briefcase"></i> ${job.employmentType}
                                </span>
                            </c:if>
                            <c:if test="${not empty job.seniorityLevel}">
                                <span class="meta-tag">
                                    <i class="fas fa-layer-group"></i> ${job.seniorityLevel}
                                </span>
                            </c:if>
                            <c:if test="${not empty job.remoteType}">
                                <span class="meta-tag">
                                    <i class="fas fa-laptop-house"></i> ${job.remoteType}
                                </span>
                            </c:if>
                        </div>
                    </div>
                </div>
                
                <div class="job-actions">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user && sessionScope.user.role == 'Candidate'}">
                            <a href="${pageContext.request.contextPath}/apply/${job.jobId}" 
                               class="btn btn-primary btn-lg">
                                <i class="fas fa-paper-plane"></i> Apply Now
                            </a>
                        </c:when>
                        <c:when test="${empty sessionScope.user}">
                            <a href="${pageContext.request.contextPath}/login?returnUrl=/job/${job.jobId}" 
                               class="btn btn-primary btn-lg">
                                <i class="fas fa-sign-in-alt"></i> Login to Apply
                            </a>
                        </c:when>
                        <c:when test="${sessionScope.user.role == 'Recruiter' || sessionScope.user.role == 'EmployerAdmin'}">
                            <a href="${pageContext.request.contextPath}/employer/dashboard" 
                               class="btn btn-primary btn-lg">
                                <i class="fas fa-tachometer-alt"></i> Go to Dashboard
                            </a>
                        </c:when>
                    </c:choose>
                    
                    <c:if test="${empty sessionScope.user || sessionScope.user.role == 'Candidate'}">
                        <c:choose>
                            <c:when test="${job.isSaved}">
                                <button class="btn btn-secondary btn-lg save-job-btn saved" data-job-id="${job.jobId}">
                                    <i class="fas fa-bookmark"></i> Saved
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-secondary btn-lg save-job-btn" data-job-id="${job.jobId}">
                                    <i class="far fa-bookmark"></i> Save Job
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </div>
            </div>
            
            <!-- Salary Information -->
            <c:if test="${not empty job.salaryMin && not empty job.salaryMax}">
                <div class="salary-info">
                    <i class="fas fa-dollar-sign"></i>
                    <strong>Salary:</strong>
                    <fmt:formatNumber value="${job.salaryMin}" type="number"/> - 
                    <fmt:formatNumber value="${job.salaryMax}" type="number"/> 
                    ${job.currency}/month
                </div>
            </c:if>
            
            <!-- Skills Required -->
            <c:if test="${not empty job.skillsList}">
                <div class="job-section">
                    <h3>Required Skills</h3>
                    <div class="skills-list">
                        <c:forEach items="${job.skillsList}" var="skill">
                            <span class="skill-badge">${skill.Name}</span>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
            
            <!-- Job Description -->
            <div class="job-section">
                <h3>Job Description</h3>
                <div class="job-description">
                    ${job.description}
                </div>
            </div>
            
            <!-- Requirements -->
            <c:if test="${not empty job.requirements}">
                <div class="job-section">
                    <h3>Requirements</h3>
                    <div class="job-requirements">
                        ${job.requirements}
                    </div>
                </div>
            </c:if>
            
            <!-- Benefits -->
            <c:if test="${not empty job.benefits}">
                <div class="job-section">
                    <h3>Benefits</h3>
                    <div class="job-benefits">
                        ${job.benefits}
                    </div>
                </div>
            </c:if>
            
            <!-- Company Information -->
            <c:if test="${not empty company}">
                <div class="job-section company-section">
                    <h3>About ${company.name}</h3>
                    <div class="company-info">
                        <div class="company-stats">
                            <c:if test="${not empty company.industry}">
                                <div class="stat-item">
                                    <i class="fas fa-industry"></i>
                                    <span>${company.industry}</span>
                                </div>
                            </c:if>
                            <c:if test="${not empty company.companySize}">
                                <div class="stat-item">
                                    <i class="fas fa-users"></i>
                                    <span>${company.companySize} employees</span>
                                </div>
                            </c:if>
                            <c:if test="${not empty company.foundedYear}">
                                <div class="stat-item">
                                    <i class="fas fa-calendar"></i>
                                    <span>Founded ${company.foundedYear}</span>
                                </div>
                            </c:if>
                            <c:if test="${not empty company.websiteUrl}">
                                <div class="stat-item">
                                    <i class="fas fa-globe"></i>
                                    <a href="${company.websiteUrl}" target="_blank">Visit Website</a>
                                </div>
                            </c:if>
                        </div>
                        
                        <c:if test="${not empty company.description}">
                            <p class="company-description">${company.description}</p>
                        </c:if>
                        
                        <a href="${pageContext.request.contextPath}/company/${company.companyId}" 
                           class="btn btn-outline">
                            View All Jobs at ${company.name}
                        </a>
                    </div>
                </div>
            </c:if>
            
            <!-- Application Deadline -->
            <c:if test="${not empty job.expiresAt}">
                <div class="application-deadline">
                    <i class="fas fa-clock"></i>
                    Application deadline: 
                    <fmt:formatDate value="${job.expiresAt}" pattern="MMMM dd, yyyy"/>
                </div>
            </c:if>
        </div>
        
        <!-- Sidebar -->
        <div class="job-detail-sidebar">
            <!-- Related Jobs -->
            <c:if test="${not empty relatedJobs}">
                <div class="sidebar-section">
                    <h3>Related Jobs</h3>
                    <div class="related-jobs-list">
                        <c:forEach items="${relatedJobs}" var="relatedJob">
                            <div class="related-job-card">
                                <h4>
                                    <a href="${pageContext.request.contextPath}/job/${relatedJob.jobId}">
                                        ${relatedJob.title}
                                    </a>
                                </h4>
                                <p class="company">${relatedJob.companyName}</p>
                                <p class="location">
                                    <i class="fas fa-map-marker-alt"></i> ${relatedJob.cityName}
                                </p>
                                <c:if test="${not empty relatedJob.salaryMin}">
                                    <p class="salary">
                                        <fmt:formatNumber value="${relatedJob.salaryMin}" type="number"/> - 
                                        <fmt:formatNumber value="${relatedJob.salaryMax}" type="number"/>
                                    </p>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
            
            <!-- Job Share -->
            <div class="sidebar-section">
                <h3>Share this Job</h3>
                <div class="share-buttons">
                    <button class="btn-share facebook" title="Share on Facebook">
                        <i class="fab fa-facebook-f"></i>
                    </button>
                    <button class="btn-share twitter" title="Share on Twitter">
                        <i class="fab fa-twitter"></i>
                    </button>
                    <button class="btn-share linkedin" title="Share on LinkedIn">
                        <i class="fab fa-linkedin-in"></i>
                    </button>
                    <button class="btn-share copy" title="Copy Link">
                        <i class="fas fa-link"></i>
                    </button>
                </div>
            </div>
            
            <!-- Report Job -->
            <div class="sidebar-section">
                <a href="${pageContext.request.contextPath}/report-job/${job.jobId}" 
                   class="btn btn-outline btn-block">
                    <i class="fas fa-flag"></i> Report this Job
                </a>
            </div>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script src="${pageContext.request.contextPath}/js/alert.js"></script>
    <script>
        // Save job functionality
        const saveJobBtn = document.querySelector('.save-job-btn');
        if (saveJobBtn) {
            saveJobBtn.addEventListener('click', function() {
                const jobId = this.dataset.jobId;
                const isSaved = this.classList.contains('saved');
                const action = isSaved ? 'unsave' : 'save';
                const button = this;
                
                // Send AJAX request
                fetch('${pageContext.request.contextPath}/api/save-job', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'jobId=' + jobId + '&action=' + action
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        if (action === 'save') {
                            button.innerHTML = '<i class="fas fa-bookmark"></i> Saved';
                            button.classList.add('saved');
                            showSuccess('Job saved successfully!', 'Saved');
                        } else {
                            button.innerHTML = '<i class="far fa-bookmark"></i> Save Job';
                            button.classList.remove('saved');
                            showInfo('Job removed from saved list', 'Removed');
                        }
                    } else {
                        if (data.message && data.message.includes('login')) {
                            showWarning('Please login to save jobs', 'Login Required');
                            setTimeout(() => {
                                window.location.href = '${pageContext.request.contextPath}/login?returnUrl=' + encodeURIComponent(window.location.pathname);
                            }, 1500);
                        } else {
                            showError(data.message || 'Failed to save job', 'Error');
                        }
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showError('An error occurred. Please try again.', 'Network Error');
                });
            });
        }
        
        // Share functionality
        document.querySelectorAll('.btn-share').forEach(btn => {
            btn.addEventListener('click', function() {
                const jobUrl = window.location.href;
                const jobTitle = '<c:out value="${job.title}" escapeXml="true"/> at <c:out value="${job.companyName}" escapeXml="true"/>';
                
                if (this.classList.contains('copy')) {
                    navigator.clipboard.writeText(jobUrl);
                    alert('Link copied to clipboard!');
                } else if (this.classList.contains('facebook')) {
                    window.open('https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(jobUrl));
                } else if (this.classList.contains('twitter')) {
                    window.open('https://twitter.com/intent/tweet?url=' + encodeURIComponent(jobUrl) + '&text=' + encodeURIComponent(jobTitle));
                } else if (this.classList.contains('linkedin')) {
                    window.open('https://www.linkedin.com/sharing/share-offsite/?url=' + encodeURIComponent(jobUrl));
                }
            });
        });
    </script>
</body>
</html>
