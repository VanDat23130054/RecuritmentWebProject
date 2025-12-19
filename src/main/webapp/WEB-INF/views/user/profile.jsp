<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>User Profile</title>
<!-- Bootstrap 5 CSS (matching other pages) -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- Font Awesome -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <!-- Header -->
    <jsp:include page="../common/header.jsp" />

    <div class="container my-5">
        <h1>User Profile</h1>

        <c:if test="${not empty user}">
            <div class="profile-card">
                <p><strong>Email:</strong> ${user.email}</p>
                <p><strong>Role:</strong> ${user.role}</p>
                <p><strong>Joined:</strong> <c:out value="${user.createdAt}"/></p>
            </div>

            <!-- Recruiter view -->
            <c:if test="${user.role == 'Recruiter'}">
                <h2>Your Posted Jobs</h2>
                <c:choose>
                    <c:when test="${not empty jobs}">
                        <div class="job-list">
                            <c:forEach var="job" items="${jobs}">
                                <div class="job-item">
                                    <h3><a href="${pageContext.request.contextPath}/job/${job.slug}">${job.title}</a></h3>
                                    <p><strong>Company:</strong> <a href="${pageContext.request.contextPath}/company/${job.companyId}">${job.companyName}</a></p>
                                    <p><strong>Location:</strong> ${job.cityName}</p>
                                    <p>
                                        <strong>Salary:</strong>
                                        <c:choose>
                                            <c:when test="${job.salaryMin != null && job.salaryMax != null}">
                                                ${job.salaryMin} - ${job.salaryMax} ${job.currency}
                                            </c:when>
                                            <c:otherwise>
                                                Not specified
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    <p><strong>Posted:</strong> <c:out value="${job.postedAt}"/></p>
                                    <div class="job-actions">
                                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/job/edit/${job.jobId}">Edit</a>
                                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/job/${job.jobId}">View</a>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p>You haven't posted any jobs yet. <a href="${pageContext.request.contextPath}/job/create">Post a job</a></p>
                    </c:otherwise>
                </c:choose>
            </c:if>

            <!-- Candidate view -->
            <c:if test="${user.role == 'Candidate'}">
                <h2>Candidate Profile</h2>
                <c:if test="${not empty candidate}">
                    <div class="candidate-card">
                        <c:if test="${not empty candidate.avatarUrl}">
                            <img src="${candidate.avatarUrl}" alt="Avatar" class="avatar img-thumbnail" style="max-width:120px;" />
                        </c:if>
                        <h3>${candidate.fullName}</h3>
                        <p class="headline">${candidate.headline}</p>
                        <p class="summary">${candidate.summary}</p>
                        <p><strong>Experience:</strong> <c:out value="${candidate.yearsOfExperience}"/> years</p>
                        <p><strong>Profile visibility:</strong> <c:out value="${candidate.publicProfile}"/></p>
                    </div>
                </c:if>

                <h3>Saved Jobs</h3>
                <c:choose>
                    <c:when test="${not empty savedJobs}">
                        <ul class="saved-jobs">
                            <c:forEach var="s" items="${savedJobs}">
                                <li>
                                    <a href="${pageContext.request.contextPath}/job/${s.jobId}">Job #${s.jobId}</a>
                                    <small> - saved at <fmt:formatDate value="${s.savedAt}" pattern="yyyy-MM-dd HH:mm"/></small>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p>You have no saved jobs.</p>
                    </c:otherwise>
                </c:choose>
            </c:if>

        </c:if>

        <c:if test="${empty user}">
            <div class="text-center my-5">
                <p class="lead">User not found.</p>
            </div>
        </c:if>
    </div>

    <!-- Footer -->
    <jsp:include page="../common/footer.jsp" />
</body>
</html>