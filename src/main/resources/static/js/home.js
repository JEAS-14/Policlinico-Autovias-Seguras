document.addEventListener("DOMContentLoaded", () => {
  // --- ANIMACIÓN DE LA SECCIÓN DE UBICACIÓN ---

  // Seleccionamos el contenedor del mapa
  const mapContainer = document.querySelector(".map-container");

  // Verificamos si el elemento existe antes de observarlo
  if (mapContainer) {
    // Creamos el observador
    const observer = new IntersectionObserver(
      (entries, observer) => {
        entries.forEach((entry) => {
          // Si el contenedor del mapa entra en la pantalla
          if (entry.isIntersecting) {
            // Añadimos la clase 'visible' que dispara la animación CSS
            entry.target.classList.add("visible");
            // Dejamos de observar una vez que se ha animado
            observer.unobserve(entry.target);
          }
        });
      },
      {
        root: null, // Observa respecto al viewport
        threshold: 0.2, // Se activa cuando el 20% del elemento es visible
      }
    );

    // Empezamos a observar el contenedor del mapa
    observer.observe(mapContainer);
  }
});
document.addEventListener('DOMContentLoaded', () => {
    
    // --- DATOS DE LOS COMENTARIOS (Aquí pegaremos los reales) ---
    const reviewsData = [
        {
            name: "Carlos Mendoza",
            text: "Excelente atención y rapidez en la entrega de resultados para mi brevete. El personal es muy amable.",
            source: "Google",
            stars: 5,
            img: "https://ui-avatars.com/api/?name=Carlos+Mendoza&background=0D8ABC&color=fff"
        },
        {
            name: "María Fernández",
            text: "Me sorprendió la limpieza y el orden. Fui por mi carnet de sanidad y salí súper rápido. Recomendado.",
            source: "Facebook",
            stars: 5,
            img: "https://ui-avatars.com/api/?name=Maria+Fernandez&background=random"
        },
        {
            name: "Jorge Luis R.",
            text: "Todo conforme para el examen de SUCAMEC. Tienen los equipos adecuados y te guían en todo.",
            source: "Google",
            stars: 4,
            img: "https://ui-avatars.com/api/?name=Jorge+Luis&background=random"
        },
        {
            name: "Ana Torres",
            text: "Muy buena atención en recepción, resolvieron todas mis dudas sobre el examen ocupacional.",
            source: "Facebook",
            stars: 5,
            img: "https://ui-avatars.com/api/?name=Ana+Torres&background=random"
        },
        {
            name: "Pedro Castillo",
            text: "Precios justos y ubicación céntrica en SJL. Volveré para mi revalidación sin duda.",
            source: "Google",
            stars: 5,
            img: "https://ui-avatars.com/api/?name=Pedro+Castillo&background=random"
        }
    ];

    const track = document.getElementById('reviewsTrack');

    if (track) {
        // Función para construir la tarjeta HTML
        const createCard = (review) => {
            const icon = review.source === 'Google' 
                ? '<i class="fab fa-google"></i>' 
                : '<i class="fab fa-facebook-f"></i>';
            
            const sourceClass = review.source === 'Google' ? 'source-google' : 'source-facebook';
            
            // Generar estrellas
            let starsHtml = '';
            for(let i=0; i<review.stars; i++) starsHtml += '<i class="fas fa-star"></i>';

            return `
                <div class="review-card">
                    <div class="review-header">
                        <img src="${review.img}" alt="${review.name}" class="review-avatar">
                        <div class="review-info">
                            <h4>${review.name}</h4>
                            <span class="review-source ${sourceClass}">${icon} ${review.source}</span>
                        </div>
                    </div>
                    <div class="review-stars">${starsHtml}</div>
                    <p class="review-text">"${review.text}"</p>
                </div>
            `;
        };

        // Generar tarjetas
        let cardsHtml = '';
        reviewsData.forEach(review => {
            cardsHtml += createCard(review);
        });

        // INYECTAR Y DUPLICAR (Truco Infinito)
        // Ponemos el contenido dos veces para que el bucle no tenga cortes
        track.innerHTML = cardsHtml + cardsHtml;
    }
});
