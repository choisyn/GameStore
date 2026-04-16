package com.gamestore.controller;

import com.gamestore.dto.request.BehaviorEventRequest;
import com.gamestore.dto.response.ApiResponse;
import com.gamestore.dto.response.CheckInResponse;
import com.gamestore.dto.response.DecisionInsightResponse;
import com.gamestore.dto.response.GrowthDashboardResponse;
import com.gamestore.dto.response.RecommendationResponse;
import com.gamestore.entity.User;
import com.gamestore.entity.UserBehaviorLog;
import com.gamestore.service.CurrentUserService;
import com.gamestore.service.InnovationService;
import com.gamestore.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/innovation")
public class InnovationController {

    private final InnovationService innovationService;
    private final CurrentUserService currentUserService;

    public InnovationController(InnovationService innovationService, CurrentUserService currentUserService) {
        this.innovationService = innovationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getRecommendations(
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "0") int batch,
            HttpServletRequest request) {
        User user = currentUserService.getCurrentUser(request);
        return ResponseUtil.success(
            "获取推荐列表成功",
            innovationService.getRecommendations(user == null ? null : user.getId(), size, batch)
        );
    }

    @GetMapping("/games/{gameId}/decision")
    public ResponseEntity<ApiResponse<DecisionInsightResponse>> getDecisionInsight(
            @PathVariable Long gameId,
            HttpServletRequest request) {
        User user = currentUserService.getCurrentUser(request);
        return ResponseUtil.success(
            "获取购前决策分析成功",
            innovationService.getDecisionInsight(user == null ? null : user.getId(), gameId)
        );
    }

    @GetMapping("/growth")
    public ResponseEntity<ApiResponse<GrowthDashboardResponse>> getGrowthDashboard(HttpServletRequest request) {
        User user = currentUserService.getCurrentUser(request);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        return ResponseUtil.success("获取成长面板成功", innovationService.getGrowthDashboard(user.getId()));
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<CheckInResponse>> checkIn(HttpServletRequest request) {
        User user = currentUserService.getCurrentUser(request);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        return ResponseUtil.success("签到成功", innovationService.checkIn(user.getId()));
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<Void>> recordEvent(
            @RequestBody BehaviorEventRequest request,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.success("游客状态不记录行为", null);
        }

        try {
            UserBehaviorLog.BehaviorType behaviorType = UserBehaviorLog.BehaviorType.valueOf(
                request.getBehaviorType().trim().toUpperCase()
            );
            innovationService.recordBehavior(
                user.getId(),
                behaviorType,
                request.getGameId(),
                request.getReferenceId(),
                request.getDetail()
            );
            return ResponseUtil.success("行为记录成功", null);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return ResponseUtil.badRequest("行为类型不支持");
        }
    }
}
