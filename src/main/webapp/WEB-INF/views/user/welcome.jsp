<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.java_web.models.User" %>

<%
    User user = (User) request.getAttribute("user");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome Page</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>
<div class="welcome-container">
    <h1>
        Welcome
        <i><%= user.getFirstName() %> <%= user.getLastName() %></i>
        of
        <b><%= user.getCompany() %></b> company.
    </h1>

    <p>// Hiển thị các thông tin khác</p>

    <h3>You can log in with:</h3>
    <div class="info-box">
        <p>username: <a href="mailto:<%= user.getEmail() %>"><%= user.getEmail() %></a></p>
        <p>password: <%= user.getPassword() %></p>
    </div>

    <p class="thank-text">Thank you for register!</p>
</div>
</body>
</html>
