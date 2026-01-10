<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Messages - JobHunter</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chat.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="chat-container">
        <div class="container">
            <div class="chat-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-comments"></i> Messages</h1>
                        <p>Your conversations with 
                            <c:choose>
                                <c:when test="${currentUserRole == 'Candidate'}">recruiters</c:when>
                                <c:otherwise>candidates</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#newConversationModal">
                        <i class="fas fa-plus"></i> New Conversation
                    </button>
                </div>
            </div>

            <c:if test="${empty conversations}">
                <div class="empty-state">
                    <div class="empty-icon">
                        <i class="fas fa-inbox"></i>
                    </div>
                    <h3>No Messages Yet</h3>
                    <p>
                        <c:choose>
                            <c:when test="${currentUserRole == 'Candidate'}">
                                Apply to jobs to message their recruiters. You can only chat with recruiters from jobs you've applied to.
                            </c:when>
                            <c:otherwise>
                                Message candidates who have applied to your jobs. Use the button above to see all applicants.
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <c:choose>
                        <c:when test="${currentUserRole == 'Candidate'}">
                            <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary mt-3">
                                <i class="fas fa-search"></i> Browse Jobs
                            </a>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-primary mt-3" data-bs-toggle="modal" data-bs-target="#newConversationModal">
                                <i class="fas fa-comments"></i> Message Applicants
                            </button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <c:if test="${not empty conversations}">
                <div class="conversations-list">
                    <c:forEach items="${conversations}" var="conv">
                        <a href="${pageContext.request.contextPath}/chat/conversation/${conv.conversationId}" 
                           class="conversation-item ${conv.unreadCount > 0 ? 'unread' : ''}">
                            
                            <div class="conversation-avatar">
                                <c:choose>
                                    <c:when test="${currentUserRole == 'Candidate' && not empty conv.recruiterAvatar}">
                                        <img src="${conv.recruiterAvatar}" alt="${conv.recruiterName}">
                                    </c:when>
                                    <c:when test="${currentUserRole != 'Candidate' && not empty conv.candidateAvatar}">
                                        <img src="${conv.candidateAvatar}" alt="${conv.candidateName}">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="avatar-placeholder">
                                            <c:choose>
                                                <c:when test="${currentUserRole == 'Candidate'}">
                                                    ${fn:substring(conv.recruiterName, 0, 1)}
                                                </c:when>
                                                <c:otherwise>
                                                    ${fn:substring(conv.candidateName, 0, 1)}
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="conversation-content">
                                <div class="conversation-header">
                                    <h4>
                                        <c:choose>
                                            <c:when test="${currentUserRole == 'Candidate'}">
                                                ${conv.recruiterName}
                                            </c:when>
                                            <c:otherwise>
                                                ${conv.candidateName}
                                            </c:otherwise>
                                        </c:choose>
                                    </h4>
                                    <span class="conversation-time">
                                        <fmt:parseDate value="${conv.lastMessageAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both"/>
                                        <fmt:formatDate value="${parsedDate}" pattern="MMM d, h:mm a"/>
                                    </span>
                                </div>

                                <c:if test="${not empty conv.companyName}">
                                    <div class="conversation-company">
                                        <i class="fas fa-building"></i> ${conv.companyName}
                                    </div>
                                </c:if>

                                <c:if test="${not empty conv.jobTitle}">
                                    <div class="conversation-job">
                                        <i class="fas fa-briefcase"></i> ${conv.jobTitle}
                                    </div>
                                </c:if>

                                <div class="conversation-preview">
                                    <c:choose>
                                        <c:when test="${fn:length(conv.lastMessage) > 80}">
                                            ${fn:substring(conv.lastMessage, 0, 80)}...
                                        </c:when>
                                        <c:otherwise>
                                            ${conv.lastMessage}
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <c:if test="${conv.unreadCount > 0}">
                                <div class="unread-badge">${conv.unreadCount}</div>
                            </c:if>
                        </a>
                    </c:forEach>
                </div>
            </c:if>
        </div>
    </div>

    <!-- New Conversation Modal -->
    <div class="modal fade" id="newConversationModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-comments me-2"></i>
                        <c:choose>
                            <c:when test="${currentUserRole == 'Candidate'}">Message a Recruiter</c:when>
                            <c:otherwise>Message a Candidate</c:otherwise>
                        </c:choose>
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="recipientSearch" class="form-label">
                            <c:choose>
                                <c:when test="${currentUserRole == 'Candidate'}">
                                    <i class="fas fa-info-circle text-primary me-1"></i>
                                    Showing recruiters from jobs you've applied to
                                </c:when>
                                <c:otherwise>
                                    <i class="fas fa-info-circle text-primary me-1"></i>
                                    Showing candidates who applied to your jobs
                                </c:otherwise>
                            </c:choose>
                        </label>
                        <input type="text" class="form-control" id="recipientSearch" 
                               placeholder="Filter by name, email, or job title...">
                    </div>
                    <div id="searchResults" class="search-results">
                        <div class="text-center py-4">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                            <p class="text-muted mt-2">Loading contacts...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="../common/footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        const contextPath = '${pageContext.request.contextPath}';
        const currentUserRole = '${currentUserRole}';
        const currentUserId = ${currentUserId};
        let searchTimeout;

        // Load contacts when modal opens
        document.getElementById('newConversationModal').addEventListener('shown.bs.modal', function () {
            loadContacts('');
        });

        // Filter contacts on input
        document.getElementById('recipientSearch').addEventListener('input', function() {
            clearTimeout(searchTimeout);
            const query = this.value.trim();

            searchTimeout = setTimeout(() => {
                loadContacts(query);
            }, 300);
        });

        function loadContacts(query) {
            const url = query ? 
                contextPath + '/chat/search?q=' + encodeURIComponent(query) :
                contextPath + '/chat/search';
            
            console.log('Loading contacts from:', url);
                
            fetch(url)
                .then(response => {
                    console.log('Response status:', response.status);
                    if (!response.ok) {
                        return response.text().then(text => {
                            console.error('Error response:', text);
                            throw new Error('Server error: ' + response.status);
                        });
                    }
                    return response.json();
                })
                .then(results => {
                    console.log('Results:', results);
                    displaySearchResults(results);
                })
                .catch(error => {
                    console.error('Search error:', error);
                    document.getElementById('searchResults').innerHTML = 
                        '<div class="alert alert-danger">Failed to load contacts</div>';
                });
        }

        function displaySearchResults(results) {
            const resultsDiv = document.getElementById('searchResults');
            
            if (results.length === 0) {
                const noResultsMsg = currentUserRole === 'Candidate' 
                    ? '<p>No recruiters found. Apply to jobs first to message recruiters.</p>' +
                      '<a href="' + contextPath + '/jobs" class="btn btn-primary btn-sm">Browse Jobs</a>'
                    : '<p>No candidates found. Candidates who apply to your jobs will appear here.</p>' +
                      '<a href="' + contextPath + '/employer/post-job" class="btn btn-primary btn-sm">Post a Job</a>';
                
                resultsDiv.innerHTML = '<div class="text-center py-4 text-muted">' +
                    '<i class="fas fa-inbox fa-3x mb-3"></i>' + noResultsMsg + '</div>';
                return;
            }

            let html = '<div class="list-group">';
            results.forEach(person => {
                const jobInfo = person.jobTitle ? 
                    '<span class="badge bg-primary me-1">' + escapeHtml(person.jobTitle) + '</span>' : '';
                const companyInfo = person.companyName ? 
                    '<span class="badge bg-secondary">' + escapeHtml(person.companyName) + '</span>' : '';
                const statusInfo = person.applicationStatus ? 
                    '<span class="badge bg-info">' + escapeHtml(person.applicationStatus) + '</span>' : '';
                
                const jobId = person.jobId || 0;
                const personName = escapeHtml(person.name);
                const personEmail = escapeHtml(person.email);
                
                html += `
                    <button type="button" class="list-group-item list-group-item-action" 
                            onclick="selectRecipient(${person.id}, '${personName}', ${jobId})">
                        <div class="d-flex align-items-center">
                            <div class="avatar-sm me-3">
                                ${person.name.charAt(0).toUpperCase()}
                            </div>
                            <div class="flex-grow-1">
                                <div class="fw-semibold">${personName}</div>
                                <small class="text-muted">${personEmail}</small>
                                <div class="mt-1">
                                    ${jobInfo}${companyInfo}${statusInfo}
                                </div>
                            </div>
                            <i class="fas fa-chevron-right text-muted"></i>
                        </div>
                    </button>
                `;
            });
            html += '</div>';
            resultsDiv.innerHTML = html;
        }

        function escapeHtml(text) {
            if (!text) return '';
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        function selectRecipient(recipientId, recipientName, jobId) {
            const candidateId = currentUserRole === 'Candidate' ? currentUserId : recipientId;
            const recruiterId = currentUserRole === 'Candidate' ? recipientId : currentUserId;

            const params = new URLSearchParams();
            params.append('candidateId', candidateId);
            params.append('recruiterId', recruiterId);
            if (jobId) {
                params.append('jobId', jobId);
            }

            fetch(contextPath + '/chat/start', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    window.location.href = data.redirectUrl;
                } else {
                    alert('Failed to start conversation: ' + (data.error || 'Unknown error'));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Failed to start conversation');
            });
        }
    </script>

    <style>
        .search-results {
            margin-top: 10px;
            max-height: 400px;
            overflow-y: auto;
        }

        .avatar-sm {
            width: 45px;
            height: 45px;
            background: linear-gradient(135deg, #3b82f6, #2563eb);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            font-size: 1.1rem;
            flex-shrink: 0;
        }

        .list-group-item:hover {
            background-color: #f8f9fa;
        }

        .list-group-item .badge {
            font-size: 0.7rem;
        }
    </style>
