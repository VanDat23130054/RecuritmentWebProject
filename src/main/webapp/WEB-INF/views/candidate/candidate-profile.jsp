<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>My Profile - JobHunter</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-dashboard.css">
</head>
<body>
	<jsp:include page="../common/header.jsp" />

	<div class="dashboard-container">
		<!-- Sidebar Toggle Button (Mobile/Tablet) -->
		<button class="sidebar-toggle" aria-label="Toggle Sidebar">
			<i class="fas fa-bars"></i>
		</button>
		<jsp:include page="_sidebar.jsp" />
		<main class="dashboard-main">
			<div class="dashboard-header">
				<div class="header-content">
					<h1><i class="fas fa-user me-2"></i>My Profile</h1>
					<p class="text-muted">Manage your professional information</p>
				</div>
			</div>

			<c:if test="${not empty candidate}">
				<div class="dashboard-card">
					<div class="card-header">
						<h3>Profile Overview</h3>
					</div>
					<div class="card-body">
						<div class="row">
							<div class="col-md-3 text-center mb-3 mb-md-0">
								<c:choose>
									<c:when test="${not empty candidate.avatarUrl}">
										<img src="${candidate.avatarUrl}" alt="Avatar" class="rounded-circle" style="width: 150px; height: 150px; object-fit: cover;">
									</c:when>
									<c:otherwise>
										<div class="rounded-circle bg-primary d-flex align-items-center justify-content-center mx-auto" style="width: 150px; height: 150px;">
											<i class="fas fa-user text-white" style="font-size: 4rem;"></i>
										</div>
									</c:otherwise>
								</c:choose>
								<div class="mt-3">
									<c:if test="${candidate.publicProfile}">
										<span class="badge bg-success"><i class="fas fa-globe me-1"></i>Public</span>
									</c:if>
									<c:if test="${!candidate.publicProfile}">
										<span class="badge bg-secondary"><i class="fas fa-lock me-1"></i>Private</span>
									</c:if>
								</div>
							</div>
							<div class="col-md-9">
								<h4>${candidate.fullName}</h4>
								<c:if test="${not empty candidate.headline}">
									<p class="text-primary mb-2">${candidate.headline}</p>
								</c:if>
								<c:if test="${not empty candidate.summary}">
									<p class="text-muted mb-3">${candidate.summary}</p>
								</c:if>
								<div class="profile-stats">
									<div><strong>Experience:</strong> 
										<c:choose>
											<c:when test="${not empty candidate.yearsOfExperience}">${candidate.yearsOfExperience} years</c:when>
											<c:otherwise><span class="text-muted">Not specified</span></c:otherwise>
										</c:choose>
									</div>
									<div><strong>Location:</strong> 
										<c:choose>
											<c:when test="${not empty candidate.cityName}">${candidate.cityName}</c:when>
											<c:otherwise><span class="text-muted">Not specified</span></c:otherwise>
										</c:choose>
									</div>
										<div><strong>Email:</strong> ${sessionScope.userEmail}</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="dashboard-card mt-4">
					<div class="card-header">
						<h3><i class="fas fa-edit me-2"></i>Edit Information</h3>
					</div>
					<div class="card-body">
						<form action="${pageContext.request.contextPath}/candidate/profile" method="POST">
							<div class="row mb-3">
								<div class="col-md-6">
									<label class="form-label">Full Name</label>
									<input type="text" class="form-control" name="fullName" value="${candidate.fullName}" required>
								</div>
								<div class="col-md-6">
									<label class="form-label">Professional Headline</label>
									<input type="text" class="form-control" name="headline" value="${candidate.headline}" placeholder="e.g., Senior Java Developer">
								</div>
							</div>
							<div class="row mb-3">
								<div class="col-md-6">
									<label class="form-label">Years of Experience</label>
									<input type="number" class="form-control" name="yearsOfExperience" value="${candidate.yearsOfExperience}" step="0.5" min="0">
								</div>
								<div class="col-md-6">
									<label class="form-label">Location</label>
									<input type="text" class="form-control" value="${candidate.cityName}" readonly style="background-color: #f0f0f0;">
								</div>
							</div>
							<div class="mb-3">
								<label class="form-label">Professional Summary</label>
								<textarea class="form-control" name="summary" rows="4" placeholder="Tell recruiters about yourself...">${candidate.summary}</textarea>
							</div>
							<div class="mb-3 form-check">
								<input type="checkbox" class="form-check-input" id="publicProfile" name="publicProfile" <c:if test="${candidate.publicProfile}">checked</c:if>>
								<label class="form-check-label" for="publicProfile">Make profile public</label>
							</div>
							<div class="d-flex gap-2">
								<button type="submit" class="btn btn-primary"><i class="fas fa-save me-2"></i>Save Changes</button>
								<a href="${pageContext.request.contextPath}/candidate/dashboard" class="btn btn-outline-secondary"><i class="fas fa-times me-2"></i>Cancel</a>
							</div>
						</form>
					</div>
				</div>
			</c:if>
		</main>
	</div>

	<jsp:include page="../common/footer.jsp" />
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/alert.js"></script>
	<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>