/**
 * Custom Alert/Toast Notification System
 * Usage: showAlert('message', 'type', 'title')
 * Types: 'success', 'error', 'warning', 'info'
 */

// Create alert container if it doesn't exist
function createAlertContainer() {
    let container = document.querySelector('.custom-alert-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'custom-alert-container';
        document.body.appendChild(container);
    }
    return container;
}

// Show alert function
function showAlert(message, type = 'info', title = null, duration = 5000) {
    const container = createAlertContainer();
    
    // Create alert element
    const alert = document.createElement('div');
    alert.className = `custom-alert alert-${type}`;
    
    // Icon based on type
    const icons = {
        success: 'fa-circle-check',
        error: 'fa-circle-xmark',
        warning: 'fa-triangle-exclamation',
        info: 'fa-circle-info'
    };
    
    // Default titles
    const defaultTitles = {
        success: 'Success',
        error: 'Error',
        warning: 'Warning',
        info: 'Information'
    };
    
    const alertTitle = title || defaultTitles[type];
    const iconClass = icons[type] || icons.info;
    
    // Build alert HTML
    alert.innerHTML = `
        <div class="custom-alert-icon">
            <i class="fas ${iconClass}"></i>
        </div>
        <div class="custom-alert-content">
            <div class="custom-alert-title">${alertTitle}</div>
            <div class="custom-alert-message">${message}</div>
        </div>
        <button class="custom-alert-close" aria-label="Close">
            <i class="fas fa-times"></i>
        </button>
        ${duration > 0 ? '<div class="custom-alert-progress"><div class="custom-alert-progress-bar"></div></div>' : ''}
    `;
    
    // Add to container
    container.appendChild(alert);
    
    // Close button functionality
    const closeBtn = alert.querySelector('.custom-alert-close');
    closeBtn.addEventListener('click', () => {
        dismissAlert(alert);
    });
    
    // Auto dismiss
    if (duration > 0) {
        setTimeout(() => {
            dismissAlert(alert);
        }, duration);
    }
    
    return alert;
}

// Dismiss alert with animation
function dismissAlert(alert) {
    alert.classList.add('alert-hide');
    setTimeout(() => {
        if (alert.parentNode) {
            alert.parentNode.removeChild(alert);
        }
    }, 300);
}

// Convenience functions
function showSuccess(message, title = null, duration = 5000) {
    return showAlert(message, 'success', title, duration);
}

function showError(message, title = null, duration = 7000) {
    return showAlert(message, 'error', title, duration);
}

function showWarning(message, title = null, duration = 6000) {
    return showAlert(message, 'warning', title, duration);
}

function showInfo(message, title = null, duration = 5000) {
    return showAlert(message, 'info', title, duration);
}
