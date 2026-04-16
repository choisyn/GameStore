// 游戏管理页面
let currentGames = [];
let allCategories = [];
let currentPage = 1;
let pageSize = 10;
let totalGames = 0;
let searchKeyword = '';

async function loadGamesPage() {
    const contentArea = document.getElementById('content-area');
    
    contentArea.innerHTML = `
        <div class="page-header d-flex justify-content-between align-items-center">
            <h2><i class="bi bi-controller"></i> 游戏管理</h2>
            <button class="btn btn-primary" onclick="showGameModal()">
                <i class="bi bi-plus-circle"></i> 添加游戏
            </button>
        </div>
        
        <!-- 搜索和筛选区域 -->
        <div class="card mb-3" style="background: #2a475e; border-color: #66c0f4;">
            <div class="card-body">
                <div class="row g-3">
                    <div class="col-md-6">
                        <div class="input-group">
                            <span class="input-group-text" style="background: #1b2838; border-color: #66c0f4; color: #c7d5e0;">
                                <i class="bi bi-search"></i>
                            </span>
                            <input type="text" 
                                   class="form-control" 
                                   id="searchInput" 
                                   placeholder="搜索游戏名称..." 
                                   style="background: #1b2838; border-color: #66c0f4; color: #c7d5e0;"
                                   onkeyup="handleSearchKeyup(event)">
                            <button class="btn btn-outline-info" onclick="searchGames()">
                                <i class="bi bi-search"></i> 搜索
                            </button>
                            <button class="btn btn-outline-secondary" onclick="clearSearch()">
                                <i class="bi bi-x-circle"></i> 清空
                            </button>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <select class="form-select" 
                                id="pageSizeSelect" 
                                style="background: #1b2838; border-color: #66c0f4; color: #c7d5e0;"
                                onchange="changePageSize()">
                            <option value="10">每页10条</option>
                            <option value="20">每页20条</option>
                            <option value="50">每页50条</option>
                            <option value="100">每页100条</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <div class="text-end">
                            <span class="badge bg-info fs-6">
                                共 <span id="totalCount">0</span> 个游戏
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="data-table">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th style="width: 5%">ID</th>
                        <th style="width: 20%">游戏名称</th>
                        <th style="width: 15%">分类</th>
                        <th style="width: 8%">价格</th>
                        <th style="width: 8%">折扣价</th>
                        <th style="width: 8%">评分</th>
                        <th style="width: 8%">状态</th>
                        <th style="width: 8%">精选</th>
                        <th style="width: 20%">操作</th>
                    </tr>
                </thead>
                <tbody id="gamesTableBody">
                    <tr><td colspan="9" class="text-center">加载中...</td></tr>
                </tbody>
            </table>
        </div>
        
        <!-- 分页区域 -->
        <div class="d-flex justify-content-between align-items-center mt-3">
            <div id="pageInfo" class="text-muted"></div>
            <nav>
                <ul class="pagination mb-0" id="pagination"></ul>
            </nav>
        </div>
        
        <!-- 游戏编辑模态框 -->
        <div class="modal fade" id="gameModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="gameModalTitle">添加游戏</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="gameForm">
                            <input type="hidden" id="gameId">
                            
                            <div class="mb-3">
                                <label class="form-label">游戏名称 *</label>
                                <input type="text" class="form-control" id="gameName" required>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">游戏分类 (可多选)</label>
                                <div id="gameCategoriesCheckboxes" class="border rounded p-3" style="max-height: 200px; overflow-y: auto;">
                                    <div class="text-muted">加载中...</div>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">发行日期</label>
                                <input type="date" class="form-control" id="gameReleaseDate">
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">游戏描述</label>
                                <textarea class="form-control" id="gameDescription" rows="3"></textarea>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">原价（元）</label>
                                    <input type="number" class="form-control" id="gamePrice" step="0.01">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">折扣价（元）</label>
                                    <input type="number" class="form-control" id="gameDiscountPrice" step="0.01">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">评分</label>
                                    <input type="number" class="form-control" id="gameRating" step="0.1" max="10">
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">封面图片URL</label>
                                <input type="text" class="form-control" id="gameImageUrl">
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">系统要求</label>
                                <textarea class="form-control" id="gameSystemRequirements" rows="2"></textarea>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">标签（逗号分隔）</label>
                                <input type="text" class="form-control" id="gameTags" placeholder="例如：动作,冒险,多人">
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">状态</label>
                                    <select class="form-select" id="gameStatus">
                                        <option value="ACTIVE">上架</option>
                                        <option value="INACTIVE">下架</option>
                                    </select>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">设为精选</label>
                                    <select class="form-select" id="gameIsFeatured">
                                        <option value="false">否</option>
                                        <option value="true">是</option>
                                    </select>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        <button type="button" class="btn btn-primary" onclick="saveGame()">保存</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // 先加载分类,再加载游戏
    await loadCategories();
    await loadGames();
}

// 加载所有分类
async function loadCategories() {
    try {
        const response = await fetch('/api/categories');
        const data = await response.json();
        
        if (data.code === 200) {
            allCategories = data.data;
        }
    } catch (error) {
        console.error('加载分类失败:', error);
    }
}

// 根据分类ID获取分类名称
function getCategoryName(categoryId) {
    if (!categoryId) return '-';
    const category = allCategories.find(c => c.id === categoryId);
    return category ? category.name : `未知分类(ID:${categoryId})`;
}

async function loadGames() {
    try {
        // 构建查询URL
        let url = '/api/admin/games';
        const params = new URLSearchParams();
        
        if (searchKeyword) {
            params.append('keyword', searchKeyword);
        }
        
        if (params.toString()) {
            url += '?' + params.toString();
        }
        
        const response = await fetch(url);
        const data = await response.json();
        
        if (data.code === 200) {
            let allGames = data.data;
            totalGames = allGames.length;
            
            // 客户端分页
            const start = (currentPage - 1) * pageSize;
            const end = start + pageSize;
            const pagedGames = allGames.slice(start, end);
            
            // 为每个游戏加载分类信息
            const gamesWithCategories = await Promise.all(
                pagedGames.map(async (game) => {
                    try {
                        const detailResponse = await fetch(`/api/games/${game.id}/with-categories`);
                        const detailData = await detailResponse.json();
                        return detailData.code === 200 ? detailData.data : game;
                    } catch {
                        return game;
                    }
                })
            );
            
            currentGames = gamesWithCategories;
            displayGames(gamesWithCategories);
            updatePagination();
            updateTotalCount();
        }
    } catch (error) {
        console.error('加载游戏失败:', error);
    }
}

// 搜索游戏
function searchGames() {
    searchKeyword = document.getElementById('searchInput').value.trim();
    currentPage = 1; // 重置到第一页
    loadGames();
}

// 清空搜索
function clearSearch() {
    document.getElementById('searchInput').value = '';
    searchKeyword = '';
    currentPage = 1;
    loadGames();
}

// 处理搜索框回车事件
function handleSearchKeyup(event) {
    if (event.key === 'Enter') {
        searchGames();
    }
}

// 改变每页显示数量
function changePageSize() {
    pageSize = parseInt(document.getElementById('pageSizeSelect').value);
    currentPage = 1;
    loadGames();
}

// 跳转到指定页
function goToPage(page) {
    if (page < 1 || page > Math.ceil(totalGames / pageSize)) return;
    currentPage = page;
    loadGames();
}

// 更新分页控件
function updatePagination() {
    const totalPages = Math.ceil(totalGames / pageSize);
    const pagination = document.getElementById('pagination');
    const pageInfo = document.getElementById('pageInfo');
    
    // 更新页面信息
    const start = (currentPage - 1) * pageSize + 1;
    const end = Math.min(currentPage * pageSize, totalGames);
    pageInfo.textContent = `显示 ${start}-${end} 条，共 ${totalGames} 条`;
    
    // 生成分页按钮
    let paginationHTML = '';
    
    // 上一页按钮
    paginationHTML += `
        <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="goToPage(${currentPage - 1})">
                <i class="bi bi-chevron-left"></i>
            </a>
        </li>
    `;
    
    // 页码按钮
    const maxVisiblePages = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
    
    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }
    
    // 第一页
    if (startPage > 1) {
        paginationHTML += `
            <li class="page-item">
                <a class="page-link" href="javascript:void(0)" onclick="goToPage(1)">1</a>
            </li>
        `;
        if (startPage > 2) {
            paginationHTML += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
        }
    }
    
    // 中间页码
    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
            <li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="javascript:void(0)" onclick="goToPage(${i})">${i}</a>
            </li>
        `;
    }
    
    // 最后一页
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            paginationHTML += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
        }
        paginationHTML += `
            <li class="page-item">
                <a class="page-link" href="javascript:void(0)" onclick="goToPage(${totalPages})">${totalPages}</a>
            </li>
        `;
    }
    
    // 下一页按钮
    paginationHTML += `
        <li class="page-item ${currentPage === totalPages || totalPages === 0 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="goToPage(${currentPage + 1})">
                <i class="bi bi-chevron-right"></i>
            </a>
        </li>
    `;
    
    pagination.innerHTML = paginationHTML;
}

// 更新游戏总数显示
function updateTotalCount() {
    document.getElementById('totalCount').textContent = totalGames;
}

function displayGames(games) {
    const tbody = document.getElementById('gamesTableBody');
    
    if (games.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center">暂无游戏</td></tr>';
        return;
    }
    
    tbody.innerHTML = games.map(game => {
        // 显示多个分类
        let categoriesDisplay = '-';
        if (game.categoryNames && game.categoryNames.length > 0) {
            categoriesDisplay = game.categoryNames.join(', ');
        } else if (game.categoryId) {
            categoriesDisplay = getCategoryName(game.categoryId);
        }
        
        return `
        <tr>
            <td>${game.id}</td>
            <td>${game.name}</td>
            <td><small>${categoriesDisplay}</small></td>
            <td>¥${game.price || '0.00'}</td>
            <td>¥${game.discountPrice || '-'}</td>
            <td>${game.rating || '-'}</td>
            <td>
                <span class="badge bg-${game.status === 'ACTIVE' ? 'success' : 'secondary'}">
                    ${game.status === 'ACTIVE' ? '上架' : '下架'}
                </span>
            </td>
            <td>
                <span class="badge bg-${game.isFeatured ? 'warning' : 'secondary'}">
                    ${game.isFeatured ? '是' : '否'}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="editGame(${game.id})" title="编辑">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-danger" onclick="deleteGame(${game.id})" title="删除">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
        `;
    }).join('');
}

async function showGameModal(gameId = null) {
    const modal = new bootstrap.Modal(document.getElementById('gameModal'));
    const title = document.getElementById('gameModalTitle');
    
    // 填充分类复选框
    await populateCategoryCheckboxes();
    
    if (gameId) {
        title.textContent = '编辑游戏';
        
        try {
            // 获取游戏详细信息(包含分类)
            const response = await fetch(`/api/games/${gameId}/with-categories`);
            const data = await response.json();
            
            if (data.code === 200) {
                const game = data.data;
                document.getElementById('gameId').value = game.id;
                document.getElementById('gameName').value = game.name || '';
                document.getElementById('gameDescription').value = game.description || '';
                document.getElementById('gamePrice').value = game.price || '';
                document.getElementById('gameDiscountPrice').value = game.discountPrice || '';
                document.getElementById('gameRating').value = game.rating || '';
                document.getElementById('gameImageUrl').value = game.imageUrl || '';
                document.getElementById('gameSystemRequirements').value = game.systemRequirements || '';
                document.getElementById('gameTags').value = game.tags || '';
                document.getElementById('gameStatus').value = game.status || 'ACTIVE';
                document.getElementById('gameIsFeatured').value = game.isFeatured ? 'true' : 'false';
                document.getElementById('gameReleaseDate').value = game.releaseDate || '';
                
                // 勾选游戏的分类
                if (game.categoryIds && game.categoryIds.length > 0) {
                    game.categoryIds.forEach(categoryId => {
                        const checkbox = document.getElementById(`category_${categoryId}`);
                        if (checkbox) {
                            checkbox.checked = true;
                        }
                    });
                }
            }
        } catch (error) {
            console.error('加载游戏详情失败:', error);
        }
    } else {
        title.textContent = '添加游戏';
        document.getElementById('gameForm').reset();
        document.getElementById('gameId').value = '';
        // 清空所有复选框
        document.querySelectorAll('#gameCategoriesCheckboxes input[type="checkbox"]').forEach(cb => {
            cb.checked = false;
        });
    }
    
    modal.show();
}

// 填充分类复选框
async function populateCategoryCheckboxes() {
    const container = document.getElementById('gameCategoriesCheckboxes');
    
    if (allCategories.length === 0) {
        await loadCategories();
    }
    
    if (allCategories.length === 0) {
        container.innerHTML = '<div class="text-muted">暂无分类</div>';
        return;
    }
    
    // 生成复选框
    container.innerHTML = allCategories.map(category => `
        <div class="form-check">
            <input class="form-check-input" type="checkbox" value="${category.id}" id="category_${category.id}">
            <label class="form-check-label" for="category_${category.id}">
                ${category.name}
            </label>
        </div>
    `).join('');
}

// 获取选中的分类ID数组
function getSelectedCategoryIds() {
    const checkboxes = document.querySelectorAll('#gameCategoriesCheckboxes input[type="checkbox"]:checked');
    return Array.from(checkboxes).map(cb => parseInt(cb.value));
}

function editGame(gameId) {
    showGameModal(gameId);
}

async function saveGame() {
    const gameId = document.getElementById('gameId').value;
    
    // 获取选中的分类ID数组
    const categoryIds = getSelectedCategoryIds();
    
    const gameData = {
        name: document.getElementById('gameName').value,
        categoryIds: categoryIds,  // 使用多分类
        description: document.getElementById('gameDescription').value,
        price: parseFloat(document.getElementById('gamePrice').value) || 0,
        discountPrice: parseFloat(document.getElementById('gameDiscountPrice').value) || null,
        rating: parseFloat(document.getElementById('gameRating').value) || null,
        imageUrl: document.getElementById('gameImageUrl').value,
        systemRequirements: document.getElementById('gameSystemRequirements').value,
        tags: document.getElementById('gameTags').value,
        status: document.getElementById('gameStatus').value,
        isFeatured: document.getElementById('gameIsFeatured').value === 'true',
        releaseDate: document.getElementById('gameReleaseDate').value || null
    };
    
    try {
        const url = gameId ? `/api/games/${gameId}` : '/api/games';
        const method = gameId ? 'PUT' : 'POST';
        
        console.log('保存游戏请求:', {
            url,
            method,
            data: gameData
        });
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(gameData)
        });
        
        const data = await response.json();
        
        console.log('保存游戏响应:', data);
        
        if (data.code === 200) {
            alert(gameId ? '更新成功！' : '添加成功！');
            bootstrap.Modal.getInstance(document.getElementById('gameModal')).hide();
            loadGames();
        } else {
            console.error('操作失败:', data);
            alert('操作失败：' + (data.message || '未知错误'));
        }
    } catch (error) {
        console.error('保存游戏失败:', error);
        alert('保存失败，请重试！');
    }
}

async function deleteGame(gameId) {
    if (!(await appConfirm('确定要删除这个游戏吗？', { title: '删除游戏', type: 'warning' }))) return;
    
    try {
        const response = await fetch(`/api/admin/games/${gameId}`, {
            method: 'DELETE'
        });
        
        const data = await response.json();
        
        if (data.code === 200) {
            alert('删除成功！');
            loadGames();
        } else {
            alert('删除失败：' + data.message);
        }
    } catch (error) {
        console.error('删除游戏失败:', error);
        alert('删除失败，请重试！');
    }
}

