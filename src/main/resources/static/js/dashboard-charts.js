// Dashboard Charts JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Check if dashboardData exists
    if (typeof backendDashboardData !== 'undefined') {
        initializeDashboardCharts(backendDashboardData);
    } else {
        // Extract data from Thymeleaf if backend data is not available
        extractDataFromThymeleaf();
    }
    
    // Handle tab switching
    const tabEls = document.querySelectorAll('button[data-bs-toggle="tab"]');
    tabEls.forEach(tabEl => {
        tabEl.addEventListener('shown.bs.tab', event => {
            // Redraw charts when tab is shown to ensure proper rendering
            if (event.target.id === 'jobs-tab') {
                if (window.jobPostChart) window.jobPostChart.update();
                if (window.statusChart) window.statusChart.update();
                if (window.trendsChart) window.trendsChart.update();
            } else if (event.target.id === 'events-tab') {
                if (window.eventsChart) window.eventsChart.update();
            }
        });
    });
});

function extractDataFromThymeleaf() {
    // Extract data from the rendered HTML
    const dashboardContainer = document.querySelector('.dashboard-container');
    if (!dashboardContainer) return;
    
    // Extract statistics from stat cards
    const extractStatValue = (selector) => {
        const element = document.querySelector(selector);
        return element ? parseInt(element.textContent) || 0 : 0;
    };
    
    const stats = {
        totalJobPosts: extractStatValue('.job-card .stat-number'),
        totalApplications: extractStatValue('.application-card .stat-number'),
        pendingApplications: extractStatValue('#jobs-tab-pane .stats-grid .stat-card:nth-child(1) .stat-number'),
        interviewApplications: extractStatValue('#jobs-tab-pane .stats-grid .stat-card:nth-child(2) .stat-number'),
        acceptedApplications: extractStatValue('#jobs-tab-pane .stats-grid .stat-card:nth-child(3) .stat-number'),
        rejectedApplications: extractStatValue('#jobs-tab-pane .stats-grid .stat-card:nth-child(4) .stat-number'),
        totalEvents: extractStatValue('.event-card .stat-number'),
        totalEventParticipants: extractStatValue('.participant-card .stat-number')
    };
    
    // Extract job post data from table
    const jobPostData = extractTableData('#jobs-tab-pane .table-container:nth-child(1) table');
    
    // Extract event data from table
    const eventData = extractTableData('#events-tab-pane .table-container:nth-child(1) table');
    
    // Use actual data only
    const dashboardData = {
        stats: stats,
        jobPostStats: jobPostData,
        eventStats: eventData,
        applicationStatusChart: {
            'PENDING': stats.pendingApplications,
            'INTERVIEW': stats.interviewApplications,
            'ACCEPTED': stats.acceptedApplications,
            'REJECTED': stats.rejectedApplications
        },
        applicationTrends: []
    };
    
    // Initialize charts
    initializeDashboardCharts(dashboardData);
}

function extractTableData(selector) {
    const table = document.querySelector(selector);
    const data = [];
    
    if (table) {
        const rows = table.querySelectorAll('tbody tr');
        rows.forEach(row => {
            const cells = row.querySelectorAll('td');
            if (cells.length >= 2 && !row.querySelector('.no-data')) {
                const title = cells[0].textContent.trim();
                const count = parseInt(cells[1].textContent) || 0;
                data.push({
                    title: title,
                    count: count
                });
            }
        });
    }
    
    return data;
}

function initializeDashboardCharts(data) {
    // Store data for potential reuse
    window.dashboardData = data;
    
    // Initialize all charts
    window.jobPostChart = createJobPostChart('jobPostChart', data.jobPostStats);
    window.statusChart = createStatusChart('statusChart', data.applicationStatusChart);
    window.trendsChart = createTrendsChart('trendsChart', data.applicationTrends);
    window.eventsChart = createEventsChart('eventsChart', data.eventStats);
}

function createJobPostChart(canvasId, jobPostStats) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;
    
    // Use empty data array if no data is available
    const labels = jobPostStats && jobPostStats.length > 0 ? 
        jobPostStats.map(item => item.jobTitle || item.title).slice(0, 7) : [];
    const data = jobPostStats && jobPostStats.length > 0 ? 
        jobPostStats.map(item => item.applicationCount || item.count).slice(0, 7) : [];
    
    // Create a gradient for bar colors
    const gradient = (color1, color2) => {
        const gradientBg = ctx.getContext('2d').createLinearGradient(0, 0, 0, 350);
        gradientBg.addColorStop(0, color1);
        gradientBg.addColorStop(1, color2);
        return gradientBg;
    };
    
    // Create beautiful gradient colors for each bar
    const gradients = [
        gradient('rgba(102, 126, 234, 0.9)', 'rgba(118, 75, 162, 0.7)'),
        gradient('rgba(79, 209, 197, 0.9)', 'rgba(99, 179, 237, 0.7)'),
        gradient('rgba(252, 129, 129, 0.9)', 'rgba(254, 178, 178, 0.7)'),
        gradient('rgba(246, 173, 85, 0.9)', 'rgba(237, 137, 54, 0.7)'),
        gradient('rgba(104, 211, 145, 0.9)', 'rgba(72, 187, 120, 0.7)'),
        gradient('rgba(159, 122, 234, 0.9)', 'rgba(128, 90, 213, 0.7)'),
        gradient('rgba(237, 100, 166, 0.9)', 'rgba(213, 63, 140, 0.7)')
    ];
    
    // Make sure we have enough gradients for our data
    while (gradients.length < data.length) {
        gradients.push(gradients[gradients.length % 7]);
    }
    
    // Create the chart
    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Applications',
                data: data,
                backgroundColor: gradients,
                borderWidth: 0,
                borderRadius: 6,
                borderSkipped: false,
                maxBarThickness: 60
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
                    backgroundColor: 'rgba(17, 24, 39, 0.9)',
                    titleColor: 'white',
                    bodyColor: 'white',
                    padding: 12,
                    cornerRadius: 8,
                    displayColors: false,
                    callbacks: {
                        title: function(tooltipItems) {
                            return tooltipItems[0].label;
                        },
                        label: function(context) {
                            return `${context.parsed.y} applications`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(160, 174, 192, 0.1)',
                        drawBorder: false
                    },
                    ticks: {
                        stepSize: 1,
                        font: {
                            size: 12
                    },
                        color: '#718096'
                    }
                },
                x: {
                    grid: {
                        display: false,
                        drawBorder: false
                    },
                    ticks: {
                        font: {
                            size: 12
                        },
                        color: '#718096',
                        maxRotation: 45,
                        minRotation: 0
                    }
                }
            },
            animation: {
                delay: (context) => context.dataIndex * 100,
                duration: 1000,
                easing: 'easeOutQuart'
            }
        }
    });
}

function createStatusChart(canvasId, applicationStatusChart) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;
    
    // Prepare the data
    const labels = [];
    const data = [];
    const colors = [];
    const hoverColors = [];
    
    // Define beautiful colors for status
    const statusColors = {
        PENDING: {
            bg: 'rgba(252, 211, 77, 0.8)',
            hover: 'rgba(252, 211, 77, 1)'
        },
        INTERVIEW: {
            bg: 'rgba(59, 130, 246, 0.8)',
            hover: 'rgba(59, 130, 246, 1)'
        },
        ACCEPTED: {
            bg: 'rgba(34, 197, 94, 0.8)',
            hover: 'rgba(34, 197, 94, 1)'
        },
        REJECTED: {
            bg: 'rgba(239, 68, 68, 0.8)',
            hover: 'rgba(239, 68, 68, 1)'
        }
    };
    
    // Add data if available
    if (applicationStatusChart.PENDING > 0) {
        labels.push('Pending');
        data.push(applicationStatusChart.PENDING);
        colors.push(statusColors.PENDING.bg);
        hoverColors.push(statusColors.PENDING.hover);
    }
    if (applicationStatusChart.INTERVIEW > 0) {
        labels.push('Interview');
        data.push(applicationStatusChart.INTERVIEW);
        colors.push(statusColors.INTERVIEW.bg);
        hoverColors.push(statusColors.INTERVIEW.hover);
    }
    if (applicationStatusChart.ACCEPTED > 0) {
        labels.push('Accepted');
        data.push(applicationStatusChart.ACCEPTED);
        colors.push(statusColors.ACCEPTED.bg);
        hoverColors.push(statusColors.ACCEPTED.hover);
    }
    if (applicationStatusChart.REJECTED > 0) {
        labels.push('Rejected');
        data.push(applicationStatusChart.REJECTED);
        colors.push(statusColors.REJECTED.bg);
        hoverColors.push(statusColors.REJECTED.hover);
    }
    
    // Create the chart
    return new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels.length > 0 ? labels : ['No Data'],
            datasets: [{
                data: data.length > 0 ? data : [1],
                backgroundColor: colors.length > 0 ? colors : ['rgba(203, 213, 225, 0.3)'],
                hoverBackgroundColor: hoverColors.length > 0 ? hoverColors : ['rgba(203, 213, 225, 0.4)'],
                borderWidth: 0,
                hoverOffset: 7,
                spacing: 0,
                circumference: 360,
                rotation: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '70%',
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        usePointStyle: true,
                        pointStyle: 'circle',
                        padding: 15,
                        font: {
                            size: 12
                        },
                        color: '#4a5568'
                    }
                },
                tooltip: {
                    enabled: data.length > 0,
                    backgroundColor: 'rgba(17, 24, 39, 0.9)',
                    titleColor: 'white',
                    bodyColor: 'white',
                    padding: 12,
                    cornerRadius: 8,
                    displayColors: false,
                    callbacks: {
                        label: function(context) {
                            if (data.length === 0) return '';
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((context.parsed * 100) / total).toFixed(1);
                            return `${context.parsed} applications (${percentage}%)`;
                        }
                    }
                }
            },
            animation: {
                animateRotate: true,
                animateScale: true,
                duration: 1200,
                easing: 'easeOutCirc'
            }
        }
    });
}

function createTrendsChart(canvasId, applicationTrends) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;
    
    // Use empty arrays if no data
    const hasData = applicationTrends && applicationTrends.length > 0;
    
    // Generate labels for the past 30 days if no data
    let labels = [];
    let data = [];
    
    if (hasData) {
        labels = applicationTrends.map(item => {
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
        data = applicationTrends.map(item => item.applicationCount);
    } else {
        // Generate empty labels for the past 30 days
        const today = new Date();
        for (let i = 29; i >= 0; i--) {
            const date = new Date(today);
            date.setDate(date.getDate() - i);
            labels.push(date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
            data.push(0); // All zeros for no data
        }
    }
    
    // Create gradient for line chart
    const gradientFill = ctx.getContext('2d').createLinearGradient(0, 0, 0, 300);
    gradientFill.addColorStop(0, 'rgba(102, 126, 234, 0.6)');
    gradientFill.addColorStop(1, 'rgba(102, 126, 234, 0.1)');
    
    // Create the chart
    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Applications',
                data: data,
                borderColor: 'rgba(102, 126, 234, 1)',
                backgroundColor: gradientFill,
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: 'rgba(102, 126, 234, 1)',
                pointBorderColor: 'white',
                pointBorderWidth: 2,
                pointRadius: 4,
                pointHoverRadius: 6,
                pointHoverBackgroundColor: 'white',
                pointHoverBorderColor: 'rgba(102, 126, 234, 1)',
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
                    backgroundColor: 'rgba(17, 24, 39, 0.9)',
                    titleColor: 'white',
                    bodyColor: 'white',
                    padding: 12,
                    cornerRadius: 8,
                    displayColors: false,
                    callbacks: {
                        title: function(tooltipItems) {
                            return tooltipItems[0].label;
                        },
                        label: function(context) {
                            return `${context.parsed.y} applications`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(160, 174, 192, 0.1)',
                        drawBorder: false
                    },
                    ticks: {
                        stepSize: 1,
                        font: {
                            size: 12
                    },
                        color: '#718096'
                    }
                },
                x: {
                    grid: {
                        color: 'rgba(160, 174, 192, 0.1)',
                        drawBorder: false
                    },
                    ticks: {
                        font: {
                            size: 12
                        },
                        color: '#718096',
                        maxRotation: 45,
                        minRotation: 0
                    }
                }
            },
            animation: {
                duration: 2000,
                easing: 'easeOutCubic'
            }
        }
    });
}

function createEventsChart(canvasId, eventStats) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;

    // Use empty data array if no data is available
    const labels = eventStats && eventStats.length > 0 ? 
        eventStats.map(item => item.eventTitle || item.title).slice(0, 7) : [];
    
    // Đảm bảo mỗi giá trị đều ít nhất là 1 để có thể nhìn thấy
    const data = eventStats && eventStats.length > 0 ? 
        eventStats.map(item => {
            const count = parseInt(item.participantCount || item.count) || 0;
            // Nếu có nhãn nhưng giá trị là 0, hiển thị tối thiểu giá trị là 1
            return count === 0 && labels.length > 0 ? 1 : count;
        }).slice(0, 7) : [];
    
    // Kiểm tra nếu không có dữ liệu
    const hasData = labels.length > 0 && data.some(value => value > 0);
    
    // Debug để kiểm tra dữ liệu
    console.log('Event Chart Data:', { labels, data, eventStats });
    
    // Create beautiful gradients
    const gradient1 = ctx.getContext('2d').createLinearGradient(0, 0, 0, 350);
    gradient1.addColorStop(0, 'rgba(72, 187, 120, 0.9)');
    gradient1.addColorStop(1, 'rgba(56, 161, 105, 0.7)');
    
    const gradient2 = ctx.getContext('2d').createLinearGradient(0, 0, 0, 350);
    gradient2.addColorStop(0, 'rgba(66, 153, 225, 0.9)');
    gradient2.addColorStop(1, 'rgba(49, 130, 206, 0.7)');
    
    // Create array of gradients for each bar
    const gradients = [];
    for (let i = 0; i < data.length; i++) {
        gradients.push(i % 2 === 0 ? gradient1 : gradient2);
    }
    
    // Create chart with beautiful styling
    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Participants',
                data: data,
                backgroundColor: gradients.length > 0 ? gradients : ['rgba(203, 213, 225, 0.3)'],
                borderWidth: 0,
                borderRadius: 6,
                borderSkipped: false,
                maxBarThickness: 60
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
                    backgroundColor: 'rgba(17, 24, 39, 0.9)',
                    titleColor: 'white',
                    bodyColor: 'white',
                    padding: 12,
                    cornerRadius: 8,
                    displayColors: false,
                    callbacks: {
                        title: function(tooltipItems) {
                            return tooltipItems[0].label;
                        },
                        label: function(context) {
                            // Nếu giá trị hiển thị là 1 nhưng giá trị thật là 0
                            const originalValue = eventStats[context.dataIndex] ? 
                                (eventStats[context.dataIndex].participantCount || eventStats[context.dataIndex].count || 0) : 0;
                            
                            return `${originalValue} participants`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    suggestedMin: 1, // Đảm bảo trục Y bắt đầu từ 0 nhưng hiển thị tối thiểu đến 1
                    suggestedMax: Math.max(...data, 5), // Đảm bảo có không gian hiển thị đủ
                    grid: {
                        color: 'rgba(160, 174, 192, 0.1)',
                        drawBorder: false
                    },
                    ticks: {
                        stepSize: 1,
                        font: {
                            size: 12
                        },
                        color: '#718096'
                    }
                },
                x: {
                    grid: {
                        display: false,
                        drawBorder: false
                    },
                    ticks: {
                        font: {
                            size: 12
                        },
                        color: '#718096',
                        maxRotation: 45,
                        minRotation: 0
                    }
                }
            },
            animation: {
                delay: (context) => context.dataIndex * 100,
                duration: 1000,
                easing: 'easeOutQuart'
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