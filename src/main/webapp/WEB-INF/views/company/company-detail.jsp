<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${company.name} - Jobs | JobHunter</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-detail.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="company-detail-container">
        <!-- Company Header Section -->
        <div class="company-header">
            <div class="company-header-content">
                <div class="container">
                    <div class="company-header-inner">
                        <div class="company-logo-section">
                            <c:if test="${not empty company.logoUrl}">
                                <img src="${company.logoUrl}" alt="${company.name}" class="company-logo-large">
                            </c:if>
                            <c:if test="${empty company.logoUrl}">
                                <div class="company-logo-placeholder">
                                    <i class="fas fa-building"></i>
                                </div>
                            </c:if>
                        </div>
                        <div class="company-info-section">
                            <h1>${company.name}</h1>
                            <div class="company-stats">
                                <c:if test="${not empty company.industry}">
                                    <div class="stat">
                                        <i class="fas fa-industry"></i>
                                        <span>${company.industry}</span>
                                    </div>
                                </c:if>
                                <c:if test="${not empty company.sizeRange}">
                                    <div class="stat">
                                        <i class="fas fa-users"></i>
                                        <span>${company.sizeRange} employees</span>
                                    </div>
                                </c:if>
                                <c:if test="${not empty company.foundedYear}">
                                    <div class="stat">
                                        <i class="fas fa-calendar"></i>
                                        <span>Founded ${company.foundedYear}</span>
                                    </div>
                                </c:if>
                                <c:if test="${not empty company.cityName}">
                                    <div class="stat">
                                        <i class="fas fa-map-marker-alt"></i>
                                        <span>${company.cityName}</span>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Company Content -->
        <div class="company-content">
            <div class="container">
                <div class="row">
                    <!-- Main Content -->
                    <div class="col-lg-8">
                        <!-- About Section -->
                        <c:if test="${not empty company.description}">
                            <div class="company-section">
                                <h2><i class="fas fa-info-circle"></i> About ${company.name}</h2>
                                <p class="company-description">${company.description}</p>
                            </div>
                        </c:if>

                        <!-- Open Positions -->
                        <div class="company-section">
                            <div class="section-header">
                                <h2><i class="fas fa-briefcase"></i> Open Positions (${companyJobs.size()})</h2>
                            </div>

                            <c:if test="${empty companyJobs}">
                                <div class="empty-state">
                                    <i class="fas fa-briefcase"></i>
                                    <p>No open positions at this time</p>
                                </div>
                            </c:if>

                            <c:if test="${not empty companyJobs}">
                                <div class="jobs-list">
                                    <c:forEach items="${companyJobs}" var="job">
                                        <div class="job-card">
                                            <div class="job-card-header">
                                                <div class="job-title-info">
                                                    <h3>${job.title}</h3>
                                                    <c:if test="${not empty job.cityName}">
                                                        <span class="location">
                                                            <i class="fas fa-map-marker-alt"></i> ${job.cityName}
                                                        </span>
                                                    </c:if>
                                                </div>
                                                <c:if test="${not empty user and user.role == 'Candidate'}">
                                                    <button class="save-job-btn ${savedJobIds.contains(job.jobId) ? 'saved' : ''}"
                                                            onclick="toggleSaveJob(this, ${job.jobId})">
                                                        <i class="fas fa-bookmark"></i>
                                                    </button>
                                                </c:if>
                                            </div>

                                            <div class="job-details">
                                                <c:if test="${not empty job.salaryMin and not empty job.salaryMax}">
                                                    <div class="detail-badge">
                                                        <i class="fas fa-dollar-sign"></i>
                                                        <fmt:formatNumber value="${job.salaryMin}" type="number" pattern="###,###"/>
                                                        - <fmt:formatNumber value="${job.salaryMax}" type="number" pattern="###,###"/>
                                                        ${job.currency}
                                                    </div>
                                                </c:if>
                                                <c:if test="${not empty job.skills}">
                                                    <div class="skills-section">
                                                        <c:set var="skillsList" value="${fn:split(job.skills, ',')}"/>
                                                        <c:forEach items="${skillsList}" var="skill" varStatus="status">
                                                            <c:if test="${status.index < 5}">
                                                                <span class="skill-badge">${fn:trim(skill)}</span>
                                                            </c:if>
                                                        </c:forEach>
                                                        <c:if test="${fn:length(skillsList) > 5}">
                                                            <span class="skill-badge more">+${fn:length(skillsList) - 5} more</span>
                                                        </c:if>
                                                    </div>
                                                </c:if>
                                            </div>

                                            <div class="job-card-footer">
                                                <a href="${pageContext.request.contextPath}/job/${job.jobId}" class="btn btn-view-job">
                                                    View Job <i class="fas fa-arrow-right"></i>
                                                </a>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- Sidebar -->
                    <div class="col-lg-4">
                        <!-- Company Details Card -->
                        <div class="sidebar-card">
                            <h3><i class="fas fa-building"></i> Company Details</h3>
                            
                            <div class="detail-item">
                                <label>Industry</label>
                                <p>${not empty company.industry ? company.industry : 'Not specified'}</p>
                            </div>

                            <div class="detail-item">
                                <label>Company Size</label>
                                <p>${not empty company.sizeRange ? company.sizeRange : 'Not specified'}</p>
                            </div>

                            <div class="detail-item">
                                <label>Founded</label>
                                <p>${not empty company.foundedYear ? company.foundedYear : 'Not specified'}</p>
                            </div>

                            <div class="detail-item">
                                <label>Location</label>
                                <p>${not empty company.cityName ? company.cityName : 'Not specified'}</p>
                            </div>

                            <c:if test="${not empty company.websiteUrl}">
                                <div class="detail-item">
                                    <a href="${company.websiteUrl}" target="_blank" class="btn btn-website w-100">
                                        <i class="fas fa-external-link-alt"></i> Visit Website
                                    </a>
                                </div>
                            </c:if>
                        </div>

                        <!-- Open Positions Summary -->
                        <div class="sidebar-card">
                            <h3><i class="fas fa-chart-bar"></i> Summary</h3>
                            <div class="summary-stat">
                                <span class="label">Open Positions</span>
                                <span class="value">${companyJobs.size()}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="../common/footer.jsp" />

    <script src="${pageContext.request.contextPath}/js/alert.js"></script>
    <script>
        function toggleSaveJob(btn, jobId) {
            const contextPath = '${pageContext.request.contextPath}';
            const isSaved = btn.classList.contains('saved');
            
            const formData = new FormData();
            formData.append('jobId', jobId);
            
            const url = isSaved ? 
                contextPath + '/api/save-job?action=unsave' :
                contextPath + '/api/save-job?action=save';
            
            fetch(url, {
                method: 'POST',
                body: new URLSearchParams(formData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    btn.classList.toggle('saved');
                    showSuccess(isSaved ? 'Job removed from saved' : 'Job saved successfully', 'Success');
                } else {
                    showError(data.message || 'Error saving job', 'Error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showError('Error saving job', 'Error');
            });
        }
    </script>
</body>
</html>
