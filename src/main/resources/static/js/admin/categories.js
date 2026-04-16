// 分类管理页面
let managementCategories = [];

async function loadCategoriesPage() {
    const contentArea = document.getElementById('content-area');
    
    contentArea.innerHTML = `
        <div class="page-header">
            <div class="d-flex justify-content-between align-items-center">
                <h2><i class="bi bi-tags"></i> 分类管理</h2>
                <button class="btn btn-primary" onclick="showCategoryModal()">
                    <i class="bi bi-plus-circle"></i> 新建分类
                </button>
            </div>
        </div>

        <div class="data-table">
            <table class="table">
                <thead>
                    <tr>
                        <th width="50">ID</th>
                        <th>分类名称</th>
                        <th>描述</th>
                        <th width="80">排序</th>
                        <th width="80">状态</th>
                        <th width="150">操作</th>
                    </tr>
                </thead>
                <tbody id="categoriesTableBody">
                    <tr>
                        <td colspan="6" class="text-center">加载中...</td>
                    </tr>
                </tbody>
            </table>
        </div>
    `;
    
    loadCategoriesForManagement();
}

// 加载分类列表
async function loadCategoriesForManagement() {
    try {
        const response = await fetch('/api/categories');
        const result = await response.json();
        
        if (result.code === 200) {
            managementCategories = result.data;
            renderCategoriesTable();
        } else {
            showAdminMessage('加载失败: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('加载分类失败:', error);
        showAdminMessage('加载失败,请稍后重试', 'error');
    }
}

// 渲染分类表格
function renderCategoriesTable() {
    const tbody = document.getElementById('categoriesTableBody');
    
    if (managementCategories.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-muted">暂无分类数据</td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = managementCategories.map(category => `
        <tr>
            <td>${category.id}</td>
            <td>
                <strong style="color: #66c0f4;">${category.name}</strong>
            </td>
            <td>
                <small class="text-muted">${category.description || '无描述'}</small>
            </td>
            <td>${category.sortOrder || 0}</td>
            <td>
                <span class="badge ${category.status === 'ACTIVE' ? 'badge-success' : 'badge-secondary'}">
                    ${category.status === 'ACTIVE' ? '启用' : '禁用'}
                </span>
            </td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary" onclick="editCategory(${category.id})" title="编辑">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-outline-danger" onclick="deleteCategory(${category.id})" title="删除">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// 显示分类模态框
function showCategoryModal(categoryId = null) {
    let category = null;
    if (categoryId) {
        category = managementCategories.find(c => c.id === categoryId);
    }
    
    const modalHtml = `
        <div class="modal fade" id="categoryModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content" style="background: #2a475e; color: #c7d5e0;">
                    <div class="modal-header" style="border-bottom: 1px solid rgba(255,255,255,0.1);">
                        <h5 class="modal-title" style="color: #66c0f4;">
                            ${category ? '编辑分类' : '新建分类'}
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="categoryForm">
                            <input type="hidden" id="categoryId" value="${category ? category.id : ''}">
                            
                            <div class="mb-3">
                                <label class="form-label">分类名称 <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="categoryName" 
                                       value="${category ? category.name : ''}" required
                                       style="background: #1e3a5f; color: #c7d5e0; border-color: #66c0f4;">
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">描述</label>
                                <textarea class="form-control" id="categoryDescription" rows="3"
                                          style="background: #1e3a5f; color: #c7d5e0; border-color: #66c0f4;">${category ? (category.description || '') : ''}</textarea>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label class="form-label">排序</label>
                                        <input type="number" class="form-control" id="categorySortOrder" 
                                               value="${category ? category.sortOrder : 0}" min="0"
                                               style="background: #1e3a5f; color: #c7d5e0; border-color: #66c0f4;">
                                        <small class="text-muted">数字越小越靠前</small>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label class="form-label">状态</label>
                                        <select class="form-select" id="categoryStatus"
                                                style="background: #1e3a5f; color: #c7d5e0; border-color: #66c0f4;">
                                            <option value="ACTIVE" ${!category || category.status === 'ACTIVE' ? 'selected' : ''}>启用</option>
                                            <option value="INACTIVE" ${category && category.status === 'INACTIVE' ? 'selected' : ''}>禁用</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer" style="border-top: 1px solid rgba(255,255,255,0.1);">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        <button type="button" class="btn btn-primary" onclick="saveCategory()">保存</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // 移除旧模态框
    const oldModal = document.getElementById('categoryModal');
    if (oldModal) {
        oldModal.remove();
    }
    
    // 添加新模态框
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    
    // 显示模态框
    const modal = new bootstrap.Modal(document.getElementById('categoryModal'));
    modal.show();
    
    // 模态框关闭后移除
    document.getElementById('categoryModal').addEventListener('hidden.bs.modal', function() {
        this.remove();
    });
}

// 编辑分类
function editCategory(id) {
    showCategoryModal(id);
}

// 保存分类
async function saveCategory() {
    const categoryId = document.getElementById('categoryId').value;
    const formData = {
        name: document.getElementById('categoryName').value.trim(),
        description: document.getElementById('categoryDescription').value.trim(),
        sortOrder: parseInt(document.getElementById('categorySortOrder').value) || 0,
        status: document.getElementById('categoryStatus').value
    };
    
    // 验证
    if (!formData.name) {
        showAdminMessage('请输入分类名称', 'warning');
        return;
    }
    
    try {
        const url = categoryId ? `/api/categories/${categoryId}` : '/api/categories';
        const method = categoryId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            showAdminMessage(categoryId ? '更新成功' : '创建成功', 'success');
            bootstrap.Modal.getInstance(document.getElementById('categoryModal')).hide();
            loadCategoriesForManagement();
        } else {
            showAdminMessage('保存失败: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('保存分类失败:', error);
        showAdminMessage('保存失败,请稍后重试', 'error');
    }
}

// 删除分类
async function deleteCategory(id) {
    if (!(await appConfirm('确定要删除这个分类吗？删除后将无法恢复。', { title: '删除分类', type: 'warning' }))) {
        return;
    }
    
    try {
        const response = await fetch(`/api/categories/${id}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            showAdminMessage('删除成功', 'success');
            loadCategoriesForManagement();
        } else {
            showAdminMessage('删除失败: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('删除分类失败:', error);
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

