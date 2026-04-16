package com.gamestore.controller;

import com.gamestore.dto.request.CreateCommentRequest;
import com.gamestore.dto.request.CreatePostRequest;
import com.gamestore.dto.response.ApiResponse;
import com.gamestore.dto.response.CommentResponse;
import com.gamestore.dto.response.PostResponse;
import com.gamestore.entity.User;
import com.gamestore.entity.UserBehaviorLog;
import com.gamestore.service.AuthService;
import com.gamestore.service.InnovationService;
import com.gamestore.service.PostImageStorageService;
import com.gamestore.service.PostService;
import com.gamestore.util.ResponseUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private AuthService authService;

    @Autowired
    private InnovationService innovationService;

    @Autowired
    private PostImageStorageService postImageStorageService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPosts(
            @RequestParam(required = false) Long gameId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseUtil.success("获取成功", postService.getPosts(gameId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable Long id) {
        return ResponseUtil.success("获取成功", postService.getPostById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @RequestBody CreatePostRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseUtil.error(401, "请先登录");
        }

        PostResponse post = postService.createPost(request, userId);
        innovationService.recordBehavior(
            userId,
            UserBehaviorLog.BehaviorType.CREATE_FORUM_POST,
            request.getGameId(),
            post.getId(),
            request.getCategory()
        );
        return ResponseUtil.success("发布成功", post);
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<String>>> uploadPostImages(
            @RequestParam("files") MultipartFile[] files,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseUtil.error(401, "请先登录");
        }

        return ResponseUtil.success("上传成功", postImageStorageService.storeForumPostImages(files));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseUtil.error(401, "请先登录");
        }

        postService.deletePost(id, userId);
        return ResponseUtil.success("删除成功");
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseUtil.success("获取成功", postService.getComments(postId, page, size));
    }

    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @RequestBody CreateCommentRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseUtil.error(401, "请先登录");
        }

        CommentResponse comment = postService.createComment(request, userId);
        innovationService.recordBehavior(
            userId,
            UserBehaviorLog.BehaviorType.COMMENT_FORUM_POST,
            postService.getPostGameId(request.getPostId()),
            request.getPostId(),
            "论坛评论"
        );
        return ResponseUtil.success("评论成功", comment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseUtil.error(401, "请先登录");
        }

        postService.deleteComment(id, userId);
        return ResponseUtil.success("删除成功");
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseUtil.success("搜索成功", postService.searchPosts(keyword, page, size));
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            return null;
        }

        User user = authService.getUserByToken(token);
        return user != null ? user.getId() : null;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
