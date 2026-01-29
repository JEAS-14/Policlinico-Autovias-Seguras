document.addEventListener("DOMContentLoaded", function() {
    
    // Función para detectar cuando el elemento entra en pantalla
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                observer.unobserve(entry.target); // Dejar de observar una vez animado
            }
        });
    }, observerOptions);

    // Elementos a animar
    const elementsToAnimate = document.querySelectorAll('.reveal-left, .reveal-right, .fade-in, .animate-up, .animate-down');
    
    elementsToAnimate.forEach(el => {
        observer.observe(el);
    });
});