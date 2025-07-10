document.addEventListener('DOMContentLoaded', function() {
    // Language switcher functionality - Enhanced version with null checks
    const languageOptions = document.querySelectorAll('.language-option');
    if (languageOptions.length > 0) {
        languageOptions.forEach(option => {
            option.addEventListener('click', function(event) {
                event.preventDefault();
                const lang = this.getAttribute('href').split('=')[1];
                const currentUrl = new URL(window.location.href);
                
                // Preserve all existing parameters and add/update language
                currentUrl.searchParams.set('lang', lang);
                
                // Redirect to the same page with language parameter
                window.location.href = currentUrl.toString();
            });
        });
    }

    // Handle both .language-switcher and .sidebar-lang-btn with null checks
    const languageButtons = document.querySelectorAll('.language-switcher, .sidebar-lang-btn');
    const languageDropdowns = document.querySelectorAll('.language-dropdown');
    const languageContainers = document.querySelectorAll('.language-switcher-container');
    
    if (languageButtons.length > 0) {
        languageButtons.forEach((button, index) => {
            const dropdown = languageDropdowns[index] || document.querySelector('.language-dropdown');
            const container = languageContainers[index] || document.querySelector('.language-switcher-container');
            
            if (button && dropdown) {
                button.addEventListener('click', function(event) {
                    event.stopPropagation();
                    
                    // Close all other dropdowns first
                    languageDropdowns.forEach(dd => dd.classList.remove('active'));
                    languageContainers.forEach(lc => lc.classList.remove('active'));
                    
                    // Toggle current dropdown
                    dropdown.classList.toggle('active');
                    if (container) {
                        container.classList.toggle('active');
                    }
                });
            }
        });
    }

    // Close dropdowns when clicking outside
    document.addEventListener('click', function(event) {
        const isLanguageElement = event.target.closest('.language-switcher-container, .language-nav-item');
        if (!isLanguageElement) {
            languageDropdowns.forEach(dropdown => {
                dropdown.classList.remove('active');
            });
            languageContainers.forEach(container => {
                container.classList.remove('active');
            });
        }
    });

    // Close dropdowns on escape key
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            languageDropdowns.forEach(dropdown => {
                dropdown.classList.remove('active');
            });
            languageContainers.forEach(container => {
                container.classList.remove('active');
            });
        }
    });

    // Booking modal functionality with null checks
    const openBtnNavbar = document.getElementById('openBookingModalNavbar');
    const modal = document.getElementById('bookingModal');
    const modalContent = document.querySelector('.booking-modal-content-popover');
    const closeBtn = document.getElementById('closeBookingModal');

    function hideModal() {
        if (modal) {
            modal.style.display = 'none';
        }
    }

    if (openBtnNavbar && modal && closeBtn && modalContent) {
        openBtnNavbar.addEventListener('click', function() {
            const rect = openBtnNavbar.getBoundingClientRect();
            const modalWidth = modalContent.offsetWidth;
            const windowWidth = window.innerWidth;

            // Position vertically below the button
            modalContent.style.top = (rect.bottom + window.scrollY + 5) + 'px';

            // Center the modal horizontally relative to the button
            let leftPosition = rect.left + window.scrollX + (rect.width / 2) - (modalWidth / 2);

            // Ensure the modal doesn't go off-screen
            if (leftPosition < 10) {
                leftPosition = 10; // Keep a small margin from the left edge
            } else if (leftPosition + modalWidth > windowWidth - 10) {
                leftPosition = windowWidth - modalWidth - 10; // Keep a small margin from the right edge
            }

            modalContent.style.left = leftPosition + 'px';
            modal.style.display = 'block';
        });

        closeBtn.addEventListener('click', hideModal);

        window.addEventListener('click', function(event) {
            if (event.target === modal) {
                hideModal();
            }
        });

        // Adjust position on window resize
        window.addEventListener('resize', function() {
            if (modal.style.display === 'block') {
                const rect = openBtnNavbar.getBoundingClientRect();
                const modalWidth = modalContent.offsetWidth;
                const windowWidth = window.innerWidth;

                modalContent.style.top = (rect.bottom + window.scrollY + 5) + 'px';
                let leftPosition = rect.left + window.scrollX + (rect.width / 2) - (modalWidth / 2);

                if (leftPosition < 10) {
                    leftPosition = 10;
                } else if (leftPosition + modalWidth > windowWidth - 10) {
                    leftPosition = windowWidth - modalWidth - 10;
                }

                modalContent.style.left = leftPosition + 'px';
            }
        });
    }
});
