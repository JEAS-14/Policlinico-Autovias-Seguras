document.addEventListener('DOMContentLoaded', function() {
    const hamburger = document.getElementById('hamburger');
    const navMenu = document.querySelector('.nav-menu');
    const navButtons = document.querySelector('.nav-buttons');
    const navLinks = document.querySelectorAll('.nav-menu a');
    const contactBubble = document.getElementById('contactBubble');
    
    // --- 1. BURBUJA DE CONTACTO ---
    if (contactBubble) {
        contactBubble.addEventListener('click', function(e) {
            e.stopPropagation();
            this.classList.toggle('active');
        });
        document.addEventListener('click', function(e) {
            if (!contactBubble.contains(e.target)) {
                contactBubble.classList.remove('active');
            }
        });
    }

    // --- 2. HAMBURGUESA ---
    function toggleMenu() {
        if(hamburger) hamburger.classList.toggle('active');
        if(navMenu) navMenu.classList.toggle('active');
        if(navButtons) navButtons.classList.toggle('active');
        
        // Bloquear scroll
        document.body.style.overflow = navMenu.classList.contains('active') ? 'hidden' : '';
    }

    if (hamburger) {
        hamburger.addEventListener('click', toggleMenu);
    }

    // --- 3. DROPDOWN EN MÓVIL (Lógica corregida) ---
    // Seleccionamos los LI que tienen dropdown
    const dropdownItems = document.querySelectorAll('.nav-menu li.has-dropdown');

    dropdownItems.forEach(item => {
        const link = item.querySelector('a'); // El enlace "Nuestros Servicios"
        
        if (link) {
            link.addEventListener('click', function(e) {
                // Solo activamos esto en pantallas móviles (menos de 992px)
                if (window.innerWidth < 992) {
                    e.preventDefault(); // Evitamos que navegue
                    e.stopPropagation(); // Evitamos conflictos
                    
                    // Cerramos otros dropdowns si hubiera más
                    dropdownItems.forEach(otherItem => {
                        if (otherItem !== item) {
                            otherItem.classList.remove('open');
                        }
                    });

                    // Abrimos/Cerramos el actual
                    item.classList.toggle('open');
                }
            });
        }
    });

    // Resetear al cambiar tamaño de pantalla
    window.addEventListener('resize', function() {
        if (window.innerWidth >= 992) {
            // Limpiar clases móviles
            if(navMenu) navMenu.classList.remove('active');
            if(hamburger) hamburger.classList.remove('active');
            if(navButtons) navButtons.classList.remove('active');
            document.body.style.overflow = '';
            
            // Cerrar todos los dropdowns abiertos
            dropdownItems.forEach(item => item.classList.remove('open'));
        }
    });

    // --- 4. ESTADO ACTIVO (Active Link) ---
    const currentUrl = window.location.pathname;
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        link.classList.remove('active-link');

        // Si coincide la URL exacta o es una sub-ruta (excepto home)
        if (href === currentUrl || (href !== '/' && currentUrl.startsWith(href))) {
            link.classList.add('active-link');
            
            // Si es un sub-elemento, activar también al padre "Nuestros Servicios"
            const parentLi = link.closest('.has-dropdown');
            if (parentLi) {
                const parentLink = parentLi.querySelector('a');
                if(parentLink) parentLink.classList.add('active-link');
            }
        }
    });
});