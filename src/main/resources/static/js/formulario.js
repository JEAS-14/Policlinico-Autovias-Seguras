document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('contactForm');

    if (form) {
        form.addEventListener('submit', function(e) {
            const btn = this.querySelector('.btn-submit');
            const termsCheckbox = document.getElementById('terms');
            
            // Validar términos y condiciones
            if (!termsCheckbox.checked) {
                e.preventDefault();
                alert('Debes aceptar las Políticas de Privacidad para continuar.');
                return;
            }
            
            // Cambiar texto del botón durante el envío
            btn.innerText = 'Enviando...';
            btn.disabled = true;
            
            // El formulario se enviará normalmente al backend
        });

        // Validación en tiempo real del teléfono
        const telefonoInput = document.getElementById('telefono');
        if (telefonoInput) {
            telefonoInput.addEventListener('input', function(e) {
                // Permitir solo números
                this.value = this.value.replace(/[^0-9]/g, '');
                
                // Limitar a 9 dígitos
                if (this.value.length > 9) {
                    this.value = this.value.slice(0, 9);
                }
            });
        }
    }
});