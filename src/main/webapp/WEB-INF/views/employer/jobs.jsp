<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Jobs - JobHunter</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-jobs.css">
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
                <a href="${pageContext.request.contextPath}/employer/dashboard" class="nav-item">
                    <i class="fas fa-chart-line"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/employer/jobs" class="nav-item active">
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
            <!-- Alert Container -->
            <div id="alert-container"></div>
            
            <div class="jobs-container">
                <!-- Page Header -->
                <div class="jobs-header">
                    <div>
                        <h1>My Jobs</h1>
                        <p class="text-muted">Manage all your job postings</p>
                    </div>
                    <div class="header-actions">
                        <a href="${pageContext.request.contextPath}/employer/post-job" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Post New Job
                        </a>
                    </div>
                </div>

                <!-- Filter Tabs -->
                <div class="filter-tabs">
                    <a href="${pageContext.request.contextPath}/employer/jobs" 
                       class="filter-tab ${selectedStatus == null ? 'active' : ''}">
                        All Jobs
                        <span class="count">${allJobsCount}</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/employer/jobs?status=2" 
                       class="filter-tab ${selectedStatus == 2 ? 'active' : ''}">
                        Published
                        <span class="count">${publishedCount}</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/employer/jobs?status=1" 
                       class="filter-tab ${selectedStatus == 1 ? 'active' : ''}">
                        Draft
                        <span class="count">${draftCount}</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/employer/jobs?status=3" 
                       class="filter-tab ${selectedStatus == 3 ? 'active' : ''}">
                        Closed
                        <span class="count">${closedCount}</span>
                    </a>
                </div>

                <!-- Search Section -->
                <div class="search-section">
                    <form method="get" action="${pageContext.request.contextPath}/employer/jobs" style="display: flex; gap: 1rem; flex: 1;">
                        <div class="search-box">
                            <i class="fas fa-search"></i>
                            <input type="text" 
                                   name="keyword" 
                                   placeholder="Search jobs by title or location..." 
                                   value="${keyword != null ? keyword : ''}">
                        </div>
                        <c:if test="${selectedStatus != null}">
                            <input type="hidden" name="status" value="${selectedStatus}">
                        </c:if>
                        <button type="submit" class="search-btn">
                            <i class="fas fa-search"></i> Search
                        </button>
                        <c:if test="${keyword != null}">
                            <a href="${pageContext.request.contextPath}/employer/jobs${selectedStatus != null ? '?status='.concat(selectedStatus) : ''}" 
                               class="search-btn" style="background: #6b7280; text-decoration: none;">
                                <i class="fas fa-times"></i> Clear
                            </a>
                        </c:if>
                    </form>
                </div>

                <!-- Jobs Table -->
                <c:choose>
                    <c:when test="${not empty jobs}">
                        <div class="jobs-table-container">
                            <table class="jobs-table">
                                <thead>
                                    <tr>
                                        <th>Job Title</th>
                                        <th>Location</th>
                                        <th>Status</th>
                                        <th>Posted Date</th>
                                        <th>Expires</th>
                                        <th>Statistics</th>
                                        <th style="text-align: center;">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${jobs}" var="job">
                                        <tr>
                                            <td>
                                                <div class="job-title-cell">
                                                    <div>
                                                        <strong>${job.title}</strong>
                                                        <c:if test="${job.isFeatured}">
                                                            <span class="featured-badge">
                                                                <i class="fas fa-star"></i> Featured
                                                            </span>
                                                        </c:if>
                                                    </div>
                                                    <div class="job-meta">
                                                        <span>
                                                            <i class="fas fa-briefcase"></i> ${job.employmentType}
                                                        </span>
                                                        <c:if test="${job.salaryMax != null}">
                                                            <span>
                                                                <i class="fas fa-money-bill-wave"></i>
                                                                <fmt:formatNumber value="${job.salaryMin}" type="number" groupingUsed="true"/> -
                                                                <fmt:formatNumber value="${job.salaryMax}" type="number" groupingUsed="true"/>
                                                                ${job.currency}
                                                            </span>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <i class="fas fa-map-marker-alt" style="color: #9ca3af;"></i>
                                                ${job.cityName}
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${job.statusId == 2}">
                                                        <span class="status-badge published">
                                                            <i class="fas fa-check-circle"></i> Published
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${job.statusId == 1}">
                                                        <span class="status-badge draft">
                                                            <i class="fas fa-pencil-alt"></i> Draft
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${job.statusId == 3}">
                                                        <span class="status-badge closed">
                                                            <i class="fas fa-times-circle"></i> Closed
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge expired">
                                                            <i class="fas fa-clock"></i> ${job.status}
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${job.postedAt}" pattern="MMM dd, yyyy" />
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${job.expiresAt != null}">
                                                        <fmt:formatDate value="${job.expiresAt}" pattern="MMM dd, yyyy" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">-</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="stats-cell">
                                                    <span class="stat-item">
                                                        <i class="fas fa-eye"></i>
                                                        ${job.viewsCount}
                                                    </span>
                                                    <span class="stat-item ${job.applicationsCount > 0 ? 'highlight' : ''}">
                                                        <i class="fas fa-users"></i>
                                                        ${job.applicationsCount}
                                                    </span>
                                                </div>
                                            </td>
                                            <td style="text-align: center;">
                                                <div class="action-buttons">
                                                    <a href="${pageContext.request.contextPath}/job/${job.jobId}" 
                                                       class="btn-icon" title="View Job">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/employer/edit-job/${job.jobId}" 
                                                       class="btn-icon" title="Edit Job">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/employer/applications?jobId=${job.jobId}" 
                                                       class="btn-icon" title="View Applications">
                                                        <i class="fas fa-users"></i>
                                                    </a>
                                                    <button class="btn-icon delete" 
                                                            onclick="deleteJob(${job.jobId}, '${job.title}')"
                                                            title="Delete Job">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- Pagination -->
                        <c:if test="${totalPages > 1}">
                            <div class="pagination">
                                <c:if test="${currentPage > 1}">
                                    <a href="?page=${currentPage - 1}${selectedStatus != null ? '&status='.concat(selectedStatus) : ''}${keyword != null ? '&keyword='.concat(keyword) : ''}">
                                        <i class="fas fa-chevron-left"></i>
                                    </a>
                                </c:if>
                                <c:if test="${currentPage == 1}">
                                    <span class="disabled">
                                        <i class="fas fa-chevron-left"></i>
                                    </span>
                                </c:if>

                                <c:forEach begin="1" end="${totalPages}" var="page">
                                    <c:choose>
                                        <c:when test="${page == currentPage}">
                                            <span class="current">${page}</span>
                                        </c:when>
                                        <c:when test="${page == 1 || page == totalPages || (page >= currentPage - 2 && page <= currentPage + 2)}">
                                            <a href="?page=${page}${selectedStatus != null ? '&status='.concat(selectedStatus) : ''}${keyword != null ? '&keyword='.concat(keyword) : ''}">${page}</a>
                                        </c:when>
                                        <c:when test="${page == currentPage - 3 || page == currentPage + 3}">
                                            <span>...</span>
                                        </c:when>
                                    </c:choose>
                                </c:forEach>

                                <c:if test="${currentPage < totalPages}">
                                    <a href="?page=${currentPage + 1}${selectedStatus != null ? '&status='.concat(selectedStatus) : ''}${keyword != null ? '&keyword='.concat(keyword) : ''}">
                                        <i class="fas fa-chevron-right"></i>
                                    </a>
                                </c:if>
                                <c:if test="${currentPage == totalPages}">
                                    <span class="disabled">
                                        <i class="fas fa-chevron-right"></i>
                                    </span>
                                </c:if>
                            </div>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <div class="jobs-table-container">
                            <div class="empty-state">
                                <i class="fas fa-briefcase"></i>
                                <h3>No jobs found</h3>
                                <p>
                                    <c:choose>
                                        <c:when test="${keyword != null}">
                                            No jobs match your search criteria. Try adjusting your filters.
                                        </c:when>
                                        <c:when test="${selectedStatus != null}">
                                            You don't have any jobs with this status yet.
                                        </c:when>
                                        <c:otherwise>
                                            You haven't posted any jobs yet. Start by creating your first job posting!
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                <a href="${pageContext.request.contextPath}/employer/post-job" class="btn btn-primary">
                                    <i class="fas fa-plus"></i> Post Your First Job
                                </a>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>

    <jsp:include page="../common/footer.jsp" />
    
    <script src="${pageContext.request.contextPath}/js/alert.js"></script>
    <script>
        // Check for URL parameters to show alerts
        window.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const success = urlParams.get('success');
            const error = urlParams.get('error');
            
            if (success === 'deleted') {
                showAlert('Job deleted successfully', 'success');
                // Clean up URL
                window.history.replaceState({}, document.title, window.location.pathname);
            } else if (error === 'delete_failed') {
                showAlert('Failed to delete job. Please try again.', 'error');
                window.history.replaceState({}, document.title, window.location.pathname);
            }
        });
        
        function deleteJob(jobId, jobTitle) {
            if (confirm('Are you sure you want to delete "' + jobTitle + '"?\n\nThis action cannot be undone.')) {
                // Show loading alert
                showAlert('Deleting job...', 'info');
                
                fetch('${pageContext.request.contextPath}/employer/delete-job', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'jobId=' + jobId
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showAlert('Job deleted successfully! Refreshing...', 'success');
                        setTimeout(function() {
                            window.location.reload();
                        }, 1000);
                    } else {
                        showAlert('Error: ' + data.message, 'error');
                    }
                })
                .catch(error => {
                    showAlert('Error deleting job. Please try again.', 'error');
                    console.error('Error:', error);
                });
            }
        }
    </script>
</body>
</html>
