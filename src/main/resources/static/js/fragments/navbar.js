document.addEventListener('DOMContentLoaded', function() {
    const hamburger = document.getElementById('hamburger');
    const navMenu = document.querySelector('.nav-menu');
    const navButtons = document.querySelector('.nav-buttons');
    const navLinks = document.querySelectorAll('.nav-menu a'); // Todos los enlaces
    const dropdownParents = document.querySelectorAll('.nav-menu .has-dropdown');
    const contactBubble = document.getElementById('contactBubble');

    // --- FUNCIÓN DROPDOWN DE CONTACTO ---
    if (contactBubble) {
        contactBubble.addEventListener('click', function(e) {
            e.stopPropagation();
            this.classList.toggle('active');
        });
        
        // Cerrar al hacer clic fuera
        document.addEventListener('click', function(e) {
            if (contactBubble && !contactBubble.contains(e.target)) {
                contactBubble.classList.remove('active');
            }
        });
    }

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

    // --- DROPDOWN EN MÓVIL (CLICK) ---
    dropdownParents.forEach(dropdown => {
        const menuLink = dropdown.querySelector('> a');
        
        menuLink.addEventListener('click', function(e) {
            // Solo prevenir en móvil
            if (window.innerWidth <= 992) {
                e.preventDefault();
                
                // Cerrar otros dropdowns abiertos
                dropdownParents.forEach(other => {
                    if (other !== dropdown) {
                        other.classList.remove('open');
                    }
                });
                
                // Toggle del actual
                dropdown.classList.toggle('open');
            }
        });
    });

    // Cerrar dropdowns al cambiar tamaño de pantalla
    window.addEventListener('resize', function() {
        if (window.innerWidth > 992) {
            dropdownParents.forEach(dropdown => {
                dropdown.classList.remove('open');
            });
        }
    });

    // --- LÓGICA DE ACTIVE STATE (MEJORADA) ---
    const currentUrl = window.location.pathname;

    navLinks.forEach(link => {
        // Obtener solo el path de la URL del enlace (sin dominio)
        let href = link.getAttribute('href');
        
        // Si es una URL completa, extraer solo el path
        if (href && href.includes('://')) {
            try {
                href = new URL(href).pathname;
            } catch (e) {
                // Si falla, usar href tal cual
            }
        }
        
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