/**
 * 个人博客系统 - 通用JavaScript功能
 */

// 全局配置
const BlogApp = {
    config: {
        alertAutoHideDelay: 5000,
        animationDuration: 300,
        debounceDelay: 300
    },
    
    // 初始化
    init() {
        this.setupEventListeners();
        this.setupAlerts();
        this.setupForms();
        this.setupAnimations();
        this.setupUtils();
    },
    
    // 设置事件监听器
    setupEventListeners() {
        document.addEventListener('DOMContentLoaded', () => {
            // 平滑滚动
            this.setupSmoothScroll();
            
            // 返回顶部按钮
            this.setupBackToTop();
            
            // 搜索功能
            this.setupSearch();
            
            // 图片懒加载
            this.setupLazyLoading();
        });
    },
    
    // 设置提示消息
    setupAlerts() {
        // 自动隐藏提示消息
        const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
        alerts.forEach(alert => {
            setTimeout(() => {
                if (alert && alert.classList.contains('show')) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                }
            }, this.config.alertAutoHideDelay);
        });
    },
    
    // 设置表单功能
    setupForms() {
        const forms = document.querySelectorAll('form');
        forms.forEach(form => {
            // 表单提交加载状态
            form.addEventListener('submit', (e) => {
                const submitBtn = form.querySelector('button[type="submit"]');
                if (submitBtn && !submitBtn.disabled) {
                    this.setButtonLoading(submitBtn, true);
                    
                    // 防止重复提交，5秒后恢复按钮
                    setTimeout(() => {
                        this.setButtonLoading(submitBtn, false);
                    }, 5000);
                }
            });
            
            // 表单验证
            this.setupFormValidation(form);
        });
    },
    
    // 设置表单验证
    setupFormValidation(form) {
        const inputs = form.querySelectorAll('input, textarea, select');
        inputs.forEach(input => {
            input.addEventListener('blur', () => {
                this.validateField(input);
            });
            
            input.addEventListener('input', this.debounce(() => {
                this.validateField(input);
            }, this.config.debounceDelay));
        });
    },
    
    // 验证字段
    validateField(field) {
        const value = field.value.trim();
        const type = field.type;
        const required = field.hasAttribute('required');
        
        // 清除之前的验证状态
        field.classList.remove('is-valid', 'is-invalid');
        
        // 必填验证
        if (required && !value) {
            this.setFieldInvalid(field, '此字段为必填项');
            return false;
        }
        
        // 邮箱验证
        if (type === 'email' && value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(value)) {
                this.setFieldInvalid(field, '请输入有效的邮箱地址');
                return false;
            }
        }
        
        // 密码验证
        if (field.name === 'password' && value) {
            if (value.length < 6) {
                this.setFieldInvalid(field, '密码长度不能少于6个字符');
                return false;
            }
        }
        
        // 确认密码验证
        if (field.name === 'confirmPassword' && value) {
            const passwordField = document.querySelector('input[name="password"]');
            if (passwordField && value !== passwordField.value) {
                this.setFieldInvalid(field, '密码不匹配');
                return false;
            }
        }
        
        // 验证通过
        this.setFieldValid(field);
        return true;
    },
    
    // 设置字段为无效状态
    setFieldInvalid(field, message) {
        field.classList.add('is-invalid');
        let feedback = field.parentNode.querySelector('.invalid-feedback');
        if (!feedback) {
            feedback = document.createElement('div');
            feedback.className = 'invalid-feedback';
            field.parentNode.appendChild(feedback);
        }
        feedback.textContent = message;
    },
    
    // 设置字段为有效状态
    setFieldValid(field) {
        field.classList.add('is-valid');
        const feedback = field.parentNode.querySelector('.invalid-feedback');
        if (feedback) {
            feedback.remove();
        }
    },
    
    // 设置按钮加载状态
    setButtonLoading(button, loading) {
        if (loading) {
            button.dataset.originalText = button.innerHTML;
            button.innerHTML = '<span class="loading me-2"></span>处理中...';
            button.disabled = true;
        } else {
            button.innerHTML = button.dataset.originalText || button.innerHTML;
            button.disabled = false;
        }
    },
    
    // 设置动画
    setupAnimations() {
        // 为新添加的元素添加动画
        const observer = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
                mutation.addedNodes.forEach((node) => {
                    if (node.nodeType === 1 && node.classList) {
                        node.classList.add('fade-in');
                    }
                });
            });
        });
        
        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    },
    
    // 设置工具功能
    setupUtils() {
        // 复制到剪贴板功能
        window.copyToClipboard = (text) => {
            navigator.clipboard.writeText(text).then(() => {
                this.showMessage('已复制到剪贴板', 'success');
            }).catch(() => {
                this.showMessage('复制失败', 'danger');
            });
        };
        
        // 分享功能
        window.shareContent = (title, url) => {
            if (navigator.share) {
                navigator.share({
                    title: title,
                    url: url
                });
            } else {
                this.copyToClipboard(url);
            }
        };
    },
    
    // 平滑滚动
    setupSmoothScroll() {
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', (e) => {
                e.preventDefault();
                const target = document.querySelector(anchor.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });
    },
    
    // 返回顶部按钮
    setupBackToTop() {
        const backToTopBtn = document.createElement('button');
        backToTopBtn.innerHTML = '<i class="fas fa-arrow-up"></i>';
        backToTopBtn.className = 'btn btn-primary position-fixed';
        backToTopBtn.style.cssText = `
            bottom: 20px;
            right: 20px;
            z-index: 1000;
            border-radius: 50%;
            width: 50px;
            height: 50px;
            display: none;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        `;
        
        document.body.appendChild(backToTopBtn);
        
        // 滚动显示/隐藏按钮
        window.addEventListener('scroll', () => {
            if (window.pageYOffset > 300) {
                backToTopBtn.style.display = 'block';
            } else {
                backToTopBtn.style.display = 'none';
            }
        });
        
        // 点击返回顶部
        backToTopBtn.addEventListener('click', () => {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    },
    
    // 搜索功能
    setupSearch() {
        const searchInputs = document.querySelectorAll('input[type="search"], .search-input');
        searchInputs.forEach(input => {
            input.addEventListener('input', this.debounce((e) => {
                const query = e.target.value.trim();
                if (query.length > 2) {
                    this.performSearch(query);
                }
            }, this.config.debounceDelay));
        });
    },
    
    // 执行搜索
    performSearch(query) {
        // 这里可以实现实时搜索建议
        console.log('搜索:', query);
    },
    
    // 图片懒加载
    setupLazyLoading() {
        const images = document.querySelectorAll('img[data-src]');
        const imageObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.classList.remove('lazy');
                    imageObserver.unobserve(img);
                }
            });
        });
        
        images.forEach(img => imageObserver.observe(img));
    },
    
    // 显示消息
    showMessage(message, type = 'info', duration = null) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
        alertDiv.style.cssText = `
            top: 20px;
            right: 20px;
            z-index: 1050;
            min-width: 300px;
        `;
        alertDiv.innerHTML = `
            <i class="fas fa-${this.getIconByType(type)} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(alertDiv);
        
        // 自动隐藏
        setTimeout(() => {
            if (alertDiv && alertDiv.classList.contains('show')) {
                const bsAlert = new bootstrap.Alert(alertDiv);
                bsAlert.close();
            }
        }, duration || this.config.alertAutoHideDelay);
    },
    
    // 根据类型获取图标
    getIconByType(type) {
        const icons = {
            success: 'check-circle',
            danger: 'exclamation-triangle',
            warning: 'exclamation-triangle',
            info: 'info-circle'
        };
        return icons[type] || 'info-circle';
    },
    
    // 确认对话框
    confirm(message, callback) {
        if (confirm(message)) {
            callback();
        }
    },
    
    // 防抖函数
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
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
    }
};

// 初始化应用
BlogApp.init();

// 导出到全局
window.BlogApp = BlogApp;