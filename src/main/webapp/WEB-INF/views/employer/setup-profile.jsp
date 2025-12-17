<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Setup Recruiter Profile - JobHunter</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="auth-page">
        <div class="container">
            <div class="auth-container" style="max-width: 600px;">
                <div class="auth-box">
                    <div class="auth-header">
                        <h2><i class="fas fa-building"></i> Complete Your Recruiter Profile</h2>
                        <p>Just one more step to start posting jobs and finding talent</p>
                    </div>
                    
                    <c:if test="${not empty error}">
                        <div class="alert alert-error">
                            <i class="fas fa-exclamation-circle"></i>
                            <span>${error}</span>
                        </div>
                    </c:if>
                    
                    <form action="${pageContext.request.contextPath}/employer/setup-profile" method="POST" class="auth-form">
                        <div class="form-group">
                            <label for="companyId">Select Your Company</label>
                            <select id="companyId" name="companyId" required onchange="toggleNewCompany()">
                                <option value="">-- Select a company --</option>
                                <c:forEach items="${companies}" var="company">
                                    <option value="${company.companyId}">${company.name}</option>
                                </c:forEach>
                                <option value="new">+ Create New Company</option>
                            </select>
                        </div>
                        
                        <div id="newCompanyField" style="display: none;">
                            <div class="form-group">
                                <label for="newCompanyName">New Company Name <span class="text-danger">*</span></label>
                                <input type="text" id="newCompanyName" name="newCompanyName" 
                                       placeholder="Enter company name">
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="title">Your Job Title</label>
                            <input type="text" id="title" name="title" 
                                   placeholder="e.g., HR Manager, Talent Acquisition Specialist"
                                   value="Recruiter">
                        </div>
                        
                        <div class="alert" style="background: #f0f9ff; border-color: #0ea5e9; color: #0c4a6e; margin: 1.5rem 0;">
                            <i class="fas fa-info-circle"></i>
                            <div>
                                <strong>What's Next?</strong>
                                <p style="margin: 0.5rem 0 0 0; font-size: 0.875rem;">
                                    After setup, you'll be able to post jobs, review applications, 
                                    and manage your hiring pipeline from your dashboard.
                                </p>
                            </div>
                        </div>
                        
                        <button type="submit" class="btn btn-primary btn-block btn-lg">
                            <i class="fas fa-check"></i> Complete Setup
                        </button>
                        
                        <div style="text-align: center; margin-top: 1rem;">
                            <a href="${pageContext.request.contextPath}/logout" class="text-muted">
                                <i class="fas fa-sign-out-alt"></i> Logout
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="../common/footer.jsp" />
    
    <script>
        function toggleNewCompany() {
            const companySelect = document.getElementById('companyId');
            const newCompanyField = document.getElementById('newCompanyField');
            const newCompanyInput = document.getElementById('newCompanyName');
            
            if (companySelect.value === 'new') {
                newCompanyField.style.display = 'block';
                newCompanyInput.required = true;
            } else {
                newCompanyField.style.display = 'none';
                newCompanyInput.required = false;
            }
        }
    </script>
</body>
</html>
