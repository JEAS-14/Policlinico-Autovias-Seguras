/* src/main/resources/static/js/preguntas-frecuentes.js */

document.addEventListener('DOMContentLoaded', () => {
    
    // 1. SELECCIÓN DE ELEMENTOS GLOBALES
    const faqItems = document.querySelectorAll('.faq-item');
    const categoryButtons = document.querySelectorAll('.faq-category-btn');
    const searchInput = document.getElementById('faq-search');
    const searchErrorMsg = document.getElementById('search-error-msg');
    const contactCta = document.getElementById('contact-cta');

    // ==========================================
    // 1. ANIMACIONES SCROLL
    // ==========================================
    const observerOptions = { root: null, rootMargin: '0px', threshold: 0.1 };
    const observer = new IntersectionObserver((entries, obs) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('is-visible');
                obs.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('.animate-on-scroll').forEach(el => observer.observe(el));

    // ==========================================
    // 2. ACORDEÓN (ACTIVACIÓN POR HOVER)
    // ==========================================
    faqItems.forEach(item => {
        const answer = item.querySelector('.faq-answer');
        const icon = item.querySelector('.faq-question i');

        item.addEventListener('mouseenter', () => {
            answer.classList.remove('hidden');
            if (icon) icon.classList.add('rotate-180');
        });

        item.addEventListener('mouseleave', () => {
            answer.classList.add('hidden');
            if (icon) icon.classList.remove('rotate-180');
        });
    });

    // ==========================================
    // 3. FUNCIÓN DE FILTRADO
    // ==========================================
    function aplicarFiltro(categoriaSeleccionada) {
        let visibleCount = 0;

        faqItems.forEach(item => {
            const itemCategory = item.dataset.category;
            if (categoriaSeleccionada === 'todos' || itemCategory === categoriaSeleccionada) {
                item.style.display = 'block';
                visibleCount++;
            } else {
                item.style.display = 'none';
            }
        });

        // La tarjeta de contacto siempre está visible al final, 
        // pero ocultamos el error de búsqueda si estamos filtrando normal
        if (searchErrorMsg) searchErrorMsg.classList.add('hidden');
    }

    // Eventos para los botones de categoría
    categoryButtons.forEach(button => {
        button.addEventListener('click', () => {
            const selectedCategory = button.dataset.category;

            categoryButtons.forEach(btn => {
                btn.classList.remove('bg-[#2E1A5F]', 'text-white');
                btn.classList.add('bg-white', 'text-[#2E1A5F]', 'border', 'border-[#2E1A5F]');
            });
            button.classList.remove('bg-white', 'text-[#2E1A5F]', 'border');
            button.classList.add('bg-[#2E1A5F]', 'text-white');

            aplicarFiltro(selectedCategory);
            
            // Limpiar buscador al filtrar por categoría
            if (searchInput) searchInput.value = "";
        });
    });

    // ==========================================
    // 4. BUSCADOR EN TIEMPO REAL
    // ==========================================
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            const searchTerm = e.target.value.toLowerCase();
            let hasResults = false;

            // Al buscar, quitamos el estado activo de los botones de categoría
            categoryButtons.forEach(btn => {
                btn.classList.remove('bg-[#2E1A5F]', 'text-white');
                btn.classList.add('bg-white', 'text-[#2E1A5F]', 'border', 'border-[#2E1A5F]');
            });

            faqItems.forEach(item => {
                const questionText = item.querySelector('.faq-question span').textContent.toLowerCase();
                const answerText = item.querySelector('.faq-answer').textContent.toLowerCase();
                
                if (questionText.includes(searchTerm) || answerText.includes(searchTerm)) {
                    item.style.display = 'block';
                    hasResults = true;
                } else {
                    item.style.display = 'none';
                }
            });

            // Lógica del mensaje de error en la tarjeta permanente
            if (searchTerm !== "") {
                if (!hasResults) {
                    if (searchErrorMsg) searchErrorMsg.classList.remove('hidden');
                } else {
                    if (searchErrorMsg) searchErrorMsg.classList.add('hidden');
                }
            } else {
                if (searchErrorMsg) searchErrorMsg.classList.add('hidden');
                aplicarFiltro('general'); // Si borra la búsqueda, vuelve a General
            }
        });
    }

    // ==========================================
    // 5. INICIALIZACIÓN
    // ==========================================
    aplicarFiltro('general');
});