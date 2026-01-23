document.addEventListener('DOMContentLoaded', function() {
    const hamburger = document.getElementById('hamburger');
    const navMenu = document.querySelector('.nav-menu');
    const navButtons = document.querySelector('.nav-buttons');
    const navLinks = document.querySelectorAll('.nav-menu a'); // Todos los enlaces

    // --- FUNCIÓN HAMBURGUESA ---
    function toggleMenu() {
        hamburger.classList.toggle('active');
        navMenu.classList.toggle('active');
        navButtons.classList.toggle('active');
        document.body.style.overflow = navMenu.classList.contains('active') ? 'hidden' : 'auto';
    }

    if (hamburger) {
        hamburger.addEventListener('click', toggleMenu);
    }

    // --- LÓGICA DE ACTIVE STATE (MEJORADA) ---
    const currentUrl = window.location.pathname;

    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        
        // Limpiamos clases previas
        link.classList.remove('active-link');

        // 1. Coincidencia exacta (Para páginas normales)
        if (href === currentUrl) {
            link.classList.add('active-link');
            
            // TRUCO: Si el enlace activo está DENTRO de un dropdown...
            // Hay que iluminar también al papá (Nuestros Servicios)
            const parentDropdown = link.closest('.has-dropdown');
            if (parentDropdown) {
                // Buscamos el enlace principal de ese dropdown (el primer <a>)
                const parentLink = parentDropdown.querySelector('a');
                if (parentLink) parentLink.classList.add('active-link');
            }
        }
        // 2. Coincidencia de "Familia" (Para marcar el padre si estás en una subruta)
        // Si la URL actual empieza con el href del enlace (ej: /nuestrosServicios/...)
        // Y no es el home '/'
        else if (href !== '/' && currentUrl.startsWith(href)) {
             link.classList.add('active-link');
        }
    });
});