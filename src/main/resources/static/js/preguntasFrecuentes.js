/* src/main/resources/static/js/preguntas-frecuentes.js */

document.addEventListener('DOMContentLoaded', () => {
    
    // ==========================================
    // 1. ANIMACIONES DE ENTRADA (SCROLL)
    // ==========================================
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('is-visible');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    const animatedElements = document.querySelectorAll('.animate-on-scroll');
    animatedElements.forEach(el => observer.observe(el));


    // ==========================================
    // 2. FUNCIONALIDAD DEL ACORDEÓN
    // ==========================================
    const faqQuestions = document.querySelectorAll('.faq-question');
    
    faqQuestions.forEach(question => {
        question.addEventListener('click', () => {
            const faqItem = question.parentElement;
            const answer = faqItem.querySelector('.faq-answer');
            const icon = question.querySelector('.faq-icon');
            
            // Alternar estado de la respuesta actual
            answer.classList.toggle('hidden');
            icon.classList.toggle('rotate-180');
            
            // (Opcional) Cerrar otras preguntas abiertas para mantener limpio el diseño
            faqQuestions.forEach(otherQuestion => {
                if (otherQuestion !== question) {
                    const otherItem = otherQuestion.parentElement;
                    const otherAnswer = otherItem.querySelector('.faq-answer');
                    const otherIcon = otherQuestion.querySelector('.faq-icon');
                    
                    if (otherAnswer && !otherAnswer.classList.contains('hidden')) {
                        otherAnswer.classList.add('hidden');
                        otherIcon.classList.remove('rotate-180');
                    }
                }
            });
        });
    });


    // ==========================================
    // 3. FILTRO POR CATEGORÍAS
    // ==========================================
    const categoryButtons = document.querySelectorAll('.faq-category-btn');
    const faqItems = document.querySelectorAll('.faq-item');
    
    categoryButtons.forEach(button => {
        button.addEventListener('click', () => {
            const category = button.dataset.category;
            
            // Actualizar estado visual de los botones
            categoryButtons.forEach(btn => {
                btn.classList.remove('bg-[#2E1A5F]', 'text-white', 'active');
                btn.classList.add('bg-white', 'text-[#2E1A5F]');
            });

            button.classList.add('bg-[#2E1A5F]', 'text-white', 'active');
            button.classList.remove('bg-white', 'text-[#2E1A5F]');
            
            // Filtrar elementos
            if (category === 'general') {
                faqItems.forEach(item => item.style.display = 'block');
            } else {
                faqItems.forEach(item => {
                    // Verificamos si la categoría coincide
                    if (item.dataset.category === category) {
                        item.style.display = 'block';
                    } else {
                        item.style.display = 'none';
                    }
                });
            }
        });
    });


    // ==========================================
    // 4. BUSCADOR EN TIEMPO REAL
    // ==========================================
    const searchInput = document.getElementById('faq-search');
    
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            const searchTerm = e.target.value.toLowerCase();
            
            faqItems.forEach(item => {
                const questionText = item.querySelector('.faq-question span').textContent.toLowerCase();
                const answerText = item.querySelector('.faq-answer p').textContent.toLowerCase();
                
                // Mostrar si coincide con la pregunta O la respuesta
                if (questionText.includes(searchTerm) || answerText.includes(searchTerm)) {
                    item.style.display = 'block';
                } else {
                    item.style.display = 'none';
                }
            });
        });
    }
});