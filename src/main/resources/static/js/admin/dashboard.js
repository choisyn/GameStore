// 仪表盘页面
async function loadDashboard() {
    const contentArea = document.getElementById('content-area');
    
    // 获取统计数据
    const overview = await fetch('/api/admin/dashboard/overview').then(r => r.json());
    const data = overview.data || {};
    
    contentArea.innerHTML = `
        <div class="page-header">
            <h2><i class="bi bi-speedometer2"></i> 仪表盘</h2>
        </div>
        
        <div class="stats-grid">
            <div class="stat-card">
                <div class="icon"><i class="bi bi-people"></i></div>
                <h3>总用户数</h3>
                <div class="value">${data.totalUsers || 0}</div>
                <small>活跃用户: ${data.activeUsers || 0}</small>
            </div>
            
            <div class="stat-card">
                <div class="icon"><i class="bi bi-controller"></i></div>
                <h3>游戏总数</h3>
                <div class="value">${data.totalGames || 0}</div>
                <small>已上架游戏: ${data.activeGames || 0}</small>
            </div>
            
            <div class="stat-card">
                <div class="icon"><i class="bi bi-chat-dots"></i></div>
                <h3>社区内容</h3>
                <div class="value">${(data.forumPosts || 0) + (data.communityPosts || 0)}</div>
                <small>论坛评论: ${data.forumComments || 0}</small>
            </div>
            
            <div class="stat-card">
                <div class="icon"><i class="bi bi-cash-stack"></i></div>
                <h3>总销售额</h3>
                <div class="value">¥${Number(data.totalSales || 0).toFixed(2)}</div>
                <small>累计订单: ${data.totalOrders || 0}</small>
            </div>
        </div>
        
        <div class="data-table">
            <div class="p-4">
                <h4 class="mb-3"><i class="bi bi-graph-up"></i> 系统概览</h4>
                <p>当前后台已经能统一查看用户、游戏、社区和商城闭环数据。</p>
                <p>管理员账号数：${data.adminUsers || 0}，论坛帖子数：${data.forumPosts || 0}，社区帖子数：${data.communityPosts || 0}。</p>
            </div>
        </div>
    `;
}

