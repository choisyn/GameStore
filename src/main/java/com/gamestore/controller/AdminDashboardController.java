package com.gamestore.controller;

import com.gamestore.entity.Comment;
import com.gamestore.entity.CommunityPost;
import com.gamestore.entity.Game;
import com.gamestore.entity.Post;
import com.gamestore.entity.User;
import com.gamestore.repository.CommentRepository;
import com.gamestore.repository.CommunityPostRepository;
import com.gamestore.repository.GameRepository;
import com.gamestore.repository.PostRepository;
import com.gamestore.repository.UserRepository;
import com.gamestore.service.StoreService;
import com.gamestore.dto.response.ApiResponse;
import com.gamestore.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommunityPostRepository communityPostRepository;
    private final StoreService storeService;

    public AdminDashboardController(
            UserRepository userRepository,
            GameRepository gameRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            CommunityPostRepository communityPostRepository,
            StoreService storeService) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.communityPostRepository = communityPostRepository;
        this.storeService = storeService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userRepository.count());
        data.put("activeUsers", userRepository.countByStatus(User.UserStatus.ACTIVE));
        data.put("adminUsers", userRepository.countByRole(User.UserRole.ADMIN));
        data.put("totalGames", gameRepository.count());
        data.put("activeGames", gameRepository.countByStatus(Game.GameStatus.ACTIVE));
        data.put("forumPosts", postRepository.countByStatus(Post.PostStatus.PUBLISHED));
        data.put("forumComments", commentRepository.countByStatus(Comment.CommentStatus.PUBLISHED));
        data.put("communityPosts", communityPostRepository.countByStatus(CommunityPost.PostStatus.PUBLISHED));
        data.put("totalOrders", storeService.countTotalOrders());
        data.put("totalSales", storeService.getTotalPaidAmount());
        return ResponseUtil.success("获取仪表盘概览成功", data);
    }
}
