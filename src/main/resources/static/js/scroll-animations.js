document.addEventListener('DOMContentLoaded', () => {
    const selectors = [
        '.animate-on-scroll',
        '.fade-in',
        '.slide-up',
        '.reveal-left',
        '.reveal-right',
        '.js-scroll-item'
    ];

    const elements = document.querySelectorAll(selectors.join(', '));
    if (!elements.length) return;

    const observerOptions = {
        threshold: 0.15,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (!entry.isIntersecting) return;
            const el = entry.target;
            el.classList.add('is-visible');
            el.classList.add('visible');
            el.classList.add('visible-state');
            el.classList.remove('hidden-state');
            observer.unobserve(el);
        });
    }, observerOptions);

    elements.forEach(el => observer.observe(el));
});
