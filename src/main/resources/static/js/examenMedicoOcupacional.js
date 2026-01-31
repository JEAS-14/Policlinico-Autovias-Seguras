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
                if (entry.target.classList.contains('stats-box')) {
                    startCounters();
                }
                
                // Dejamos de observar este elemento (para que no se anime 2 veces)
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Buscamos todos los elementos que deben animarse
    const elementsToAnimate = document.querySelectorAll('.fade-in, .slide-up, .stats-box, .js-scroll-item');
    elementsToAnimate.forEach(el => observer.observe(el));

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
            const updateCount = () => {
                const target = +counter.getAttribute('data-target');
                const count = +counter.innerText.replace('+', ''); // Limpia símbolos
                
                const inc = target / speed;

                if (count < target) {
                    counter.innerText = Math.ceil(count + inc);
                    setTimeout(updateCount, 20);
                } else {
                    counter.innerText = "+" + target; // Finaliza con el "+"
                }
            };
            updateCount();
        });
    }
});