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
</body>
</html>
