/**
 * 表单验证增强功能
 */

const FormValidator = {
    // 验证规则
    rules: {
        required: {
            test: (value) => value && value.trim().length > 0,
            message: '此字段为必填项'
        },
        email: {
            test: (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
            message: '请输入有效的邮箱地址'
        },
        minLength: {
            test: (value, param) => value.length >= param,
            message: (param) => `最少需要${param}个字符`
        },
        maxLength: {
            test: (value, param) => value.length <= param,
            message: (param) => `最多允许${param}个字符`
        },
        password: {
            test: (value) => value.length >= 6 && /^(?=.*[a-zA-Z])(?=.*\d)/.test(value),
            message: '密码至少6位，包含字母和数字'
        },
        confirmPassword: {
            test: (value, param, form) => {
                const passwordField = form.querySelector('input[name="password"]');
                return passwordField && value === passwordField.value;
            },
            message: '密码不匹配'
        },
        username: {
            test: (value) => /^[a-zA-Z0-9_]{3,20}$/.test(value),
            message: '用户名只能包含字母、数字和下划线，3-20个字符'
        }
    },

    // 初始化表单验证
    init() {
        document.addEventListener('DOMContentLoaded', () => {
            this.setupForms();
        });
    },

    // 设置表单验证
    setupForms() {
        const forms = document.querySelectorAll('form[data-validate="true"], .needs-validation');
        forms.forEach(form => {
            this.initForm(form);
        });
    },

    // 初始化单个表单
    initForm(form) {
        const fields = form.querySelectorAll('input, textarea, select');
        
        fields.forEach(field => {
            // 实时验证
            field.addEventListener('input', () => {
                this.validateField(field, form);
            });
            
            // 失焦验证
            field.addEventListener('blur', () => {
                this.validateField(field, form);
            });
        });

        // 表单提交验证
        form.addEventListener('submit', (e) => {
            if (!this.validateForm(form)) {
                e.preventDefault();
                e.stopPropagation();
                
                // 滚动到第一个错误字段
                const firstError = form.querySelector('.is-invalid');
                if (firstError) {
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    firstError.focus();
                }
            }
        });
    },

    // 验证单个字段
    validateField(field, form) {
        const value = field.value.trim();
        const rules = this.getFieldRules(field);
        
        // 清除之前的验证状态
        this.clearFieldValidation(field);
        
        // 如果字段为空且不是必填，跳过验证
        if (!value && !rules.required) {
            return true;
        }
        
        // 执行验证规则
        for (const [ruleName, ruleConfig] of Object.entries(rules)) {
            const rule = this.rules[ruleName];
            if (!rule) continue;
            
            let isValid;
            if (typeof ruleConfig === 'boolean' && ruleConfig) {
                isValid = rule.test(value, null, form);
            } else if (typeof ruleConfig === 'number' || typeof ruleConfig === 'string') {
                isValid = rule.test(value, ruleConfig, form);
            } else {
                continue;
            }
            
            if (!isValid) {
                const message = typeof rule.message === 'function' 
                    ? rule.message(ruleConfig) 
                    : rule.message;
                this.setFieldInvalid(field, message);
                return false;
            }
        }
        
        this.setFieldValid(field);
        return true;
    },

    // 验证整个表单
    validateForm(form) {
        const fields = form.querySelectorAll('input, textarea, select');
        let isValid = true;
        
        fields.forEach(field => {
            if (!this.validateField(field, form)) {
                isValid = false;
            }
        });
        
        return isValid;
    },

    // 获取字段验证规则
    getFieldRules(field) {
        const rules = {};
        
        // 从HTML属性获取规则
        if (field.hasAttribute('required')) {
            rules.required = true;
        }
        
        if (field.type === 'email') {
            rules.email = true;
        }
        
        if (field.hasAttribute('minlength')) {
            rules.minLength = parseInt(field.getAttribute('minlength'));
        }
        
        if (field.hasAttribute('maxlength')) {
            rules.maxLength = parseInt(field.getAttribute('maxlength'));
        }
        
        // 从data属性获取规则
        if (field.dataset.validate) {
            const customRules = field.dataset.validate.split(',');
            customRules.forEach(rule => {
                const [ruleName, param] = rule.split(':');
                rules[ruleName.trim()] = param ? param.trim() : true;
            });
        }
        
        // 特殊字段规则
        if (field.name === 'password') {
            rules.password = true;
        }
        
        if (field.name === 'confirmPassword' || field.name === 'confirmNewPassword') {
            rules.confirmPassword = true;
        }
        
        if (field.name === 'username') {
            rules.username = true;
        }
        
        return rules;
    },

    // 设置字段为无效状态
    setFieldInvalid(field, message) {
        field.classList.remove('is-valid');
        field.classList.add('is-invalid');
        
        // 移除旧的错误信息
        this.removeFieldFeedback(field);
        
        // 添加错误信息
        const feedback = document.createElement('div');
        feedback.className = 'invalid-feedback';
        feedback.textContent = message;
        field.parentNode.appendChild(feedback);
        
        // 添加错误图标
        this.addFieldIcon(field, 'error');
    },

    // 设置字段为有效状态
    setFieldValid(field) {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
        
        // 移除错误信息
        this.removeFieldFeedback(field);
        
        // 添加成功图标
        this.addFieldIcon(field, 'success');
    },

    // 清除字段验证状态
    clearFieldValidation(field) {
        field.classList.remove('is-valid', 'is-invalid');
        this.removeFieldFeedback(field);
        this.removeFieldIcon(field);
    },

    // 移除字段反馈信息
    removeFieldFeedback(field) {
        const feedback = field.parentNode.querySelector('.invalid-feedback, .valid-feedback');
        if (feedback) {
            feedback.remove();
        }
    },

    // 添加字段图标
    addFieldIcon(field, type) {
        this.removeFieldIcon(field);
        
        const icon = document.createElement('i');
        icon.className = `field-icon fas ${type === 'success' ? 'fa-check-circle text-success' : 'fa-exclamation-circle text-danger'}`;
        icon.style.cssText = `
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            pointer-events: none;
            z-index: 5;
        `;
        
        // 确保父元素有相对定位
        if (getComputedStyle(field.parentNode).position === 'static') {
            field.parentNode.style.position = 'relative';
        }
        
        field.parentNode.appendChild(icon);
        
        // 调整输入框padding
        field.style.paddingRight = '35px';
    },

    // 移除字段图标
    removeFieldIcon(field) {
        const icon = field.parentNode.querySelector('.field-icon');
        if (icon) {
            icon.remove();
            field.style.paddingRight = '';
        }
    },

    // 实时字符计数
    setupCharacterCount(field, maxLength) {
        const counter = document.createElement('small');
        counter.className = 'form-text text-muted character-count';
        field.parentNode.appendChild(counter);
        
        const updateCount = () => {
            const current = field.value.length;
            counter.textContent = `${current}/${maxLength}`;
            
            if (current > maxLength * 0.9) {
                counter.classList.add('text-warning');
            } else {
                counter.classList.remove('text-warning');
            }
            
            if (current > maxLength) {
                counter.classList.add('text-danger');
                counter.classList.remove('text-warning');
            } else {
                counter.classList.remove('text-danger');
            }
        };
        
        field.addEventListener('input', updateCount);
        updateCount();
    },

    // 密码强度指示器
    setupPasswordStrength(field) {
        const strengthBar = document.createElement('div');
        strengthBar.className = 'password-strength mt-2';
        strengthBar.innerHTML = `
            <div class="progress" style="height: 4px;">
                <div class="progress-bar" role="progressbar" style="width: 0%"></div>
            </div>
            <small class="form-text text-muted">密码强度：<span class="strength-text">弱</span></small>
        `;
        
        field.parentNode.appendChild(strengthBar);
        
        const progressBar = strengthBar.querySelector('.progress-bar');
        const strengthText = strengthBar.querySelector('.strength-text');
        
        field.addEventListener('input', () => {
            const password = field.value;
            const strength = this.calculatePasswordStrength(password);
            
            progressBar.style.width = `${strength.score * 25}%`;
            progressBar.className = `progress-bar bg-${strength.color}`;
            strengthText.textContent = strength.text;
            strengthText.className = `strength-text text-${strength.color}`;
        });
    },

    // 计算密码强度
    calculatePasswordStrength(password) {
        let score = 0;
        
        if (password.length >= 6) score++;
        if (password.length >= 10) score++;
        if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score++;
        if (/\d/.test(password)) score++;
        if (/[^a-zA-Z0-9]/.test(password)) score++;
        
        const levels = [
            { score: 0, text: '太弱', color: 'danger' },
            { score: 1, text: '弱', color: 'danger' },
            { score: 2, text: '一般', color: 'warning' },
            { score: 3, text: '良好', color: 'info' },
            { score: 4, text: '强', color: 'success' },
            { score: 5, text: '很强', color: 'success' }
        ];
        
        return levels[score] || levels[0];
    },

    // 添加自定义验证规则
    addRule(name, rule) {
        this.rules[name] = rule;
    }
};

// 初始化表单验证
FormValidator.init();

// 导出到全局
window.FormValidator = FormValidator;