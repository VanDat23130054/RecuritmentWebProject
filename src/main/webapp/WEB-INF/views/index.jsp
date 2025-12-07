<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JobHunter - Tìm việc làm IT hàng đầu</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome 6 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <!-- Custom CSS (loads after Bootstrap for overrides) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <!-- Header -->
    <jsp:include page="common/header.jsp" />
    <!-- Hero Search Section -->
    <section class="hero">
        <div class="container">
            <h1>
                <span class="job-count">${totalJobs}</span> IT Jobs For 
                <span class="highlight">"Chất"</span> Developers
            </h1>
            
            <form action="${pageContext.request.contextPath}/jobs/search" method="GET" class="search-form">
                <!-- Location Dropdown -->
                <div class="form-group">
                    <select name="cityId" id="citySelect">
                        <option value="">Tất cả thành phố</option>
                        <c:forEach items="${cities}" var="city">
                            <option value="${city.cityId}" 
                                    ${param.cityId == city.cityId ? 'selected' : ''}>
                                ${city.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <!-- Keyword Input -->
                <div class="form-group flex-grow">
                    <input type="text" 
                           name="keyword" 
                           placeholder="Enter keyword skill (Java, iOS...), job title, company..."
                           value="${param.keyword}">
                </div>
                
                <button type="submit" class="btn-search">
                    <i class="fa-solid fa-magnifying-glass"></i> Search
                </button>
            </form>
            
            <!-- Skill Suggestions -->
            <div class="suggestions">
                <span>Suggestions for you:</span>
                <c:forEach items="${topSkills}" var="skill">
                    <a href="?skill=${skill.skillId}" class="skill-tag">${skill.name}</a>
                </c:forEach>
            </div>
        </div>
    </section>
     
    <!-- Job Listings -->
    <section class="job-listings">
        <div class="container">
            <c:forEach items="${jobs}" var="job">
                <div class="job-card ${job.isFeatured ? 'featured' : ''}">
                    <div class="job-card-header">
                        <a href="${pageContext.request.contextPath}/job/${job.jobId}">
                            <h3 class="job-title">${job.title}</h3>
                        </a>
                        <c:if test="${job.isFeatured}">
                            <span class="badge-hot">🔥 HOT</span>
                        </c:if>
                    </div> 
                    
                    <div class="job-card-body">
                        <div class="company-info">
                            <img src="${job.logoUrl != null ? job.logoUrl : pageContext.request.contextPath.concat('/images/default-company.png')}" 
                                 alt="${job.companyName}" 
                                 class="company-logo">
                            <a href="${pageContext.request.contextPath}/companies/${job.companyId}">
                                ${job.companyName}
                            </a>
                        </div>
                        
                        <div class="job-meta">
                            <span class="location">
                                <i class="fa-solid fa-location-dot"></i> ${job.cityName}
                            </span>
                            <c:if test="${job.salaryMax != null}">
                                <span class="salary">
                                    <i class="fa-solid fa-money-bill-wave"></i>
                                    <fmt:formatNumber value="${job.salaryMin}" type="number"/> - 
                                    <fmt:formatNumber value="${job.salaryMax}" type="number"/> ${job.currency}
                                </span>
                            </c:if>
                        </div>
                        
                        <div class="job-skills">
                            <c:forEach items="${job.skillsList}" var="skill">
                                <span class="skill-badge"><i class="fa-solid fa-code"></i> ${skill.Name}</span>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
    
    <!-- Top Employers -->
    <section class="top-employers">
        <div class="container">
            <h2>Top Employers</h2>
            
            <div class="employer-grid">
                <c:forEach items="${topEmployers}" var="company">
                    <div class="employer-card">
                        <a href="${pageContext.request.contextPath}/companies/${company.companyId}">
                            <img src="${company.logoUrl != null ? company.logoUrl : pageContext.request.contextPath.concat('/images/default-company.png')}" 
                                 alt="${company.name}">
                        </a>
                        
                        <h4>${company.name}</h4>
                        
                        <div class="employer-skills">
                            <c:forEach items="${company.topSkillsList}" var="skill" varStatus="status">
                                <c:if test="${status.index < 5}">
                                    <span class="skill-tag">${skill.Name}</span>
                                </c:if>
                            </c:forEach>
                        </div>
                        
                        <div class="employer-meta">
                            <span class="location"><i class="fa-solid fa-building"></i> ${company.cityName}</span>
                            <a href="${pageContext.request.contextPath}/companies/${company.companyId}" 
                               class="job-count">
                                <i class="fa-solid fa-briefcase"></i> ${company.activeJobCount} Jobs
                            </a>
                        </div>
                        
                        <a href="${pageContext.request.contextPath}/companies/${company.companyId}" 
                           class="btn-view">
                            View company <i class="fa-solid fa-arrow-right"></i>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <jsp:include page="common/footer.jsp" />
    
    <%-- <script src="${pageContext.request.contextPath}/js/main.js"></script> --%>
</body>
</html>