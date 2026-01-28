document.addEventListener("DOMContentLoaded", () => {

    // =========================================================
    // 1. ANIMACIÓN DE CONTADORES (+10, +1500)
    // =========================================================
    const statsSection = document.querySelector('.stats-container');
    const counters = document.querySelectorAll('.stat-number');
    let started = false;

    if (statsSection && counters.length > 0) {
        const statsObserver = new IntersectionObserver((entries) => {
            if (entries[0].isIntersecting && !started) {
                started = true;
                counters.forEach(counter => {
                    const target = +counter.getAttribute('data-target');
                    const addPlus = counter.getAttribute('data-plus') === 'true';
                    const duration = 2000;
                    const increment = target / (duration / 16);
                    let current = 0;

                    const updateCounter = () => {
                        current += increment;
                        if (current < target) {
                            counter.innerText = (addPlus ? '+' : '') + Math.ceil(current);
                            requestAnimationFrame(updateCounter);
                        } else {
                            counter.innerText = (addPlus ? '+' : '') + target;
                        }
                    };
                    updateCounter();
                });
            }
        });
        statsObserver.observe(statsSection);
    }

    // =========================================================
    // 2. ACORDEÓN "¿POR QUÉ ELEGIRNOS?"
    // =========================================================
    const accordionItems = document.querySelectorAll('.accordion-item');

    accordionItems.forEach(item => {
        const header = item.querySelector('.item-header');
        if (header) {
            header.addEventListener('click', () => {
                // Cerrar los otros (opcional)
                accordionItems.forEach(otherItem => {
                    if (otherItem !== item) {
                        otherItem.classList.remove('active');
                    }
                });
                // Abrir/Cerrar actual
                item.classList.toggle('active');
            });
        }
    });

    // =========================================================
    // 3. ANIMACIÓN SCROLL (VISUALIZACIÓN DE SECCIONES)
    // =========================================================
    const chooseSection = document.getElementById('choose-us-section');
    if (chooseSection) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('active-section');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.3 });
        observer.observe(chooseSection);
    }

    const locationGrid = document.querySelector('.location-grid');
    if (locationGrid) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.2 });
        observer.observe(locationGrid);
    }

    // =========================================================
    // 4. MAPA INTERACTIVO / CROQUIS (NUEVO CÓDIGO AGREGADO)
    // =========================================================
    const triggers = document.querySelectorAll('.route-trigger');
    const dynamicLink = document.getElementById('dynamic-link');
    const dynamicImage = document.getElementById('dynamic-image');
    const mapContainer = document.querySelector('.map-container');

    // Función para mostrar
    function showCroquis(triggerElement) {
        const croquisUrl = triggerElement.getAttribute('data-croquis');
        const mapsUrl = triggerElement.getAttribute('data-maps');

        if (croquisUrl && mapsUrl && dynamicImage && dynamicLink) {
            dynamicImage.src = croquisUrl;
            dynamicLink.href = mapsUrl;
            dynamicLink.classList.add('active');
        }
    }

    // Función para ocultar
    function hideCroquis() {
        if (dynamicLink) {
            dynamicLink.classList.remove('active');
            setTimeout(() => {
                if (!dynamicLink.classList.contains('active') && dynamicImage) {
                    dynamicImage.src = ""; // Limpiar para ahorrar memoria
                }
            }, 400);
        }
    }

    // Eventos Mouse Hover
    if (triggers.length > 0) {
        triggers.forEach(trigger => {
            trigger.addEventListener('mouseenter', () => {
                showCroquis(trigger);
            });
        });
    }

    // Ocultar al salir del mapa
    if (mapContainer) {
        mapContainer.addEventListener('mouseleave', () => {
            hideCroquis();
        });
    }

    // =========================================================
    // 5. CARUSEL DE TESTIMONIOS (REVIEWS)
    // =========================================================
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
            text: "Me sorprendió la atención personalizada que me brindaron al realizar mi examen médico para mi licencia de conducir A1, lo recomendaría a mis amigos. ",
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
            name: "Cesar Correa",
            text: "Excelente atención de todo el personal. Te explican paso a paso el proceso y te hacen sentir cómodo",
            source: "Google",
            stars: 5,
            img: "https://ui-avatars.com/api/?name=Pedro+Castillo&background=random"
        },
        {
            name: "JUAN PEREZ",
            text: "Excelente Servicio , procesos muy claros , grata experiencia lo recomiendo 100%",
            source: "Facebook",
            stars: 5,
            img: "https://ui-avatars.com/api/?name=Pedro+Castillo&background=random"
        },
        {
            name: "Luis Ángel Ortiz Mejía",
            text: "El personal es muy profesional y el proceso fue rápido. Ideal para exámenes de salud ocupacional.",
            source: "Vortice contratista Generales SAC",
            stars: 5,
            img: "https://ui-avatars.com/api/?name=Luis+Ortiz&background=random"
        }
    ];

    const track = document.getElementById('reviewsTrack');

    if (track) {
        const createCard = (review) => {
            const icon = review.source === 'Google' 
                ? '<i class="fab fa-google"></i>' 
                : (review.source === 'Facebook' ? '<i class="fab fa-facebook-f"></i>' : '<i class="fas fa-building"></i>');
            
            const sourceClass = review.source === 'Google' ? 'source-google' : 'source-facebook';
            
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

        let cardsHtml = '';
        reviewsData.forEach(review => {
            cardsHtml += createCard(review);
        });

        track.innerHTML = cardsHtml + cardsHtml;
    }
});