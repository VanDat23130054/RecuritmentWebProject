<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chat - JobHunter</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chat.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="chat-page">
        <div class="container">
            <!-- Back button and header -->
            <div class="chat-page-header">
                <a href="${pageContext.request.contextPath}/chat" class="back-btn">
                    <i class="fas fa-arrow-left"></i> Back to Messages
                </a>
            </div>

            <div class="chat-window">
                <!-- Chat Header -->
                <div class="chat-window-header">
                    <div class="chat-participant">
                        <div class="participant-avatar">
                            <c:choose>
                                <c:when test="${currentUserRole == 'Candidate' && not empty conversation.recruiterAvatar}">
                                    <img src="${conversation.recruiterAvatar}" alt="${conversation.recruiterName}">
                                </c:when>
                                <c:when test="${currentUserRole != 'Candidate' && not empty conversation.candidateAvatar}">
                                    <img src="${conversation.candidateAvatar}" alt="${conversation.candidateName}">
                                </c:when>
                                <c:otherwise>
                                    <div class="avatar-placeholder">
                                        <c:choose>
                                            <c:when test="${currentUserRole == 'Candidate'}">
                                                ${fn:substring(conversation.recruiterName, 0, 1)}
                                            </c:when>
                                            <c:otherwise>
                                                ${fn:substring(conversation.candidateName, 0, 1)}
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="participant-info">
                            <h3>
                                <c:choose>
                                    <c:when test="${currentUserRole == 'Candidate'}">
                                        ${conversation.recruiterName}
                                    </c:when>
                                    <c:otherwise>
                                        ${conversation.candidateName}
                                    </c:otherwise>
                                </c:choose>
                            </h3>
                            <c:if test="${not empty conversation.companyName}">
                                <span class="company-name">
                                    <i class="fas fa-building"></i> ${conversation.companyName}
                                </span>
                            </c:if>
                        </div>
                    </div>
                    <c:if test="${not empty conversation.jobTitle}">
                        <div class="chat-job-context">
                            <span class="job-badge">
                                <i class="fas fa-briefcase"></i> ${conversation.jobTitle}
                            </span>
                        </div>
                    </c:if>
                </div>

                <!-- Messages Area -->
                <div class="messages-container" id="messagesContainer">
                    <c:if test="${empty messages}">
                        <div class="no-messages">
                            <i class="fas fa-comments"></i>
                            <p>No messages yet. Start the conversation!</p>
                        </div>
                    </c:if>

                    <c:forEach items="${messages}" var="msg">
                        <div class="message ${msg.senderId == currentUserId ? 'sent' : 'received'}" data-message-id="${msg.messageId}">
                            <c:if test="${msg.senderId != currentUserId}">
                                <div class="message-avatar">
                                    <c:if test="${not empty msg.senderAvatar}">
                                        <img src="${msg.senderAvatar}" alt="${msg.senderName}">
                                    </c:if>
                                    <c:if test="${empty msg.senderAvatar}">
                                        <div class="avatar-placeholder small">
                                            ${fn:substring(msg.senderName, 0, 1)}
                                        </div>
                                    </c:if>
                                </div>
                            </c:if>
                            <div class="message-content">
                                <div class="message-bubble">
                                    ${fn:escapeXml(msg.body)}
                                </div>
                                <div class="message-meta">
                                    <span class="message-time">
                                        <fmt:parseDate value="${msg.sentAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both"/>
                                        <fmt:formatDate value="${parsedDate}" pattern="MMM d, h:mm a"/>
                                    </span>
                                    <c:if test="${msg.senderId == currentUserId && msg.read}">
                                        <span class="message-read"><i class="fas fa-check-double"></i></span>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Message Input -->
                <div class="message-input-container">
                    <form id="messageForm" class="message-form">
                        <input type="hidden" name="conversationId" value="${conversation.conversationId}">
                        <div class="input-wrapper">
                            <textarea 
                                name="body" 
                                id="messageInput" 
                                placeholder="Type your message..." 
                                rows="1"
                                maxlength="2000"
                                required></textarea>
                            <button type="submit" class="send-btn" id="sendBtn">
                                <i class="fas fa-paper-plane"></i>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="../common/footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        const contextPath = '${pageContext.request.contextPath}';
        const conversationId = ${conversation.conversationId};
        const currentUserId = ${currentUserId};
        
        document.addEventListener('DOMContentLoaded', function() {
            const messageForm = document.getElementById('messageForm');
            const messageInput = document.getElementById('messageInput');
            const messagesContainer = document.getElementById('messagesContainer');
            const sendBtn = document.getElementById('sendBtn');

            // Scroll to bottom on load
            scrollToBottom();

            // Auto-resize textarea
            messageInput.addEventListener('input', function() {
                this.style.height = 'auto';
                this.style.height = Math.min(this.scrollHeight, 120) + 'px';
            });

            // Send message on Enter (Shift+Enter for new line)
            messageInput.addEventListener('keydown', function(e) {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    messageForm.dispatchEvent(new Event('submit'));
                }
            });

            // Handle form submission
            messageForm.addEventListener('submit', async function(e) {
                e.preventDefault();

                const body = messageInput.value.trim();
                if (!body) return;

                sendBtn.disabled = true;

                try {
                    const params = new URLSearchParams();
                    params.append('conversationId', conversationId);
                    params.append('body', body);

                    const response = await fetch(contextPath + '/chat/send', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body: params
                    });

                    if (response.ok) {
                        const message = await response.json();
                        appendMessage(message, true);
                        messageInput.value = '';
                        messageInput.style.height = 'auto';
                        scrollToBottom();
                    } else {
                        const error = await response.json();
                        alert('Failed to send message: ' + (error.error || 'Unknown error'));
                    }
                } catch (error) {
                    console.error('Error sending message:', error);
                    alert('Failed to send message. Please try again.');
                } finally {
                    sendBtn.disabled = false;
                }
            });

            // Poll for new messages every 5 seconds
            let isPolling = false;
            const pollInterval = setInterval(checkNewMessages, 5000);

            async function checkNewMessages() {
                // Prevent concurrent polling requests
                if (isPolling) {
                    console.warn('[CHAT] Polling already in progress, skipping this cycle');
                    return;
                }
                
                isPolling = true;
                try {
                    const response = await fetch(contextPath + '/chat/messages?conversationId=' + conversationId, {
                        method: 'GET',
                        signal: AbortSignal.timeout(10000) // 10 second timeout
                    });
                    
                    if (response.ok) {
                        const messages = await response.json();
                        updateMessages(messages);
                    } else if (response.status === 403 || response.status === 401) {
                        console.error('[CHAT] Access denied, stopping poll');
                        clearInterval(pollInterval);
                    }
                } catch (error) {
                    console.error('[CHAT] Error checking messages:', error.message);
                } finally {
                    isPolling = false;
                }
            }

            function updateMessages(messages) {
                const existingIds = new Set();
                document.querySelectorAll('.message').forEach(el => {
                    const id = el.dataset.messageId;
                    if (id) existingIds.add(parseInt(id));
                });

                // Sort messages by timestamp to ensure correct order
                messages.sort((a, b) => new Date(a.sentAt) - new Date(b.sentAt));

                // Only add new messages that don't exist yet
                messages.forEach(msg => {
                    if (!existingIds.has(msg.messageId)) {
                        appendMessage(msg, msg.senderId === currentUserId);
                    }
                });

                scrollToBottom();
            }

            function appendMessage(msg, isSent) {
                // Remove "no messages" placeholder if exists
                const noMessages = messagesContainer.querySelector('.no-messages');
                if (noMessages) noMessages.remove();

                const messageDiv = document.createElement('div');
                messageDiv.className = 'message ' + (isSent ? 'sent' : 'received');
                messageDiv.dataset.messageId = msg.messageId;

                const time = new Date(msg.sentAt).toLocaleString('en-US', {
                    month: 'short',
                    day: 'numeric',
                    hour: 'numeric',
                    minute: '2-digit',
                    hour12: true
                });

                let avatarHtml = '';
                if (!isSent) {
                    if (msg.senderAvatar) {
                        avatarHtml = '<div class="message-avatar"><img src="' + msg.senderAvatar + '" alt="' + msg.senderName + '"></div>';
                    } else {
                        avatarHtml = '<div class="message-avatar"><div class="avatar-placeholder small">' + (msg.senderName ? msg.senderName.charAt(0) : '?') + '</div></div>';
                    }
                }

                messageDiv.innerHTML = avatarHtml + 
                    '<div class="message-content">' +
                        '<div class="message-bubble">' + escapeHtml(msg.body) + '</div>' +
                        '<div class="message-meta">' +
                            '<span class="message-time">' + time + '</span>' +
                        '</div>' +
                    '</div>';

                messagesContainer.appendChild(messageDiv);
            }

            function scrollToBottom() {
                messagesContainer.scrollTop = messagesContainer.scrollHeight;
            }

            function escapeHtml(text) {
                const div = document.createElement('div');
                div.textContent = text;
                return div.innerHTML;
            }
        });
    </script>
</body>
</html>
