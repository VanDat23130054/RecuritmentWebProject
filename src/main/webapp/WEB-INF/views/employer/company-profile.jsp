<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Profile - JobHunter</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-forms.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="dashboard-container">
        <!-- Sidebar Toggle Button (Mobile/Tablet) -->
        <button class="sidebar-toggle" aria-label="Toggle Sidebar">
            <i class="fas fa-bars"></i>
        </button>

        <aside class="dashboard-sidebar">
            <div class="sidebar-header">
                <h3><i class="fas fa-building"></i> Employer Portal</h3>
            </div>
            
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/employer/dashboard" class="nav-item">
                    <i class="fas fa-chart-line"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/employer/jobs" class="nav-item">
                    <i class="fas fa-briefcase"></i> My Jobs
                </a>
                <a href="${pageContext.request.contextPath}/employer/post-job" class="nav-item">
                    <i class="fas fa-plus-circle"></i> Post New Job
                </a>
                <a href="${pageContext.request.contextPath}/employer/applications" class="nav-item">
                    <i class="fas fa-file-alt"></i> Applications
                </a>
                <a href="${pageContext.request.contextPath}/employer/company-profile" class="nav-item active">
                    <i class="fas fa-building"></i> Company Profile
                </a>
                <a href="${pageContext.request.contextPath}/employer/graph" class="nav-item">
                    <i class="fa-solid fa-chart-line"></i> Analytics
                </a>
            </nav>
        </aside>

        <main class="dashboard-main">
            <div id="alertContainer"></div>
            
            <div class="dashboard-header">
                <div class="header-content">
                    <h1><i class="fas fa-building"></i> Company Profile</h1>
                    <c:choose>
                        <c:when test="${user.role == 'EmployerAdmin'}">
                            <p class="text-muted">Manage your company information</p>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted">View your company information</p>
                        </c:otherwise>
                    </c:choose>
                </div>
                <c:if test="${user.role != 'EmployerAdmin'}">
                    <div class="alert alert-info" style="margin-bottom: 20px;">
                        <i class="fas fa-info-circle"></i>
                        <span>Only Company Administrators can edit the company profile. Contact your administrator to make changes.</span>
                    </div>
                </c:if>
            </div>

            <div class="form-container">

                <form action="${pageContext.request.contextPath}/employer/company-profile" 
                      method="POST" class="job-form">

                    <!-- Basic Information -->
                    <div class="form-section">
                        <h3><i class="fas fa-info-circle"></i> Basic Information</h3>
                        
                        <div class="form-group">
                            <label for="name">Company Name <span class="required">*</span></label>
                            <input type="text" id="name" name="name" required
                                   value="${company.name}"
                                   ${user.role != 'EmployerAdmin' ? 'readonly' : ''}>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="website">Website</label>
                                <input type="url" id="website" name="website"
                                       placeholder="https://example.com"
                                       value="${company.websiteUrl}"
                                       ${user.role != 'EmployerAdmin' ? 'readonly' : ''}>
                            </div>

                            <div class="form-group">
                                <label for="logoUrl">Logo URL</label>
                                <input type="url" id="logoUrl" name="logoUrl"
                                       placeholder="https://example.com/logo.png"
                                       value="${company.logoUrl}"
                                       ${user.role != 'EmployerAdmin' ? 'readonly' : ''}>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="description">Company Description</label>
                            <textarea id="description" name="description" rows="5"
                                      placeholder="Tell candidates about your company..."
                                      ${user.role != 'EmployerAdmin' ? 'readonly' : ''}>${company.description}</textarea>
                        </div>
                    </div>

                    <!-- Company Details -->
                    <div class="form-section">
                        <h3><i class="fas fa-building"></i> Company Details</h3>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="industry">Industry</label>
                                <select id="industry" name="industry" ${user.role != 'EmployerAdmin' ? 'disabled' : ''}>
                                    <option value="">Select Industry</option>
                                    <option value="Technology" ${company.industry == 'Technology' ? 'selected' : ''}>Technology</option>
                                    <option value="Finance" ${company.industry == 'Finance' ? 'selected' : ''}>Finance</option>
                                    <option value="Healthcare" ${company.industry == 'Healthcare' ? 'selected' : ''}>Healthcare</option>
                                    <option value="Education" ${company.industry == 'Education' ? 'selected' : ''}>Education</option>
                                    <option value="Retail" ${company.industry == 'Retail' ? 'selected' : ''}>Retail</option>
                                    <option value="Manufacturing" ${company.industry == 'Manufacturing' ? 'selected' : ''}>Manufacturing</option>
                                    <option value="Consulting" ${company.industry == 'Consulting' ? 'selected' : ''}>Consulting</option>
                                    <option value="Other" ${company.industry == 'Other' ? 'selected' : ''}>Other</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="sizeRange">Company Size</label>
                                <select id="sizeRange" name="sizeRange" ${user.role != 'EmployerAdmin' ? 'disabled' : ''}>
                                    <option value="">Select Size</option>
                                    <option value="1-10" ${company.sizeRange == '1-10' ? 'selected' : ''}>1-10 employees</option>
                                    <option value="11-50" ${company.sizeRange == '11-50' ? 'selected' : ''}>11-50 employees</option>
                                    <option value="51-200" ${company.sizeRange == '51-200' ? 'selected' : ''}>51-200 employees</option>
                                    <option value="201-500" ${company.sizeRange == '201-500' ? 'selected' : ''}>201-500 employees</option>
                                    <option value="501-1000" ${company.sizeRange == '501-1000' ? 'selected' : ''}>501-1000 employees</option>
                                    <option value="1001-5000" ${company.sizeRange == '1001-5000' ? 'selected' : ''}>1001-5000 employees</option>
                                    <option value="5001+" ${company.sizeRange == '5001+' ? 'selected' : ''}>5001+ employees</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="foundedYear">Founded Year</label>
                                <input type="number" id="foundedYear" name="foundedYear"
                                       min="1800" max="2024"
                                       value="${company.foundedYear}"
                                       ${user.role != 'EmployerAdmin' ? 'readonly' : ''}>
                            </div>

                            <div class="form-group">
                                <label for="cityId">Location</label>
                                <select id="cityId" name="cityId" ${user.role != 'EmployerAdmin' ? 'disabled' : ''}>
                                    <option value="">Select City</option>
                                    <c:forEach items="${cities}" var="city">
                                        <option value="${city.cityId}" 
                                                ${company.headquartersCityId == city.cityId ? 'selected' : ''}>
                                            ${city.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <c:if test="${user.role == 'EmployerAdmin'}">
                        <div class="form-actions">
                            <a href="${pageContext.request.contextPath}/employer/dashboard" 
                            class="btn btn-secondary">
                                <i class="fas fa-times"></i> Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Save Changes
                            </button>
                        </div>
                    </c:if>
                </form>
            </div>
        </main>
    </div>

    <jsp:include page="../common/footer.jsp" />
    
    <script src="${pageContext.request.contextPath}/js/alert.js"></script>
    <script>
        // Show custom alerts for success or error messages
        <c:if test="${not empty success}">
            showSuccess('${success}', 'Success');
        </c:if>
        
        <c:if test="${not empty error}">
            showError('${error}', 'Error');
        </c:if>
    </script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>