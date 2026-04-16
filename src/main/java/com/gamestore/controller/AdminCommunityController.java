package com.gamestore.controller;

import com.gamestore.dto.response.AdminCommunityPostResponse;
import com.gamestore.dto.response.ApiResponse;
import com.gamestore.entity.CommunityComment;
import com.gamestore.entity.CommunityPost;
import com.gamestore.repository.CommunitySectionRepository;
import com.gamestore.service.CommunityService;
import com.gamestore.service.PostService;
import com.gamestore.util.ResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台社区管理 API
 */
@RestController
@RequestMapping("/api/admin/community")
public class AdminCommunityController {

    private final CommunityService communityService;
    private final PostService postService;
    private final CommunitySectionRepository communitySectionRepository;

    public AdminCommunityController(
        CommunityService communityService,
        PostService postService,
        CommunitySectionRepository communitySectionRepository
    ) {
        this.communityService = communityService;
        this.postService = postService;
        this.communitySectionRepository = communitySectionRepository;
    }

    /**
     * 获取后台帖子列表
     * source:
     * - all: 讨论广场 + 社区板块
     * - forum: 仅讨论广场
     * - community: 仅社区板块
     */
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<AdminCommunityPostResponse>>> getAllPosts(
        @RequestParam(required = false) Long sectionId,
        @RequestParam(defaultValue = "all") String source
    ) {
        String normalizedSource = normalizeSource(source);
        Map<Long, String> sectionNameMap = communitySectionRepository.findAll().stream()
            .collect(Collectors.toMap(section -> section.getId(), section -> section.getName(), (left, right) -> left));

        List<AdminCommunityPostResponse> result = new ArrayList<>();

        if (!"community".equals(normalizedSource)) {
            result.addAll(postService.getAdminPosts().stream()
                .map(AdminCommunityPostResponse::fromForumPost)
                .collect(Collectors.toList()));
        }

        if (!"forum".equals(normalizedSource)) {
            result.addAll(communityService.getAllPostsForAdmin().stream()
                .filter(post -> sectionId == null || sectionId.equals(post.getSectionId()))
                .map(post -> AdminCommunityPostResponse.fromCommunityPost(
                    post,
                    sectionNameMap.getOrDefault(post.getSectionId(), "板块 #" + post.getSectionId())
                ))
                .collect(Collectors.toList()));
        }

        result.sort(Comparator.comparing(AdminCommunityPostResponse::getCreatedAt,
            Comparator.nullsLast(Comparator.reverseOrder())));

        return ResponseUtil.success("获取成功", result);
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<AdminCommunityPostResponse>> getPost(
        @PathVariable Long id,
        @RequestParam(defaultValue = "forum") String source
    ) {
        String normalizedSource = normalizeSource(source);

        if ("community".equals(normalizedSource)) {
            CommunityPost post = communityService.getPostByIdForAdmin(id);
            String sectionName = communitySectionRepository.findById(post.getSectionId())
                .map(section -> section.getName())
                .orElse("板块 #" + post.getSectionId());
            return ResponseUtil.success("获取成功", AdminCommunityPostResponse.fromCommunityPost(post, sectionName));
        }

        return ResponseUtil.success("获取成功", AdminCommunityPostResponse.fromForumPost(postService.getAdminPostById(id)));
    }

    /**
     * 设置精华 / 推荐
     * forum: 对应 isFeatured
     * community: 对应 isEssence
     */
    @PutMapping("/posts/{id}/essence")
    public ResponseEntity<ApiResponse<Void>> setEssence(
        @PathVariable Long id,
        @RequestParam boolean isEssence,
        @RequestParam(defaultValue = "forum") String source
    ) {
        String normalizedSource = normalizeSource(source);

        if ("community".equals(normalizedSource)) {
            CommunityPost post = communityService.getPostByIdForAdmin(id);
            post.setIsEssence(isEssence);
            communityService.updatePost(post);
        } else {
            postService.setAdminFeatured(id, isEssence);
        }

        return ResponseUtil.success(isEssence ? "设置成功" : "取消成功", null);
    }

    /**
     * 设置置顶
     */
    @PutMapping("/posts/{id}/pin")
    public ResponseEntity<ApiResponse<Void>> setPinned(
        @PathVariable Long id,
        @RequestParam boolean isPinned,
        @RequestParam(defaultValue = "forum") String source
    ) {
        String normalizedSource = normalizeSource(source);

        if ("community".equals(normalizedSource)) {
            CommunityPost post = communityService.getPostByIdForAdmin(id);
            post.setIsPinned(isPinned);
            communityService.updatePost(post);
        } else {
            postService.setAdminPinned(id, isPinned);
        }

        return ResponseUtil.success(isPinned ? "置顶成功" : "取消置顶成功", null);
    }

    /**
     * 关闭/开启帖子评论
     * 仅社区板块帖子支持
     */
    @PutMapping("/posts/{id}/close")
    public ResponseEntity<ApiResponse<Void>> closePost(
        @PathVariable Long id,
        @RequestParam boolean isClosed,
        @RequestParam(defaultValue = "community") String source
    ) {
        String normalizedSource = normalizeSource(source);

        if (!"community".equals(normalizedSource)) {
            return ResponseUtil.error(400, "讨论广场帖子暂不支持关闭评论");
        }

        CommunityPost post = communityService.getPostByIdForAdmin(id);
        post.setIsClosed(isClosed);
        communityService.updatePost(post);
        return ResponseUtil.success(isClosed ? "关闭评论成功" : "开启评论成功", null);
    }

    /**
     * 删除帖子（管理员权限）
     */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
        @PathVariable Long id,
        @RequestParam(defaultValue = "forum") String source
    ) {
        String normalizedSource = normalizeSource(source);

        if ("community".equals(normalizedSource)) {
            communityService.adminDeletePost(id);
        } else {
            postService.adminDeletePost(id);
        }

        return ResponseUtil.success("删除成功", null);
    }

    /**
     * 隐藏帖子
     * 仅社区板块帖子支持
     */
    @PutMapping("/posts/{id}/hide")
    public ResponseEntity<ApiResponse<Void>> hidePost(@PathVariable Long id) {
        CommunityPost post = communityService.getPostByIdForAdmin(id);
        post.setStatus(CommunityPost.PostStatus.HIDDEN);
        communityService.updatePost(post);
        return ResponseUtil.success("隐藏成功", null);
    }

    /**
     * 获取所有评论
     */
    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<List<CommunityComment>>> getAllComments(
        @RequestParam(required = false) Long postId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {

        if (postId != null) {
            Page<CommunityComment> commentsPage = communityService.getCommentsByPost(postId, page, size);
            return ResponseUtil.success("获取成功", commentsPage.getContent());
        } else {
            return ResponseUtil.success("获取成功", List.of());
        }
    }

    /**
     * 删除评论（管理员权限）
     */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        communityService.adminDeleteComment(id);
        return ResponseUtil.success("删除成功", null);
    }

    private String normalizeSource(String source) {
        if (source == null || source.isBlank()) {
            return "all";
        }
        String normalized = source.trim().toLowerCase(Locale.ROOT);
        if ("community".equals(normalized) || "forum".equals(normalized) || "all".equals(normalized)) {
            return normalized;
        }
        return "all";
    }
}
