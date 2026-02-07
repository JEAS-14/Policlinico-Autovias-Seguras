/* ================================================================
   1. FUNCIONES PARA INTERACCIÓN (Llamadas desde el HTML)
   ================================================================ */

// Función para abrir tarjetas de Licencias (Botón "Ver más")
function toggleCard(boton) {
    const tarjeta = boton.closest('.license-card');
    
    // Cierra las demás tarjetas para que no se amontonen
    document.querySelectorAll('.license-card').forEach(card => {
        if (card !== tarjeta) card.classList.remove('tarjeta-abierta');
    });

    // Abre o cierra la tarjeta actual
    tarjeta.classList.toggle('tarjeta-abierta');
}

// Función para el Acordeón de Salud Ocupacional
function toggleOcupational(card) {
    // Si ya está abierta, la cerramos
    if (card.classList.contains('active')) {
        card.classList.remove('active');
        return;
    }

    // Cerramos todas las demás primero (efecto acordeón)
    document.querySelectorAll('.type-card').forEach(c => {
        c.classList.remove('active');
    });

    // Abrimos la que se clickeó
    card.classList.add('active');
}


/* ================================================================
   2. LÓGICA AUTOMÁTICA (Al cargar la página)
   ================================================================ */

document.addEventListener('DOMContentLoaded', () => {

    // --- A. ANIMACIÓN SCROLL (Elementos aparecen al bajar) ---
    
    const observerOptions = {
        threshold: 0.15, 
        rootMargin: "0px 0px -50px 0px"
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                // Añadimos las clases para activar la animación CSS
                entry.target.classList.add('visible');
                
                // Soporte para tus otras clases (si las usas en otras secciones)
                entry.target.classList.remove('hidden-state'); 
                entry.target.classList.add('visible-state');

                // Si el elemento visible es la caja de estadísticas, iniciamos los números
                // Si el elemento contiene contadores, iniciamos los números
                if (entry.target.querySelector && entry.target.querySelector('.stat-number')) {
                    console.log('examenMedicoOcupacional.js: detected stat-number in observed element, starting counters');
                    startCounters();
                }
                
                // Dejamos de observar este elemento (para que no se anime 2 veces)
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Buscamos todos los elementos que deben animarse
    const elementsToAnimate = document.querySelectorAll('.fade-in, .slide-up, .fade-in-up, .stats-container, .stats-box, .js-scroll-item');
    elementsToAnimate.forEach(el => observer.observe(el));

    // Fallback: si al cargar hay contadores ya visibles (por ejemplo en pantallas grandes), iniciarlos
    setTimeout(() => {
        try {
            if (!countersStarted) {
                const anyCounter = document.querySelector('.stat-number');
                if (anyCounter) {
                    const rect = anyCounter.getBoundingClientRect();
                    if (rect.top >= 0 && rect.top < window.innerHeight) {
                        console.log('examenMedicoOcupacional.js: fallback - stat-number visible on load, starting counters');
                        startCounters();
                    }
                }
            }
        } catch (e) {
            console.error('examenMedicoOcupacional.js fallback error', e);
        }
    }, 300);

    // --- C. PASOS EN MÓVIL (Mostrar tooltip al tocar) ---
    const stepsContainer = document.querySelector('.steps-container');
    if (stepsContainer) {
        stepsContainer.addEventListener('click', (event) => {
            if (!window.matchMedia('(max-width: 1024px)').matches) return;
            const step = event.target.closest('.step');
            if (!step) return;
            event.preventDefault();
            const steps = stepsContainer.querySelectorAll('.step');
            const wasActive = step.classList.contains('active');
            steps.forEach(s => s.classList.remove('active'));
            if (!wasActive) step.classList.add('active');
        });
    }


    // --- B. CONTADORES DE NÚMEROS (+1500) ---
    
    let countersStarted = false;

    function startCounters() {
        if (countersStarted) return; // Evita que corra dos veces
        countersStarted = true;

        const counters = document.querySelectorAll('.stat-number');
        const speed = 200; // Velocidad de la animación

        counters.forEach(counter => {
            const wantsPlus = counter.getAttribute('data-plus') === 'true';
            console.log('examenMedicoOcupacional.js: starting counter for target=', counter.getAttribute('data-target'));
            const updateCount = () => {
                const target = +counter.getAttribute('data-target');
                const currentText = (counter.innerText || '').replace(/[^0-9]/g, '');
                const count = currentText ? +currentText : 0;

                const inc = Math.max(1, Math.floor(target / speed));

                if (count < target) {
                    const next = Math.min(target, count + inc);
                    counter.innerText = next;
                    setTimeout(updateCount, 20);
                } else {
                    counter.innerText = wantsPlus ? ('+' + target) : String(target);
                }
            };
            updateCount();
        });
    }
});