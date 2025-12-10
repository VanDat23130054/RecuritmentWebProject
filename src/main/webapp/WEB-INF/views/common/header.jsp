<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="main-header">
    <div class="container">
        <div class="header-content">
            <!-- Logo -->
            <div class="logo">
                <a href="${pageContext.request.contextPath}/">
                    <i class="fa-solid fa-briefcase"></i>
                    <span>JobHunter</span>
                </a>
            </div>

            <!-- Navigation -->
            <nav class="main-nav">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/jobs">Jobs</a></li>
                    <li><a href="${pageContext.request.contextPath}/companies">Companies</a></li>
                    <li><a href="${pageContext.request.contextPath}/about">About</a></li>
                </ul>
            </nav>

            <!-- User Actions -->
            <div class="header-actions">
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <!-- Logged In User -->
                        <div class="user-menu">
                            <button class="user-profile-btn">
                                <i class="fa-solid fa-user-circle"></i>
                                <span>${sessionScope.user.email}</span>
                                <i class="fa-solid fa-chevron-down"></i>
                            </button>
                            <div class="user-dropdown">
                                <c:choose>
                                    <c:when test="${sessionScope.userRole == 'Recruiter'}">
                                        <!-- Recruiter Menu -->
                                        <a href="${pageContext.request.contextPath}/employer/dashboard">
                                            <i class="fa-solid fa-chart-line"></i> Employer Portal
                                        </a>
                                        <a href="${pageContext.request.contextPath}/employer/jobs">
                                            <i class="fa-solid fa-briefcase"></i> My Jobs
                                        </a>
                                        <a href="${pageContext.request.contextPath}/employer/applications">
                                            <i class="fa-solid fa-file-alt"></i> Applications
                                        </a>
                                        <a href="${pageContext.request.contextPath}/employer/company-profile">
                                            <i class="fa-solid fa-building"></i> Company Profile
                                        </a>
                                        <hr>
                                        <a href="${pageContext.request.contextPath}/profile"><i class="fa-solid fa-user"></i> My Profile</a>
                                    </c:when>
                                    <c:otherwise>
                                        <!-- Candidate Menu -->
                                        <a href="${pageContext.request.contextPath}/profile"><i class="fa-solid fa-user"></i> My Profile</a>
                                        <a href="${pageContext.request.contextPath}/applications"><i class="fa-solid fa-file-alt"></i> My Applications</a>
                                        <a href="${pageContext.request.contextPath}/saved-jobs"><i class="fa-solid fa-bookmark"></i> Saved Jobs</a>
                                    </c:otherwise>
                                </c:choose>
                                <hr>
                                <a href="${pageContext.request.contextPath}/logout"><i class="fa-solid fa-sign-out-alt"></i> Logout</a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Guest User -->
                        <a href="${pageContext.request.contextPath}/login" class="btn-login">
                            <i class="fa-solid fa-sign-in-alt"></i> Login
                        </a>
                        <a href="${pageContext.request.contextPath}/register" class="btn-register">
                            <i class="fa-solid fa-user-plus"></i> Register
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Mobile Menu Toggle -->
            <button class="mobile-menu-toggle">
                <i class="fa-solid fa-bars"></i>
            </button>
        </div>
    </div>
</header>