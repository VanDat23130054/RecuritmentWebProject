<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>About Us - JobHunter</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome 6 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/about.css"/>
</head>
<body>
    <!-- Header -->
    <jsp:include page="common/header.jsp" />
    
    <!-- Hero Section -->
    <section class="about-hero">
        <div class="container">
            <h1><i class="fas fa-briefcase me-3"></i>About JobHunter</h1>
            <p>Connecting talented IT professionals with leading tech companies in Vietnam and beyond</p>
        </div>
    </section>
    
    <!-- Mission Section -->
    <section class="about-section">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-6 mb-4 mb-lg-0">
                    <h2 class="section-title">Our Mission</h2>
                    <p class="lead text-muted mb-4">
                        To revolutionize the IT job market by creating a seamless platform that connects 
                        skilled developers with their dream careers.
                    </p>
                    <p class="text-muted">
                        At JobHunter, we believe that finding the perfect job should be as exciting as 
                        writing clean code. Our platform is designed specifically for the tech industry, 
                        understanding the unique skills, technologies, and culture that make IT careers special.
                    </p>
                    <p class="text-muted">
                        Whether you're a fresh graduate looking for your first opportunity or a seasoned 
                        architect seeking new challenges, JobHunter is your gateway to the best IT opportunities 
                        in the market.
                    </p>
                </div>
                <div class="col-lg-6">
                    <div class="row g-4">
                        <div class="col-6">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-rocket"></i>
                                </div>
                                <h4>Fast Matching</h4>
                                <p>AI-powered job matching to find your perfect fit quickly</p>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-shield-alt"></i>
                                </div>
                                <h4>Verified Jobs</h4>
                                <p>All job postings are verified by our team</p>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-users"></i>
                                </div>
                                <h4>Community</h4>
                                <p>Join thousands of IT professionals</p>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-chart-line"></i>
                                </div>
                                <h4>Career Growth</h4>
                                <p>Resources to advance your career</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Stats Section -->
    <section class="stats-section">
        <div class="container">
            <div class="row">
                <div class="col-md-3 col-6">
                    <div class="stat-item">
                        <span class="stat-number">${totalJobs}</span>
                        <span class="stat-label">Active Jobs</span>
                    </div>
                </div>
                <div class="col-md-3 col-6">
                    <div class="stat-item">
                        <span class="stat-number">${totalCompanies}</span>
                        <span class="stat-label">Companies</span>
                    </div>
                </div>
                <div class="col-md-3 col-6">
                    <div class="stat-item">
                        <span class="stat-number">${totalCandidates}</span>
                        <span class="stat-label">Candidates</span>
                    </div>
                </div>
                <div class="col-md-3 col-6">
                    <div class="stat-item">
                        <span class="stat-number">${totalApplications}</span>
                        <span class="stat-label">Applications</span>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Why Choose Us Section -->
    <section class="about-section bg-light">
        <div class="container">
            <div class="text-center mb-5">
                <h2 class="section-title">Why Choose JobHunter?</h2>
                <p class="section-subtitle">We're not just another job board. We're your career partner.</p>
            </div>
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="fas fa-code"></i>
                        </div>
                        <h4>Tech-Focused</h4>
                        <p>Built by developers, for developers. We understand your skills and career aspirations in the tech industry.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="fas fa-building"></i>
                        </div>
                        <h4>Top Companies</h4>
                        <p>Partner with leading tech companies, startups, and enterprises looking for the best talent.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="fas fa-filter"></i>
                        </div>
                        <h4>Smart Filters</h4>
                        <p>Search by skill, experience level, salary range, remote options, and more to find your perfect match.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="fas fa-file-alt"></i>
                        </div>
                        <h4>Easy Applications</h4>
                        <p>Apply with one click using your saved profile and resume. Track all applications in one place.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="fas fa-bell"></i>
                        </div>
                        <h4>Job Alerts</h4>
                        <p>Get notified instantly when new jobs matching your preferences are posted.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="fas fa-lock"></i>
                        </div>
                        <h4>Privacy First</h4>
                        <p>Control who sees your profile. Your data is always protected and never sold.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Team Section -->
    <section class="about-section">
        <div class="container">
            <div class="text-center mb-5">
                <h2 class="section-title">Meet Our Team</h2>
                <p class="section-subtitle">The passionate people behind JobHunter</p>
            </div>
            <div class="row g-4 justify-content-center">
                <div class="col-lg-3 col-md-6">
                    <div class="team-card">
                        <div class="team-avatar">VN</div>
                        <div class="card-body">
                            <h5>Vo Minh Nhut</h5>
                            <p class="role">Full Stack Developer</p>
                            <p>Building robust backend systems and intuitive user interfaces</p>
                            <div class="team-social">
                                <a href="#"><i class="fab fa-github"></i></a>
                                <a href="#"><i class="fab fa-linkedin"></i></a>
                                <a href="#"><i class="fas fa-envelope"></i></a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="team-card">
                        <div class="team-avatar">HN</div>
                        <div class="card-body">
                            <h5>Nguyen Thanh Dat</h5>
                            <p class="role">Full Stack Developer</p>
                            <p>Creating seamless experiences across frontend and backend</p>
                            <div class="team-social">
                                <a href="#"><i class="fab fa-github"></i></a>
                                <a href="#"><i class="fab fa-linkedin"></i></a>
                                <a href="#"><i class="fas fa-envelope"></i></a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Tech Stack Section -->
    <section class="about-section bg-light">
        <div class="container">
            <div class="text-center">
                <h2 class="section-title">Built With Modern Technology</h2>
                <p class="section-subtitle">Our platform is powered by industry-leading technologies</p>
                <div class="tech-stack">
                    <span class="tech-badge"><i class="fab fa-java"></i>Java EE</span>
                    <span class="tech-badge"><i class="fas fa-database"></i>SQL Server</span>
                    <span class="tech-badge"><i class="fab fa-bootstrap"></i>Bootstrap 5</span>
                    <span class="tech-badge"><i class="fab fa-js"></i>JavaScript</span>
                    <span class="tech-badge"><i class="fas fa-server"></i>Apache Tomcat</span>
                    <span class="tech-badge"><i class="fas fa-code"></i>JSP/JSTL</span>
                    <span class="tech-badge"><i class="fab fa-google"></i>Google Drive API</span>
                    <span class="tech-badge"><i class="fas fa-lock"></i>BCrypt Security</span>
                </div>
            </div>
        </div>
    </section>
    
    <!-- CTA Section -->
    <section class="cta-section">
        <div class="container">
            <h2>Ready to Find Your Dream IT Job?</h2>
            <p>Join thousands of developers who have found their perfect career match with JobHunter</p>
            <div class="d-flex justify-content-center gap-3 flex-wrap">
                <a href="${pageContext.request.contextPath}/jobs" class="btn btn-light btn-lg">
                    <i class="fas fa-search me-2"></i>Browse Jobs
                </a>
                <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-light btn-lg">
                    <i class="fas fa-user-plus me-2"></i>Create Account
                </a>
            </div>
        </div>
    </section>
    
    <!-- Footer -->
    <jsp:include page="common/footer.jsp" />
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
