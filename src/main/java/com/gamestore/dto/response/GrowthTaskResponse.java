package com.gamestore.dto.response;

public record GrowthTaskResponse(
    String name,
    String description,
    Integer currentProgress,
    Integer targetProgress,
    Boolean completed,
    String hint
) {
}
