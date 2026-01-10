<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Top Companies - JobHunter</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/companies-list.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="companies-container">
        <!-- Hero Section -->
        <div class="companies-hero">
            <div class="hero-content">
                <h1>Discover Top Companies</h1>
                <p>Explore opportunities at leading organizations</p>
            </div>
        </div>

        <!-- Companies Grid -->
        <div class="container">
            <div class="companies-section">
                <div class="section-header">
                    <h2>Companies Hiring Now</h2>
                    <p>Browse ${totalCompanies} companies looking for talented professionals</p>
                </div>

                <c:if test="${empty companies}">
                    <div class="empty-state">
                        <i class="fas fa-building"></i>
                        <p>No companies available at this time</p>
                    </div>
                </c:if>

                <c:if test="${not empty companies}">
                    <div class="companies-grid">
                        <c:forEach items="${companies}" var="company">
                            <div class="company-card">
                                <div class="company-card-header">
                                    <c:if test="${not empty company.logoUrl}">
                                        <img src="${company.logoUrl}" alt="${company.name}" class="company-logo">
                                    </c:if>
                                    <c:if test="${empty company.logoUrl}">
                                        <div class="company-logo-placeholder">
                                            <i class="fas fa-building"></i>
                                        </div>
                                    </c:if>
                                </div>

                                <div class="company-card-body">
                                    <h3>${company.name}</h3>
                                    
                                    <c:if test="${not empty company.cityName}">
                                        <p class="location">
                                            <i class="fas fa-map-marker-alt"></i> ${company.cityName}
                                        </p>
                                    </c:if>

                                    <div class="company-meta">
                                        <c:if test="${company.activeJobCount > 0}">
                                            <span class="job-count">
                                                <i class="fas fa-briefcase"></i> ${company.activeJobCount} Jobs
                                            </span>
                                        </c:if>
                                    </div>

                                    <c:if test="${not empty company.topSkills}">
                                        <div class="skills-preview">
                                            <c:forEach items="${company.topSkills.split(',')}" var="skill" varStatus="status">
                                                <c:if test="${status.index < 3}">
                                                    <span class="skill-tag">${fn:trim(skill)}</span>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                    </c:if>
                                </div>

                                <div class="company-card-footer">
                                    <a href="${pageContext.request.contextPath}/company/${company.companyId}" class="btn btn-explore">
                                        View Company <i class="fas fa-arrow-right"></i>
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <div class="pagination-wrapper">
                            <nav aria-label="Page navigation">
                                <ul class="pagination">
                                    <c:if test="${currentPage > 1}">
                                        <li class="page-item">
                                            <a class="page-link" href="${pageContext.request.contextPath}/companies?page=1">
                                                <i class="fas fa-chevron-left"></i> First
                                            </a>
                                        </li>
                                        <li class="page-item">
                                            <a class="page-link" href="${pageContext.request.contextPath}/companies?page=${currentPage - 1}">
                                                Previous
                                            </a>
                                        </li>
                                    </c:if>

                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <c:choose>
                                            <c:when test="${i == currentPage}">
                                                <li class="page-item active">
                                                    <span class="page-link">${i}</span>
                                                </li>
                                            </c:when>
                                            <c:otherwise>
                                                <li class="page-item">
                                                    <a class="page-link" href="${pageContext.request.contextPath}/companies?page=${i}">${i}</a>
                                                </li>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>

                                    <c:if test="${currentPage < totalPages}">
                                        <li class="page-item">
                                            <a class="page-link" href="${pageContext.request.contextPath}/companies?page=${currentPage + 1}">
                                                Next
                                            </a>
                                        </li>
                                        <li class="page-item">
                                            <a class="page-link" href="${pageContext.request.contextPath}/companies?page=${totalPages}">
                                                Last <i class="fas fa-chevron-right"></i>
                                            </a>
                                        </li>
                                    </c:if>
                                </ul>
                            </nav>
                        </div>
                    </c:if>
                </c:if>
            </div>
        </div>
    </div>

    <jsp:include page="../common/footer.jsp" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
