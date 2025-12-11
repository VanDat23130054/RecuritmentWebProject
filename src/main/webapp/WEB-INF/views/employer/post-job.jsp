<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Post New Job - JobHunter</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-forms.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alert.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" />
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="dashboard-container">
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
                <a href="${pageContext.request.contextPath}/employer/post-job" class="nav-item active">
                    <i class="fas fa-plus-circle"></i> Post New Job
                </a>
                <a href="${pageContext.request.contextPath}/employer/applications" class="nav-item">
                    <i class="fas fa-file-alt"></i> Applications
                </a>
                <a href="${pageContext.request.contextPath}/employer/company-profile" class="nav-item">
                    <i class="fas fa-building"></i> Company Profile
                </a>
                <a href="${pageContext.request.contextPath}/employer/settings" class="nav-item">
                    <i class="fas fa-cog"></i> Settings
                </a>
            </nav>
        </aside>

        <main class="dashboard-main">
            <div class="dashboard-header">
                <div class="header-content">
                    <h1><i class="fas fa-plus-circle"></i> Post New Job</h1>
                    <p class="text-muted">Fill in the details below to create a new job posting</p>
                </div>
            </div>

            <div class="form-container">
                <c:if test="${not empty error}">
                    <div class="alert-error">
                        <i class="fas fa-exclamation-circle"></i> ${error}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/employer/post-job" method="POST" id="postJobForm">
                    <!-- Basic Information -->
                    <div class="form-section">
                        <h3><i class="fas fa-info-circle"></i> Basic Information</h3>
                        
                        <div class="form-group">
                            <label for="title">Job Title <span class="required">*</span></label>
                            <input type="text" id="title" name="title" required
                                   placeholder="e.g. Senior Java Developer">
                        </div>

                        <div class="form-group">
                            <label for="description">Job Description <span class="required">*</span></label>
                            <textarea id="description" name="description" required
                                      placeholder="Provide a detailed description of the job role, responsibilities, and what the successful candidate will be doing..."></textarea>
                        </div>

                        <div class="form-group">
                            <label for="requirements">Requirements</label>
                            <textarea id="requirements" name="requirements"
                                      placeholder="List the required skills, experience, education, and qualifications..."></textarea>
                        </div>

                        <div class="form-group">
                            <label for="benefits">Benefits</label>
                            <textarea id="benefits" name="benefits"
                                      placeholder="Describe the benefits and perks offered..."></textarea>
                        </div>
                    </div>

                    <!-- Job Details -->
                    <div class="form-section">
                        <h3><i class="fas fa-briefcase"></i> Job Details</h3>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="cityId">Location <span class="required">*</span></label>
                                <select id="cityId" name="cityId" required>
                                    <option value="">Select City</option>
                                    <c:forEach items="${cities}" var="city">
                                        <option value="${city.cityId}">${city.name}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="employmentType">Employment Type <span class="required">*</span></label>
                                <select id="employmentType" name="employmentType" required>
                                    <option value="">Select Type</option>
                                    <c:forEach items="${employmentTypes}" var="type">
                                        <option value="${type.id}">${type.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="seniorityLevel">Seniority Level</label>
                                <select id="seniorityLevel" name="seniorityLevel">
                                    <option value="">Select Level</option>
                                    <c:forEach items="${seniorityLevels}" var="level">
                                        <option value="${level.id}">${level.name}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="remoteType">Work Mode</label>
                                <select id="remoteType" name="remoteType">
                                    <option value="">Select Mode</option>
                                    <c:forEach items="${remoteTypes}" var="remote">
                                        <option value="${remote.id}">${remote.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="skillIds">Required Skills</label>
                            <select id="skillIds" name="skillIds[]" multiple="multiple" class="select2-skills">
                                <c:forEach items="${skills}" var="skill">
                                    <option value="${skill.skillId}">${skill.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <!-- Salary Information -->
                    <div class="form-section">
                        <h3><i class="fas fa-dollar-sign"></i> Salary Information</h3>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="salaryMin">Minimum Salary</label>
                                <input type="number" id="salaryMin" name="salaryMin" min="0"
                                       placeholder="e.g. 1000">
                            </div>

                            <div class="form-group">
                                <label for="salaryMax">Maximum Salary</label>
                                <input type="number" id="salaryMax" name="salaryMax" min="0"
                                       placeholder="e.g. 2000">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="currency">Currency</label>
                            <select id="currency" name="currency">
                                <option value="USD" selected>USD</option>
                                <option value="VND">VND</option>
                                <option value="EUR">EUR</option>
                            </select>
                        </div>
                    </div>

                    <!-- Additional Settings -->
                    <div class="form-section">
                        <h3><i class="fas fa-cog"></i> Additional Settings</h3>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="expiresAt">Expiration Date</label>
                                <input type="date" id="expiresAt" name="expiresAt">
                            </div>

                            <div class="form-group">
                                <label for="statusId">Status</label>
                                <select id="statusId" name="statusId">
                                    <option value="1">Draft</option>
                                    <option value="2" selected>Published</option>
                                    <option value="3">Closed</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/employer/dashboard" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Cancel
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-check"></i> Post Job
                        </button>
                    </div>
                </form>
            </div>
        </main>
    </div>

    <jsp:include page="../common/footer.jsp" />
    
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/alert.js"></script>
    <script>
        $(document).ready(function() {
            // Initialize Select2 for skills
            $('.select2-skills').select2({
                placeholder: 'Select skills...',
                allowClear: true,
                width: '100%'
            });

            // Form validation
            $('#postJobForm').on('submit', function(e) {
                const title = $('#title').val().trim();
                const description = $('#description').val().trim();
                const cityId = $('#cityId').val();
                const employmentType = $('#employmentType').val();

                if (!title || !description || !cityId || !employmentType) {
                    e.preventDefault();
                    showError('Please fill in all required fields', 'Validation Error');
                    return false;
                }

                const salaryMin = parseInt($('#salaryMin').val());
                const salaryMax = parseInt($('#salaryMax').val());

                if (salaryMin && salaryMax && salaryMin > salaryMax) {
                    e.preventDefault();
                    showError('Minimum salary cannot be greater than maximum salary', 'Validation Error');
                    return false;
                }

                return true;
            });
        });
    </script>
</body>
</html>
