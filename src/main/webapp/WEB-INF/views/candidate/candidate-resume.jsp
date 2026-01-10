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
        <!-- Sidebar Toggle Button (Mobile/Tablet) -->
        <button class="sidebar-toggle" aria-label="Toggle Sidebar">
            <i class="fas fa-bars"></i>
        </button>
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

                <div class="mb-4">
                            <h4 class="mb-2">Your uploaded resumes</h4>

                            <c:if test="${not empty resumes}">
                                <ul id="resumeList" class="list-group">
                                    <c:forEach var="r" items="${resumes}">
                                        <li class="list-group-item d-flex justify-content-between align-items-center" data-resume-id="${r.resumeId}">
                                            <div class="resume-meta d-flex align-items-center gap-2">
                                                <c:if test="${r.isPrimary}">
                                                    <span class="badge bg-success" title="Primary Resume"><i class="fas fa-star"></i></span>
                                                </c:if>
                                                <div>
                                                    <strong class="resume-filename"><c:out value="${r.fileName}"/></strong>
                                                    <div><small class="text-muted">Uploaded: <fmt:formatDate value="${r.uploadedAt}" pattern="yyyy-MM-dd HH:mm"/></small></div>
                                                </div>
                                            </div>
                                            <div class="d-flex gap-2 align-items-center">
                                                <c:choose>
                                                    <c:when test="${not empty r.fileUrl}">
                                                        <c:choose>
                                                            <c:when test="${fn:startsWith(r.fileUrl, '/')}">
                                                                <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}${r.fileUrl}" target="_blank" title="View/Download">
                                                                    <i class="fas fa-download"></i>
                                                                </a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a class="btn btn-outline-primary btn-sm" href="${r.fileUrl}" target="_blank" title="View/Download">
                                                                    <i class="fas fa-download"></i>
                                                                </a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/employer/applications/resume?id=${r.resumeId}" target="_blank" title="View/Download">
                                                            <i class="fas fa-download"></i>
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>
                                                <button class="btn btn-outline-secondary btn-sm rename-btn" data-resume-id="${r.resumeId}" data-filename="${r.fileName}" title="Rename">
                                                    <i class="fas fa-pen"></i>
                                                </button>
                                                <c:if test="${!r.isPrimary}">
                                                    <button class="btn btn-outline-success btn-sm primary-btn" data-resume-id="${r.resumeId}" title="Set as Primary">
                                                        <i class="fas fa-star"></i>
                                                    </button>
                                                </c:if>
                                                <button class="btn btn-outline-danger btn-sm delete-btn" data-resume-id="${r.resumeId}" data-filename="${r.fileName}" title="Delete">
                                                    <i class="fas fa-trash"></i>
                                                </button>
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
                let visibleCount = 0;
                
                items.forEach(function(li) {
                    const filenameEl = li.querySelector('.resume-filename');
                    const text = filenameEl ? filenameEl.textContent.trim().toLowerCase() : '';
                    const matches = q === '' || text.indexOf(q) !== -1;
                    li.style.display = matches ? '' : 'none';
                    if (matches) visibleCount++;
                });
                
                // Show message if no matches
                if (visibleCount === 0 && q !== '') {
                    console.log('No resumes found matching: ' + q);
                }
            }

            search.addEventListener('input', applyFilter);
            search.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    applyFilter();
                }
            });
            if (btn) {
                btn.addEventListener('click', function (e) {
                    e.preventDefault();
                    applyFilter();
                });
            }
        })();

        // Resume management functions
        (function() {
            const contextPath = '${pageContext.request.contextPath}';

            // Rename handler
            document.querySelectorAll('.rename-btn').forEach(btn => {
                btn.addEventListener('click', function() {
                    const resumeId = this.dataset.resumeId;
                    const currentName = this.dataset.filename;
                    const newName = prompt('Enter new name for the resume:', currentName);
                    
                    if (newName && newName.trim() !== '' && newName !== currentName) {
                        fetch(contextPath + '/api/resume/rename', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: 'resumeId=' + resumeId + '&newName=' + encodeURIComponent(newName)
                        })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                const li = document.querySelector('li[data-resume-id="' + resumeId + '"]');
                                if (li) {
                                    li.querySelector('.resume-filename').textContent = data.newName;
                                    li.querySelector('.rename-btn').dataset.filename = data.newName;
                                }
                                alert('Resume renamed successfully!');
                            } else {
                                alert('Error: ' + data.message);
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('An error occurred while renaming.');
                        });
                    }
                });
            });

            // Delete handler
            document.querySelectorAll('.delete-btn').forEach(btn => {
                btn.addEventListener('click', function() {
                    const resumeId = this.dataset.resumeId;
                    const filename = this.dataset.filename;
                    
                    if (confirm('Are you sure you want to delete "' + filename + '"? This action cannot be undone.')) {
                        fetch(contextPath + '/api/resume/delete', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: 'resumeId=' + resumeId
                        })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                const li = document.querySelector('li[data-resume-id="' + resumeId + '"]');
                                if (li) {
                                    li.remove();
                                }
                                // Check if list is now empty
                                const remaining = document.querySelectorAll('#resumeList li');
                                if (remaining.length === 0) {
                                    location.reload();
                                }
                            } else {
                                alert('Error: ' + data.message);
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('An error occurred while deleting.');
                        });
                    }
                });
            });

            // Set Primary handler
            document.querySelectorAll('.primary-btn').forEach(btn => {
                btn.addEventListener('click', function() {
                    const resumeId = this.dataset.resumeId;
                    
                    fetch(contextPath + '/api/resume/primary', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body: 'resumeId=' + resumeId
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            location.reload(); // Reload to reflect changes
                        } else {
                            alert('Error: ' + data.message);
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('An error occurred while setting primary.');
                    });
                });
            });
        })();
    </script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>