document.addEventListener('DOMContentLoaded', () => {
    
    // Observer para animaciones al hacer scroll
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                
                // Si llegamos a las estadísticas, iniciar conteo
                if (entry.target.classList.contains('stats-box')) {
                    startCounters();
                }
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.1 });

    document.querySelectorAll('.fade-in, .slide-up, .stats-box').forEach(el => {
        observer.observe(el);
    });

    // Función para animar los números (+98, +2000)
    let countersStarted = false;
    function startCounters() {
        if (countersStarted) return;
        countersStarted = true;

        document.querySelectorAll('.stat-number').forEach(counter => {
            const target = +counter.getAttribute('data-target');
            const duration = 2000; 
            const step = Math.ceil(target / (duration / 16)); 
            
            let current = 0;
            const timer = setInterval(() => {
                current += step;
                if (current >= target) {
                    counter.innerText = "+" + target;
                    clearInterval(timer);
                } else {
                    counter.innerText = "+" + current;
                }
            }, 16);
        });
    }
});