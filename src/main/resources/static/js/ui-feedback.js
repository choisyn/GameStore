(function () {
    if (window.__appFeedbackReady) {
        return;
    }
    window.__appFeedbackReady = true;

    const style = document.createElement('style');
    style.textContent = `
        .app-toast-container {
            position: fixed;
            top: 20px;
            right: 20px;
            display: flex;
            flex-direction: column;
            gap: 12px;
            z-index: 9999;
            width: min(360px, calc(100vw - 24px));
        }
        .app-toast {
            position: relative;
            overflow: hidden;
            border-radius: 16px;
            padding: 14px 16px 14px 18px;
            color: #e9f3fb;
            background: linear-gradient(145deg, rgba(24, 37, 54, 0.96), rgba(33, 56, 78, 0.96));
            box-shadow: 0 18px 40px rgba(0, 0, 0, 0.35);
            border: 1px solid rgba(255, 255, 255, 0.08);
            backdrop-filter: blur(12px);
            animation: app-toast-in 0.22s ease;
        }
        .app-toast::before {
            content: "";
            position: absolute;
            inset: 0 auto 0 0;
            width: 5px;
            background: var(--accent-color, #66c0f4);
        }
        .app-toast-title {
            font-weight: 700;
            margin-bottom: 4px;
        }
        .app-toast-message {
            font-size: 14px;
            line-height: 1.6;
            color: rgba(233, 243, 251, 0.88);
            white-space: pre-wrap;
            word-break: break-word;
        }
        .app-toast-success { --accent-color: #6bdc8b; }
        .app-toast-warning { --accent-color: #f4b860; }
        .app-toast-error { --accent-color: #ff7f7f; }
        .app-toast-info { --accent-color: #66c0f4; }
        .app-dialog-backdrop {
            position: fixed;
            inset: 0;
            background: rgba(8, 14, 24, 0.62);
            backdrop-filter: blur(6px);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 10000;
            padding: 20px;
        }
        .app-dialog-backdrop.show {
            display: flex;
        }
        .app-dialog {
            width: min(var(--app-dialog-width, 460px), 100%);
            max-height: min(var(--app-dialog-max-height, 82vh), calc(100vh - 40px));
            background: linear-gradient(155deg, rgba(27, 40, 56, 0.98), rgba(42, 71, 94, 0.98));
            color: #dce8f2;
            border-radius: 22px;
            border: 1px solid rgba(255, 255, 255, 0.08);
            box-shadow: 0 28px 60px rgba(0, 0, 0, 0.45);
            overflow: hidden;
            display: flex;
            flex-direction: column;
            transform: translateY(10px) scale(0.98);
            opacity: 0;
            transition: transform 0.18s ease, opacity 0.18s ease;
        }
        .app-dialog-backdrop.show .app-dialog {
            transform: translateY(0) scale(1);
            opacity: 1;
        }
        .app-dialog-head {
            padding: 18px 20px 8px;
            display: flex;
            align-items: center;
            gap: 12px;
            flex-shrink: 0;
        }
        .app-dialog-icon {
            width: 42px;
            height: 42px;
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            background: rgba(255, 255, 255, 0.08);
        }
        .app-dialog-title {
            margin: 0;
            font-size: 18px;
            font-weight: 700;
        }
        .app-dialog-body {
            padding: 6px 20px 20px;
            font-size: 15px;
            line-height: 1.8;
            color: rgba(220, 232, 242, 0.88);
            white-space: pre-wrap;
            word-break: break-word;
            overflow-y: auto;
            flex: 1 1 auto;
            min-height: 0;
            overscroll-behavior: contain;
            scrollbar-width: thin;
            scrollbar-color: rgba(102, 192, 244, 0.55) rgba(255, 255, 255, 0.08);
        }
        .app-dialog-body::-webkit-scrollbar {
            width: 10px;
        }
        .app-dialog-body::-webkit-scrollbar-track {
            background: rgba(255, 255, 255, 0.06);
            border-radius: 999px;
        }
        .app-dialog-body::-webkit-scrollbar-thumb {
            background: rgba(102, 192, 244, 0.55);
            border-radius: 999px;
            border: 2px solid transparent;
            background-clip: padding-box;
        }
        .app-dialog-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            padding: 0 20px 20px;
            flex-shrink: 0;
        }
        .app-dialog-btn {
            border: none;
            border-radius: 12px;
            padding: 10px 18px;
            font-weight: 700;
            cursor: pointer;
            transition: transform 0.15s ease, opacity 0.15s ease;
        }
        .app-dialog-btn:hover {
            transform: translateY(-1px);
        }
        .app-dialog-btn-secondary {
            background: rgba(255, 255, 255, 0.08);
            color: #dce8f2;
        }
        .app-dialog-btn-primary {
            background: linear-gradient(135deg, #66c0f4, #9ce4ff);
            color: #183247;
        }
        .app-dialog-info .app-dialog-icon { color: #66c0f4; }
        .app-dialog-success .app-dialog-icon { color: #6bdc8b; }
        .app-dialog-warning .app-dialog-icon { color: #f4b860; }
        .app-dialog-error .app-dialog-icon { color: #ff7f7f; }
        @keyframes app-toast-in {
            from { opacity: 0; transform: translateY(-8px) scale(0.98); }
            to { opacity: 1; transform: translateY(0) scale(1); }
        }
    `;
    document.head.appendChild(style);

    const toastContainer = document.createElement('div');
    toastContainer.className = 'app-toast-container';
    document.body.appendChild(toastContainer);

    const dialogBackdrop = document.createElement('div');
    dialogBackdrop.className = 'app-dialog-backdrop';
    dialogBackdrop.innerHTML = `
        <div class="app-dialog app-dialog-info" role="dialog" aria-modal="true">
            <div class="app-dialog-head">
                <div class="app-dialog-icon">i</div>
                <h3 class="app-dialog-title">提示</h3>
            </div>
            <div class="app-dialog-body"></div>
            <div class="app-dialog-actions">
                <button type="button" class="app-dialog-btn app-dialog-btn-secondary" data-role="cancel">取消</button>
                <button type="button" class="app-dialog-btn app-dialog-btn-primary" data-role="confirm">确定</button>
            </div>
        </div>
    `;
    document.body.appendChild(dialogBackdrop);

    const dialog = dialogBackdrop.querySelector('.app-dialog');
    const dialogIcon = dialogBackdrop.querySelector('.app-dialog-icon');
    const dialogTitle = dialogBackdrop.querySelector('.app-dialog-title');
    const dialogBody = dialogBackdrop.querySelector('.app-dialog-body');
    const confirmButton = dialogBackdrop.querySelector('[data-role="confirm"]');
    const cancelButton = dialogBackdrop.querySelector('[data-role="cancel"]');
    let closeDialog = null;

    function normalizeMessage(message) {
        if (message === undefined || message === null) {
            return '';
        }
        return typeof message === 'string' ? message : String(message);
    }

    function iconText(type) {
        switch (type) {
            case 'success':
                return '✓';
            case 'warning':
                return '!';
            case 'error':
                return '×';
            default:
                return 'i';
        }
    }

    function showToast(message, type = 'info', options = {}) {
        const toast = document.createElement('div');
        toast.className = `app-toast app-toast-${type}`;
        toast.innerHTML = `
            <div class="app-toast-title">${options.title || '提示'}</div>
            <div class="app-toast-message">${normalizeMessage(message)}</div>
        `;
        toastContainer.appendChild(toast);
        const duration = Number(options.duration || 2600);
        window.setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateY(-6px)';
            window.setTimeout(() => toast.remove(), 180);
        }, duration);
    }

    function showDialog(message, options = {}) {
        return new Promise((resolve) => {
            const type = options.type || 'info';
            dialog.className = `app-dialog app-dialog-${type}`;
            dialog.style.setProperty('--app-dialog-width', options.width || '460px');
            dialog.style.setProperty('--app-dialog-max-height', options.maxHeight || '82vh');
            dialogIcon.textContent = iconText(type);
            dialogTitle.textContent = options.title || '提示';
            dialogBody.textContent = normalizeMessage(message);
            dialogBody.scrollTop = 0;
            confirmButton.textContent = options.confirmText || '确定';
            cancelButton.textContent = options.cancelText || '取消';
            cancelButton.style.display = options.showCancel ? 'inline-flex' : 'none';
            dialogBackdrop.classList.add('show');

            const cleanup = (result) => {
                dialogBackdrop.classList.remove('show');
                confirmButton.removeEventListener('click', onConfirm);
                cancelButton.removeEventListener('click', onCancel);
                dialogBackdrop.removeEventListener('click', onBackdropClick);
                document.removeEventListener('keydown', onKeyDown);
                closeDialog = null;
                dialog.style.removeProperty('--app-dialog-width');
                dialog.style.removeProperty('--app-dialog-max-height');
                resolve(result);
            };

            const onConfirm = () => cleanup(true);
            const onCancel = () => cleanup(false);
            const onBackdropClick = (event) => {
                if (event.target === dialogBackdrop && options.showCancel) {
                    cleanup(false);
                }
            };
            const onKeyDown = (event) => {
                if (event.key === 'Escape' && options.showCancel) {
                    cleanup(false);
                }
                if (event.key === 'Enter') {
                    cleanup(true);
                }
            };

            closeDialog = cleanup;
            confirmButton.addEventListener('click', onConfirm);
            cancelButton.addEventListener('click', onCancel);
            dialogBackdrop.addEventListener('click', onBackdropClick);
            document.addEventListener('keydown', onKeyDown);
        });
    }

    window.appToast = showToast;
    window.appAlert = function (message, options = {}) {
        return showDialog(message, {
            title: options.title || '提示',
            type: options.type || 'info',
            confirmText: options.confirmText || '知道了',
            showCancel: false
        });
    };
    window.appConfirm = function (message, options = {}) {
        return showDialog(message, {
            title: options.title || '请确认',
            type: options.type || 'warning',
            confirmText: options.confirmText || '确定',
            cancelText: options.cancelText || '取消',
            showCancel: true
        });
    };
    window.alert = function (message) {
        return window.appAlert(message);
    };
    window.addEventListener('beforeunload', function () {
        if (typeof closeDialog === 'function') {
            closeDialog(false);
        }
    });
})();
