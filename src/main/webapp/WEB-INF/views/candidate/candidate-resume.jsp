<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Resume</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="container my-5">
        <h1>My Resume</h1>

        <c:if test="${not empty user}">
            <c:if test="${user.role == 'Candidate'}">

                <c:if test="${not empty candidate}">
                    <div class="card mb-4">
                        <div class="card-body">
                            <h3 class="card-title">${candidate.fullName}</h3>
                            <p class="card-text">${candidate.headline}</p>
                            <p><strong>Experience:</strong> <c:out value="${candidate.yearsOfExperience}"/> years</p>

                            <c:choose>
                                <c:when test="${not empty candidate.resumeUrl}">
                                    <p>
                                        <strong>Resume:</strong>
                                        <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}${candidate.resumeUrl}" target="_blank">View / Download</a>
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <p>No resume uploaded yet.</p>
                                </c:otherwise>
                            </c:choose>

                        </div>
                    </div>
                </c:if>

                <div>
                    <form action="${pageContext.request.contextPath}/candidate/resume/upload" method="post" enctype="multipart/form-data">
                        <div class="mb-3">
                            <label for="resumeFile" class="form-label">Upload / Replace Resume (PDF or DOC)</label>
                            <input class="form-control" type="file" id="resumeFile" name="resumeFile" accept="application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document">
                        </div>
                        <button type="submit" class="btn btn-primary">Upload</button>
                    </form>
                </div>

            </c:if>
            <c:if test="${user.role != 'Candidate'}">
                <div class="alert alert-warning">This page is for candidates only.</div>
            </c:if>
        </c:if>

        <c:if test="${empty user}">
            <div class="text-center my-5">
                <p class="lead">Please log in to manage your resume.</p>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Login</a>
            </div>
        </c:if>

    </div>

    <jsp:include page="../common/footer.jsp" />
</body>
</html>
