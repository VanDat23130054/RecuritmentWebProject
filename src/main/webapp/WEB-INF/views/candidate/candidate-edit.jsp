<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    com.java_web.model.candidate.Candidate candidate = (com.java_web.model.candidate.Candidate) request.getAttribute("candidate");
    if (candidate == null) {
        candidate = new com.java_web.model.candidate.Candidate();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Profile</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
    <!-- Add Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
<div class="container">
    <h1>Edit Profile</h1>

    <c:if test="${not empty error}">
        <div class="error"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="<%=request.getContextPath()%>/candidate/profile">
        <div class="candidate-form">
            <div class="row">
                <div class="col-md-3 text-center">
                    <c:if test="${not empty candidate.avatarUrl}">
                        <img src="${candidate.avatarUrl}" alt="Avatar" class="avatar img-fluid mb-2" />
                    </c:if>
                    <div class="form-group">
                        <label for="avatarUrl">Avatar URL</label>
                        <input class="form-control" type="text" id="avatarUrl" name="avatarUrl" value="<%= candidate.getAvatarUrl() != null ? candidate.getAvatarUrl() : "" %>" />
                    </div>
                    <small class="text-muted">Paste an image URL to update your avatar</small>
                </div>

                <div class="col-md-9">
                    <div class="mb-3">
                        <label for="fullName" class="form-label">Full Name</label>
                        <input class="form-control" type="text" id="fullName" name="fullName" value="<%= candidate.getFullName() != null ? candidate.getFullName() : "" %>" />
                    </div>

                    <div class="mb-3">
                        <label for="headline" class="form-label">Headline</label>
                        <input class="form-control" type="text" id="headline" name="headline" value="<%= candidate.getHeadline() != null ? candidate.getHeadline() : "" %>" />
                    </div>

                    <div class="mb-3">
                        <label for="summary" class="form-label">Summary</label>
                        <textarea class="form-control" id="summary" name="summary" rows="6"><%= candidate.getSummary() != null ? candidate.getSummary() : "" %></textarea>
                    </div>

                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label for="yearsOfExperience" class="form-label">Years of Experience</label>
                            <input class="form-control" type="text" id="yearsOfExperience" name="yearsOfExperience" value="<%= candidate.getYearsOfExperience() != null ? candidate.getYearsOfExperience() : "" %>" />
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

                    <div class="form-actions mt-3">
                        <button class="btn btn-primary" type="submit">Save</button>
                        <a class="btn btn-secondary" href="<%=request.getContextPath()%>/candidate/profile">Cancel</a>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<!-- Add Bootstrap JS and dependencies -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>