// 轮播图管理页面脚本
let banners = [];
let currentBanner = null;

// 加载轮播图管理页面
function loadBannersPage() {
    const contentArea = document.getElementById('content-area');
    
    contentArea.innerHTML = `
        <div class="page-header">
            <div class="d-flex justify-content-between align-items-center">
                <h2><i class="bi bi-images"></i> 轮播图管理</h2>
                <button class="btn btn-primary" onclick="showCreateBannerModal()">
                    <i class="bi bi-plus-circle"></i> 新建轮播图
                </button>
            </div>
        </div>

        <div class="data-table">
            <table class="table">
                <thead>
                    <tr>
                        <th width="50">ID</th>
                        <th width="120">图片预览</th>
                        <th>标题/描述</th>
                        <th width="100">链接</th>
                        <th width="80">类型</th>
                        <th width="80">排序</th>
                        <th width="80">状态</th>
                        <th width="150">操作</th>
                    </tr>
                </thead>
                <tbody id="bannersTableBody">
                    <tr>
                        <td colspan="8" class="text-center">加载中...</td>
                    </tr>
                </tbody>
            </table>
        </div>
    `;
    
    loadBanners();
}

// 加载轮播图列表
async function loadBanners() {
    try {
        const response = await fetch('/api/banners');
        const result = await response.json();
        
        if (result.code === 200) {
            banners = result.data;
            renderBannersTable();
        } else {
            showAdminMessage('加载失败: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('加载轮播图失败:', error);
        showAdminMessage('加载失败,请稍后重试', 'error');
    }
}

// 渲染轮播图表格
function renderBannersTable() {
    const tbody = document.getElementById('bannersTableBody');
    
    if (banners.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-muted">暂无轮播图数据</td>
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
                <strong style="color: #66c0f4;">${banner.title}</strong><br>
                <small class="text-muted">${banner.description || '无描述'}</small>
            </td>
            <td>
                ${banner.linkUrl ? `<a href="${banner.linkUrl}" target="_blank" style="color: #66c0f4;">
                    <i class="bi bi-link-45deg"></i> 查看
                </a>` : '<span class="text-muted">无链接</span>'}
            </td>
            <td>
                <span class="badge ${banner.type === 'HOME' ? 'badge-primary' : banner.type === 'COMMUNITY' ? 'badge-info' : 'badge-secondary'}">
                    ${banner.type}
                </span>
            </td>
            <td>${banner.sortOrder}</td>
            <td>
                <span class="badge ${banner.isActive ? 'badge-success' : 'badge-secondary'}">
                    ${banner.isActive ? '启用' : '禁用'}
                </span>
            </td>
            <td>
                <div class="btn-group btn-group-sm">
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

// 显示创建轮播图模态框
function showCreateBannerModal() {
    currentBanner = null;
    showBannerModal('新建轮播图', {
        title: '',
        imageUrl: '',
        linkUrl: '',
        description: '',
        type: 'HOME',
        sortOrder: 0,
        isActive: true
    });
}

// 编辑轮播图
function editBanner(id) {
    currentBanner = banners.find(b => b.id === id);
    if (!currentBanner) return;
    
    showBannerModal('编辑轮播图', currentBanner);
}

// 显示轮播图模态框
function showBannerModal(title, data) {
    const modalHtml = `
        <div class="modal fade" id="bannerModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content" style="background: #2a475e; color: #c7d5e0;">
                    <div class="modal-header" style="border-bottom: 1px solid rgba(255,255,255,0.1);">
                        <h5 class="modal-title" style="color: #66c0f4;">${title}</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="bannerForm">
                            <div class="mb-3">
                                <label class="form-label">标题 <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="bannerTitle" value="${data.title}" required
                                       style="background: #1e3a5f; color: #c7d5e0; border-color: #66c0f4;">
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">图片URL <span class="text-danger">*</span></label>
                                <input type="url" class="form-control" id="bannerImageUrl" value="${data.imageUrl}" required
                                       placeholder="https://example.com/image.jpg"
                                       style="background: #1e3a5f; color: #c7d5e0; border-color: #66c0f4;">
                                <small class="text-muted">建议尺寸: 1920x600px</small>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">跳转链接</label>
                                <input type="text" class="form-control" id="bannerLinkUrl" value="${data.linkUrl || ''}"
                                       placeholder="/game/1 或 https://example.com"
                                       style="background: #1e3a5f; color: #c7d5e0; border-color: #66c0f4;">
                                <small class="text-muted">点击轮播图时跳转的页面</small>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">描述</label>
                                <textarea class="form-control" id="bannerDescription" rows="3"
                                          style="background: #1e3a5f; color: #c7d5e0; border-color: #66c0f4;">${data.description || ''}</textarea>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label class="form-label">类型</label>
                                        <select class="form-select" id="bannerType"
                                                style="background: #1e3a5f; color: #c7d5e0; border-color: #66c0f4;">
                                            <option value="HOME" ${data.type === 'HOME' ? 'selected' : ''}>首页</option>
                                            <option value="COMMUNITY" ${data.type === 'COMMUNITY' ? 'selected' : ''}>社区</option>
                                            <option value="CUSTOM" ${data.type === 'CUSTOM' ? 'selected' : ''}>自定义</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label class="form-label">排序</label>
                                        <input type="number" class="form-control" id="bannerSortOrder" value="${data.sortOrder}" min="0"
                                               style="background: #1e3a5f; color: #c7d5e0; border-color: #66c0f4;">
                                        <small class="text-muted">数字越小越靠前</small>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label class="form-label">状态</label>
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="bannerIsActive" 
                                                   ${data.isActive ? 'checked' : ''}>
                                            <label class="form-check-label">启用</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer" style="border-top: 1px solid rgba(255,255,255,0.1);">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        <button type="button" class="btn btn-primary" onclick="saveBanner()">保存</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // 移除旧模态框
    const oldModal = document.getElementById('bannerModal');
    if (oldModal) {
        oldModal.remove();
    }
    
    // 添加新模态框
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    
    // 显示模态框
    const modal = new bootstrap.Modal(document.getElementById('bannerModal'));
    modal.show();
    
    // 模态框关闭后移除
    document.getElementById('bannerModal').addEventListener('hidden.bs.modal', function() {
        this.remove();
    });
}

// 保存轮播图
async function saveBanner() {
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
        showAdminMessage('请输入标题', 'warning');
        return;
    }
    if (!formData.imageUrl) {
        showAdminMessage('请输入图片URL', 'warning');
        return;
    }
    
    try {
        const url = currentBanner ? `/api/banners/${currentBanner.id}` : '/api/banners';
        const method = currentBanner ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            showAdminMessage(currentBanner ? '更新成功' : '创建成功', 'success');
            bootstrap.Modal.getInstance(document.getElementById('bannerModal')).hide();
            loadBanners();
        } else {
            showAdminMessage('保存失败: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('保存轮播图失败:', error);
        showAdminMessage('保存失败,请稍后重试', 'error');
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
            showAdminMessage('状态已更新', 'success');
            loadBanners();
        } else {
            showAdminMessage('操作失败: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('切换状态失败:', error);
        showAdminMessage('操作失败,请稍后重试', 'error');
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
            showAdminMessage('删除成功', 'success');
            loadBanners();
        } else {
            showAdminMessage('删除失败: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('删除轮播图失败:', error);
        showAdminMessage('删除失败,请稍后重试', 'error');
    }
}

// 显示管理后台消息提示
function showAdminMessage(message, type = 'info') {
    const alertDiv = document.createElement('div');
    const bgColor = type === 'error' ? 'danger' : type;
    alertDiv.className = `alert alert-${bgColor} alert-dismissible fade show position-fixed`;
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(alertDiv);
    
    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}

