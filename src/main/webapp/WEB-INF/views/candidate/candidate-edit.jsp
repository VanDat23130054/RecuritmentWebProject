<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    com.java_web.model.candidate.Candidate candidate = (com.java_web.model.candidate.Candidate) request.getAttribute("candidate");
    if (candidate == null) {
        candidate = new com.java_web.model.candidate.Candidate();
    }
%>
<c:choose>
  <c:when test="${param.partial != 'true'}">
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Edit Profile</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/employer-dashboard.css">
    </head>
    <body>
    <jsp:include page="../common/header.jsp" />
    <div class="dashboard-container">
        <!-- Sidebar Toggle Button (Mobile/Tablet) -->
        <button class="sidebar-toggle" aria-label="Toggle Sidebar">
            <i class="fas fa-bars"></i>
        </button>
        <jsp:include page="_sidebar.jsp" />
        <main class="dashboard-main">
            <div class="dashboard-header">
                <div class="header-content">
                    <h1><i class="fas fa-edit me-2"></i>Edit Profile</h1>
                    <p class="text-muted">Update your professional information</p>
                </div>
            </div>
            <c:if test="${not empty error}">
                <div class="alert alert-danger"><i class="fas fa-exclamation-circle me-2"></i><c:out value="${error}"/></div>
            </c:if>
  </c:when>
  <c:otherwise>
    <%-- partial mode: render only the form fragment --%>
  </c:otherwise>
</c:choose>

<div class="dashboard-card">
    <div class="card-header">
        <h3>Professional Information</h3>
    </div>
    <div class="card-body">
        <form method="post" action="<%=request.getContextPath()%>/candidate/profile">
            <div class="row mb-3">
                <div class="col-md-3 text-center mb-3 mb-md-0">
                    <c:if test="${not empty candidate.avatarUrl}">
                        <img src="${candidate.avatarUrl}" alt="Avatar" class="rounded-circle img-fluid" style="width: 150px; height: 150px; object-fit: cover;" />
                    </c:if>
                    <div class="form-group mt-2">
                        <label for="avatarUrl" class="form-label">Avatar URL</label>
                        <input class="form-control form-control-sm" type="text" id="avatarUrl" name="avatarUrl" value="<%= candidate.getAvatarUrl() != null ? candidate.getAvatarUrl() : "" %>" />
                        <small class="text-muted">Paste an image URL to update your avatar</small>
                    </div>
                </div>

                <div class="col-md-9">
                    <div class="mb-3">
                        <label for="fullName" class="form-label">Full Name</label>
                        <input class="form-control" type="text" id="fullName" name="fullName" value="<%= candidate.getFullName() != null ? candidate.getFullName() : "" %>" required/>
                    </div>

                    <div class="mb-3">
                        <label for="headline" class="form-label">Professional Headline</label>
                        <input class="form-control" type="text" id="headline" name="headline" value="<%= candidate.getHeadline() != null ? candidate.getHeadline() : "" %>" placeholder="e.g., Senior Software Engineer"/>
                    </div>

                    <div class="mb-3">
                        <label for="summary" class="form-label">Professional Summary</label>
                        <textarea class="form-control" id="summary" name="summary" rows="4"><%= candidate.getSummary() != null ? candidate.getSummary() : "" %></textarea>
                    </div>

                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label for="yearsOfExperience" class="form-label">Years of Experience</label>
                            <input class="form-control" type="number" id="yearsOfExperience" name="yearsOfExperience" value="<%= candidate.getYearsOfExperience() != null ? candidate.getYearsOfExperience() : "" %>" step="0.5" min="0"/>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="cityId" class="form-label">City ID</label>
                            <input class="form-control" type="text" id="cityId" name="cityId" value="<%= candidate.getCityId() != null ? candidate.getCityId() : "" %>" />
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="countryId" class="form-label">Country ID</label>
                            <input class="form-control" type="text" id="countryId" name="countryId" value="<%= candidate.getCountryId() != null ? candidate.getCountryId() : "" %>" />
                        </div>
                    </div>

                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" id="publicProfile" name="publicProfile" <%= candidate.isPublicProfile() ? "checked" : "" %> />
                        <label class="form-check-label" for="publicProfile">Make my profile public</label>
                    </div>

                    <div class="d-flex gap-2">
                        <button class="btn btn-primary" type="submit"><i class="fas fa-save me-2"></i>Save Changes</button>
                        <c:choose>
                          <c:when test="${param.partial == 'true'}">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"><i class="fas fa-times me-2"></i>Cancel</button>
                          </c:when>
                          <c:otherwise>
                            <a class="btn btn-secondary" href="<%=request.getContextPath()%>/candidate/profile"><i class="fas fa-times me-2"></i>Cancel</a>
                          </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<c:choose>
  <c:when test="${param.partial != 'true'}">
        </main>
    </div>
    <jsp:include page="../common/footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/alert.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    </body>
    </html>
  </c:when>
  <c:otherwise>
    <%-- partial mode: nothing else to output --%>
  </c:otherwise>
</c:choose>