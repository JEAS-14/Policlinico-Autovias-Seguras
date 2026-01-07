document.addEventListener('DOMContentLoaded', () => {
    
    // Configuración del Intersection Observer (detector de scroll)
    const observerOptions = {
        root: null, // null significa que usa el viewport del navegador
        rootMargin: '0px',
        threshold: 0.15 // La animación se activa cuando el 15% del elemento es visible
    };

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                // Cuando el elemento entra en pantalla, añadimos la clase que tiene la animación CSS
                entry.target.classList.add('is-visible');
                
                // Dejamos de observar el elemento para que la animación solo ocurra una vez
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Seleccionar todos los elementos que queremos animar (los que tienen la clase .animate-on-scroll)
    const animatedElements = document.querySelectorAll('.animate-on-scroll');
    
    animatedElements.forEach((el) => {
        observer.observe(el);
    });
});