// Dashboard Charts JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Check if dashboardData exists
    if (typeof dashboardData !== 'undefined') {
        initializeDashboardCharts();
    } else {
        // Extract data from Thymeleaf
        extractDataFromThymeleaf();
    }
});

function extractDataFromThymeleaf() {
    // Extract data from the rendered HTML
    const dashboardContainer = document.querySelector('.dashboard-container');
    if (!dashboardContainer) return;
    
    // Extract statistics from stat cards
    const statCards = document.querySelectorAll('.stat-card');
    const stats = {
        totalJobPosts: 0,
        totalApplications: 0,
        pendingApplications: 0,
        interviewApplications: 0,
        acceptedApplications: 0
    };
    
    statCards.forEach((card, index) => {
        const number = card.querySelector('.stat-number');
        if (number) {
            const value = parseInt(number.textContent) || 0;
            switch(index) {
                case 0: stats.totalJobPosts = value; break;
                case 1: stats.totalApplications = value; break;
                case 2: stats.pendingApplications = value; break;
                case 3: stats.interviewApplications = value; break;
                case 4: stats.acceptedApplications = value; break;
            }
        }
    });
    
    // Extract job post data from tables
    const jobPostData = [];
    const jobPostTable = document.querySelector('.table-container:first-child .dashboard-table tbody');
    if (jobPostTable) {
        const rows = jobPostTable.querySelectorAll('tr');
        rows.forEach(row => {
            const cells = row.querySelectorAll('td');
            if (cells.length >= 2) {
                const jobTitle = cells[0].textContent.trim();
                const applicationCount = parseInt(cells[1].textContent) || 0;
                if (jobTitle && jobTitle !== 'No data available') {
                    jobPostData.push({
                        jobTitle: jobTitle,
                        applicationCount: applicationCount
                    });
                }
            }
        });
    }
    
    // Create mock data for demonstration
    const mockData = {
        jobPostStats: jobPostData.length > 0 ? jobPostData : [
            { jobTitle: 'Software Engineer', applicationCount: 25 },
            { jobTitle: 'Product Manager', applicationCount: 18 },
            { jobTitle: 'UI/UX Designer', applicationCount: 12 },
            { jobTitle: 'Data Analyst', applicationCount: 8 },
            { jobTitle: 'Marketing Specialist', applicationCount: 15 }
        ],
        applicationStatusChart: {
            'PENDING': stats.pendingApplications,
            'INTERVIEW': stats.interviewApplications,
            'ACCEPTED': stats.acceptedApplications,
            'REJECTED': Math.max(0, stats.totalApplications - stats.pendingApplications - stats.interviewApplications - stats.acceptedApplications)
        },
        applicationTrends: window.backendDashboardData && window.backendDashboardData.applicationTrends && window.backendDashboardData.applicationTrends.length > 0 ? 
            window.backendDashboardData.applicationTrends : generateMockTrends()
    };
    
    // Set global data and initialize charts
    window.dashboardData = mockData;
    initializeDashboardCharts();
}

function generateMockTrends() {
    const trends = [];
    const today = new Date();
    for (let i = 29; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(date.getDate() - i);
        trends.push({
            date: date.toISOString().split('T')[0],
            applicationCount: Math.floor(Math.random() * 10) + 1
        });
    }
    return trends;
}

function initializeDashboardCharts() {
    const data = window.dashboardData;
    if (!data) return;
    
    // Initialize all charts
    createJobPostChart(data.jobPostStats);
    createStatusChart(data.applicationStatusChart);
    createTrendsChart(data.applicationTrends);
}

function createJobPostChart(jobPostStats) {
    const ctx = document.getElementById('jobPostChart');
    if (!ctx) return;
    
    const labels = jobPostStats.map(item => item.jobTitle);
    const data = jobPostStats.map(item => item.applicationCount);
    
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Applications',
                data: data,
                backgroundColor: [
                    'rgba(102, 126, 234, 0.8)',
                    'rgba(118, 75, 162, 0.8)',
                    'rgba(240, 147, 251, 0.8)',
                    'rgba(74, 172, 254, 0.8)',
                    'rgba(67, 233, 123, 0.8)'
                ],
                borderColor: [
                    'rgba(102, 126, 234, 1)',
                    'rgba(118, 75, 162, 1)',
                    'rgba(240, 147, 251, 1)',
                    'rgba(74, 172, 254, 1)',
                    'rgba(67, 233, 123, 1)'
                ],
                borderWidth: 2,
                borderRadius: 8,
                borderSkipped: false
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleColor: 'white',
                    bodyColor: 'white',
                    borderColor: 'rgba(102, 126, 234, 0.3)',
                    borderWidth: 1,
                    cornerRadius: 8,
                    callbacks: {
                        label: function(context) {
                            return context.parsed.y + ' applications';
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        color: '#6c757d'
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                },
                x: {
                    ticks: {
                        color: '#6c757d',
                        maxRotation: 45,
                        minRotation: 0
                    },
                    grid: {
                        display: false
                    }
                }
            },
            animation: {
                delay: (context) => {
                    return context.dataIndex * 100;
                },
                duration: 1000,
                easing: 'easeInOutQuart'
            }
        }
    });
}

function createStatusChart(applicationStatusChart) {
    const ctx = document.getElementById('statusChart');
    if (!ctx) return;
    
    const labels = [];
    const data = [];
    const colors = [];
    
    if (applicationStatusChart.PENDING > 0) {
        labels.push('Pending');
        data.push(applicationStatusChart.PENDING);
        colors.push('rgba(255, 193, 7, 0.8)');
    }
    if (applicationStatusChart.INTERVIEW > 0) {
        labels.push('Interview');
        data.push(applicationStatusChart.INTERVIEW);
        colors.push('rgba(13, 110, 253, 0.8)');
    }
    if (applicationStatusChart.ACCEPTED > 0) {
        labels.push('Accepted');
        data.push(applicationStatusChart.ACCEPTED);
        colors.push('rgba(40, 167, 69, 0.8)');
    }
    if (applicationStatusChart.REJECTED > 0) {
        labels.push('Rejected');
        data.push(applicationStatusChart.REJECTED);
        colors.push('rgba(220, 53, 69, 0.8)');
    }
    
    // If no data, show placeholder
    if (data.length === 0) {
        labels.push('No Applications');
        data.push(1);
        colors.push('rgba(108, 117, 125, 0.3)');
    }
    
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: colors,
                borderColor: colors.map(color => color.replace('0.8', '1')),
                borderWidth: 2,
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        usePointStyle: true,
                        padding: 20,
                        color: '#6c757d'
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleColor: 'white',
                    bodyColor: 'white',
                    borderColor: 'rgba(102, 126, 234, 0.3)',
                    borderWidth: 1,
                    cornerRadius: 8,
                    callbacks: {
                        label: function(context) {
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((context.parsed * 100) / total).toFixed(1);
                            return context.label + ': ' + context.parsed + ' (' + percentage + '%)';
                        }
                    }
                }
            },
            animation: {
                animateRotate: true,
                animateScale: true,
                duration: 1000,
                easing: 'easeInOutQuart'
            }
        }
    });
}

function createTrendsChart(applicationTrends) {
    const ctx = document.getElementById('trendsChart');
    if (!ctx) return;
    
    const labels = applicationTrends.map(item => {
        // Handle both backend date format and mock date format
        let date;
        if (typeof item.date === 'string') {
            date = new Date(item.date);
        } else if (item.date && item.date.year && item.date.month && item.date.day) {
            // Backend LocalDate format: {year: 2024, month: 1, day: 15}
            date = new Date(item.date.year, item.date.month - 1, item.date.day);
        } else {
            date = new Date(item.date);
        }
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    });
    const data = applicationTrends.map(item => item.applicationCount);
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Applications',
                data: data,
                borderColor: 'rgba(102, 126, 234, 1)',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: 'rgba(102, 126, 234, 1)',
                pointBorderColor: 'white',
                pointBorderWidth: 2,
                pointRadius: 5,
                pointHoverRadius: 7,
                pointHoverBackgroundColor: 'rgba(102, 126, 234, 1)',
                pointHoverBorderColor: 'white',
                pointHoverBorderWidth: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: 'index'
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleColor: 'white',
                    bodyColor: 'white',
                    borderColor: 'rgba(102, 126, 234, 0.3)',
                    borderWidth: 1,
                    cornerRadius: 8,
                    callbacks: {
                        label: function(context) {
                            return context.parsed.y + ' applications';
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        color: '#6c757d'
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                },
                x: {
                    ticks: {
                        color: '#6c757d',
                        maxRotation: 45,
                        minRotation: 0
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                }
            },
            animation: {
                delay: (context) => {
                    return context.dataIndex * 50;
                },
                duration: 1500,
                easing: 'easeInOutQuart'
            }
        }
    });
}

// Utility function to format numbers
function formatNumber(num) {
    if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
}

// Add smooth scrolling for navigation
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Add loading animation
function showLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = '<div class="loading">Loading chart...</div>';
    }
}

// Add error handling
function showError(elementId, message) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = `<div class="error-message">${message}</div>`;
    }
}

// Add resize handler for charts
window.addEventListener('resize', function() {
    // Chart.js automatically handles resize, but we can add custom logic here if needed
    console.log('Dashboard charts will automatically resize');
});

// Add animation for stat cards
function animateStatCards() {
    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach((card, index) => {
        card.style.animationDelay = `${index * 0.1}s`;
        card.classList.add('animate-fade-in');
    });
}

// Call animation when page loads
document.addEventListener('DOMContentLoaded', function() {
    setTimeout(animateStatCards, 100);
});

// Add click handlers for interactive elements
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('stat-card')) {
        e.target.style.transform = 'scale(0.95)';
        setTimeout(() => {
            e.target.style.transform = '';
        }, 150);
    }
});

// Export functions for testing
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        initializeDashboardCharts,
        createJobPostChart,
        createStatusChart,
        createTrendsChart,
        formatNumber
    };
} 