document.addEventListener('DOMContentLoaded', function() {
    const navbar = document.getElementById('adminNavbar');
    const toggleBtn = document.getElementById('toggleBtn');
    const body = document.body;

    toggleBtn.addEventListener('click', function() {
        navbar.classList.toggle('collapsed');
        body.classList.toggle('collapsed');
    });
});