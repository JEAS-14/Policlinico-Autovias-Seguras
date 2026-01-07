document.addEventListener('DOMContentLoaded', () => {
    
    // --- 1. ANIMACIÓN DE APARICIÓN AL HACER SCROLL (Scroll Reveal) ---
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1 // Se activa cuando el 10% del elemento es visible
    };

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                observer.unobserve(entry.target); // Dejar de observar una vez animado
            }
        });
    }, observerOptions);

    // Seleccionamos los elementos que queremos animar
    const animatedElements = document.querySelectorAll('.card, .featured-post');
    animatedElements.forEach(el => {
        el.classList.add('hidden-scroll'); // Añadimos clase inicial oculta
        observer.observe(el);
    });

    // --- 2. FILTRADO POR CATEGORÍAS (Interactivo) ---
    // Esto permite que al hacer clic en un badge (ej: "Salud"), se filtren los artículos
    const badges = document.querySelectorAll('.badge');
    const cards = document.querySelectorAll('.card');

    badges.forEach(badge => {
        badge.style.cursor = 'pointer'; // Cambiamos el cursor para indicar que es clicable
        
        badge.addEventListener('click', (e) => {
            e.preventDefault(); // Evitamos que salte la página si está dentro de un <a>
            const category = e.target.textContent.trim();

            // Resaltar visualmente el filtro activo (opcional)
            badges.forEach(b => b.style.opacity = '0.5');
            e.target.style.opacity = '1';

            cards.forEach(card => {
                const cardBadge = card.querySelector('.badge').textContent.trim();
                
                // Si la categoría coincide o si hacemos clic de nuevo para resetear
                if (cardBadge === category) {
                    card.style.display = 'block';
                    // Pequeña animación al filtrar
                    card.classList.remove('visible');
                    setTimeout(() => card.classList.add('visible'), 100);
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });

    // Añadir un botón o forma de "Ver todo" si se filtra (Reset)
    const headerTitle = document.querySelector('.blog-header h1');
    headerTitle.style.cursor = 'pointer';
    headerTitle.title = "Clic para ver todas las noticias";
    headerTitle.addEventListener('click', () => {
        cards.forEach(card => card.style.display = 'block');
        badges.forEach(b => b.style.opacity = '1');
    });
});