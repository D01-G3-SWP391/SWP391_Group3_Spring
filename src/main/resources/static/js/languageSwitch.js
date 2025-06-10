    document.addEventListener('DOMContentLoaded', function() {
    // Language switcher functionality
    const languageOptions = document.querySelectorAll('.language-option');
    languageOptions.forEach(option => {
    option.addEventListener('click', function(event) {
    event.preventDefault();
    const lang = this.getAttribute('href').split('=')[1];
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set('lang', lang);
    window.location.href = currentUrl.toString();
});
});

    const languageButton = document.querySelector('.language-switcher');
    const languageDropdown = document.querySelector('.language-dropdown');
    if (languageButton && languageDropdown) {
    languageButton.addEventListener('click', function(event) {
    event.stopPropagation();
    languageDropdown.classList.toggle('active');
});
    document.addEventListener('click', function(event) {
    if (!languageButton.contains(event.target) && !languageDropdown.contains(event.target)) {
    languageDropdown.classList.remove('active');
}
});
}

    // Booking modal functionality
    const openBtnNavbar = document.getElementById('openBookingModalNavbar');
    const modal = document.getElementById('bookingModal');
    const modalContent = document.querySelector('.booking-modal-content-popover');
    const closeBtn = document.getElementById('closeBookingModal');

    function hideModal() {
    modal.style.display = 'none';
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
