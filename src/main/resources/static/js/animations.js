/**
 * 页面动画和交互效果
 */

const AnimationManager = {
    // 配置
    config: {
        duration: 300,
        easing: 'ease-out',
        staggerDelay: 100
    },

    // 初始化
    init() {
        document.addEventListener('DOMContentLoaded', () => {
            this.setupPageTransitions();
            this.setupScrollAnimations();
            this.setupHoverEffects();
            this.setupLoadingStates();
            this.setupCounters();
            this.setupParallax();
        });
    },

    // 页面过渡动画
    setupPageTransitions() {
        // 页面加载动画
        document.body.style.opacity = '0';
        document.body.style.transform = 'translateY(20px)';
        
        window.addEventListener('load', () => {
            document.body.style.transition = `opacity ${this.config.duration}ms ${this.config.easing}, transform ${this.config.duration}ms ${this.config.easing}`;
            document.body.style.opacity = '1';
            document.body.style.transform = 'translateY(0)';
            
            // 逐个显示卡片
            this.staggerAnimation('.card, .blog-card, .comment-item');
        });

        // 链接点击动画
        document.addEventListener('click', (e) => {
            const link = e.target.closest('a[href]:not([href^="#"]):not([target="_blank"])');
            if (link && !e.ctrlKey && !e.metaKey) {
                e.preventDefault();
                this.pageTransition(link.href);
            }
        });
    },

    // 页面切换动画
    pageTransition(url) {
        document.body.style.transition = `opacity ${this.config.duration}ms ${this.config.easing}`;
        document.body.style.opacity = '0';
        
        setTimeout(() => {
            window.location.href = url;
        }, this.config.duration);
    },

    // 错开动画
    staggerAnimation(selector, delay = this.config.staggerDelay) {
        const elements = document.querySelectorAll(selector);
        elements.forEach((element, index) => {
            element.style.opacity = '0';
            element.style.transform = 'translateY(30px)';
            element.style.transition = `opacity ${this.config.duration}ms ${this.config.easing}, transform ${this.config.duration}ms ${this.config.easing}`;
            
            setTimeout(() => {
                element.style.opacity = '1';
                element.style.transform = 'translateY(0)';
            }, index * delay);
        });
    },

    // 滚动动画
    setupScrollAnimations() {
        const observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        };

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('animate-in');
                    observer.unobserve(entry.target);
                }
            });
        }, observerOptions);

        // 观察需要动画的元素
        const animateElements = document.querySelectorAll('.card, .alert, .btn-group, .form-group');
        animateElements.forEach(el => {
            el.classList.add('animate-on-scroll');
            observer.observe(el);
        });

        // 添加CSS类
        this.addScrollAnimationCSS();
    },

    // 添加滚动动画CSS
    addScrollAnimationCSS() {
        const style = document.createElement('style');
        style.textContent = `
            .animate-on-scroll {
                opacity: 0;
                transform: translateY(30px);
                transition: opacity 0.6s ease-out, transform 0.6s ease-out;
            }
            
            .animate-in {
                opacity: 1;
                transform: translateY(0);
            }
            
            .fade-in-up {
                animation: fadeInUp 0.6s ease-out forwards;
            }
            
            @keyframes fadeInUp {
                from {
                    opacity: 0;
                    transform: translateY(30px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }
            
            .bounce-in {
                animation: bounceIn 0.6s ease-out forwards;
            }
            
            @keyframes bounceIn {
                0% {
                    opacity: 0;
                    transform: scale(0.3);
                }
                50% {
                    opacity: 1;
                    transform: scale(1.05);
                }
                70% {
                    transform: scale(0.9);
                }
                100% {
                    opacity: 1;
                    transform: scale(1);
                }
            }
            
            .slide-in-left {
                animation: slideInLeft 0.6s ease-out forwards;
            }
            
            @keyframes slideInLeft {
                from {
                    opacity: 0;
                    transform: translateX(-100px);
                }
                to {
                    opacity: 1;
                    transform: translateX(0);
                }
            }
        `;
        document.head.appendChild(style);
    },

    // 悬停效果
    setupHoverEffects() {
        // 卡片悬停效果
        document.addEventListener('mouseenter', (e) => {
            const card = e.target.closest('.card, .blog-card');
            if (card) {
                card.style.transition = 'transform 0.3s ease, box-shadow 0.3s ease';
                card.style.transform = 'translateY(-5px)';
                card.style.boxShadow = '0 8px 25px rgba(0,0,0,0.15)';
            }
        }, true);

        document.addEventListener('mouseleave', (e) => {
            const card = e.target.closest('.card, .blog-card');
            if (card) {
                card.style.transform = 'translateY(0)';
                card.style.boxShadow = '';
            }
        }, true);

        // 按钮波纹效果
        document.addEventListener('click', (e) => {
            const button = e.target.closest('.btn');
            if (button) {
                this.createRipple(button, e);
            }
        });
    },

    // 创建波纹效果
    createRipple(element, event) {
        const ripple = document.createElement('span');
        const rect = element.getBoundingClientRect();
        const size = Math.max(rect.width, rect.height);
        const x = event.clientX - rect.left - size / 2;
        const y = event.clientY - rect.top - size / 2;

        ripple.style.cssText = `
            position: absolute;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.6);
            transform: scale(0);
            animation: ripple 0.6s linear;
            left: ${x}px;
            top: ${y}px;
            width: ${size}px;
            height: ${size}px;
            pointer-events: none;
        `;

        // 确保按钮有相对定位
        if (getComputedStyle(element).position === 'static') {
            element.style.position = 'relative';
        }
        element.style.overflow = 'hidden';

        element.appendChild(ripple);

        // 添加波纹动画CSS
        if (!document.querySelector('#ripple-style')) {
            const style = document.createElement('style');
            style.id = 'ripple-style';
            style.textContent = `
                @keyframes ripple {
                    to {
                        transform: scale(4);
                        opacity: 0;
                    }
                }
            `;
            document.head.appendChild(style);
        }

        setTimeout(() => {
            ripple.remove();
        }, 600);
    },

    // 加载状态
    setupLoadingStates() {
        // 表单提交加载
        document.addEventListener('submit', (e) => {
            const form = e.target;
            const submitBtn = form.querySelector('button[type="submit"]');
            
            if (submitBtn) {
                this.setLoadingState(submitBtn, true);
                
                // 模拟加载完成（实际应该在服务器响应后处理）
                setTimeout(() => {
                    this.setLoadingState(submitBtn, false);
                }, 3000);
            }
        });

        // AJAX请求加载指示器
        this.setupAjaxLoader();
    },

    // 设置加载状态
    setLoadingState(element, loading) {
        if (loading) {
            element.dataset.originalText = element.innerHTML;
            element.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>处理中...';
            element.disabled = true;
            element.classList.add('loading');
        } else {
            element.innerHTML = element.dataset.originalText || element.innerHTML;
            element.disabled = false;
            element.classList.remove('loading');
        }
    },

    // AJAX加载器
    setupAjaxLoader() {
        let activeRequests = 0;
        const loader = this.createGlobalLoader();

        // 监听fetch请求
        const originalFetch = window.fetch;
        window.fetch = function(...args) {
            activeRequests++;
            loader.show();
            
            return originalFetch.apply(this, args)
                .finally(() => {
                    activeRequests--;
                    if (activeRequests === 0) {
                        loader.hide();
                    }
                });
        };
    },

    // 创建全局加载器
    createGlobalLoader() {
        const loader = document.createElement('div');
        loader.id = 'global-loader';
        loader.innerHTML = `
            <div class="loader-backdrop">
                <div class="loader-content">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">加载中...</span>
                    </div>
                    <div class="mt-2">加载中...</div>
                </div>
            </div>
        `;
        loader.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            z-index: 9999;
            display: none;
        `;

        const style = document.createElement('style');
        style.textContent = `
            .loader-backdrop {
                background: rgba(255, 255, 255, 0.9);
                width: 100%;
                height: 100%;
                display: flex;
                align-items: center;
                justify-content: center;
                backdrop-filter: blur(2px);
            }
            
            .loader-content {
                text-align: center;
                color: #007bff;
                font-weight: 500;
            }
        `;
        document.head.appendChild(style);
        document.body.appendChild(loader);

        return {
            show: () => {
                loader.style.display = 'block';
                setTimeout(() => loader.style.opacity = '1', 10);
            },
            hide: () => {
                loader.style.opacity = '0';
                setTimeout(() => loader.style.display = 'none', 300);
            }
        };
    },

    // 数字计数动画
    setupCounters() {
        const counters = document.querySelectorAll('[data-count]');
        const counterObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.animateCounter(entry.target);
                    counterObserver.unobserve(entry.target);
                }
            });
        });

        counters.forEach(counter => counterObserver.observe(counter));
    },

    // 数字计数动画
    animateCounter(element) {
        const target = parseInt(element.dataset.count);
        const duration = 2000;
        const step = target / (duration / 16);
        let current = 0;

        const timer = setInterval(() => {
            current += step;
            if (current >= target) {
                current = target;
                clearInterval(timer);
            }
            element.textContent = Math.floor(current);
        }, 16);
    },

    // 视差滚动效果
    setupParallax() {
        const parallaxElements = document.querySelectorAll('[data-parallax]');
        
        if (parallaxElements.length === 0) return;

        window.addEventListener('scroll', this.throttle(() => {
            const scrolled = window.pageYOffset;
            
            parallaxElements.forEach(element => {
                const rate = scrolled * (element.dataset.parallax || 0.5);
                element.style.transform = `translateY(${rate}px)`;
            });
        }, 16));
    },

    // 节流函数
    throttle(func, limit) {
        let inThrottle;
        return function() {
            const args = arguments;
            const context = this;
            if (!inThrottle) {
                func.apply(context, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    },

    // 添加动画类
    addClass(element, className, delay = 0) {
        setTimeout(() => {
            element.classList.add(className);
        }, delay);
    },

    // 移除动画类
    removeClass(element, className, delay = 0) {
        setTimeout(() => {
            element.classList.remove(className);
        }, delay);
    }
};

// 初始化动画管理器
AnimationManager.init();

// 导出到全局
window.AnimationManager = AnimationManager;