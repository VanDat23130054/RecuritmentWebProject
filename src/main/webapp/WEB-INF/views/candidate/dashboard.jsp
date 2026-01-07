<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Candidate Dashboard - JobHunter</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="dashboard-container">
        <!-- Sidebar Navigation -->
        <jsp:include page="_sidebar.jsp" />

        <!-- Main Content -->
        <main class="dashboard-main">
            <!-- Page Header -->
            <div class="dashboard-header">
                <div class="header-content">
                    <h1>Dashboard</h1>
                    <p class="text-muted">Welcome back, <strong>${not empty candidate.fullName ? candidate.fullName : sessionScope.userEmail}</strong></p>
                </div>
                <div class="header-actions">
                    <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary">
                        <i class="fas fa-search"></i> Browse Jobs
                    </a>
                </div>
            </div>

            <!-- Statistics Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #4F46E5;">
                        <i class="fas fa-file-alt"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${stats.totalApplications != null ? stats.totalApplications : 0}</h3>
                        <p>Total Applications</p>
                        <span class="stat-detail">
                            <c:if test="${stats.newApplications != null && stats.newApplications > 0}">
                                ${stats.newApplications} new responses
                            </c:if>
                            <c:if test="${stats.newApplications == null || stats.newApplications == 0}">
                                No new responses
                            </c:if>
                        </span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background: #10B981;">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${stats.underReviewCount != null ? stats.underReviewCount : 0}</h3>
                        <p>Under Review</p>
                        <span class="stat-detail">
                            <c:if test="${stats.totalApplications > 0}">
                                <fmt:formatNumber type="number" groupingUsed="true" pattern="0.0" 
                                    value="${(stats.underReviewCount != null ? stats.underReviewCount : 0) / stats.totalApplications * 100}"/>%
                            </c:if>
                        </span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background: #F59E0B;">
                        <i class="fas fa-calendar"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${stats.interviewCount != null ? stats.interviewCount : 0}</h3>
                        <p>Interviews Scheduled</p>
                        <span class="stat-detail">
                            <c:if test="${not empty stats.nextInterview}">
                                Next: ${stats.nextInterview}
                            </c:if>
                            <c:if test="${empty stats.nextInterview}">
                                None upcoming
                            </c:if>
                        </span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background: #EF4444;">
                        <i class="fas fa-bookmark"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${stats.savedJobsCount != null ? stats.savedJobsCount : 0}</h3>
                        <p>Saved Jobs</p>
                        <span class="stat-detail">
                            <a href="${pageContext.request.contextPath}/candidate/saved-jobs" style="color: #EF4444;">
                                View All →
                            </a>
                        </span>
                    </div>
                </div>
            </div>

            <!-- Profile Section -->
            <div class="dashboard-card mt-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h3><i class="fas fa-user"></i> My Profile</h3>
                    <a href="${pageContext.request.contextPath}/candidate/profile" class="btn btn-sm btn-outline-primary">
                        <i class="fas fa-edit"></i> Edit Profile
                    </a>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty candidate}">
                            <div class="row">
                                <div class="col-md-3 text-center">
                                    <c:if test="${not empty candidate.avatarUrl}">
                                        <img src="${candidate.avatarUrl}" alt="Avatar" class="rounded-circle" style="width: 150px; height: 150px; object-fit: cover;">
                                    </c:if>
                                </div>
                                <div class="col-md-9">
                                    <h5>${candidate.fullName}</h5>
                                    <p class="text-muted">${candidate.headline}</p>
                                    <div class="mb-3">
                                        <strong>Experience:</strong> ${candidate.yearsOfExperience} years<br>
                                        <strong>Location:</strong> ${candidate.cityName}<br>
                                        <strong>Profile Status:</strong> 
                                        <c:if test="${candidate.publicProfile}">
                                            <span class="badge bg-success">Public</span>
                                        </c:if>
                                        <c:if test="${!candidate.publicProfile}">
                                            <span class="badge bg-secondary">Private</span>
                                        </c:if>
                                    </div>
                                    <p>${candidate.summary}</p>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-info">
                                <i class="fas fa-info-circle me-2"></i>
                                Profile not yet created. <a href="${pageContext.request.contextPath}/candidate/profile">Create Profile</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Recent Applications -->
            <div class="dashboard-card mt-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h3><i class="fas fa-file-alt"></i> Recent Applications</h3>
                    <a href="${pageContext.request.contextPath}/candidate/applications" class="btn btn-sm btn-outline-primary">
                        View All
                    </a>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty recentApplications}">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Job Title</th>
                                            <th>Company</th>
                                            <th>Applied</th>
                                            <th>Status</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="app" items="${recentApplications}" begin="0" end="4">
                                            <tr>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/job/${app.jobId}">
                                                        ${app.jobTitle}
                                                    </a>
                                                </td>
                                                <td>${app.companyName}</td>
                                                <td>
                                                    <fmt:formatDate value="${app.appliedAt}" pattern="MMM dd, yyyy"/>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${app.status == 'Applied'}">
                                                            <span class="badge bg-primary">${app.status}</span>
                                                        </c:when>
                                                        <c:when test="${app.status == 'Under Review'}">
                                                            <span class="badge bg-info">${app.status}</span>
                                                        </c:when>
                                                        <c:when test="${app.status == 'Interview Scheduled'}">
                                                            <span class="badge bg-warning text-dark">${app.status}</span>
                                                        </c:when>
                                                        <c:when test="${app.status == 'Rejected'}">
                                                            <span class="badge bg-danger">${app.status}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-secondary">${app.status}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/candidate/applications?app=${app.applicationId}" class="btn btn-sm btn-outline-primary">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-4">
                                <p class="text-muted">No applications yet. <a href="${pageContext.request.contextPath}/jobs">Browse jobs</a> to apply!</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="row mt-4">
                <div class="col-md-6">
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3><i class="fas fa-bookmark"></i> Saved Jobs</h3>
                        </div>
                        <div class="card-body">
                            <p>You have <strong>${stats.savedJobsCount != null ? stats.savedJobsCount : 0}</strong> saved jobs</p>
                            <a href="${pageContext.request.contextPath}/candidate/saved-jobs" class="btn btn-outline-primary">
                                <i class="fas fa-arrow-right"></i> View Saved Jobs
                            </a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3><i class="fas fa-file"></i> Resume</h3>
                        </div>
                        <div class="card-body">
                            <p>Keep your resume up-to-date for better opportunities</p>
                            <a href="${pageContext.request.contextPath}/candidate/resume" class="btn btn-outline-primary">
                                <i class="fas fa-arrow-right"></i> Manage Resume
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <jsp:include page="../common/footer.jsp" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/alert.js"></script>
</body>
</html>
