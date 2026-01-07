document.addEventListener('DOMContentLoaded', () => {
    
    // --- 1. ANIMACIÓN DE APARICIÓN (Scroll Reveal) ---
    const observerOptions = {
        threshold: 0.15, // Se activa cuando el 15% del elemento es visible
        rootMargin: "0px 0px -50px 0px"
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                
                // Si el elemento visible es la caja de estadísticas, iniciamos el contador
                if (entry.target.classList.contains('stats-box')) {
                    startCounters();
                }
                
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Seleccionar elementos a animar (tienen clase fade-in o slide-up en el HTML)
    const animatedElements = document.querySelectorAll('.fade-in, .slide-up, .stats-box');
    animatedElements.forEach(el => observer.observe(el));


    // --- 2. LÓGICA DEL CONTADOR DE NÚMEROS ---
    let countersStarted = false;

    function startCounters() {
        if (countersStarted) return; // Evitar que corra dos veces
        countersStarted = true;

        const counters = document.querySelectorAll('.stat-number');
        const speed = 200; // Mientras más bajo, más rápido

        counters.forEach(counter => {
            const updateCount = () => {
                const target = +counter.getAttribute('data-target'); // Obtener el número final (ej: 1500)
                const count = +counter.innerText; // Número actual
                
                // Calcular el incremento para que todos terminen al mismo tiempo aprox
                const inc = target / speed; 

                if (count < target) {
                    // Sumar y mostrar sin decimales feos, usando Math.ceil
                    counter.innerText = Math.ceil(count + inc);
                    setTimeout(updateCount, 20); // Repetir cada 20ms
                } else {
                    // Asegurar que termine en el número exacto y añadir el "+" si es necesario
                    counter.innerText = "+" + target; 
                }
            };
            updateCount();
        });
    }
});