<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Saved Jobs</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-dashboard.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="_sidebar.jsp" />

        <main class="dashboard-main">
            <div class="dashboard-header">
                <div class="header-content">
                    <h1><i class="fas fa-bookmark me-2"></i>My Saved Jobs</h1>
                    <p class="text-muted">Jobs you've saved for later review</p>
                </div>
            </div>

            <div class="dashboard-card">
                <div class="card-body">
                <c:choose>
                    <c:when test="${not empty jobs}">
                        <div class="job-listings">
                            <c:forEach items="${jobs}" var="job">
                                <div class="job-card mb-3">
                                    <div class="job-card-header">
                                        <c:choose>
                                            <c:when test="${not empty job.logoUrl}">
                                                <img src="${job.logoUrl}" alt="${job.companyName}" class="company-logo">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="company-logo bg-secondary d-flex align-items-center justify-content-center">
                                                    <i class="fas fa-building text-white"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                        <div class="job-info">
                                            <h3 class="mb-1"><a href="${pageContext.request.contextPath}/job/${job.jobId}">${job.title}</a></h3>
                                            <p class="company-name mb-0"><i class="fas fa-building"></i> ${job.companyName}</p>
                                        </div>
                                    </div>

                                    <div class="job-card-body">
                                        <div class="job-meta">
                                            <c:if test="${not empty job.cityName}">
                                                <span class="job-location"><i class="fas fa-map-marker-alt"></i> ${job.cityName}</span>
                                            </c:if>
                                            <c:if test="${not empty job.salaryMin && not empty job.salaryMax}">
                                                <span class="job-salary"><i class="fas fa-dollar-sign"></i>
                                                    <fmt:formatNumber value="${job.salaryMin}" type="number"/> - 
                                                    <fmt:formatNumber value="${job.salaryMax}" type="number"/> ${job.currency}
                                                </span>
                                            </c:if>
                                        </div>

                                        <c:if test="${not empty job.skillsList}">
                                            <div class="job-skills mt-2">
                                                <c:forEach items="${job.skillsList}" var="skill" varStatus="status">
                                                    <c:if test="${status.index < 5}">
                                                        <span class="skill-tag">${skill.Name}</span>
                                                    </c:if>
                                                </c:forEach>
                                                <c:if test="${job.skillsList.size() > 5}">
                                                    <span class="skill-tag">+${job.skillsList.size() - 5} more</span>
                                                </c:if>
                                            </div>
                                        </c:if>
                                    </div>

                                    <div class="job-card-footer">
                                        <a href="${pageContext.request.contextPath}/job/${job.jobId}" class="btn btn-primary btn-sm">View Details</a>
                                        <button class="btn btn-outline-danger btn-sm save-job-btn saved" data-job-id="${job.jobId}">
                                            <i class="fas fa-bookmark"></i> Remove
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <!-- include save-job JS -->
                        <script src="${pageContext.request.contextPath}/js/alert.js"></script>
                        <script>
                            document.querySelectorAll('.save-job-btn').forEach(btn => {
                                btn.addEventListener('click', function() {
                                    const jobId = this.dataset.jobId;
                                    const button = this;

                                    // Since we're on saved jobs page, action is always unsave
                                    fetch('${pageContext.request.contextPath}/api/save-job', {
                                        method: 'POST',
                                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                                        body: 'jobId=' + jobId + '&action=unsave'
                                    })
                                    .then(response => response.json())
                                    .then(data => {
                                        if (data.success) {
                                            // Remove the job card from the page
                                            const jobCard = button.closest('.job-card');
                                            if (jobCard) {
                                                jobCard.remove();
                                            }
                                            // Check if there are no more jobs
                                            const remaining = document.querySelectorAll('.job-card');
                                            if (remaining.length === 0) {
                                                location.reload();
                                            }
                                        } else {
                                            alert('Error: ' + (data.message || 'Failed to remove job'));
                                        }
                                    })
                                    .catch(error => {
                                        console.error('Error:', error);
                                        alert('An error occurred. Please try again.');
                                    });
                                });
                            });
                        </script>

                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-5">
                            <div class="mb-4">
                                <i class="fas fa-bookmark fa-4x text-muted"></i>
                            </div>
                            <h4>No saved jobs yet</h4>
                            <p class="text-muted mb-4">Start browsing jobs and save the ones you're interested in!</p>
                            <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary">
                                <i class="fas fa-search me-2"></i>Browse Jobs
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
                </div>
            </div>
        </main>
    </div>

    <jsp:include page="../common/footer.jsp" />
</body>
</html>