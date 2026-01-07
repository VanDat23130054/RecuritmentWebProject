<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Apply - ${job.title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome (icons used in header) -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css"/>
</head>
<body>
<jsp:include page="../common/header.jsp" />

<div class="container my-5">
    <h2>Apply to: ${job.title}</h2>
    <p class="text-muted">Company: ${job.companyName}</p>

    <form method="post" action="${pageContext.request.contextPath}/apply/${jobId}">
        <div class="mb-3">
            <label class="form-label">Select Resume</label>
            <c:if test="${empty resumes}">
                <p class="text-muted">You don't have any uploaded resumes. You can upload one from your profile.</p>
            </c:if>
            <c:if test="${not empty resumes}">
                <select name="resumeId" class="form-select">
                    <option value="">-- Use no resume --</option>
                    <c:forEach var="r" items="${resumes}">
                        <option value="${r.resumeId}">${r.fileName} <c:if test="${r.isPrimary}">(Primary)</c:if></option>
                    </c:forEach>
                </select>
            </c:if>
        </div>

        <div class="mb-3">
            <label class="form-label">Cover Letter (optional)</label>
            <textarea name="coverLetter" class="form-control" rows="8" placeholder="Write a short cover letter to the employer..."></textarea>
        </div>

        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/job/${jobId}" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">Apply</button>
        </div>
    </form>
</div>

<jsp:include page="../common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>