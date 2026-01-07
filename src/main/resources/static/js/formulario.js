document.addEventListener('DOMContentLoaded', () => {
    
    const form = document.getElementById('contactForm');

    if (form) {
        form.addEventListener('submit', (e) => {
            e.preventDefault(); // Evita que la página se recargue

            // Aquí iría la lógica para enviar los datos al servidor (AJAX/Fetch)
            
            // Simulación de éxito
            const btn = form.querySelector('.btn-submit');
            const originalText = btn.innerText;
            
            btn.innerText = 'Enviando...';
            btn.disabled = true;

            setTimeout(() => {
                alert('¡Gracias! Hemos recibido tus datos. Nos pondremos en contacto contigo pronto.');
                form.reset();
                btn.innerText = originalText;
                btn.disabled = false;
            }, 1500);
        });
    }
});