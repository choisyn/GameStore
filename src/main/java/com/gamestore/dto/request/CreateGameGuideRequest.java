package com.gamestore.dto.request;

import com.gamestore.entity.GameGuide;

public class CreateGameGuideRequest {

    private Long gameId;
    private String title;
    private String summary;
    private String content;
    private String coverImageUrl;
    private GameGuide.GuideDifficulty difficulty;
    private Integer estimatedMinutes;
    private String tags;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
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

    public GameGuide.GuideDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(GameGuide.GuideDifficulty difficulty) {
        this.difficulty = difficulty;
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
}
