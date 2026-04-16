package com.gamestore.controller;

import com.gamestore.dto.request.ContentRewardRequest;
import com.gamestore.dto.response.ApiResponse;
import com.gamestore.dto.response.ContentRewardActionResponse;
import com.gamestore.dto.response.ContentRewardSummaryResponse;
import com.gamestore.entity.User;
import com.gamestore.service.ContentRewardService;
import com.gamestore.service.CurrentUserService;
import com.gamestore.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rewards")
public class ContentRewardController {

    private final ContentRewardService contentRewardService;
    private final CurrentUserService currentUserService;

    public ContentRewardController(
        ContentRewardService contentRewardService,
        CurrentUserService currentUserService
    ) {
        this.contentRewardService = contentRewardService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<ContentRewardActionResponse>> rewardPost(
        @PathVariable Long postId,
        @RequestBody ContentRewardRequest request,
        HttpServletRequest httpRequest
    ) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        return ResponseUtil.success(
            "打赏成功",
            contentRewardService.rewardForumPost(postId, user.getId(), request.getPoints())
        );
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<ContentRewardSummaryResponse>> getPostRewardPreview(@PathVariable Long postId) {
        return ResponseUtil.success("获取打赏摘要成功", contentRewardService.getForumPostRewardPreview(postId));
    }

    @GetMapping("/posts/{postId}/all")
    public ResponseEntity<ApiResponse<ContentRewardSummaryResponse>> getPostRewardAll(@PathVariable Long postId) {
        return ResponseUtil.success("获取完整打赏名单成功", contentRewardService.getForumPostRewardAll(postId));
    }

    @PostMapping("/guides/{guideId}")
    public ResponseEntity<ApiResponse<ContentRewardActionResponse>> rewardGuide(
        @PathVariable Long guideId,
        @RequestBody ContentRewardRequest request,
        HttpServletRequest httpRequest
    ) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        return ResponseUtil.success(
            "打赏成功",
            contentRewardService.rewardGuide(guideId, user.getId(), request.getPoints())
        );
    }

    @GetMapping("/guides/{guideId}")
    public ResponseEntity<ApiResponse<ContentRewardSummaryResponse>> getGuideRewardPreview(@PathVariable Long guideId) {
        return ResponseUtil.success("获取打赏摘要成功", contentRewardService.getGuideRewardPreview(guideId));
    }

    @GetMapping("/guides/{guideId}/all")
    public ResponseEntity<ApiResponse<ContentRewardSummaryResponse>> getGuideRewardAll(@PathVariable Long guideId) {
        return ResponseUtil.success("获取完整打赏名单成功", contentRewardService.getGuideRewardAll(guideId));
    }
}
