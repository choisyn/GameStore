package com.gamestore.dto.response;

public record CheckInResponse(
    Integer rewardPoints,
    Integer currentPoints,
    Integer streakDays
) {
}
