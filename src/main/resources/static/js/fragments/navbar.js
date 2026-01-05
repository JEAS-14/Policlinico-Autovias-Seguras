document. addEventListener('DOMContentLoaded', function() {
    const hamburger = document.getElementById('hamburger');
    const navMenu = document. querySelector('.nav-menu');
    const navButtons = document.querySelector('. nav-buttons');

    // Toggle menú al hacer clic en hamburguesa
    hamburger.addEventListener('click', function() {
        this.classList.toggle('active');
        navMenu.classList.toggle('active');
        navButtons.classList.toggle('active');
        
        // Prevenir scroll del body cuando el menú está abierto
        document.body.style.overflow = navMenu.classList.contains('active') ? 'hidden' : 'auto';
    });

    // Cerrar menú al hacer clic en un enlace
    const navLinks = document.querySelectorAll('.nav-menu a, .nav-buttons a');
    navLinks.forEach(link => {
        link.addEventListener('click', function() {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
            navButtons. classList.remove('active');
            document.body.style.overflow = 'auto';
        });
    });

    // Cerrar menú al hacer clic fuera
    document.addEventListener('click', function(event) {
        const isClickInside = navMenu. contains(event.target) || 
                            navButtons.contains(event.target) || 
                            hamburger.contains(event.target);
        
        if (!isClickInside && navMenu.classList.contains('active')) {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
            navButtons.classList.remove('active');
            document.body.style.overflow = 'auto';
        }
    });
});