package com.gamestore.dto.request;

/**
 * 创建帖子请求DTO
 * 兼容讨论广场和社区系统
 */
public class CreatePostRequest {
    
    private String title;
    
    private String content;
    
    private Long gameId;  // 关联的游戏ID（讨论广场使用），可选
    
    private Long sectionId;  // 关联的板块ID（社区系统使用），可选
    
    private String category;  // 分类，可选
    
    private String images;  // 图片列表（JSON格式），可选
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getGameId() {
        return gameId;
    }
    
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
    
    public Long getSectionId() {
        return sectionId;
    }
    
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getImages() {
        return images;
    }
    
    public void setImages(String images) {
        this.images = images;
    }
}
