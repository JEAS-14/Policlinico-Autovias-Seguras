document.addEventListener('DOMContentLoaded', () => {
    
    // 1. ANIMACIÓN SCROLL (Elementos aparecen al bajar)
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                // Si vemos las estadísticas, iniciar conteo
                if (entry.target.classList.contains('stats-box')) {
                    startCounters();
                }
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.15, rootMargin: "0px 0px -50px 0px" });

    document.querySelectorAll('.fade-in, .slide-up, .stats-box').forEach(el => {
        observer.observe(el);
    });

    // 2. CONTADORES DE NÚMEROS (Animación +10, +1500)
    let countersStarted = false;
    function startCounters() {
        if (countersStarted) return;
        countersStarted = true;

        document.querySelectorAll('.stat-number').forEach(counter => {
            const target = +counter.getAttribute('data-target');
            const duration = 2000; 
            const stepTime = 20;
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
});