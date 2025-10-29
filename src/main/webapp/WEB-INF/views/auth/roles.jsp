<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.java_web.model.auth.Role" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Roles</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<h2>All Roles</h2>

<% String error = (String) request.getAttribute("error");
   if (error != null) { %>
  <p style="color:red;">DB error: <%= error %></p>
<% } %>

<%
  List<Role> roles = (List<Role>) request.getAttribute("roles");
  if (roles != null && !roles.isEmpty()) {
%>
  <table border="1" cellpadding="6" cellspacing="0">
    <tr><th>Role</th><th>Description</th></tr>
    <% for (Role r : roles) { %>
      <tr>
        <td><%= r.getRoleName() %></td>
        <td><%= r.getDescription() %></td>
      </tr>
    <% } %>
  </table>
<% } else { %>
  <p>No roles found.</p>
<% } %>
</body>
</html>