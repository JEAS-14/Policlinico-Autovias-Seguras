document.addEventListener('DOMContentLoaded', () => {

    // 1. ANIMACIÓN SCROLL Y CONTADORES (TU CÓDIGO)
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
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


    function handleCardInteraction(btn, licenseType) {
    const card = btn.closest('.license-card');
    const isAlreadyOpen = card.classList.contains('active');

    // 1. Si el botón ya está en modo "Cotizar" (tarjeta abierta), redirigir a WhatsApp
    if (isAlreadyOpen) {
        const message = `Hola, deseo cotizar el examen para Brevete: ${licenseType}`;
        const whatsappUrl = `https://wa.me/51913889497?text=${encodeURIComponent(message)}`;
        window.open(whatsappUrl, '_blank');
        return;
    }

    // 2. Si la tarjeta está cerrada:
    // Primero, cerramos TODAS las otras tarjetas para mantener el orden
    document.querySelectorAll('.license-card').forEach(c => {
        c.classList.remove('active');
        const b = c.querySelector('.btn-card-action');
        b.innerText = "VER MÁS";
        b.classList.remove('btn-quote-mode');
        // Reseteamos el icono si quieres
        b.innerHTML = 'VER MÁS <i class="fas fa-chevron-down"></i>';
    });

    // 3. Abrimos la tarjeta actual
    card.classList.add('active');
    
    // 4. Transformamos el botón a "COTIZAR"
    btn.innerHTML = '<i class="fab fa-whatsapp"></i> COTIZAR AHORA';
    btn.classList.add('btn-quote-mode');
}

    // ==========================================
    // 2. LÓGICA DE TARJETAS (AUTOMÁTICO - HOVER)
    // ==========================================
    const cards = document.querySelectorAll('.license-card');

    cards.forEach(card => {
        // AL ENTRAR EL MOUSE (PC): Se abre sola
        card.addEventListener('mouseenter', () => {
            card.classList.add('active');
        });

        // AL SALIR EL MOUSE (PC): Se cierra sola
        card.addEventListener('mouseleave', () => {
            card.classList.remove('active');
        });

        // EN CELULAR (Donde no hay mouse): Al tocar se abre/cierra
        card.addEventListener('click', () => {
            // En celular el 'mouseenter' a veces falla, el click asegura que funcione
            if (window.innerWidth <= 768) {
                if (card.classList.contains('active')) {
                    card.classList.remove('active');
                } else {
                    // Cierra las otras para que no estorben en pantalla pequeña
                    cards.forEach(c => c.classList.remove('active'));
                    card.classList.add('active');
                }
            }
        });
    });
});