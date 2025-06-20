// Validation Utility Functions
class FormValidator {
    constructor() {
        this.errors = new Map();
    }

    // Validate full name
    validateFullName(value) {
        const trimmed = value.trim();
        
        if (trimmed.length === 0) {
            return { isValid: false, message: 'Họ và tên không được để trống' };
        }
        
        if (trimmed.length < 2) {
            return { isValid: false, message: 'Họ và tên phải có ít nhất 2 ký tự' };
        }
        
        if (trimmed.length > 100) {
            return { isValid: false, message: 'Họ và tên không được quá 100 ký tự' };
        }
        
        const namePattern = /^[\p{L}\s]+$/u;
        if (!namePattern.test(trimmed)) {
            return { isValid: false, message: 'Họ và tên chỉ được chứa chữ cái và khoảng trắng' };
        }
        
        return { isValid: true, message: '' };
    }

    // Validate email
    validateEmail(value) {
        const trimmed = value.trim();
        
        if (trimmed.length === 0) {
            return { isValid: false, message: 'Email không được để trống' };
        }
        
        if (trimmed.length > 255) {
            return { isValid: false, message: 'Email không được quá 255 ký tự' };
        }
        
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(trimmed)) {
            return { isValid: false, message: 'Email không đúng định dạng' };
        }
        
        return { isValid: true, message: '' };
    }

    // Validate phone number
    validatePhone(value) {
        const trimmed = value.trim();
        
        if (trimmed.length === 0) {
            return { isValid: false, message: 'Số điện thoại không được để trống' };
        }
        
        if (trimmed.length > 15) {
            return { isValid: false, message: 'Số điện thoại không được quá 15 ký tự' };
        }
        
        const phonePattern = /^(\+84|0)[3|5|7|8|9][0-9]{8}$/;
        if (!phonePattern.test(trimmed)) {
            return { isValid: false, message: 'Số điện thoại không đúng định dạng' };
        }
        
        return { isValid: true, message: '' };
    }

    // Validate description
    validateDescription(value) {
        const trimmed = value.trim();
        
        if (trimmed.length === 0) {
            return { isValid: false, message: 'Nội dung ứng tuyển không được để trống' };
        }
        
        if (trimmed.length < 10) {
            return { isValid: false, message: 'Nội dung phải có ít nhất 10 ký tự' };
        }
        
        if (trimmed.length > 1000) {
            return { isValid: false, message: 'Nội dung không được quá 1000 ký tự' };
        }
        
        return { isValid: true, message: '' };
    }

    // Validate file
    validateFile(file, options = {}) {
        const {
            maxSize = 2048 * 1024, // 2048KB default
            allowedTypes = ['application/pdf', 'image/png', 'image/jpg', 'image/jpeg', 'image/gif'],
            allowedExtensions = ['.pdf', '.png', '.jpg', '.jpeg', '.gif']
        } = options;

        if (!file) {
            return { isValid: true, message: '' }; // File is optional
        }

        // Check file type
        if (!allowedTypes.includes(file.type)) {
            return { isValid: false, message: 'Chỉ chấp nhận file PDF và ảnh (PNG, JPG, JPEG, GIF)' };
        }

        // Check file extension
        const fileName = file.name.toLowerCase();
        const hasValidExtension = allowedExtensions.some(ext => fileName.endsWith(ext));
        if (!hasValidExtension) {
            return { isValid: false, message: 'Chỉ chấp nhận file PDF và ảnh (PNG, JPG, JPEG, GIF)' };
        }

        // Check file size
        if (file.size > maxSize) {
            return { isValid: false, message: `Kích thước file không được vượt quá ${Math.round(maxSize / 1024)}KB` };
        }

        return { isValid: true, message: '' };
    }

    // Show error message
    showError(elementId, message) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
        
        // Add error class to input
        const inputElement = document.getElementById(elementId.replace('Error', ''));
        if (inputElement) {
            inputElement.classList.add('error');
            inputElement.classList.remove('success');
        }
    }

    // Hide error message
    hideError(elementId) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.style.display = 'none';
        }
        
        // Remove error class and add success class to input
        const inputElement = document.getElementById(elementId.replace('Error', ''));
        if (inputElement) {
            inputElement.classList.remove('error');
            inputElement.classList.add('success');
        }
    }

    // Clear all errors
    clearAllErrors() {
        document.querySelectorAll('.error-message[id$="Error"]').forEach(el => {
            el.style.display = 'none';
        });
        
        document.querySelectorAll('input, textarea').forEach(el => {
            el.classList.remove('error', 'success');
        });
    }

    // Format file size
    formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    // Debounce function for performance
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // Throttle function for performance
    throttle(func, limit) {
        let inThrottle;
        return function() {
            const args = arguments;
            const context = this;
            if (!inThrottle) {
                func.apply(context, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    }
}

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = FormValidator;
} else {
    window.FormValidator = FormValidator;
} 