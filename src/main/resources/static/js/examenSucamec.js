document.addEventListener('DOMContentLoaded', () => {

    // --- 1. ANIMACIÓN SCROLL (Elementos aparecen al bajar) ---
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');

                // Si es stats-box, iniciar conteo
                if (entry.target.classList.contains('stats-box')) {
                    startCounters();
                }

                observer.unobserve(entry.target); // solo animar una vez
            }
        });
    }, { threshold: 0.15, rootMargin: "0px 0px -50px 0px" });

    // Seleccionamos todos los elementos animables
    document.querySelectorAll('.fade-in, .slide-up, .stats-box').forEach(el => {
        observer.observe(el);
    });

    // --- 2. CONTADORES DE NÚMEROS ---
    let countersStarted = false;
    function startCounters() {
        if (countersStarted) return;
        countersStarted = true;

        document.querySelectorAll('.stat-number').forEach(counter => {
            const target = +counter.getAttribute('data-target');
            const duration = 2000; // duración total en ms
            const stepTime = 20; // intervalo
            const steps = duration / stepTime;
            const increment = target / steps;

            let current = 0;
            const timer = setInterval(() => {
                current += increment;
                if (current >= target) {
                    counter.innerText = "+" + target;
                    clearInterval(timer);
                } else {
                    counter.innerText = "+" + Math.floor(current);
                }
            }, stepTime);
        });
    }

    // --- 3. Botón flotante animado (opcional) ---
    const whatsappBtn = document.querySelector('.whatsapp-float');
    whatsappBtn.addEventListener('mouseenter', () => {
        whatsappBtn.style.transform = "scale(1.2)";
        whatsappBtn.style.boxShadow = "0 10px 25px rgba(0,0,0,0.3)";
    });
    whatsappBtn.addEventListener('mouseleave', () => {
        whatsappBtn.style.transform = "scale(1)";
        whatsappBtn.style.boxShadow = "2px 2px 5px rgba(0,0,0,0.3)";
    });
});
