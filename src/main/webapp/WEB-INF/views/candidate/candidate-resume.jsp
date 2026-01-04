<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Resume</title>
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
            <div class="container my-4">

                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h1 class="mb-0">Resumes</h1>
                    <div class="d-flex gap-2 align-items-center">
                        <a href="${pageContext.request.contextPath}/candidate/uploadResume" class="btn btn-primary">
                            <i class="fas fa-upload me-1"></i> Upload Resume
                        </a>
                    </div>
                 </div>

                <!-- Full-width centered search bar between title and list -->
                <div class="row mb-3">
                    <div class="col-12">
                        <div class="input-group">
                            <input id="resumeSearch" type="search" class="form-control" placeholder="Search resumes by filename..." aria-label="Search resumes">
                            <button id="resumeSearchBtn" class="btn btn-primary" type="button"><i class="fas fa-search me-1"></i>Search</button>
                        </div>
                    </div>
                </div>

                <c:if test="${not empty user}">
                    <c:if test="${user.role == 'Candidate'}">

                        <div class="mb-4">
                            <h4 class="mb-2">Your uploaded resumes</h4>

                            <c:if test="${not empty resumes}">
                                <ul id="resumeList" class="list-group">
                                    <c:forEach var="r" items="${resumes}">
                                        <li class="list-group-item d-flex justify-content-between align-items-center">
                                            <div class="resume-meta">
                                                <strong class="resume-filename"><c:out value="${r.fileName}"/></strong>
                                                <div><small>Uploaded: <fmt:formatDate value="${r.uploadedAt}" pattern="yyyy-MM-dd HH:mm"/></small></div>
                                            </div>
                                            <div>
                                                <c:choose>
                                                    <c:when test="${not empty r.fileUrl}">
                                                        <c:choose>
                                                            <c:when test="${fn:startsWith(r.fileUrl, '/')}">
                                                                <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}${r.fileUrl}" target="_blank">View / Download</a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a class="btn btn-outline-primary btn-sm" href="${r.fileUrl}" target="_blank">View / Download</a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/employer/applications/resume?id=${r.resumeId}" target="_blank">View / Download</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:if>

                            <c:if test="${empty resumes}">
                                <div class="text-center my-4">
                                    <p>No resumes uploaded yet.</p>
                                    <a href="${pageContext.request.contextPath}/candidate/uploadResume" class="btn btn-primary">Upload Resume</a>
                                </div>
                            </c:if>
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
        </main>
    </div>

    <jsp:include page="../common/footer.jsp" />

    <script>
        // Client-side filter for resume list
        (function() {
            const search = document.getElementById('resumeSearch');
            const list = document.getElementById('resumeList');
            const btn = document.getElementById('resumeSearchBtn');
            if (!search || !list) return;

            function applyFilter() {
                const q = search.value.trim().toLowerCase();
                const items = list.querySelectorAll('li');
                items.forEach(function(li) {
                    const filenameEl = li.querySelector('.resume-filename');
                    const text = filenameEl ? filenameEl.textContent.trim().toLowerCase() : '';
                    li.style.display = text.indexOf(q) === -1 ? 'none' : '';
                });
            }

            search.addEventListener('input', applyFilter);
            if (btn) {
                btn.addEventListener('click', function (e) {
                    e.preventDefault();
                    applyFilter();
                });
            }
        })();
    </script>
</body>
</html>