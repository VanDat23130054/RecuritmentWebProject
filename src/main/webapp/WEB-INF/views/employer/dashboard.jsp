<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Employer Dashboard - JobHunter</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="dashboard-container">
        <!-- Sidebar Navigation -->
        <aside class="dashboard-sidebar">
            <div class="sidebar-header">
                <h3><i class="fas fa-building"></i> Employer Portal</h3>
            </div>
            
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/employer/dashboard" class="nav-item active">
                    <i class="fas fa-chart-line"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/employer/jobs" class="nav-item">
                    <i class="fas fa-briefcase"></i> My Jobs
                </a>
                <a href="${pageContext.request.contextPath}/employer/post-job" class="nav-item">
                    <i class="fas fa-plus-circle"></i> Post New Job
                </a>
                <a href="${pageContext.request.contextPath}/employer/applications" class="nav-item">
                    <i class="fas fa-file-alt"></i> Applications
                </a>
                <a href="${pageContext.request.contextPath}/employer/company-profile" class="nav-item">
                    <i class="fas fa-building"></i> Company Profile
                </a>
                <a href="${pageContext.request.contextPath}/employer/settings" class="nav-item">
                    <i class="fas fa-cog"></i> Settings
                </a>
            </nav>
        </aside>

        <!-- Main Content -->
        <main class="dashboard-main">
            <!-- Page Header -->
            <div class="dashboard-header">
                <div class="header-content">
                    <h1>Dashboard Overview</h1>
                    <p class="text-muted">Welcome back, ${user.email}</p>
                </div>
                <div class="header-actions">
                    <a href="${pageContext.request.contextPath}/employer/post-job" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Post New Job
                    </a>
                </div>
            </div>

            <!-- Statistics Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #4F46E5;">
                        <i class="fas fa-briefcase"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${stats.activeJobs != null ? stats.activeJobs : 0}</h3>
                        <p>Active Jobs</p>
                        <span class="stat-detail">of ${stats.totalJobs != null ? stats.totalJobs : 0} total</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background: #10B981;">
                        <i class="fas fa-file-alt"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${stats.totalApplications != null ? stats.totalApplications : 0}</h3>
                        <p>Total Applications</p>
                        <c:if test="${stats.newApplications != null && stats.newApplications > 0}">
                            <span class="stat-badge new">${stats.newApplications} new</span>
                        </c:if>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background: #F59E0B;">
                        <i class="fas fa-calendar-check"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${stats.interviewsScheduled != null ? stats.interviewsScheduled : 0}</h3>
                        <p>Interviews Scheduled</p>
                        <span class="stat-detail">Upcoming</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background: #6366F1;">
                        <i class="fas fa-eye"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${stats.totalViews != null ? stats.totalViews : 0}</h3>
                        <p>Total Job Views</p>
                        <span class="stat-detail">All time</span>
                    </div>
                </div>
            </div>

            <!-- Charts Section -->
            <div class="dashboard-row">
                <!-- Application Status Chart -->
                <div class="dashboard-card chart-card">
                    <div class="card-header">
                        <h3><i class="fas fa-chart-pie"></i> Application Status</h3>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty applicationStats}">
                                <div class="status-chart">
                                    <c:forEach items="${applicationStats}" var="stat">
                                        <div class="status-item">
                                            <div class="status-label">
                                                <span class="status-dot status-${stat.status}"></span>
                                                ${stat.status}
                                            </div>
                                            <div class="status-count">${stat.count}</div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-chart-pie"></i>
                                    <p>No application data available</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Company Info -->
                <div class="dashboard-card">
                    <div class="card-header">
                        <h3><i class="fas fa-building"></i> Company Information</h3>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty company}">
                                <div class="company-info">
                                    <c:if test="${not empty company.logoUrl}">
                                        <img src="${company.logoUrl}" alt="${company.name}" class="company-logo">
                                    </c:if>
                                    <h4>${company.name}</h4>
                                    <p class="company-location">
                                        <i class="fas fa-map-marker-alt"></i> ${company.cityName}, ${company.countryName}
                                    </p>
                                    <c:if test="${not empty company.website}">
                                        <p class="company-website">
                                            <i class="fas fa-globe"></i> 
                                            <a href="${company.website}" target="_blank">${company.website}</a>
                                        </p>
                                    </c:if>
                                    <a href="${pageContext.request.contextPath}/employer/company-profile" class="btn btn-sm btn-outline">
                                        Edit Company Profile
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-building"></i>
                                    <p>Company profile not set up</p>
                                    <a href="${pageContext.request.contextPath}/employer/company-profile" class="btn btn-sm btn-primary">
                                        Set Up Company
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Recent Jobs -->
            <div class="dashboard-card">
                <div class="card-header">
                    <h3><i class="fas fa-briefcase"></i> Recent Jobs</h3>
                    <a href="${pageContext.request.contextPath}/employer/jobs" class="btn btn-sm btn-outline">
                        View All
                    </a>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty recentJobs}">
                            <div class="table-responsive">
                                <table class="dashboard-table">
                                    <thead>
                                        <tr>
                                            <th>Job Title</th>
                                            <th>Location</th>
                                            <th>Status</th>
                                            <th>Posted</th>
                                            <th>Views</th>
                                            <th>Applications</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${recentJobs}" var="job">
                                            <tr>
                                                <td>
                                                    <div class="job-title-cell">
                                                        <strong>${job.title}</strong>
                                                        <c:if test="${job.isFeatured}">
                                                            <span class="badge badge-featured">Featured</span>
                                                        </c:if>
                                                    </div>
                                                </td>
                                                <td>${job.cityName}</td>
                                                <td>
                                                    <span class="badge badge-${job.statusId == 1 ? 'active' : job.statusId == 2 ? 'closed' : 'draft'}">
                                                        ${job.status}
                                                    </span>
                                                </td>
                                                <td>
                                                    <fmt:formatDate value="${job.postedAt}" pattern="MMM dd, yyyy" />
                                                </td>
                                                <td>${job.viewsCount}</td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/employer/applications?jobId=${job.jobId}" class="applications-link">
                                                        ${job.applicationsCount}
                                                    </a>
                                                </td>
                                                <td>
                                                    <div class="action-buttons">
                                                        <a href="${pageContext.request.contextPath}/job/${job.jobId}" 
                                                           class="btn-icon" title="View">
                                                            <i class="fas fa-eye"></i>
                                                        </a>
                                                        <a href="${pageContext.request.contextPath}/employer/edit-job/${job.jobId}" 
                                                           class="btn-icon" title="Edit">
                                                            <i class="fas fa-edit"></i>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="fas fa-briefcase"></i>
                                <p>You haven't posted any jobs yet</p>
                                <a href="${pageContext.request.contextPath}/employer/post-job" class="btn btn-primary">
                                    Post Your First Job
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Recent Applications -->
            <div class="dashboard-card">
                <div class="card-header">
                    <h3><i class="fas fa-users"></i> Recent Applications</h3>
                    <a href="${pageContext.request.contextPath}/employer/applications" class="btn btn-sm btn-outline">
                        View All
                    </a>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty recentApplications}">
                            <div class="table-responsive">
                                <table class="dashboard-table">
                                    <thead>
                                        <tr>
                                            <th>Candidate</th>
                                            <th>Job Title</th>
                                            <th>Applied Date</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${recentApplications}" var="app">
                                            <tr>
                                                <td>
                                                    <div class="candidate-cell">
                                                        <strong>${app.candidateName}</strong>
                                                        <span class="candidate-email">${app.candidateEmail}</span>
                                                    </div>
                                                </td>
                                                <td>${app.jobTitle}</td>
                                                <td>
                                                    <fmt:formatDate value="${app.appliedAt}" pattern="MMM dd, yyyy HH:mm" />
                                                </td>
                                                <td>
                                                    <span class="badge badge-${app.status}">
                                                        ${app.status}
                                                    </span>
                                                </td>
                                                <td>
                                                    <div class="action-buttons">
                                                        <a href="${pageContext.request.contextPath}/employer/application/${app.applicationId}" 
                                                           class="btn-icon" title="View Application">
                                                            <i class="fas fa-eye"></i>
                                                        </a>
                                                        <c:if test="${not empty app.fileUrl}">
                                                            <a href="${app.fileUrl}" 
                                                               class="btn-icon" title="Download Resume" target="_blank">
                                                                <i class="fas fa-download"></i>
                                                            </a>
                                                        </c:if>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="fas fa-users"></i>
                                <p>No applications received yet</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </main>
    </div>

    <jsp:include page="../common/footer.jsp" />
    
    <script src="${pageContext.request.contextPath}/js/alert.js"></script>
</body>
</html>
