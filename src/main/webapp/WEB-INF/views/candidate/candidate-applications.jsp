<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Applications - Recruitment Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-dashboard.css">
</head>
<body>
<jsp:include page="../common/header.jsp" />

<!-- New dashboard layout: sidebar + main -->
<div class="dashboard-container">
    <jsp:include page="_sidebar.jsp" />

    <!-- Main Content -->
    <main class="dashboard-main">
        <div class="container-fluid py-4">
            <div id="alertContainer"></div>

            <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
                <div>
                    <h2 class="mb-1"><i class="fas fa-file-alt me-2"></i>My Applications</h2>
                    <p class="text-muted mb-0">Track your job applications and manage them from here</p>
                </div>
            </div>

            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success">${sessionScope.message}</div>
                <c:remove var="message" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-danger">${sessionScope.error}</div>
                <c:remove var="error" scope="session" />
            </c:if>

            <!-- Status Tabs -->
            <ul class="nav nav-tabs mb-3" role="tablist">
                <li class="nav-item">
                    <a class="nav-link ${empty selectedStatus ? 'active' : ''}" href="${pageContext.request.contextPath}/candidate/applications">All</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${selectedStatus == 'Applied' ? 'active' : ''}" href="${pageContext.request.contextPath}/candidate/applications?status=Applied">Applied</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${selectedStatus == 'Under Review' ? 'active' : ''}" href="${pageContext.request.contextPath}/candidate/applications?status=Under Review">Under Review</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${selectedStatus == 'Interview Scheduled' ? 'active' : ''}" href="${pageContext.request.contextPath}/candidate/applications?status=Interview Scheduled">Interview</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${selectedStatus == 'Rejected' ? 'active' : ''}" href="${pageContext.request.contextPath}/candidate/applications?status=Rejected">Rejected</a>
                </li>
            </ul>

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
                                            <th>Job</th>
                                            <th>Company</th>
                                            <th>Applied Date</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="app" items="${applications}">
                                            <tr id="app-row-${app.applicationId}">
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/job/${app.jobId}" class="fw-semibold">${app.jobTitle}</a>
                                                </td>
                                                <td>${app.companyName}</td>
                                                <td><fmt:formatDate value="${app.appliedAt}" pattern="MMM dd, yyyy"/></td>
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
                                                        <button type="button" class="btn btn-outline-primary" onclick="viewApplication(${app.applicationId})" title="View Details">
                                                            <i class="fas fa-eye"></i>
                                                        </button>
                                                        <c:if test="${not empty app.resumeId}">
                                                            <button type="button" class="btn btn-outline-success" onclick="downloadResume(${app.resumeId})" title="Download Resume">
                                                                <i class="fas fa-download"></i>
                                                            </button>
                                                        </c:if>
                                                        <c:if test="${app.status == 'Applied' || app.status == 'Under Review'}">
                                                            <form class="withdraw-form d-inline" data-app-id="${app.applicationId}" method="post" action="${pageContext.request.contextPath}/candidate/applications">
                                                                <input type="hidden" name="action" value="withdraw" />
                                                                <input type="hidden" name="applicationId" value="${app.applicationId}" />
                                                                <button type="submit" class="btn btn-sm btn-danger withdraw-btn">Withdraw</button>
                                                            </form>
                                                        </c:if>
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
                                            <a class="page-link" href="?page=${currentPage-1}&status=${selectedStatus}">Previous</a>
                                        </li>
                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                            <c:choose>
                                                <c:when test="${i == currentPage}">
                                                    <li class="page-item active"><span class="page-link">${i}</span></li>
                                                </c:when>
                                                <c:otherwise>
                                                    <li class="page-item"><a class="page-link" href="?page=${i}&status=${selectedStatus}">${i}</a></li>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="?page=${currentPage+1}&status=${selectedStatus}">Next</a>
                                        </li>
                                    </ul>
                                </nav>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
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
    </main>
</div>

<jsp:include page="../common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/alert.js"></script>
<script>
    // View application details (candidate endpoint)
    function viewApplication(applicationId) {
        const modal = new bootstrap.Modal(document.getElementById('applicationModal'));
        modal.show();

        const url = '${pageContext.request.contextPath}/candidate/application/detail?id=' + applicationId;
        fetch(url)
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const app = data.application;

                    // Format applied date nicely
                    let appliedDate = 'N/A';
                    if (app.appliedAt) {
                        try {
                            const date = new Date(app.appliedAt);
                            appliedDate = date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
                        } catch (e) {
                            appliedDate = app.appliedAt;
                        }
                    }

                    const coverHtml = app.coverLetter ? `<div class="border rounded p-3 bg-light">${app.coverLetter}</div>` : '<p class="text-muted">No cover letter provided.</p>';
                    const resumeHtml = app.resumeFileUrl ? `<a href="${pageContext.request.contextPath}${app.resumeFileUrl}" target="_blank" class="btn btn-outline-primary btn-sm"><i class="fas fa-file-download me-1"></i>${app.resumeFileName || 'Download Resume'}</a>` : '<span class="text-muted">No resume uploaded</span>';

                    document.getElementById('applicationDetails').innerHTML = `
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <h6 class="text-muted">Job Information</h6>
                                <p class="mb-1"><strong>Position:</strong> ${app.jobTitle || 'N/A'}</p>
                                <p class="mb-1"><strong>Company:</strong> ${app.companyName || 'N/A'}</p>
                                <p class="mb-1"><strong>Applied:</strong> ${appliedDate}</p>
                            </div>
                            <div class="col-12 mb-3">
                                <h6 class="text-muted">Cover Letter</h6>
                                ${coverHtml}
                            </div>
                            <div class="col-12 mb-3">
                                <h6 class="text-muted">Resume</h6>
                                ${resumeHtml}
                            </div>
                            <div class="col-12">
                                <h6 class="text-muted">Recruiter Note</h6>
                                <div class="border rounded p-3 bg-light">${app.recruiterNote || '<em class="text-muted">No notes</em>'}</div>
                            </div>
                        </div>
                    `;
                } else {
                    document.getElementById('applicationDetails').innerHTML = `<div class="alert alert-danger">${data.message}</div>`;
                }
            })
            .catch(err => {
                console.error('Detail fetch error', err);
                document.getElementById('applicationDetails').innerHTML = `<div class="alert alert-danger">Failed to load application details.</div>`;
            });
    }

    // Download resume using hidden iframe to preserve session
    function downloadResume(resumeId) {
        if (!resumeId) {
            alert('Resume ID not found');
            return;
        }
        let iframe = document.getElementById('downloadFrame');
        if (!iframe) {
            iframe = document.createElement('iframe');
            iframe.id = 'downloadFrame';
            iframe.style.display = 'none';
            document.body.appendChild(iframe);
        }
        // Use employer resume endpoint which serves files
        iframe.src = '${pageContext.request.contextPath}/employer/applications/resume?id=' + resumeId;
    }

    // Withdraw handler: attach AJAX to forms
    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('.withdraw-form').forEach(function(form) {
            form.addEventListener('submit', function (e) {
                e.preventDefault();
                var appId = form.getAttribute('data-app-id');
                var btn = form.querySelector('.withdraw-btn');
                if (!confirm('Are you sure you want to withdraw this application?')) return;
                btn.disabled = true; btn.textContent = 'Withdrawing...';

                var formData = new FormData(form);
                fetch(form.action, {
                    method: 'POST',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest',
                        'Accept': 'application/json'
                    },
                    body: formData,
                    credentials: 'same-origin'
                })
                .then(function(response) {
                    return response.json().catch(function() { return { success: false, message: 'Invalid server response' }; });
                })
                .then(function(json) {
                    if (json && json.success) {
                        var statusCell = document.querySelector('#app-row-' + appId + ' .badge') || document.querySelector('.app-status[data-app-id="' + appId + '"]');
                        if (statusCell) {
                            // replace status badge or cell content
                            if (statusCell.classList.contains('badge')) {
                                statusCell.className = 'badge bg-secondary';
                                statusCell.textContent = json.newStatus || 'Withdrawn';
                            } else {
                                statusCell.textContent = json.newStatus || 'Withdrawn';
                            }
                        }
                        form.parentNode.innerHTML = '-';
                    } else {
                        alert(json && json.message ? json.message : 'Unable to withdraw application');
                        btn.disabled = false; btn.textContent = 'Withdraw';
                    }
                })
                .catch(function(err) {
                    console.error('Withdraw error', err);
                    alert('Server error while withdrawing application');
                    btn.disabled = false; btn.textContent = 'Withdraw';
                });
            });
        });
    });
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