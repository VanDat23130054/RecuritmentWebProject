<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job Applications - Recruitment Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="../common/header.jsp" %>

    <div class="container-fluid py-4">
        <div class="row mb-4">
            <div class="col-12">
                <h2 class="mb-3">
                    <i class="fas fa-file-alt me-2"></i>Job Applications
                </h2>
                
                <!-- Alert Container -->
                <div id="alertContainer"></div>

                <!-- Filter Card -->
                <div class="card shadow-sm mb-4">
                    <div class="card-body">
                        <form method="get" action="${pageContext.request.contextPath}/employer/applications" class="row g-3">
                            <div class="col-md-4">
                                <label for="jobFilter" class="form-label">Filter by Job</label>
                                <select class="form-select" id="jobFilter" name="jobId">
                                    <option value="">All Jobs</option>
                                    <c:forEach var="job" items="${recruiterJobs}">
                                        <option value="${job.jobId}" ${param.jobId == job.jobId ? 'selected' : ''}>
                                            ${job.title}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <label for="searchKeyword" class="form-label">Search Candidate</label>
                                <input type="text" class="form-control" id="searchKeyword" name="keyword" 
                                       placeholder="Name or email..." value="${param.keyword}">
                            </div>
                            <div class="col-md-4 d-flex align-items-end gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-search me-1"></i>Search
                                </button>
                                <a href="${pageContext.request.contextPath}/employer/applications" class="btn btn-secondary">
                                    <i class="fas fa-redo me-1"></i>Reset
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Status Tabs -->
                <ul class="nav nav-tabs mb-3" role="tablist">
                    <li class="nav-item">
                        <a class="nav-link ${empty param.status ? 'active' : ''}" 
                           href="${pageContext.request.contextPath}/employer/applications?jobId=${param.jobId}&keyword=${param.keyword}">
                            All <span class="badge bg-secondary">${allCount}</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.status == 'Applied' ? 'active' : ''}" 
                           href="${pageContext.request.contextPath}/employer/applications?status=Applied&jobId=${param.jobId}&keyword=${param.keyword}">
                            Applied <span class="badge bg-primary">${appliedCount}</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.status == 'Under Review' ? 'active' : ''}" 
                           href="${pageContext.request.contextPath}/employer/applications?status=Under Review&jobId=${param.jobId}&keyword=${param.keyword}">
                            Under Review <span class="badge bg-info">${underReviewCount}</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.status == 'Interview Scheduled' ? 'active' : ''}" 
                           href="${pageContext.request.contextPath}/employer/applications?status=Interview Scheduled&jobId=${param.jobId}&keyword=${param.keyword}">
                            Interview <span class="badge bg-warning">${interviewCount}</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.status == 'Rejected' ? 'active' : ''}" 
                           href="${pageContext.request.contextPath}/employer/applications?status=Rejected&jobId=${param.jobId}&keyword=${param.keyword}">
                            Rejected <span class="badge bg-danger">${rejectedCount}</span>
                        </a>
                    </li>
                </ul>

                <!-- Applications Table -->
                <div class="card shadow-sm">
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty applications}">
                                <div class="text-center py-5">
                                    <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                    <p class="text-muted">No applications found</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle">
                                        <thead class="table-light">
                                            <tr>
                                                <th>Candidate</th>
                                                <th>Job Position</th>
                                                <th>Applied Date</th>
                                                <th>Status</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="app" items="${applications}">
                                                <tr>
                                                    <td>
                                                        <div class="d-flex align-items-center">
                                                            <div class="avatar-circle me-2">
                                                                ${app.candidateName.substring(0,1).toUpperCase()}
                                                            </div>
                                                            <div>
                                                                <div class="fw-bold">${app.candidateName}</div>
                                                                <small class="text-muted">${app.candidateEmail}</small>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <div class="fw-semibold">${app.jobTitle}</div>
                                                        <small class="text-muted">${app.companyName}</small>
                                                    </td>
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
                                                            <c:when test="${app.status == 'Offer Extended'}">
                                                                <span class="badge bg-success">${app.status}</span>
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
                                                        <div class="btn-group btn-group-sm" role="group">
                                                            <button type="button" class="btn btn-outline-primary" 
                                                                    onclick="viewApplication(${app.applicationId})"
                                                                    title="View Details">
                                                                <i class="fas fa-eye"></i>
                                                            </button>
                                                            <c:if test="${not empty app.resumeId}">
                                                                <button type="button" class="btn btn-outline-success" 
                                                                        onclick="downloadResume(${app.resumeId})"
                                                                        title="Download Resume">
                                                                    <i class="fas fa-download"></i>
                                                                </button>
                                                            </c:if>
                                                            <div class="btn-group btn-group-sm" role="group">
                                                                <button type="button" class="btn btn-outline-secondary dropdown-toggle" 
                                                                        data-bs-toggle="dropdown" aria-expanded="false"
                                                                        title="Change Status">
                                                                    <i class="fas fa-edit"></i>
                                                                </button>
                                                                <ul class="dropdown-menu">
                                                                    <li><a class="dropdown-item" href="#" onclick="return updateStatus(event, ${app.applicationId}, 'Applied')">Applied</a></li>
                                                                    <li><a class="dropdown-item" href="#" onclick="return updateStatus(event, ${app.applicationId}, 'Under Review')">Under Review</a></li>
                                                                    <li><a class="dropdown-item" href="#" onclick="return updateStatus(event, ${app.applicationId}, 'Interview Scheduled')">Interview Scheduled</a></li>
                                                                    <li><a class="dropdown-item" href="#" onclick="return updateStatus(event, ${app.applicationId}, 'Offer Extended')">Offer Extended</a></li>
                                                                    <li><hr class="dropdown-divider"></li>
                                                                    <li><a class="dropdown-item text-danger" href="#" onclick="return updateStatus(event, ${app.applicationId}, 'Rejected')">Reject</a></li>
                                                                </ul>
                                                            </div>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <!-- Pagination -->
                                <c:if test="${totalPages > 1}">
                                    <nav aria-label="Application pagination" class="mt-3">
                                        <ul class="pagination justify-content-center">
                                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                <a class="page-link" href="?page=${currentPage-1}&status=${param.status}&jobId=${param.jobId}&keyword=${param.keyword}">
                                                    Previous
                                                </a>
                                            </li>
                                            
                                            <c:forEach begin="1" end="${totalPages}" var="i">
                                                <c:choose>
                                                    <c:when test="${i == currentPage}">
                                                        <li class="page-item active">
                                                            <span class="page-link">${i}</span>
                                                        </li>
                                                    </c:when>
                                                    <c:when test="${i == 1 || i == totalPages || (i >= currentPage-2 && i <= currentPage+2)}">
                                                        <li class="page-item">
                                                            <a class="page-link" href="?page=${i}&status=${param.status}&jobId=${param.jobId}&keyword=${param.keyword}">
                                                                ${i}
                                                            </a>
                                                        </li>
                                                    </c:when>
                                                    <c:when test="${i == currentPage-3 || i == currentPage+3}">
                                                        <li class="page-item disabled">
                                                            <span class="page-link">...</span>
                                                        </li>
                                                    </c:when>
                                                </c:choose>
                                            </c:forEach>
                                            
                                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                <a class="page-link" href="?page=${currentPage+1}&status=${param.status}&jobId=${param.jobId}&keyword=${param.keyword}">
                                                    Next
                                                </a>
                                            </li>
                                        </ul>
                                    </nav>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Application Detail Modal -->
    <div class="modal fade" id="applicationModal" tabindex="-1" aria-labelledby="applicationModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="applicationModalLabel">Application Details</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" id="applicationDetails">
                    <div class="text-center py-5">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <%@ include file="../common/footer.jsp" %>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/alert.js"></script>
    <script>
        // View application details
        function viewApplication(applicationId) {
            console.log('viewApplication called:', applicationId);
            
            const modal = new bootstrap.Modal(document.getElementById('applicationModal'));
            modal.show();
            
            const url = '${pageContext.request.contextPath}/employer/applications/detail?id=' + applicationId;
            console.log('Fetching application details from:', url);
            
            fetch(url)
                .then(response => {
                    console.log('Response status:', response.status);
                    return response.json();
                })
                .then(data => {
                    console.log('Response data:', data);
                    if (data.success) {
                        const app = data.application;
                        console.log('Application data:', app);
                        
                        // Format date
                        let appliedDate = 'N/A';
                        if (app.appliedAt) {
                            try {
                                const date = new Date(app.appliedAt);
                                appliedDate = date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
                            } catch (e) {
                                appliedDate = app.appliedAt;
                            }
                        }
                        
                        const summaryHtml = app.candidateSummary
                            ? `<p class="mb-0">${app.candidateSummary}</p>`
                            : '<p class="text-muted mb-0">No candidate summary provided.</p>';
                        
                        const resumeHtml = app.resumeFileUrl
                            ? `<a href="${app.resumeFileUrl}" target="_blank" class="btn btn-outline-primary btn-sm">
                                    <i class="fas fa-file-download me-1"></i>${app.resumeFileName || 'Download Resume'}
                               </a>`
                            : '<span class="text-muted">No resume uploaded</span>';
                        
                        document.getElementById('applicationDetails').innerHTML = `
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <h6 class="text-muted">Candidate Information</h6>
                                    <p class="mb-1"><strong>Name:</strong> \${app.candidateName || 'N/A'}</p>
                                    <p class="mb-1"><strong>Email:</strong> \${app.candidateEmail || 'N/A'}</p>
                                    <p class="mb-1"><strong>Location:</strong> \${app.candidateCity || 'N/A'}</p>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <h6 class="text-muted">Job Information</h6>
                                    <p class="mb-1"><strong>Position:</strong> \${app.jobTitle || 'N/A'}</p>
                                    <p class="mb-1"><strong>Company:</strong> \${app.companyName || 'N/A'}</p>
                                    <p class="mb-1"><strong>Applied:</strong> \${appliedDate}</p>
                                    <p class="mb-1"><strong>Source:</strong> \${app.source || 'N/A'}</p>
                                </div>
                                <div class="col-12 mb-3">
                                    <h6 class="text-muted">Candidate Summary</h6>
                                    <div class="border rounded p-3 bg-light">
                                        \${summaryHtml}
                                    </div>
                                </div>
                                <div class="col-12 mb-3">
                                    <h6 class="text-muted">Cover Letter</h6>
                                    <div class="border rounded p-3 bg-light">
                                        \${app.coverLetter || '<em class="text-muted">No cover letter provided</em>'}
                                    </div>
                                </div>
                                <div class="col-12 mb-3">
                                    <h6 class="text-muted">Resume</h6>
                                    \${resumeHtml}
                                </div>
                                <div class="col-12">
                                    <h6 class="text-muted">Application Status</h6>
                                    <p><span class="badge bg-primary">\${app.status || 'Unknown'}</span></p>
                                </div>
                            </div>
                        `;
                    } else {
                        console.error('Error from server:', data.message);
                        document.getElementById('applicationDetails').innerHTML = `
                            <div class="alert alert-danger">${data.message}</div>
                        `;
                    }
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    document.getElementById('applicationDetails').innerHTML = `
                        <div class="alert alert-danger">Failed to load application details. Error: ${error.message}</div>
                    `;
                });
        }

        // Download resume
        function downloadResume(resumeId) {
            window.location.href = '${pageContext.request.contextPath}/employer/applications/resume?id=' + resumeId;
        }

        // Update application status
        function updateStatus(event, applicationId, newStatus) {
            event.preventDefault();
            
            console.log('updateStatus called:', applicationId, newStatus);
            
            if (!confirm('Change application status to "' + newStatus + '"?')) {
                console.log('User cancelled');
                return false;
            }

            console.log('User confirmed, sending request...');

            // Check if alert functions exist
            if (typeof showInfo === 'function') {
                showInfo('Updating application status...', 'Processing', 0);
            } else {
                console.warn('showInfo function not found');
            }

            const url = '${pageContext.request.contextPath}/employer/applications/updateStatus';
            const body = 'applicationId=' + applicationId + '&status=' + encodeURIComponent(newStatus);
            
            console.log('Fetch URL:', url);
            console.log('Request body:', body);

            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: body
            })
            .then(response => {
                console.log('Response status:', response.status);
                return response.json();
            })
            .then(data => {
                console.log('Response data:', data);
                if (data.success) {
                    if (typeof showSuccess === 'function') {
                        showSuccess(data.message || 'Status updated successfully!', 'Updated');
                    } else {
                        alert(data.message || 'Status updated successfully!');
                    }
                    setTimeout(() => {
                        location.reload();
                    }, 1500);
                } else {
                    if (typeof showError === 'function') {
                        showError(data.message || 'Failed to update status', 'Error');
                    } else {
                        alert('Error: ' + (data.message || 'Failed to update status'));
                    }
                }
            })
            .catch(error => {
                console.error('Fetch error:', error);
                if (typeof showError === 'function') {
                    showError('An error occurred. Please try again.', 'Network Error');
                } else {
                    alert('An error occurred. Please try again.');
                }
            });
            
            return false;
        }

        // Check for success message in URL
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('success') === 'statusUpdated') {
            showSuccess('Application status updated successfully!', 'Success');
            // Clean up URL
            window.history.replaceState({}, document.title, window.location.pathname + '?' + 
                new URLSearchParams([...urlParams].filter(([key]) => key !== 'success')).toString());
        }
    </script>

    <style>
        .avatar-circle {
            width: 40px;
            height: 40px;
            background-color: #6c757d;
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 1.2rem;
        }
        
        .nav-tabs .nav-link {
            color: #495057;
        }
        
        .nav-tabs .nav-link.active {
            font-weight: 600;
        }
        
        .nav-tabs .badge {
            margin-left: 5px;
        }
    </style>
</body>
</html>
