<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analytics & Insights - JobHunter</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employer-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <div class="dashboard-container">
        <!-- Sidebar Toggle Button (Mobile/Tablet) -->
        <button class="sidebar-toggle" aria-label="Toggle Sidebar">
            <i class="fas fa-bars"></i>
        </button>

        <!-- Sidebar Navigation -->
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
                <a href="${pageContext.request.contextPath}/employer/post-job" class="nav-item">
                    <i class="fas fa-plus-circle"></i> Post New Job
                </a>
                <a href="${pageContext.request.contextPath}/employer/applications" class="nav-item">
                    <i class="fas fa-file-alt"></i> Applications
                </a>
                <a href="${pageContext.request.contextPath}/employer/company-profile" class="nav-item">
                    <i class="fas fa-building"></i> Company Profile
                </a>
                <a href="${pageContext.request.contextPath}/employer/graph" class="nav-item active">
                    <i class="fa-solid fa-chart-line"></i> Analytics
                </a>
            </nav>
        </aside>

        <!-- Main Content -->
        <main class="dashboard-main">
            <!-- Page Header -->
            <div class="dashboard-header">
                <div class="header-content">
                    <h1><i class="fas fa-chart-bar"></i> Analytics & Insights</h1>
                    <p class="text-muted">Track your recruitment performance and trends</p>
                </div>
            </div>

            <!-- Charts Grid -->
            <div class="row g-4">
                <!-- Application Status Distribution -->
                <div class="col-md-6">
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3><i class="fas fa-chart-pie"></i> Application Status Distribution</h3>
                        </div>
                        <div class="card-body">
                            <canvas id="statusChart" height="300"></canvas>
                        </div>
                    </div>
                </div>

                <!-- Application Funnel -->
                <div class="col-md-6">
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3><i class="fas fa-filter"></i> Recruitment Funnel</h3>
                        </div>
                        <div class="card-body">
                            <canvas id="funnelChart" height="300"></canvas>
                        </div>
                    </div>
                </div>

                <!-- Applications Over Time -->
                <div class="col-12">
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3><i class="fas fa-chart-line"></i> Applications Timeline (Last 30 Days)</h3>
                        </div>
                        <div class="card-body">
                            <canvas id="timelineChart" height="100"></canvas>
                        </div>
                    </div>
                </div>

                <!-- Job Performance Comparison -->
                <div class="col-12">
                    <div class="dashboard-card">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h3><i class="fas fa-chart-bar"></i> Job Performance Comparison</h3>
                            <span class="badge bg-secondary">Top 5 Jobs</span>
                        </div>
                        <div class="card-body" style="height: 450px;">
                            <canvas id="performanceChart"></canvas>
                        </div>
                    </div>
                </div>

                <!-- Conversion Rates Cards -->
                <div class="col-md-3">
                    <div class="stat-card">
                        <div class="stat-icon" style="background: #6366F1;">
                            <i class="fas fa-percentage"></i>
                        </div>
                        <div class="stat-content">
                            <h3 id="viewToAppRate">0%</h3>
                            <p>View to Application</p>
                            <span class="stat-detail">Conversion Rate</span>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="stat-card">
                        <div class="stat-icon" style="background: #10B981;">
                            <i class="fas fa-percentage"></i>
                        </div>
                        <div class="stat-content">
                            <h3 id="appToReviewRate">0%</h3>
                            <p>Application to Review</p>
                            <span class="stat-detail">Conversion Rate</span>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="stat-card">
                        <div class="stat-icon" style="background: #F59E0B;">
                            <i class="fas fa-percentage"></i>
                        </div>
                        <div class="stat-content">
                            <h3 id="reviewToInterviewRate">0%</h3>
                            <p>Review to Interview</p>
                            <span class="stat-detail">Conversion Rate</span>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="stat-card">
                        <div class="stat-icon" style="background: #EF4444;">
                            <i class="fas fa-percentage"></i>
                        </div>
                        <div class="stat-content">
                            <h3 id="interviewToOfferRate">0%</h3>
                            <p>Interview to Offer</p>
                            <span class="stat-detail">Conversion Rate</span>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <jsp:include page="../common/footer.jsp" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Parse data from server
        const statusCounts = ${statusCounts != null ? '{' : '{}'}
            <c:if test="${statusCounts != null}">
                all: ${statusCounts.all != null ? statusCounts.all : 0},
                applied: ${statusCounts.applied != null ? statusCounts.applied : 0},
                underReview: ${statusCounts.underReview != null ? statusCounts.underReview : 0},
                interview: ${statusCounts.interview != null ? statusCounts.interview : 0},
                rejected: ${statusCounts.rejected != null ? statusCounts.rejected : 0}
            </c:if>
        };

        const funnelData = ${funnelData != null ? '{' : '{}'}
            <c:if test="${funnelData != null}">
                totalViews: ${funnelData.totalViews != null ? funnelData.totalViews : 0},
                totalApplications: ${funnelData.totalApplications != null ? funnelData.totalApplications : 0},
                underReview: ${funnelData.underReview != null ? funnelData.underReview : 0},
                interviewed: ${funnelData.interviewed != null ? funnelData.interviewed : 0},
                offered: ${funnelData.offered != null ? funnelData.offered : 0},
                viewToAppRate: ${funnelData.viewToAppRate != null ? funnelData.viewToAppRate : 0},
                appToReviewRate: ${funnelData.appToReviewRate != null ? funnelData.appToReviewRate : 0},
                reviewToInterviewRate: ${funnelData.reviewToInterviewRate != null ? funnelData.reviewToInterviewRate : 0},
                interviewToOfferRate: ${funnelData.interviewToOfferRate != null ? funnelData.interviewToOfferRate : 0}
            </c:if>
        };

        const timelineData = [
            <c:forEach items="${applicationsTimeline}" var="data" varStatus="status">
                {
                    date: '<fmt:formatDate value="${data.date}" pattern="MMM dd"/>',
                    count: ${data.count}
                }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        const jobPerformance = [
            <c:forEach items="${jobPerformance}" var="job" varStatus="status">
                {
                    title: '${job.title}',
                    views: ${job.viewsCount},
                    applications: ${job.applicationsCount},
                    conversionRate: ${job.conversionRate}
                }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        // Update conversion rate cards
        document.getElementById('viewToAppRate').textContent = funnelData.viewToAppRate.toFixed(1) + '%';
        document.getElementById('appToReviewRate').textContent = funnelData.appToReviewRate.toFixed(1) + '%';
        document.getElementById('reviewToInterviewRate').textContent = funnelData.reviewToInterviewRate.toFixed(1) + '%';
        document.getElementById('interviewToOfferRate').textContent = funnelData.interviewToOfferRate.toFixed(1) + '%';

        // Chart 1: Application Status Distribution (Pie Chart)
        const statusCtx = document.getElementById('statusChart').getContext('2d');
        new Chart(statusCtx, {
            type: 'pie',
            data: {
                labels: ['Applied', 'Under Review', 'Interview', 'Rejected'],
                datasets: [{
                    data: [
                        statusCounts.applied,
                        statusCounts.underReview,
                        statusCounts.interview,
                        statusCounts.rejected
                    ],
                    backgroundColor: [
                        '#3B82F6', // Blue
                        '#10B981', // Green
                        '#F59E0B', // Orange
                        '#EF4444'  // Red
                    ],
                    borderWidth: 2,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const label = context.label || '';
                                const value = context.parsed || 0;
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                                return label + ': ' + value + ' (' + percentage + '%)';
                            }
                        }
                    }
                }
            }
        });

        // Chart 2: Recruitment Funnel (Bar Chart)
        const funnelCtx = document.getElementById('funnelChart').getContext('2d');
        new Chart(funnelCtx, {
            type: 'bar',
            data: {
                labels: ['Views', 'Applications', 'Under Review', 'Interviews', 'Offers'],
                datasets: [{
                    label: 'Count',
                    data: [
                        funnelData.totalViews,
                        funnelData.totalApplications,
                        funnelData.underReview,
                        funnelData.interviewed,
                        funnelData.offered
                    ],
                    backgroundColor: [
                        '#6366F1',
                        '#3B82F6',
                        '#10B981',
                        '#F59E0B',
                        '#EF4444'
                    ],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            afterLabel: function(context) {
                                const rates = [
                                    funnelData.viewToAppRate,
                                    funnelData.appToReviewRate,
                                    funnelData.reviewToInterviewRate,
                                    funnelData.interviewToOfferRate
                                ];
                                if (context.dataIndex < rates.length) {
                                    return 'Conversion: ' + rates[context.dataIndex].toFixed(1) + '%';
                                }
                                return '';
                            }
                        }
                    }
                }
            }
        });

        // Chart 3: Applications Timeline (Line Chart)
        const timelineCtx = document.getElementById('timelineChart').getContext('2d');
        new Chart(timelineCtx, {
            type: 'line',
            data: {
                labels: timelineData.map(d => d.date),
                datasets: [{
                    label: 'Applications',
                    data: timelineData.map(d => d.count),
                    borderColor: '#3B82F6',
                    backgroundColor: 'rgba(59, 130, 246, 0.1)',
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });

        // Chart 4: Job Performance Comparison (Grouped Bar Chart)
        // Limit to top 5 jobs for better visualization
        const topJobs = jobPerformance.slice(0, 5);
        
        const performanceCtx = document.getElementById('performanceChart').getContext('2d');
        new Chart(performanceCtx, {
            type: 'bar',
            data: {
                labels: topJobs.map(j => {
                    // Truncate long titles for better display
                    if (j.title.length > 25) {
                        return j.title.substring(0, 25) + '...';
                    }
                    return j.title;
                }),
                datasets: [
                    {
                        label: 'Views',
                        data: topJobs.map(j => j.views),
                        backgroundColor: '#6366F1',
                        hoverBackgroundColor: '#4F46E5',
                        borderWidth: 0,
                        borderRadius: 6,
                        barThickness: 40
                    },
                    {
                        label: 'Applications',
                        data: topJobs.map(j => j.applications),
                        backgroundColor: '#10B981',
                        hoverBackgroundColor: '#059669',
                        borderWidth: 0,
                        borderRadius: 6,
                        barThickness: 40
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    mode: 'index',
                    intersect: false
                },
                scales: {
                    x: {
                        grid: {
                            display: false
                        },
                        ticks: {
                            font: {
                                size: 12,
                                weight: '500'
                            },
                            maxRotation: 45,
                            minRotation: 45
                        }
                    },
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        },
                        ticks: {
                            stepSize: 5,
                            font: {
                                size: 12
                            }
                        }
                    }
                },
                plugins: {
                    legend: {
                        position: 'top',
                        labels: {
                            usePointStyle: true,
                            padding: 15,
                            font: {
                                size: 13,
                                weight: '500'
                            }
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        titleFont: {
                            size: 14,
                            weight: 'bold'
                        },
                        bodyFont: {
                            size: 13
                        },
                        callbacks: {
                            title: function(context) {
                                // Show full job title in tooltip
                                return topJobs[context[0].dataIndex].title;
                            },
                            afterLabel: function(context) {
                                const jobData = topJobs[context.dataIndex];
                                if (context.datasetIndex === 0) {
                                    // Views dataset
                                    return 'Days posted: ' + jobData.daysSincePosted;
                                } else if (context.datasetIndex === 1) {
                                    // Applications dataset
                                    return 'Conversion: ' + jobData.conversionRate.toFixed(1) + '%';
                                }
                                return '';
                            }
                        }
                    }
                }
            }
        });
    </script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
