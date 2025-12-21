<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Candidate Profile</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="container my-5">
        <h1>Candidate Profile</h1>

        <c:if test="${not empty user}">
            <c:if test="${user.role == 'Candidate'}">
                <c:if test="${not empty candidate}">
                    <!-- Candidate card uses shared styles from style.css -->
                    <div class="candidate-card">
                        <div class="top">
                            <c:if test="${not empty candidate.avatarUrl}">
                                <img src="${candidate.avatarUrl}" alt="Avatar" class="avatar img-thumbnail" />
                            </c:if>
                            <div>
                                <h3 class="mb-1">${candidate.fullName}</h3>
                                <p class="headline mb-1">${candidate.headline}</p>
                                <p class="text-muted mb-0"><strong>Experience:</strong> <c:out value="${candidate.yearsOfExperience}"/> years</p>
                                <p class="text-muted"><strong>Profile visibility:</strong> <c:out value="${candidate.publicProfile}"/></p>
                            </div>
                        </div>

                        <hr />

                        <div class="summary">
                            <h5>About</h5>
                            <p>${candidate.summary}</p>
                        </div>

                        <div class="mt-3">
                            <a class="btn btn-primary" href="${pageContext.request.contextPath}/candidate/profile/edit">Edit Profile</a>
                            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/candidate/applications">My Applications</a>
                        </div>
                    </div>
                </c:if>

                <h3 class="mt-4">Saved Jobs</h3>
                <c:choose>
                    <c:when test="${not empty savedJobs}">
                        <div class="job-listings">
                            <c:forEach items="${savedJobs}" var="job" varStatus="s">
                                <c:if test="${s.index < 3}">
                                    <div class="job-card mb-3">
                                        <div class="job-card-header">
                                            <img src="${job.logoUrl}" alt="${job.companyName}" class="company-logo">
                                            <div class="job-info">
                                                <h3 class="mb-1">
                                                    <a href="${pageContext.request.contextPath}/job/${job.jobId}">${job.title}</a>
                                                </h3>
                                                <p class="company-name mb-0"><i class="fas fa-building"></i> ${job.companyName}</p>
                                            </div>
                                        </div>

                                        <div class="job-card-body">
                                            <div class="job-meta">
                                                <span class="job-location"><i class="fas fa-map-marker-alt"></i> ${job.cityName}</span>
                                                <c:if test="${not empty job.salaryMin && not empty job.salaryMax}">
                                                    <span class="job-salary"><i class="fas fa-dollar-sign"></i>
                                                        <fmt:formatNumber value="${job.salaryMin}" type="number"/> - 
                                                        <fmt:formatNumber value="${job.salaryMax}" type="number"/> ${job.currency}
                                                    </span>
                                                </c:if>
                                            </div>
                                        </div>

                                        <div class="job-card-footer">
                                            <a href="${pageContext.request.contextPath}/job/${job.jobId}" class="btn btn-primary">View Details</a>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>

                            <div class="mt-2">
                                <a href="${pageContext.request.contextPath}/candidate/saved-jobs" class="btn btn-outline-primary">See all saved jobs</a>
                            </div>
                        </div>
                     </c:when>
                     <c:otherwise>
                         <p>You have no saved jobs.</p>
                     </c:otherwise>
                 </c:choose>
            </c:if>

            <c:if test="${user.role != 'Candidate'}">
                <div class="alert alert-warning">This profile page is for candidates only.</div>
            </c:if>
        </c:if>

        <c:if test="${empty user}">
            <div class="text-center my-5">
                <p class="lead">User not found.</p>
            </div>
        </c:if>
    </div>

    <jsp:include page="../common/footer.jsp" />
    <script src="${pageContext.request.contextPath}/js/alert.js"></script>
    <script>
        // Save/unsave handler (same behavior as job listings)
        document.querySelectorAll('.save-job-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const jobId = this.dataset.jobId;
                const isSaved = this.classList.contains('saved');
                const action = isSaved ? 'unsave' : 'save';
                const button = this;

                fetch('${pageContext.request.contextPath}/api/save-job', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
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
                            button.innerHTML = '<i class="far fa-bookmark"></i> Save';
                            button.classList.remove('saved');
                            showInfo('Job removed from saved list', 'Removed');
                        }
                    } else {
                        if (data.message && data.message.includes('login')) {
                            showWarning('Please login to save jobs', 'Login Required');
                            setTimeout(() => {
                                window.location.href = '${pageContext.request.contextPath}/login?redirect=' + encodeURIComponent(window.location.pathname + window.location.search);
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
        });
    </script>
</body>
</html>