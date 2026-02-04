/* src/main/resources/static/js/nuestroBlog.js */

document.addEventListener('DOMContentLoaded', () => {
    
    // --- 1. ANIMACIÓN SCROLL ---
    const observerOptions = { root: null, rootMargin: '0px', threshold: 0.1 };
    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    const animatedElements = document.querySelectorAll('.card, .featured-post');
    animatedElements.forEach(el => {
        el.classList.add('hidden-scroll');
        observer.observe(el);
    });

    // --- 2. FILTRADO ---
    const badges = document.querySelectorAll('.badge');
    const cards = document.querySelectorAll('.card');
    const backToBlognContainer = document.getElementById('back-to-blog-container');
    const backToBlognBtn = document.getElementById('back-to-blog-btn');
    let activeFilter = null;

    badges.forEach(badge => {
        badge.style.cursor = 'pointer';
        badge.addEventListener('click', (e) => {
            e.preventDefault(); 
            e.stopPropagation();
            
            if(badge.classList.contains('badge-overlay')) return;

            const category = e.target.textContent.trim();
            activeFilter = category;
            
            // Mostrar botón de volver
            backToBlognContainer.style.display = 'block';

            // Cambiar opacidad de badges (solo del blog grid, no del modal)
            document.querySelectorAll('.card .badge, .featured-post .badge').forEach(b => {
                if(!b.classList.contains('badge-overlay')) {
                    b.style.opacity = '0.5';
                }
            });
            e.target.style.opacity = '1';

            cards.forEach(card => {
                const cardBadge = card.querySelector('.badge').textContent.trim();
                if (cardBadge === category) {
                    card.style.display = 'flex';
                    card.classList.remove('visible');
                    setTimeout(() => card.classList.add('visible'), 100);
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });

    // Botón volver al blog completo
    if(backToBlognBtn) {
        backToBlognBtn.addEventListener('click', () => {
            activeFilter = null;
            backToBlognContainer.style.display = 'none';
            
            // Mostrar todas las tarjetas
            cards.forEach(card => {
                card.style.display = 'flex';
                card.classList.remove('visible');
                setTimeout(() => card.classList.add('visible'), 100);
            });
            
            // Restaurar opacidad de badges
            badges.forEach(b => {
                if(!b.classList.contains('badge-overlay')) {
                    b.style.opacity = '1';
                }
            });
        });
    }

    // ===============================================
    // 3. LÓGICA DEL MODAL (TARJETA FLOTANTE)
    // ===============================================
    const modal = document.getElementById('news-modal');
    const closeModalX = document.querySelector('.close-modal');
    const closeModalBtn = document.querySelector('.close-modal-btn');
    const articleTriggers = document.querySelectorAll('.article-trigger'); // Todas las tarjetas clicables

    // Elementos dentro del modal para rellenar
    const modalImg = document.getElementById('modal-img');
    const modalBadge = document.getElementById('modal-badge');
    const modalDate = document.getElementById('modal-date');
    const modalTitle = document.getElementById('modal-title');
    const modalText = document.getElementById('modal-text');

    // Función abrir modal
    function openModal(card) {
        // 1. Obtener datos de la tarjeta clicada
        const imgSrc = card.querySelector('.source-img').src;
        const badgeText = card.querySelector('.source-badge').textContent;
        const badgeClass = card.querySelector('.source-badge').classList[1]; // ej: badge-teal
        const dateText = card.querySelector('.source-date').innerHTML; 
        const titleText = card.querySelector('.source-title').textContent;
        
        // El contenido completo oculto
        const fullContent = card.querySelector('.article-full-content').innerHTML;

        // 2. Inyectar datos al modal
        modalImg.src = imgSrc;
        
        modalBadge.textContent = badgeText;
        modalBadge.className = `badge badge-overlay ${badgeClass}`; // Mantener color original
        
        modalDate.innerHTML = dateText;
        modalTitle.textContent = titleText;
        modalText.innerHTML = fullContent; // Insertamos el HTML completo

        // 3. Mostrar Modal
        modal.classList.add('active');
        document.body.style.overflow = 'hidden'; // Evitar scroll de fondo
    }

    // Función cerrar modal
    function closeModal() {
        modal.classList.remove('active');
        document.body.style.overflow = 'auto'; // Restaurar scroll
    }

    // Event Listeners para las tarjetas
    articleTriggers.forEach(trigger => {
        trigger.addEventListener('click', (e) => {
            e.preventDefault(); // Evitar que el link recargue
            openModal(trigger);
        });
    });

    // Cerrar con botones
    closeModalX.addEventListener('click', closeModal);
    closeModalBtn.addEventListener('click', closeModal);

    // Cerrar al hacer clic fuera del contenido (en el fondo oscuro)
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            closeModal();
        }
    });

    // Cerrar con tecla ESC
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && modal.classList.contains('active')) {
            closeModal();
        }
    });
    if (typeof particlesJS !== 'undefined') {
        particlesJS("particles-js", {
            "particles": {
                "number": { "value": 70, "density": { "enable": true, "value_area": 800 } },
                "color": { "value": "#5836be" }, /* Tu color primario */
                "shape": { "type": "circle" },
                "opacity": { "value": 0.3, "random": false },
                "size": { "value": 3, "random": true },
                "line_linked": {
                    "enable": true,
                    "distance": 150,
                    "color": "#2dc4ad", /* Tu color secundario */
                    "opacity": 0.25,
                    "width": 1
                },
                "move": {
                    "enable": true,
                    "speed": 1.5,
                    "direction": "none",
                    "out_mode": "out"
                }
            },
            "interactivity": {
                "detect_on": "canvas",
                "events": {
                    "onhover": { "enable": true, "mode": "grab" }, /* Los puntos se unen al mouse */
                    "onclick": { "enable": true, "mode": "push" }
                },
                "modes": {
                    "grab": { "distance": 200, "line_linked": { "opacity": 0.5 } }
                }
            },
            "retina_detect": true
        });
    }

    if (typeof particlesJS !== 'undefined') {
        particlesJS("particles-js", {
            "particles": {
                "number": { "value": 70, "density": { "enable": true, "value_area": 800 } },
                "color": { "value": "#5836be" }, /* Tu color primario */
                "shape": { "type": "circle" },
                "opacity": { "value": 0.3, "random": false },
                "size": { "value": 3, "random": true },
                "line_linked": {
                    "enable": true,
                    "distance": 150,
                    "color": "#2dc4ad", /* Tu color secundario */
                    "opacity": 0.25,
                    "width": 1
                },
                "move": {
                    "enable": true,
                    "speed": 1.5,
                    "direction": "none",
                    "out_mode": "out"
                }
            },
            "interactivity": {
                "detect_on": "canvas",
                "events": {
                    "onhover": { "enable": true, "mode": "grab" }, /* Los puntos se unen al mouse */
                    "onclick": { "enable": true, "mode": "push" }
                },
                "modes": {
                    "grab": { "distance": 200, "line_linked": { "opacity": 0.5 } }
                }
            },
            "retina_detect": true
        });
    }

});
