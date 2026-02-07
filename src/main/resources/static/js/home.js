document.addEventListener("DOMContentLoaded", () => {

    // =========================================================
    // 1. ANIMACIÓN DE CONTADORES (+10, +1500, +15)
    // =========================================================
    const statsBar = document.querySelector('.stats-bar-floating');
    const statNumbers = document.querySelectorAll('.stat-float-number');
    let started = false;

    if (statsBar && statNumbers.length > 0) {
        const statsObserver = new IntersectionObserver((entries) => {
            if (entries[0].isIntersecting && !started) {
                started = true;
                statNumbers.forEach((counter, index) => {
                    // Extraer número del texto (ej: "+1500" -> 1500)
                    const textContent = counter.innerText.replace('+', '');
                    const target = parseInt(textContent);
                    const hasPlus = counter.innerText.includes('+');
                    const duration = 2000;
                    const increment = target / (duration / 16);
                    let current = 0;

                    // Mostrar 0 al inicio
                    counter.innerText = (hasPlus ? '+' : '') + '0';

                    const updateCounter = () => {
                        current += increment;
                        if (current < target) {
                            counter.innerText = (hasPlus ? '+' : '') + Math.ceil(current);
                            requestAnimationFrame(updateCounter);
                        } else {
                            counter.innerText = (hasPlus ? '+' : '') + target;
                        }
                    };
                    updateCounter();
                });
            }
        });
        statsObserver.observe(statsBar);
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
    // HERO VISIBILITY OBSERVER - asegurar que ambos paneles salten
    // =========================================================
    try {
        const hero = document.querySelector('.hero-section-modern');
        if (hero) {
            const heroObserver = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        console.log('home.js: hero is visible -> adding hero-visible');
                        const left = hero.querySelector('.hero-left-panel');
                        const right = hero.querySelector('.hero-right-panel');
                        if (left) left.classList.add('hero-visible');
                        if (right) right.classList.add('hero-visible');
                        // trigger image render/update in case it's delayed
                        try { renderServiceImages(); } catch(e) { /* noop */ }
                        heroObserver.unobserve(entry.target);
                    }
                });
            }, { threshold: 0.05 });
            heroObserver.observe(hero);
        } else {
            console.log('home.js: hero element not found');
        }
    } catch (err) {
        console.error('home.js hero observer error', err);
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

// =========================================================
// 5. HERO SECTION MODERNO - SERVICIOS DINÁMICOS
// =========================================================
const servicesData = [
    {
        id: 0,
        title: "Examen Médico Ocupacional - Emo",
        desc: "Salud ocupacional para empresas.",
        fullDesc: "Realizamos evaluaciones médicas ocupacionales (EMO) cumpliendo todas las normativas de ley vigentes.",
        icon: "stethoscope",
        image: "https://res.cloudinary.com/dtozni6ik/image/upload/f_auto,q_auto/v1767590080/a656839e-4c63-4fa8-8040-8f94551e1a9c.png",
        link: "/servicios/examenMedicoOcupacional"
    },
    {
        id: 1,
        title: "Examen Médico Para Licencias De Conducir - Brevete",
        desc: "Brevetes A1, A2, A3.",
        fullDesc: "Obtén tu certificado médico para brevete en tiempo récord. Validado directamente en el sistema del MTC.",
        icon: "car",
        image: "https://res.cloudinary.com/dtozni6ik/image/upload/f_auto,q_auto/v1770217808/imagen_2026-02-04_101005479_ptkzeq.png",
        link: "/servicios/examenMedicoBrevete"
    },
    {
        id: 2,
        title: "Examen Médico-SUCAMEC Fisico y Mental-Psicosomático",
        desc: "Carne Sucamec - Licencia de arma de fuego.",
        fullDesc: "Evaluación integral de salud mental y física para personal de seguridad y uso civil de armas.",
        icon: "shield-alt",
        image: "https://res.cloudinary.com/dtozni6ik/image/upload/f_auto,q_auto/v1770040501/imagen_2026-02-02_085501377_kitinb.png",
        link: "/servicios/examenSucamec"
    },
    {
        id: 3,
        title: "Escuela de Conductores",
        desc: "Recategorización profesional.",
        fullDesc: "Cursos teóricos y prácticos con instructores calificados para mejorar tu categoría de licencia.",
        icon: "car",
        image: "https://res.cloudinary.com/dtozni6ik/image/upload/f_auto,q_auto/v1767590149/82645900-9f7f-4ba5-b4f2-dcb7cd583bef.png",
        link: "/servicios/escuelaConductores"
    }
];

let activeServiceId = 0;

function initHeroSection() {
    renderServiceTabs();
    updateActiveTab();
    renderServiceImages();
}

function renderServiceTabs() {
    const container = document.getElementById('servicesTabs');
    if (!container) return;
    
    // Solo crear los tabs la primera vez
    if (container.children.length === 0) {
        container.innerHTML = servicesData.map((service) => `
            <a href="${service.link}" class="service-tab" data-id="${service.id}">
                <div class="service-tab-icon">
                    <i class="fas fa-${service.icon}"></i>
                </div>
                <div class="service-tab-content">
                    <h3 class="service-tab-title">${service.title}</h3>
                    <p class="service-tab-desc">${service.desc}</p>
                </div>
                <i class="fas fa-arrow-right arrow-pulse"></i>
            </a>
        `).join('');
        
        // Agregar listeners UNA SOLA VEZ
        document.querySelectorAll('.service-tab').forEach((tab) => {
            tab.addEventListener('mouseenter', () => {
                const newId = parseInt(tab.getAttribute('data-id'));
                if (activeServiceId !== newId) {
                    activeServiceId = newId;
                    updateActiveTab();
                    renderServiceImages();
                }
            });
        });
    } else {
        // Solo actualizar la clase active
        updateActiveTab();
    }
}

function updateActiveTab() {
    document.querySelectorAll('.service-tab').forEach((tab) => {
        const tabId = parseInt(tab.getAttribute('data-id'));
        if (tabId === activeServiceId) {
            tab.classList.add('active');
        } else {
            tab.classList.remove('active');
        }
    });
}

function renderServiceImages() {
    const container = document.getElementById('imagesContainer');
    if (!container) return;
    
    container.innerHTML = servicesData.map((service) => `
        <div class="hero-image-card ${activeServiceId === service.id ? 'active' : ''}">
            <img src="${service.image}" alt="${service.title}" class="hero-img">
            <div class="hero-description-floating">
                <h3>${service.title}</h3>
                <p>${service.fullDesc}</p>
            </div>
        </div>
    `).join('');
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initHeroSection);
} else {
    initHeroSection();
}