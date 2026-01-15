document.addEventListener('DOMContentLoaded', () => {
    
    // --- ANIMACIÓN SCROLL ---
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

    // --- FILTRADO (Corrección para links) ---
    const badges = document.querySelectorAll('.badge');
    const cards = document.querySelectorAll('.card');

    badges.forEach(badge => {
        badge.style.cursor = 'pointer';
        
        badge.addEventListener('click', (e) => {
            e.preventDefault(); // Evita navegar al link del <a>
            e.stopPropagation(); // Evita que el clic suba a la tarjeta padre
            
            const category = e.target.textContent.trim();

            // Reset visual
            badges.forEach(b => b.style.opacity = '0.5');
            e.target.style.opacity = '1';

            cards.forEach(card => {
                // Buscamos el badge dentro del object para que funcione
                const cardBadge = card.querySelector('.badge').textContent.trim();
                
                if (cardBadge === category) {
                    card.style.display = 'flex'; // Flex para mantener diseño
                    card.classList.remove('visible');
                    setTimeout(() => card.classList.add('visible'), 100);
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });
});

document.addEventListener('DOMContentLoaded', () => {
    
    // --- 1. ANIMACIÓN SCROLL (Existente) ---
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

    // --- 2. FILTRADO (Existente, con corrección de link) ---
    const badges = document.querySelectorAll('.badge');
    const cards = document.querySelectorAll('.card');

    badges.forEach(badge => {
        badge.style.cursor = 'pointer';
        badge.addEventListener('click', (e) => {
            e.preventDefault(); 
            e.stopPropagation();
            
            // Si es un badge dentro del modal, no filtramos, solo cerramos modal? No, mejor ignorar
            if(badge.classList.contains('badge-overlay')) return;

            const category = e.target.textContent.trim();
            badges.forEach(b => b.style.opacity = '0.5');
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

    const headerTitle = document.querySelector('.blog-header h1');
    headerTitle.style.cursor = 'pointer';
    headerTitle.addEventListener('click', () => {
        cards.forEach(card => card.style.display = 'flex');
        badges.forEach(b => b.style.opacity = '1');
    });


    // ===============================================
    // 3. LÓGICA DEL MODAL (NUEVO)
    // ===============================================
    const modal = document.getElementById('news-modal');
    const closeModalX = document.querySelector('.close-modal');
    const closeModalBtn = document.querySelector('.close-modal-btn');
    const articleTriggers = document.querySelectorAll('.article-trigger'); // Tarjetas

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
        const dateText = card.querySelector('.source-date').innerHTML; // innerHTML para el icono
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

});