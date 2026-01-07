<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- compute path after context to use for active detection --%>
<%
    String currentPath = request.getRequestURI().substring(request.getContextPath().length());
    request.setAttribute("currentPath", currentPath);
%>
<div class="dashboard-sidebar">
    <div class="sidebar-header">
        <h3><i class="fas fa-user-tie"></i> Candidate Portal</h3>
    </div>
    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/candidate/dashboard" class="nav-item ${fn:startsWith(currentPath, '/candidate/dashboard') ? 'active' : ''}">
            <i class="fas fa-chart-line"></i> Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/candidate/profile" class="nav-item ${fn:startsWith(currentPath, '/candidate/profile') ? 'active' : ''}">
            <i class="fas fa-user"></i> My Profile
        </a>
        <a href="${pageContext.request.contextPath}/candidate/resume" class="nav-item ${fn:startsWith(currentPath, '/candidate/resume') ? 'active' : ''}">
            <i class="fas fa-file"></i> Resume
        </a>
        <a href="${pageContext.request.contextPath}/candidate/applications" class="nav-item ${fn:startsWith(currentPath, '/candidate/applications') ? 'active' : ''}">
            <i class="fas fa-file-alt"></i> Applications
        </a>
        <a href="${pageContext.request.contextPath}/candidate/saved-jobs" class="nav-item ${fn:startsWith(currentPath, '/candidate/saved-jobs') ? 'active' : ''}">
            <i class="fas fa-bookmark"></i> Saved Jobs
        </a>
        <hr class="my-3">
        <a href="${pageContext.request.contextPath}/jobs" class="nav-item">
            <i class="fas fa-search"></i> Browse Jobs
        </a>
    </nav>
</div>