<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Candidate Profile</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/employer-dashboard.css">
</head>
<body>
	<jsp:include page="../common/header.jsp" />

	<div class="dashboard-container">
		<jsp:include page="_sidebar.jsp" />

		<main class="dashboard-main">
			<div class="container my-5">
				<h1>Candidate Profile</h1>

				<c:if test="${not empty user}">
					<c:if test="${user.role == 'Candidate'}">
						<c:if test="${not empty candidate}">
							<!-- Candidate card uses shared styles from style.css -->
							<div class="candidate-card">
								<div class="top">
									<c:if test="${not empty candidate.avatarUrl}">
										<img src="${candidate.avatarUrl}" alt="Avatar"
											class="avatar img-thumbnail" />
									</c:if>
									<div class="candidate-info">
										<h3 class="mb-1">${candidate.fullName}</h3>
										<p class="headline mb-1">${candidate.headline}</p>
										<p class="text-muted mb-0">
											<strong>Experience:</strong>
											<c:out value="${candidate.yearsOfExperience}" />
											years
										</p>
										<p class="text-muted">
											<strong>Profile visibility:</strong>
											<c:out value="${candidate.publicProfile}" />
										</p>
									</div>
									<!-- edit button opens modal containing the edit form -->
									<button type="button" class="btn btn-primary edit-profile-btn"
										data-bs-toggle="modal" data-bs-target="#editCandidateModal">Edit
										Profile</button>
								</div>

								<hr />

								<div class="summary">
									<h5>About</h5>
									<p>${candidate.summary}</p>
								</div>

								<!-- Edit Candidate Modal -->
								<div class="modal fade" id="editCandidateModal" tabindex="-1"
									aria-labelledby="editCandidateModalLabel" aria-hidden="true">
									<div class="modal-dialog modal-lg modal-dialog-centered">
										<div class="modal-content">
											<div class="modal-header">
												<h5 class="modal-title" id="editCandidateModalLabel">Edit
													Profile</h5>
												<button type="button" class="btn-close"
													data-bs-dismiss="modal" aria-label="Close"></button>
											</div>
											<div class="modal-body p-0">
												<div id="editCandidateModalBody" class="p-3">Loading…
												</div>
											</div>
										</div>
									</div>
								</div>

							</div>

							<!-- buttons removed from bottom; edit is now top-right -->
			</div>
			<!-- end candidate-card -->
			</c:if>
			<!-- end not empty candidate -->
			</c:if>
			<!-- end role == Candidate -->

			<c:if test="${user.role != 'Candidate'}">
				<div class="alert alert-warning">This profile page is for
					candidates only.</div>
			</c:if>
			</c:if>
			<!-- end not empty user -->

			<c:if test="${empty user}">
				<div class="text-center my-5">
					<p class="lead">User not found.</p>
				</div>
			</c:if>

			<script src="${pageContext.request.contextPath}/js/alert.js"></script>
			<!-- Bootstrap 5 JS (bundle includes Popper) -->
			<script
				src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
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

         // Load edit form via AJAX into the modal
         const editCandidateModal = document.getElementById('editCandidateModal');
         editCandidateModal.addEventListener('show.bs.modal', function (event) {
             const modalBody = editCandidateModal.querySelector('#editCandidateModalBody');
             modalBody.innerHTML = '<div class="py-4"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div></div>';

             const url = '${pageContext.request.contextPath}/candidate/profile/edit?partial=true';
             fetch(url)
                 .then(response => {
                     if (!response.ok) throw new Error('Network response was not ok');
                     return response.text();
                 })
                 .then(html => {
                     modalBody.innerHTML = html;
                 })
                 .catch(error => {
                     console.error('Error loading edit form:', error);
                     modalBody.innerHTML = '<div class="alert alert-danger">Failed to load form. Please try again later.</div>';
                 });
         });
     </script>
</body>
</html>