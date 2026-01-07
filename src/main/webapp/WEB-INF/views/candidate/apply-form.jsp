<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Apply - ${job.title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-dashboard.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css"/>
</head>
<body>
<jsp:include page="../common/header.jsp" />

<div class="dashboard-container">
    <jsp:include page="_sidebar.jsp" />
    <main class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-content">
                <h1><i class="fas fa-briefcase me-2"></i>Apply to Job</h1>
                <p class="text-muted">${job.title} at ${job.companyName}</p>
            </div>
        </div>

        <div class="dashboard-card">
            <div class="card-header">
                <h3>Job Application Form</h3>
            </div>
            <div class="card-body">
                <div class="mb-4 p-3 bg-light rounded">
                    <h5>${job.title}</h5>
                    <p class="mb-1"><strong><i class="fas fa-building me-2"></i>Company:</strong> ${job.companyName}</p>
                    <p class="mb-1"><strong><i class="fas fa-map-marker-alt me-2"></i>Location:</strong> ${job.location}</p>
                    <c:if test="${not empty job.salary}">
                        <p class="mb-0"><strong><i class="fas fa-dollar-sign me-2"></i>Salary:</strong> ${job.salary}</p>
                    </c:if>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/apply/${jobId}">
                    <div class="mb-3">
                        <label class="form-label"><i class="fas fa-file me-2"></i>Select Resume</label>
                        <c:if test="${empty resumes}">
                            <div class="alert alert-info">
                                <i class="fas fa-info-circle me-2"></i>
                                You don't have any uploaded resumes. <a href="${pageContext.request.contextPath}/candidate/uploadResume" class="alert-link">Upload one now</a>
                            </div>
                        </c:if>
                        <c:if test="${not empty resumes}">
                            <select name="resumeId" class="form-select">
                                <option value="">-- Without Resume --</option>
                                <c:forEach var="r" items="${resumes}">
                                    <option value="${r.resumeId}">${r.fileName} <c:if test="${r.isPrimary}">(Primary)</c:if></option>
                                </c:forEach>
                            </select>
                        </c:if>
                    </div>

                    <div class="mb-3">
                        <label class="form-label"><i class="fas fa-pen me-2"></i>Cover Letter (Optional)</label>
                        <textarea name="coverLetter" class="form-control" rows="6" placeholder="Tell the employer why you're a great fit for this role..."></textarea>
                    </div>

                    <div class="d-flex gap-2">
                        <a href="${pageContext.request.contextPath}/job/${jobId}" class="btn btn-outline-secondary"><i class="fas fa-arrow-left me-2"></i>Back to Job</a>
                        <button type="submit" class="btn btn-primary"><i class="fas fa-paper-plane me-2"></i>Submit Application</button>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>

<jsp:include page="../common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>