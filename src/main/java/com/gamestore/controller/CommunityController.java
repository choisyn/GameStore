package com.gamestore.controller;

import com.gamestore.dto.request.CreateCommentRequest;
import com.gamestore.dto.request.CreatePostRequest;
import com.gamestore.dto.response.ApiResponse;
import com.gamestore.entity.CommunityComment;
import com.gamestore.entity.CommunityPost;
import com.gamestore.entity.CommunitySection;
import com.gamestore.entity.User;
import com.gamestore.entity.UserBehaviorLog;
import com.gamestore.service.CommunityService;
import com.gamestore.service.CurrentUserService;
import com.gamestore.service.InnovationService;
import com.gamestore.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;
    private final CurrentUserService currentUserService;
    private final InnovationService innovationService;

    public CommunityController(
            CommunityService communityService,
            CurrentUserService currentUserService,
            InnovationService innovationService) {
        this.communityService = communityService;
        this.currentUserService = currentUserService;
        this.innovationService = innovationService;
    }

    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<List<CommunitySection>>> getAllSections() {
        return ResponseUtil.success("获取板块列表成功", communityService.getAllSections());
    }

    @GetMapping("/sections/{id}")
    public ResponseEntity<ApiResponse<CommunitySection>> getSectionById(@PathVariable Long id) {
        return ResponseUtil.success("获取板块详情成功", communityService.getSectionById(id));
    }

    @GetMapping("/sections/{sectionId}/posts")
    public ResponseEntity<ApiResponse<List<CommunityPost>>> getPostsBySection(
            @PathVariable Long sectionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CommunityPost> postsPage = communityService.getPostsBySection(sectionId, page, size);
        return ResponseUtil.success("获取帖子列表成功", postsPage.getContent());
    }

    @GetMapping("/posts/essence")
    public ResponseEntity<ApiResponse<List<CommunityPost>>> getEssencePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CommunityPost> postsPage = communityService.getEssencePosts(page, size);
        return ResponseUtil.success("获取精华帖子成功", postsPage.getContent());
    }

    @GetMapping("/posts/search")
    public ResponseEntity<ApiResponse<List<CommunityPost>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CommunityPost> postsPage = communityService.searchPosts(keyword, page, size);
        return ResponseUtil.success("搜索成功", postsPage.getContent());
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<CommunityPost>> getPostById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        CommunityPost post = communityService.getPostById(id);
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user != null) {
            innovationService.recordBehavior(
                user.getId(),
                UserBehaviorLog.BehaviorType.VIEW_COMMUNITY_POST,
                null,
                post.getId(),
                post.getTitle()
            );
        }
        boolean useTrackedCommunityView = true;
        if (useTrackedCommunityView) {
            return ResponseUtil.success("获取帖子详情成功", post);
        }
        return ResponseUtil.success("获取帖子详情成功", communityService.getPostById(id));
    }

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<CommunityPost>> createPost(
            @RequestBody CreatePostRequest request,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }

        CommunityPost post = communityService.createPost(request, user.getId());
        innovationService.recordBehavior(
            user.getId(),
            UserBehaviorLog.BehaviorType.CREATE_COMMUNITY_POST,
            null,
            post.getId(),
            request.getTitle()
        );
        return ResponseUtil.success("发帖成功", post);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        communityService.deletePost(id, user.getId());
        return ResponseUtil.success("删除成功", null);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommunityComment>>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<CommunityComment> commentsPage = communityService.getCommentsByPost(postId, page, size);
        return ResponseUtil.success("获取评论列表成功", commentsPage.getContent());
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<ApiResponse<List<CommunityComment>>> getRepliesByComment(@PathVariable Long commentId) {
        return ResponseUtil.success("获取回复列表成功", communityService.getRepliesByComment(commentId));
    }

    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommunityComment>> createComment(
            @RequestBody CreateCommentRequest request,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }

        CommunityComment comment = communityService.createComment(request, user.getId());
        innovationService.recordBehavior(
            user.getId(),
            UserBehaviorLog.BehaviorType.COMMENT_COMMUNITY_POST,
            null,
            request.getPostId(),
            "社区评论"
        );
        return ResponseUtil.success("评论成功", comment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        communityService.deleteComment(id, user.getId());
        return ResponseUtil.success("删除成功", null);
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<ApiResponse<List<CommunityPost>>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CommunityPost> postsPage = communityService.getUserPosts(userId, page, size);
        return ResponseUtil.success("获取用户帖子成功", postsPage.getContent());
    }

    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<ApiResponse<List<CommunityComment>>> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CommunityComment> commentsPage = communityService.getUserComments(userId, page, size);
        return ResponseUtil.success("获取用户评论成功", commentsPage.getContent());
    }
}
