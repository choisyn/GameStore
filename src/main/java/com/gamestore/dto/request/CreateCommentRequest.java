package com.gamestore.dto.request;

/**
 * 创建评论请求DTO
 */
public class CreateCommentRequest {
    
    private Long postId;
    
    private String content;
    
    private Long parentId;  // 父评论ID，用于回复评论
    
    // Getters and Setters
    public Long getPostId() {
        return postId;
    }
    
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
