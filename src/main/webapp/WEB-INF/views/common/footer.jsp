<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<footer class="main-footer">
    <div class="container">
        <div class="footer-content">
            <!-- About Section -->
            <div class="footer-column">
                <h3><i class="fa-solid fa-briefcase"></i> JobHunter</h3>
                <p>The best platform to find IT jobs in Vietnam. Connect with top employers and advance your career.</p>
                <div class="social-links">
                    <a href="https://github.com/VanDat23130054/RecuritmentWebProject" target="_blank" rel="noopener">
                        <i class="fa-brands fa-github"></i>
                    </a>
                    <a href="#" target="_blank">
                        <i class="fa-brands fa-facebook"></i>
                    </a>
                    <a href="#" target="_blank">
                        <i class="fa-brands fa-linkedin"></i>
                    </a>
                    <a href="#" target="_blank">
                        <i class="fa-brands fa-twitter"></i>
                    </a>
                </div>
            </div>

            <!-- Quick Links -->
            <div class="footer-column">
                <h4>For Candidates</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/jobs"><i class="fa-solid fa-angle-right"></i> Browse Jobs</a></li>
                    <li><a href="${pageContext.request.contextPath}/companies"><i class="fa-solid fa-angle-right"></i> Companies</a></li>
                    <li><a href="${pageContext.request.contextPath}/profile"><i class="fa-solid fa-angle-right"></i> My Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/saved-jobs"><i class="fa-solid fa-angle-right"></i> Saved Jobs</a></li>
                </ul>
            </div>

            <!-- Employer Links -->
            <div class="footer-column">
                <h4>For Employers</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/employer/post-job"><i class="fa-solid fa-angle-right"></i> Post a Job</a></li>
                    <li><a href="${pageContext.request.contextPath}/employer/dashboard"><i class="fa-solid fa-angle-right"></i> Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/pricing"><i class="fa-solid fa-angle-right"></i> Pricing</a></li>
                    <li><a href="${pageContext.request.contextPath}/contact"><i class="fa-solid fa-angle-right"></i> Contact Us</a></li>
                </ul>
            </div>

            <!-- Support -->
            <div class="footer-column">
                <h4>Support</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/about"><i class="fa-solid fa-angle-right"></i> About Us</a></li>
                    <li><a href="${pageContext.request.contextPath}/faq"><i class="fa-solid fa-angle-right"></i> FAQ</a></li>
                    <li><a href="${pageContext.request.contextPath}/privacy"><i class="fa-solid fa-angle-right"></i> Privacy Policy</a></li>
                    <li><a href="${pageContext.request.contextPath}/terms"><i class="fa-solid fa-angle-right"></i> Terms of Service</a></li>
                </ul>
            </div>
        </div>

        <!-- Footer Bottom -->
        <div class="footer-bottom">
            <p>&copy; 2025 JobHunter. All rights reserved. | 
                <a href="https://github.com/VanDat23130054/RecuritmentWebProject" target="_blank" rel="noopener">
                    <i class="fa-brands fa-github"></i> View on GitHub
                </a>
            </p>
        </div>
    </div>
</footer>