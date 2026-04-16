package com.gamestore.controller;

import com.gamestore.dto.request.CreateGameGuideRequest;
import com.gamestore.dto.response.ApiResponse;
import com.gamestore.dto.response.GameGuideResponse;
import com.gamestore.entity.GameGuide;
import com.gamestore.entity.User;
import com.gamestore.service.CurrentUserService;
import com.gamestore.service.GameGuideService;
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
@RequestMapping("/api/guides")
public class GameGuideController {

    private final GameGuideService gameGuideService;
    private final CurrentUserService currentUserService;

    public GameGuideController(GameGuideService gameGuideService, CurrentUserService currentUserService) {
        this.gameGuideService = gameGuideService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GameGuideResponse>>> getGuides(
        @RequestParam(required = false) Long gameId,
        @RequestParam(required = false) GameGuide.GuideDifficulty difficulty,
        @RequestParam(required = false) Boolean featuredOnly,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseUtil.success(
            "获取攻略列表成功",
            gameGuideService.getGuides(gameId, difficulty, featuredOnly, keyword, page, size)
        );
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<GameGuideResponse>>> getFeaturedGuides(
        @RequestParam(defaultValue = "3") int size
    ) {
        return ResponseUtil.success("获取精选攻略成功", gameGuideService.getFeaturedGuides(size));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getGuideCount() {
        return ResponseUtil.success("获取攻略数量成功", gameGuideService.getPublishedGuideCount());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GameGuideResponse>> createGuide(
        @RequestBody CreateGameGuideRequest request,
        HttpServletRequest httpRequest
    ) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        return ResponseUtil.success("发布攻略成功", gameGuideService.createGuide(request, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GameGuideResponse>> getGuideDetail(@PathVariable Long id) {
        return ResponseUtil.success("获取攻略详情成功", gameGuideService.getGuideDetail(id));
    }
}
