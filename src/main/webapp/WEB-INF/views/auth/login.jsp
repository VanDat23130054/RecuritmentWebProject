<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - JobHunter</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container auth-container">
        <div class="auth-box">
            <h2>Sign In</h2>
            
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${error}
                </div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/login" method="POST">
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required 
                           placeholder="Enter your email" value="${param.email}">
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required 
                           placeholder="Enter your password">
                </div>
                
                <div class="form-group checkbox-group">
                    <label>
                        <input type="checkbox" name="remember"> Remember me
                    </label>
                    <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-password">
                        Forgot password?
                    </a>
                </div>
                
                <button type="submit" class="btn btn-primary btn-block">Sign In</button>
            </form>
            
            <div class="auth-footer">
                Don't have an account? 
                <a href="${pageContext.request.contextPath}/register">Sign Up</a>
            </div>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>
