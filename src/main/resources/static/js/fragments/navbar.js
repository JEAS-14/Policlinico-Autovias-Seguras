document.addEventListener('DOMContentLoaded', function() {
    const hamburger = document.getElementById('hamburger');
    const navMenu = document.querySelector('.nav-menu');
    const navButtons = document.querySelector('.nav-buttons');
    const navLinks = document.querySelectorAll('.nav-menu a');

    // Función para abrir/cerrar
    function toggleMenu() {
        hamburger.classList.toggle('active');
        navMenu.classList.toggle('active');
        navButtons.classList.toggle('active');
        document.body.style.overflow = navMenu.classList.contains('active') ? 'hidden' : 'auto';
    }

    // Click en hamburguesa
    if (hamburger) {
        hamburger.addEventListener('click', toggleMenu);
    }

    // Cerrar menú al hacer click en un enlace
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (navMenu.classList.contains('active')) {
                toggleMenu();
            }
        });
    });

    // Marcar el enlace activo según la URL actual
    const currentPath = window.location.pathname;
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        // Si la URL actual comienza con el href del enlace, marcar como activo
        if (currentPath === href || currentPath.startsWith(href + '/')) {
            link.classList.add('active-link');
        } else {
            link.classList.remove('active-link');
        }
    });
});