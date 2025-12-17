<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - JobHunter</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome 6 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css"/>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="auth-page">
        <div class="container">
            <div class="auth-container">
                <div class="auth-box">
                    <div class="auth-header">
                        <h2>Create Your Account</h2>
                        <p>Join thousands of IT professionals finding their dream jobs</p>
                    </div>
                    
                    <c:if test="${not empty error}">
                        <div class="alert alert-error">
                            <i class="fas fa-exclamation-circle"></i>
                            <span>${error}</span>
                        </div>
                    </c:if>
                    
                    <form action="${pageContext.request.contextPath}/register" method="POST" class="auth-form">
                        <div class="form-row">
                            <div class="form-group">
                                <label for="firstName">First Name</label>
                                <input type="text" id="firstName" name="firstName" required 
                                       placeholder="Enter first name" value="${firstName}">
                            </div>
                            
                            <div class="form-group">
                                <label for="lastName">Last Name</label>
                                <input type="text" id="lastName" name="lastName" required 
                                       placeholder="Enter last name" value="${lastName}">
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="email">Email Address</label>
                            <input type="email" id="email" name="email" required 
                                   placeholder="your.email@example.com" value="${email}">
                        </div>
                        
                        <div class="form-group">
                            <label for="password">Password</label>
                            <input type="password" id="password" name="password" required 
                                   placeholder="At least 6 characters" minlength="6">
                            <small class="form-hint">Must be at least 6 characters long</small>
                        </div>
                        
                        <div class="form-group">
                            <label for="confirmPassword">Confirm Password</label>
                            <input type="password" id="confirmPassword" name="confirmPassword" required 
                                   placeholder="Re-enter your password" minlength="6">
                        </div>
                        
                        <div class="form-group">
                            <label>I want to register as:</label>
                            <div class="radio-group">
                                <label class="radio-label">
                                    <input type="radio" name="role" value="Candidate" ${empty role || role == 'Candidate' ? 'checked' : ''} 
                                           onchange="toggleRecruiterFields()">
                                    <span class="radio-custom"></span>
                                    <div class="radio-content">
                                        <strong>Job Seeker</strong>
                                        <small>Looking for IT jobs</small>
                                    </div>
                                </label>
                                
                                <label class="radio-label">
                                    <input type="radio" name="role" value="Recruiter" ${role == 'Recruiter' ? 'checked' : ''}
                                           onchange="toggleRecruiterFields()">
                                    <span class="radio-custom"></span>
                                    <div class="radio-content">
                                        <strong>Employer/Recruiter</strong>
                                        <small>Hiring IT talent</small>
                                    </div>
                                </label>
                            </div>
                        </div>
                        
                        <!-- Recruiter-specific fields -->
                        <div id="recruiterFields" style="display: none;">
                            <div class="form-group">
                                <label for="companyName">Company Name <span class="text-danger">*</span></label>
                                <input type="text" id="companyName" name="companyName" 
                                       placeholder="Enter your company name" value="${companyName}">
                                <small class="form-hint">If your company exists, we'll link you to it</small>
                            </div>
                            
                            <div class="form-group">
                                <label for="recruiterTitle">Your Title/Position</label>
                                <input type="text" id="recruiterTitle" name="recruiterTitle" 
                                       placeholder="e.g., HR Manager, Talent Acquisition Specialist" value="${recruiterTitle}">
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label class="checkbox-label">
                                <input type="checkbox" name="terms" required>
                                <span class="checkbox-custom"></span>
                                <span>I agree to the <a href="${pageContext.request.contextPath}/terms" target="_blank">Terms of Service</a> 
                                and <a href="${pageContext.request.contextPath}/privacy" target="_blank">Privacy Policy</a></span>
                            </label>
                        </div>
                        
                        <button type="submit" class="btn btn-primary btn-block btn-lg">
                            <i class="fas fa-user-plus"></i> Create Account
                        </button>
                    </form>
                    
                    <div class="auth-divider">
                        <span>or</span>
                    </div>
                    
                    <div class="social-login">
                        <button class="btn btn-social btn-google">
                            <i class="fab fa-google"></i> Continue with Google
                        </button>
                        <button class="btn btn-social btn-linkedin">
                            <i class="fab fa-linkedin"></i> Continue with LinkedIn
                        </button>
                    </div>
                    
                    <div class="auth-footer">
                        Already have an account? 
                        <a href="${pageContext.request.contextPath}/login">Sign In</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script>
        // Toggle recruiter fields
        function toggleRecruiterFields() {
            const recruiterRadio = document.querySelector('input[name="role"][value="Recruiter"]');
            const recruiterFields = document.getElementById('recruiterFields');
            const companyNameField = document.getElementById('companyName');
            
            if (recruiterRadio.checked) {
                recruiterFields.style.display = 'block';
                companyNameField.required = true;
            } else {
                recruiterFields.style.display = 'none';
                companyNameField.required = false;
            }
        }
        
        // Initialize on page load
        document.addEventListener('DOMContentLoaded', function() {
            toggleRecruiterFields();
        });
        
        // Password match validation
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');
        
        confirmPassword.addEventListener('input', function() {
            if (password.value !== confirmPassword.value) {
                confirmPassword.setCustomValidity('Passwords do not match');
            } else {
                confirmPassword.setCustomValidity('');
            }
        });
    </script>
</body>
</html>
