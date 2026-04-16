package com.gamestore.dto.response;

import com.gamestore.entity.GameGuide;

import java.time.LocalDateTime;

public class GameGuideResponse {

    private Long id;
    private Long gameId;
    private String gameName;
    private String gameImageUrl;
    private Long authorId;
    private String authorName;
    private String title;
    private String summary;
    private String content;
    private String coverImageUrl;
    private String difficulty;
    private String difficultyText;
    private Integer estimatedMinutes;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private Boolean featured;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;

    public static GameGuideResponse fromEntity(GameGuide guide) {
        GameGuideResponse response = new GameGuideResponse();
        response.setId(guide.getId());
        response.setGameId(guide.getGameId());
        response.setGameName(guide.getGame() != null ? guide.getGame().getDisplayName() : "未知游戏");
        response.setGameImageUrl(guide.getGame() != null ? guide.getGame().getImageUrl() : null);
        response.setAuthorId(guide.getAuthorId());
        response.setAuthorName(guide.getAuthor() != null ? guide.getAuthor().getUsername() : "匿名作者");
        response.setTitle(guide.getTitle());
        response.setSummary(guide.getSummary());
        response.setContent(guide.getContent());
        response.setCoverImageUrl(
            guide.getCoverImageUrl() != null && !guide.getCoverImageUrl().isBlank()
                ? guide.getCoverImageUrl()
                : response.getGameImageUrl()
        );
        response.setDifficulty(guide.getDifficulty().name());
        response.setDifficultyText(toDifficultyText(guide.getDifficulty()));
        response.setEstimatedMinutes(guide.getEstimatedMinutes());
        response.setTags(guide.getTags());
        response.setViewCount(guide.getViewCount());
        response.setLikeCount(guide.getLikeCount());
        response.setFeatured(Boolean.TRUE.equals(guide.getIsFeatured()));
        response.setPublishedAt(guide.getPublishedAt() != null ? guide.getPublishedAt() : guide.getCreatedAt());
        response.setCreatedAt(guide.getCreatedAt());
        return response;
    }

    private static String toDifficultyText(GameGuide.GuideDifficulty difficulty) {
        return switch (difficulty) {
            case BEGINNER -> "入门";
            case INTERMEDIATE -> "进阶";
            case ADVANCED -> "高阶";
        };
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getGameImageUrl() {
        return gameImageUrl;
    }

    public void setGameImageUrl(String gameImageUrl) {
        this.gameImageUrl = gameImageUrl;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDifficultyText() {
        return difficultyText;
    }

    public void setDifficultyText(String difficultyText) {
        this.difficultyText = difficultyText;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
