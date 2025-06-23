/**
 * Language Dropdown JavaScript
 * Handles language switching functionality in navbar
 */

console.log('🔧 Language dropdown script loaded');

document.addEventListener('DOMContentLoaded', function() {
    console.log('🔧 Language dropdown initializing...');
    
    // Wait for DOM to be fully ready
    setTimeout(function() {
        const languageDropdownMenu = document.getElementById('languageDropdownMenu');
        const languageNavItem = document.querySelector('.navbar-lang-item');
        const languageDropdownBtn = document.getElementById('languageDropdownBtn');
        
        if (!languageDropdownMenu || !languageNavItem || !languageDropdownBtn) {
            console.log('⚠️ Language dropdown elements not found - might be using different language system');
            return;
        }
        
        // FORCE hide dropdown initially
        languageDropdownMenu.style.cssText = 'display: none !important; opacity: 0 !important; visibility: hidden !important; pointer-events: none !important;';
        languageNavItem.classList.remove('active');
        
        console.log('✅ Language dropdown hidden initially');
        
        // Click handler for dropdown button
        languageDropdownBtn.addEventListener('click', function(event) {
            event.preventDefault();
            event.stopPropagation();
            
            const isActive = languageNavItem.classList.contains('active');
            console.log('👆 Language dropdown clicked, current state:', isActive ? 'open' : 'closed');
            
            if (isActive) {
                // Hide dropdown
                languageNavItem.classList.remove('active');
                languageDropdownMenu.style.cssText = 'display: none !important; opacity: 0 !important; visibility: hidden !important; pointer-events: none !important;';
                console.log('🔒 Dropdown hidden');
            } else {
                // Show dropdown
                languageNavItem.classList.add('active');
                languageDropdownMenu.style.cssText = 'display: block !important; opacity: 1 !important; visibility: visible !important; pointer-events: auto !important;';
                console.log('🔓 Dropdown shown');
            }
        });
        
        // Close dropdown when clicking outside
        document.addEventListener('click', function(event) {
            if (!languageNavItem.contains(event.target)) {
                languageNavItem.classList.remove('active');
                languageDropdownMenu.style.cssText = 'display: none !important; opacity: 0 !important; visibility: hidden !important; pointer-events: none !important;';
            }
        });
        
        // Language option click handlers
        const languageOptions = document.querySelectorAll('.navbar-lang-option');
        languageOptions.forEach(option => {
            option.addEventListener('click', function(event) {
                event.preventDefault();
                const lang = this.getAttribute('href').split('=')[1];
                const currentUrl = new URL(window.location.href);
                currentUrl.searchParams.set('lang', lang);
                console.log('🌐 Switching language to:', lang);
                window.location.href = currentUrl.toString();
            });
        });
        
        console.log('✅ Language dropdown fully initialized');
        
    }, 300); // Increased delay to ensure all CSS is loaded
});

// Close dropdown when pressing Escape
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        const languageDropdownMenu = document.getElementById('languageDropdownMenu');
        const languageNavItem = document.querySelector('.navbar-lang-item');
        
        if (languageDropdownMenu && languageNavItem) {
            languageNavItem.classList.remove('active');
            languageDropdownMenu.style.cssText = 'display: none !important; opacity: 0 !important; visibility: hidden !important; pointer-events: none !important;';
        }
    }
}); 