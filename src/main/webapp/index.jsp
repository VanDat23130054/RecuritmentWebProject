<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DatabaseMetaData" %>
<%@ page import="com.java_web.config.DB" %>
<html>
<body>
<h2>Hello World!</h2>

<%
    String dbStatus;
    String dbDetails = "";
    try (Connection conn = DB.getConnection()) {
        if (conn != null && !conn.isClosed()) {
            DatabaseMetaData md = conn.getMetaData();
            dbStatus = "Connected to SQL Server ✅";
            dbDetails = "Driver: " + md.getDriverName() + " " + md.getDriverVersion()
                    + " | DB: " + md.getDatabaseProductName() + " " + md.getDatabaseProductVersion();
        } else {
            dbStatus = "Connection object is null or closed.";
        }
    } catch (Exception e) {
        dbStatus = "Failed to connect ❌: " + e.getMessage();
    }
%>

<p><b>DB status:</b> <%= dbStatus %>
</p>
<% if (!dbDetails.isEmpty()) { %>
<p><b>Details:</b> <%= dbDetails %>
</p>
<% } %>

<hr/>
<p>
    <a href="${pageContext.request.contextPath}/roles">View all roles</a>
    &nbsp;|&nbsp;
    <a href="${pageContext.request.contextPath}/register.jsp">Register</a>
    &nbsp;|&nbsp;
    <a href="${pageContext.request.contextPath}/index.jsp">Home</a>
    <br/>

</p>
</body>
</html>