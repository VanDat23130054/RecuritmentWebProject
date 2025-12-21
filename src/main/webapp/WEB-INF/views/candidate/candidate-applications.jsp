<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Applications</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="../common/header.jsp" />

<div class="container my-5">
    <h1>My Applications</h1>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success">${sessionScope.message}</div>
        <c:remove var="message" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger">${sessionScope.error}</div>
        <c:remove var="error" scope="session" />
    </c:if>

    <c:if test="${empty applications}">
        <p>You have not applied to any jobs yet.</p>
    </c:if>

    <c:if test="${not empty applications}">
        <div class="table-responsive">
            <table class="table table-striped" id="applications-table">
                <thead>
                    <tr>
                        <th>Job</th>
                        <th>Company</th>
                        <th>Applied At</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="app" items="${applications}">
                        <tr id="app-row-${app.applicationId}">
                            <td>
                                <a href="${pageContext.request.contextPath}/job/${app.jobId}">${app.jobTitle}</a>
                            </td>
                            <td>${app.companyName}</td>
                            <td><fmt:formatDate value="${app.appliedAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td class="app-status" data-app-id="${app.applicationId}">${app.status}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${app.status == 'Applied' || app.status == 'Under Review'}">
                                        <form class="withdraw-form d-inline" data-app-id="${app.applicationId}" method="post" action="${pageContext.request.contextPath}/candidate/applications">
                                            <input type="hidden" name="action" value="withdraw" />
                                            <input type="hidden" name="applicationId" value="${app.applicationId}" />
                                            <button type="submit" class="btn btn-sm btn-danger withdraw-btn">Withdraw</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        -
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <nav aria-label="Page navigation">
            <ul class="pagination">
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:url var="pageUrl" value="/candidate/applications">
                        <c:param name="page" value="${i}" />
                        <c:if test="${not empty selectedStatus}">
                            <c:param name="status" value="${selectedStatus}" />
                        </c:if>
                    </c:url>
                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                        <a class="page-link" href="${pageUrl}">${i}</a>
                    </li>
                </c:forEach>
            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
document.addEventListener('DOMContentLoaded', function () {
    // Attach submit handler to all withdraw forms
    document.querySelectorAll('.withdraw-form').forEach(function(form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            var appId = form.getAttribute('data-app-id');
            var btn = form.querySelector('.withdraw-btn');
            if (!confirm('Are you sure you want to withdraw this application?')) {
                return;
            }
            btn.disabled = true;
            btn.textContent = 'Withdrawing...';

            var formData = new FormData(form);

            fetch(form.action, {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    'Accept': 'application/json'
                },
                body: formData,
                credentials: 'same-origin'
            })
            .then(function(response) {
                return response.json().catch(function() { return { success: false, message: 'Invalid server response' }; });
            })
            .then(function(json) {
                if (json && json.success) {
                    // Update status cell to Withdrawn
                    var statusCell = document.querySelector('.app-status[data-app-id="' + appId + '"]');
                    if (statusCell) {
                        statusCell.textContent = json.newStatus || 'Withdrawn';
                    }
                    // Remove the withdraw form to prevent further action
                    form.parentNode.innerHTML = '-';
                } else {
                    alert(json && json.message ? json.message : 'Unable to withdraw application');
                    btn.disabled = false;
                    btn.textContent = 'Withdraw';
                }
            })
            .catch(function(err) {
                console.error('Withdraw error', err);
                alert('Server error while withdrawing application');
                btn.disabled = false;
                btn.textContent = 'Withdraw';
            });
        });
    });
});
</script>
</body>
</html>