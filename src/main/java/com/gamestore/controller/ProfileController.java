package com.gamestore.controller;

import com.gamestore.dto.request.ChangePasswordRequest;
import com.gamestore.dto.request.UpdateProfileRequest;
import com.gamestore.dto.response.ApiResponse;
import com.gamestore.dto.response.PointTransactionResponse;
import com.gamestore.dto.response.ProfileSummaryResponse;
import com.gamestore.entity.Comment;
import com.gamestore.entity.CommunityComment;
import com.gamestore.entity.CommunityPost;
import com.gamestore.entity.Post;
import com.gamestore.entity.User;
import com.gamestore.repository.CommentRepository;
import com.gamestore.repository.CommunityCommentRepository;
import com.gamestore.repository.CommunityPostRepository;
import com.gamestore.repository.PostRepository;
import com.gamestore.service.CurrentUserService;
import com.gamestore.service.StoreService;
import com.gamestore.service.UserService;
import com.gamestore.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final CurrentUserService currentUserService;
    private final UserService userService;
    private final StoreService storeService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;

    public ProfileController(
            CurrentUserService currentUserService,
            UserService userService,
            StoreService storeService,
            PostRepository postRepository,
            CommentRepository commentRepository,
            CommunityPostRepository communityPostRepository,
            CommunityCommentRepository communityCommentRepository) {
        this.currentUserService = currentUserService;
        this.userService = userService;
        this.storeService = storeService;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.communityPostRepository = communityPostRepository;
        this.communityCommentRepository = communityCommentRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ProfileSummaryResponse>> getSummary(HttpServletRequest request) {
        User user = currentUserService.getCurrentUser(request);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }

        List<PointTransactionResponse> recentPoints = storeService.getRecentPointTransactions(user.getId());
        ProfileSummaryResponse response = new ProfileSummaryResponse(
            user.getUsername(),
            user.getEmail(),
            user.getAvatar(),
            user.getRole().name(),
            user.getPoints(),
            postRepository.countByUserIdAndStatus(user.getId(), Post.PostStatus.PUBLISHED),
            commentRepository.countByUserIdAndStatus(user.getId(), Comment.CommentStatus.PUBLISHED),
            communityPostRepository.countByUserIdAndStatus(user.getId(), CommunityPost.PostStatus.PUBLISHED),
            communityCommentRepository.countByUserIdAndStatus(user.getId(), CommunityComment.CommentStatus.PUBLISHED),
            storeService.countOrders(user.getId()),
            storeService.countLibraryGames(user.getId()),
            recentPoints
        );

        return ResponseUtil.success("获取个人中心信息成功", response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateProfile(
            @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }

        User updatedUser = userService.updateProfile(user.getId(), request);
        Map<String, Object> data = new HashMap<>();
        data.put("username", updatedUser.getUsername());
        data.put("email", updatedUser.getEmail());
        data.put("avatar", updatedUser.getAvatar());
        return ResponseUtil.success("更新个人信息成功", data);
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        userService.changePassword(user, request);
        return ResponseUtil.success("密码修改成功", null);
    }
}
