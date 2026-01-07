document.addEventListener('DOMContentLoaded', () => {
    
    const form = document.getElementById('resultsForm');
    const resultDiv = document.getElementById('resultOutput');
    const nameSpan = document.getElementById('resName');

    if (form) {
        form.addEventListener('submit', (e) => {
            e.preventDefault();

            const btn = form.querySelector('.btn-search');
            const originalContent = btn.innerHTML;
            const dniInput = document.getElementById('dni').value;

            // 1. Cambiar estado del botón a "Buscando..."
            btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Buscando...';
            btn.disabled = true;
            resultDiv.classList.add('hidden'); // Ocultar resultado anterior si hubo

            // 2. Simular petición al servidor (2 segundos)
            setTimeout(() => {
                
                // Aquí iría tu lógica real de backend
                // Por ahora, simulamos éxito:
                
                btn.innerHTML = originalContent;
                btn.disabled = false;
                
                // Mostrar nombre simulado (o podrías poner el DNI)
                nameSpan.textContent = "Usuario DNI " + dniInput; 
                
                // Mostrar la caja de resultado
                resultDiv.classList.remove('hidden');

            }, 1500);
        });
    }
});