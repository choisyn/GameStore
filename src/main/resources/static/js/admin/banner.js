// 轮播图管理页面脚本
let banners = [];
let currentBanner = null;

// 页面加载时初始化
document.addEventListener('DOMContentLoaded', function() {
    loadBanners();
    initEventListeners();
});

// 加载轮播图列表
async function loadBanners() {
    try {
        const response = await fetch('/api/banners');
        const result = await response.json();
        
        if (result.code === 200) {
            banners = result.data;
            renderBannersTable();
        } else {
            showMessage('加载失败: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('加载轮播图失败:', error);
        showMessage('加载失败,请稍后重试', 'error');
    }
}

// 渲染轮播图表格
function renderBannersTable() {
    const tbody = document.getElementById('bannersTableBody');
    
    if (banners.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted">暂无轮播图数据</td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = banners.map(banner => `
        <tr>
            <td>${banner.id}</td>
            <td>
                <img src="${banner.imageUrl}" alt="${banner.title}" 
                     style="height: 50px; max-width: 100px; object-fit: cover; border-radius: 4px;"
                     onerror="this.src='https://via.placeholder.com/100x50?text=图片加载失败'">
            </td>
            <td>
                <strong>${banner.title}</strong><br>
                <small class="text-muted">${banner.description || '无描述'}</small>
            </td>
            <td>
                ${banner.linkUrl ? `<a href="${banner.linkUrl}" target="_blank" class="text-primary">
                    <i class="bi bi-link-45deg"></i> 查看
                </a>` : '<span class="text-muted">无链接</span>'}
            </td>
            <td>
                <span class="badge bg-${banner.type === 'HOME' ? 'primary' : banner.type === 'COMMUNITY' ? 'info' : 'secondary'}">
                    ${banner.type}
                </span>
            </td>
            <td>${banner.sortOrder}</td>
            <td>
                <span class="badge bg-${banner.isActive ? 'success' : 'secondary'}">
                    ${banner.isActive ? '启用' : '禁用'}
                </span>
            </td>
            <td>
                <div class="btn-group btn-group-sm" role="group">
                    <button class="btn btn-outline-primary" onclick="editBanner(${banner.id})" title="编辑">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-outline-${banner.isActive ? 'warning' : 'success'}" 
                            onclick="toggleBanner(${banner.id})" 
                            title="${banner.isActive ? '禁用' : '启用'}">
                        <i class="bi bi-${banner.isActive ? 'eye-slash' : 'eye'}"></i>
                    </button>
                    <button class="btn btn-outline-danger" onclick="deleteBanner(${banner.id})" title="删除">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// 初始化事件监听
function initEventListeners() {
    // 新建轮播图按钮
    document.getElementById('createBannerBtn').addEventListener('click', () => {
        currentBanner = null;
        document.getElementById('bannerModalLabel').textContent = '新建轮播图';
        document.getElementById('bannerForm').reset();
        new bootstrap.Modal(document.getElementById('bannerModal')).show();
    });
    
    // 保存按钮
    document.getElementById('saveBannerBtn').addEventListener('click', saveBanner);
}

// 编辑轮播图
function editBanner(id) {
    currentBanner = banners.find(b => b.id === id);
    if (!currentBanner) return;
    
    document.getElementById('bannerModalLabel').textContent = '编辑轮播图';
    document.getElementById('bannerId').value = currentBanner.id;
    document.getElementById('bannerTitle').value = currentBanner.title;
    document.getElementById('bannerImageUrl').value = currentBanner.imageUrl;
    document.getElementById('bannerLinkUrl').value = currentBanner.linkUrl || '';
    document.getElementById('bannerDescription').value = currentBanner.description || '';
    document.getElementById('bannerType').value = currentBanner.type;
    document.getElementById('bannerSortOrder').value = currentBanner.sortOrder;
    document.getElementById('bannerIsActive').checked = currentBanner.isActive;
    
    new bootstrap.Modal(document.getElementById('bannerModal')).show();
}

// 保存轮播图
async function saveBanner() {
    const form = document.getElementById('bannerForm');
    const formData = {
        title: document.getElementById('bannerTitle').value.trim(),
        imageUrl: document.getElementById('bannerImageUrl').value.trim(),
        linkUrl: document.getElementById('bannerLinkUrl').value.trim(),
        description: document.getElementById('bannerDescription').value.trim(),
        type: document.getElementById('bannerType').value,
        sortOrder: parseInt(document.getElementById('bannerSortOrder').value) || 0,
        isActive: document.getElementById('bannerIsActive').checked
    };
    
    // 验证
    if (!formData.title) {
        showMessage('请输入标题', 'warning');
        return;
    }
    if (!formData.imageUrl) {
        showMessage('请输入图片URL', 'warning');
        return;
    }
    
    try {
        const bannerId = document.getElementById('bannerId').value;
        const url = bannerId ? `/api/banners/${bannerId}` : '/api/banners';
        const method = bannerId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            showMessage(bannerId ? '更新成功' : '创建成功', 'success');
            bootstrap.Modal.getInstance(document.getElementById('bannerModal')).hide();
            loadBanners();
        } else {
            showMessage('保存失败: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('保存轮播图失败:', error);
        showMessage('保存失败,请稍后重试', 'error');
    }
}

// 切换轮播图状态
async function toggleBanner(id) {
    try {
        const response = await fetch(`/api/banners/${id}/toggle`, {
            method: 'PATCH'
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            showMessage('状态已更新', 'success');
            loadBanners();
        } else {
            showMessage('操作失败: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('切换状态失败:', error);
        showMessage('操作失败,请稍后重试', 'error');
    }
}

// 删除轮播图
async function deleteBanner(id) {
    if (!(await appConfirm('确定要删除这个轮播图吗？', { title: '删除轮播图', type: 'warning' }))) {
        return;
    }
    
    try {
        const response = await fetch(`/api/banners/${id}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            showMessage('删除成功', 'success');
            loadBanners();
        } else {
            showMessage('删除失败: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('删除轮播图失败:', error);
        showMessage('删除失败,请稍后重试', 'error');
    }
}

// 显示消息提示
function showMessage(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed`;
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(alertDiv);
    
    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}

