package com.gamestore.dto.response;

import com.gamestore.entity.CommunityPost;

import java.time.LocalDateTime;

public class AdminCommunityPostResponse {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String username;
    private Long sectionId;
    private String sectionName;
    private Long gameId;
    private String gameName;
    private String category;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isPinned;
    private Boolean isEssence;
    private Boolean isClosed;
    private String status;
    private String sourceType;
    private String sourceLabel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastCommentAt;

    public static AdminCommunityPostResponse fromForumPost(PostResponse post) {
        AdminCommunityPostResponse response = new AdminCommunityPostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setUserId(post.getUserId());
        response.setUsername(post.getUsername());
        response.setGameId(post.getGameId());
        response.setGameName(post.getGameName());
        response.setCategory(post.getCategory());
        response.setViewCount(post.getViewCount());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setIsPinned(Boolean.TRUE.equals(post.getIsPinned()));
        response.setIsEssence(Boolean.TRUE.equals(post.getIsFeatured()));
        response.setIsClosed(false);
        response.setStatus(post.getStatus());
        response.setSourceType("FORUM");
        response.setSourceLabel("讨论广场");
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setLastCommentAt(post.getLastCommentAt());
        return response;
    }

    public static AdminCommunityPostResponse fromCommunityPost(CommunityPost post, String sectionName) {
        AdminCommunityPostResponse response = new AdminCommunityPostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setUserId(post.getUserId());
        response.setSectionId(post.getSectionId());
        response.setSectionName(sectionName);
        response.setViewCount(post.getViewCount());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setIsPinned(Boolean.TRUE.equals(post.getIsPinned()));
        response.setIsEssence(Boolean.TRUE.equals(post.getIsEssence()));
        response.setIsClosed(Boolean.TRUE.equals(post.getIsClosed()));
        response.setStatus(post.getStatus().name());
        response.setSourceType("COMMUNITY");
        response.setSourceLabel(sectionName != null && !sectionName.isBlank()
            ? "社区板块 / " + sectionName
            : "社区板块");
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setLastCommentAt(post.getLastCommentAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Boolean isPinned) {
        this.isPinned = isPinned;
    }

    public Boolean getIsEssence() {
        return isEssence;
    }

    public void setIsEssence(Boolean isEssence) {
        this.isEssence = isEssence;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceLabel() {
        return sourceLabel;
    }

    public void setSourceLabel(String sourceLabel) {
        this.sourceLabel = sourceLabel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastCommentAt() {
        return lastCommentAt;
    }

    public void setLastCommentAt(LocalDateTime lastCommentAt) {
        this.lastCommentAt = lastCommentAt;
    }
}
