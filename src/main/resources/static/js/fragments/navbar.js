document.addEventListener('DOMContentLoaded', function() {
    const hamburger = document.getElementById('hamburger');
    const navMenu = document.querySelector('.nav-menu');
    const navButtons = document.querySelector('.nav-buttons');
    const navLinks = document.querySelectorAll('.nav-menu a'); // Selecciona los enlaces

    // Función para abrir/cerrar
    function toggleMenu() {
        hamburger.classList.toggle('active');
        navMenu.classList.toggle('active');
        navButtons.classList.toggle('active');

        // Bloquear scroll del fondo cuando el menú está abierto
        document.body.style.overflow = navMenu.classList.contains('active') ? 'hidden' : 'auto';
    }

    // Click en hamburguesa
    if (hamburger) {
        hamburger.addEventListener('click', toggleMenu);
    }

    // CRUCIAL: Cerrar menú al hacer click en un enlace para poder ver la sección
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (navMenu.classList.contains('active')) {
                toggleMenu(); // Cierra el menú y permite ver el sitio
            }
        });
    });
});