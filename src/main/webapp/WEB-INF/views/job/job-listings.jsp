<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job Listings - JobHunter</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container job-listings-container">
        <div class="job-filters-sidebar">
            <h3>Filter Jobs</h3>
            
            <form action="${pageContext.request.contextPath}/jobs" method="GET" id="filterForm">
                <!-- Keyword Search -->
                <div class="filter-group">
                    <label for="keyword">Keyword</label>
                    <input type="text" id="keyword" name="keyword" 
                           placeholder="Job title, skill, company..." 
                           value="${keyword}">
                </div>
                
                <!-- Location Filter -->
                <div class="filter-group">
                    <label for="cityId">Location</label>
                    <select name="cityId" id="cityId">
                        <option value="">All Cities</option>
                        <c:forEach items="${cities}" var="city">
                            <option value="${city.cityId}" 
                                    ${selectedCityId == city.cityId ? 'selected' : ''}>
                                ${city.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <!-- Skill Filter -->
                <div class="filter-group">
                    <label for="skillId">Skills</label>
                    <select name="skillId" id="skillId">
                        <option value="">All Skills</option>
                        <c:forEach items="${skills}" var="skill">
                            <option value="${skill.skillId}" 
                                    ${selectedSkillId == skill.skillId ? 'selected' : ''}>
                                ${skill.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <button type="submit" class="btn btn-primary btn-block">
                    <i class="fas fa-search"></i> Apply Filters
                </button>
                
                <a href="${pageContext.request.contextPath}/jobs" class="btn btn-secondary btn-block">
                    Clear Filters
                </a>
            </form>
        </div>
        
        <div class="job-listings-content">
            <div class="job-listings-header">
                <h2>
                    <c:choose>
                        <c:when test="${not empty keyword}">
                            Search results for "${keyword}"
                        </c:when>
                        <c:otherwise>
                            All Job Listings
                        </c:otherwise>
                    </c:choose>
                </h2>
                
                <div class="sort-options">
                    <label for="sortBy">Sort by:</label>
                    <select name="sortBy" id="sortBy" onchange="updateSort(this.value)">
                        <option value="date" ${sortBy == 'date' ? 'selected' : ''}>Newest First</option>
                        <option value="relevance" ${sortBy == 'relevance' ? 'selected' : ''}>Most Relevant</option>
                        <option value="salary" ${sortBy == 'salary' ? 'selected' : ''}>Highest Salary</option>
                    </select>
                </div>
            </div>
            
            <div class="job-listings">
                <c:forEach items="${jobs}" var="job">
                    <div class="job-card">
                        <div class="job-card-header">
                            <img src="${job.logoUrl}" alt="${job.companyName}" class="company-logo">
                            <div class="job-info">
                                <h3>
                                    <a href="${pageContext.request.contextPath}/job/${job.jobId}">
                                        ${job.title}
                                    </a>
                                    <c:if test="${job.isFeatured}">
                                        <span class="badge badge-featured">Featured</span>
                                    </c:if>
                                </h3>
                                <p class="company-name">
                                    <i class="fas fa-building"></i> ${job.companyName}
                                </p>
                            </div>
                        </div>
                        
                        <div class="job-card-body">
                            <div class="job-meta">
                                <span class="job-location">
                                    <i class="fas fa-map-marker-alt"></i> ${job.cityName}
                                </span>
                                <c:if test="${not empty job.salaryMin && not empty job.salaryMax}">
                                    <span class="job-salary">
                                        <i class="fas fa-dollar-sign"></i>
                                        <fmt:formatNumber value="${job.salaryMin}" type="number"/> - 
                                        <fmt:formatNumber value="${job.salaryMax}" type="number"/> 
                                        ${job.currency}
                                    </span>
                                </c:if>
                            </div>
                            
                            <c:if test="${not empty job.skillsList}">
                                <div class="job-skills">
                                    <c:forEach items="${job.skillsList}" var="skill" varStatus="status">
                                        <c:if test="${status.index < 5}">
                                            <span class="skill-tag">${skill.Name}</span>
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${job.skillsList.size() > 5}">
                                        <span class="skill-tag">+${job.skillsList.size() - 5} more</span>
                                    </c:if>
                                </div>
                            </c:if>
                        </div>
                        
                        <div class="job-card-footer">
                            <a href="${pageContext.request.contextPath}/job/${job.jobId}" 
                               class="btn btn-primary">
                                View Details
                            </a>
                            <button class="btn btn-secondary save-job-btn" 
                                    data-job-id="${job.jobId}">
                                <i class="far fa-bookmark"></i> Save
                            </button>
                        </div>
                    </div>
                </c:forEach>
                
                <c:if test="${empty jobs}">
                    <div class="no-results">
                        <i class="fas fa-search"></i>
                        <h3>No jobs found</h3>
                        <p>Try adjusting your filters or search criteria</p>
                    </div>
                </c:if>
            </div>
            
            <!-- Pagination -->
            <c:if test="${not empty jobs}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="?page=${currentPage - 1}&keyword=${keyword}&cityId=${selectedCityId}&skillId=${selectedSkillId}&sortBy=${sortBy}" 
                           class="btn btn-secondary">
                            <i class="fas fa-chevron-left"></i> Previous
                        </a>
                    </c:if>
                    
                    <span class="page-info">Page ${currentPage}</span>
                    
                    <c:if test="${hasMore}">
                        <a href="?page=${currentPage + 1}&keyword=${keyword}&cityId=${selectedCityId}&skillId=${selectedSkillId}&sortBy=${sortBy}" 
                           class="btn btn-secondary">
                            Next <i class="fas fa-chevron-right"></i>
                        </a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script>
        function updateSort(sortValue) {
            const urlParams = new URLSearchParams(window.location.search);
            urlParams.set('sortBy', sortValue);
            urlParams.set('page', '1');
            window.location.search = urlParams.toString();
        }
        
        // Save job functionality
        document.querySelectorAll('.save-job-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const jobId = this.dataset.jobId;
                // TODO: Implement AJAX call to save job
                console.log('Saving job:', jobId);
                this.innerHTML = '<i class="fas fa-bookmark"></i> Saved';
                this.classList.add('saved');
            });
        });
    </script>
</body>
</html>
