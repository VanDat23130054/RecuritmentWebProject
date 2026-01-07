<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
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
            <div class="container my-5">
                <h1>My Saved Jobs</h1>

                <c:choose>
                    <c:when test="${not empty jobs}">
                        <div class="job-listings">
                            <c:forEach items="${jobs}" var="job">
                                <div class="job-card mb-3">
                                    <div class="job-card-header">
                                        <img src="${job.logoUrl}" alt="${job.companyName}" class="company-logo">
                                        <div class="job-info">
                                            <h3 class="mb-1"><a href="${pageContext.request.contextPath}/job/${job.jobId}">${job.title}</a></h3>
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
                                        <a href="${pageContext.request.contextPath}/job/${job.jobId}" class="btn btn-primary">View Details</a>
                                        <c:if test="${empty sessionScope.user || sessionScope.user.role == 'Candidate'}">
                                            <c:choose>
                                                <c:when test="${job.isSaved}">
                                                    <button class="btn btn-secondary save-job-btn saved" data-job-id="${job.jobId}"><i class="fas fa-bookmark"></i> Saved</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button class="btn btn-secondary save-job-btn" data-job-id="${job.jobId}"><i class="far fa-bookmark"></i> Save</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
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

                    </c:when>
                    <c:otherwise>
                        <div class="text-center my-5">
                            <p class="lead">You have no saved jobs.</p>
                            <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary">Browse Jobs</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>

    <jsp:include page="../common/footer.jsp" />
</body>
</html>