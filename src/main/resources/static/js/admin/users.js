// 用户管理页面
let currentUsers = [];

async function loadUsersPage() {
    const contentArea = document.getElementById('content-area');
    
    contentArea.innerHTML = `
        <div class="page-header">
            <h2><i class="bi bi-people"></i> 用户管理</h2>
        </div>
        
        <div class="data-table">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>用户名</th>
                        <th>邮箱</th>
                        <th>角色</th>
                        <th>状态</th>
                        <th>注册时间</th>
                        <th>最后登录</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody id="usersTableBody">
                    <tr><td colspan="8" class="text-center">加载中...</td></tr>
                </tbody>
            </table>
        </div>
        
        <!-- 用户编辑模态框 -->
        <div class="modal fade" id="userModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">编辑用户</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="userForm">
                            <input type="hidden" id="userId">
                            
                            <div class="mb-3">
                                <label class="form-label">用户名</label>
                                <input type="text" class="form-control" id="username" readonly>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">邮箱</label>
                                <input type="email" class="form-control" id="email">
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">角色</label>
                                <select class="form-select" id="role">
                                    <option value="USER">普通用户</option>
                                    <option value="ADMIN">管理员</option>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">状态</label>
                                <select class="form-select" id="status">
                                    <option value="ACTIVE">正常</option>
                                    <option value="BANNED">封禁</option>
                                    <option value="DELETED">删除</option>
                                </select>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        <button type="button" class="btn btn-primary" onclick="saveUser()">保存</button>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 重置密码模态框 -->
        <div class="modal fade" id="resetPasswordModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">重置密码</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" id="resetUserId">
                        <div class="mb-3">
                            <label class="form-label">新密码</label>
                            <input type="password" class="form-control" id="newPassword" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">确认密码</label>
                            <input type="password" class="form-control" id="confirmPassword" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        <button type="button" class="btn btn-primary" onclick="resetPassword()">重置</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    loadUsers();
}

async function loadUsers() {
    try {
        const response = await fetch('/api/admin/users');
        const data = await response.json();
        
        if (data.code === 200) {
            currentUsers = data.data;
            displayUsers(data.data);
        }
    } catch (error) {
        console.error('加载用户失败:', error);
    }
}

function displayUsers(users) {
    const tbody = document.getElementById('usersTableBody');
    
    if (users.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">暂无用户</td></tr>';
        return;
    }
    
    tbody.innerHTML = users.map(user => `
        <tr>
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.email}</td>
            <td>
                <span class="badge bg-${user.role === 'ADMIN' ? 'danger' : 'primary'}">
                    ${user.role === 'ADMIN' ? '管理员' : '普通用户'}
                </span>
            </td>
            <td>
                <span class="badge bg-${user.status === 'ACTIVE' ? 'success' : user.status === 'BANNED' ? 'danger' : 'secondary'}">
                    ${user.status === 'ACTIVE' ? '正常' : user.status === 'BANNED' ? '封禁' : '已删除'}
                </span>
            </td>
            <td>${formatDate(user.createdAt)}</td>
            <td>${formatDate(user.lastLoginAt)}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="editUser(${user.id})" title="编辑">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-warning" onclick="showResetPassword(${user.id})" title="重置密码">
                    <i class="bi bi-key"></i>
                </button>
                ${user.status === 'ACTIVE' ? 
                    `<button class="btn btn-sm btn-danger" onclick="banUser(${user.id})" title="封禁">
                        <i class="bi bi-ban"></i>
                    </button>` :
                    `<button class="btn btn-sm btn-success" onclick="unbanUser(${user.id})" title="解封">
                        <i class="bi bi-check-circle"></i>
                    </button>`
                }
            </td>
        </tr>
    `).join('');
}

function editUser(userId) {
    const user = currentUsers.find(u => u.id === userId);
    if (!user) return;
    
    document.getElementById('userId').value = user.id;
    document.getElementById('username').value = user.username;
    document.getElementById('email').value = user.email;
    document.getElementById('role').value = user.role;
    document.getElementById('status').value = user.status;
    
    const modal = new bootstrap.Modal(document.getElementById('userModal'));
    modal.show();
}

async function saveUser() {
    const userId = document.getElementById('userId').value;
    const userData = {
        email: document.getElementById('email').value,
        role: document.getElementById('role').value,
        status: document.getElementById('status').value
    };
    
    try {
        const response = await fetch(`/api/admin/users/${userId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });
        
        const data = await response.json();
        
        if (data.code === 200) {
            alert('更新成功！');
            bootstrap.Modal.getInstance(document.getElementById('userModal')).hide();
            loadUsers();
        } else {
            alert('更新失败：' + data.message);
        }
    } catch (error) {
        console.error('保存用户失败:', error);
        alert('保存失败，请重试！');
    }
}

function showResetPassword(userId) {
    document.getElementById('resetUserId').value = userId;
    document.getElementById('newPassword').value = '';
    document.getElementById('confirmPassword').value = '';
    
    const modal = new bootstrap.Modal(document.getElementById('resetPasswordModal'));
    modal.show();
}

async function resetPassword() {
    const userId = document.getElementById('resetUserId').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    if (newPassword !== confirmPassword) {
        alert('两次输入的密码不一致！');
        return;
    }
    
    if (newPassword.length < 6) {
        alert('密码长度不能少于6位！');
        return;
    }
    
    try {
        const response = await fetch(`/api/admin/users/${userId}/reset-password`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ newPassword })
        });
        
        const data = await response.json();
        
        if (data.code === 200) {
            alert('密码重置成功！');
            bootstrap.Modal.getInstance(document.getElementById('resetPasswordModal')).hide();
        } else {
            alert('重置失败：' + data.message);
        }
    } catch (error) {
        console.error('重置密码失败:', error);
        alert('重置失败，请重试！');
    }
}

async function banUser(userId) {
    if (!(await appConfirm('确定要封禁该用户吗？', { title: '封禁用户', type: 'warning' }))) return;
    
    try {
        const response = await fetch(`/api/admin/users/${userId}/status?status=BANNED`, {
            method: 'PUT'
        });
        
        const data = await response.json();
        
        if (data.code === 200) {
            alert('封禁成功！');
            loadUsers();
        } else {
            alert('封禁失败：' + data.message);
        }
    } catch (error) {
        console.error('封禁用户失败:', error);
        alert('封禁失败，请重试！');
    }
}

async function unbanUser(userId) {
    if (!(await appConfirm('确定要解封该用户吗？', { title: '解封用户', type: 'warning' }))) return;
    
    try {
        const response = await fetch(`/api/admin/users/${userId}/status?status=ACTIVE`, {
            method: 'PUT'
        });
        
        const data = await response.json();
        
        if (data.code === 200) {
            alert('解封成功！');
            loadUsers();
        } else {
            alert('解封失败：' + data.message);
        }
    } catch (error) {
        console.error('解封用户失败:', error);
        alert('解封失败，请重试！');
    }
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN');
}

