package com.gamestore.service;

import com.gamestore.dto.request.CreateCommentRequest;
import com.gamestore.dto.request.CreatePostRequest;
import com.gamestore.dto.response.CommentResponse;
import com.gamestore.dto.response.PostResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamestore.entity.*;
import com.gamestore.exception.CustomException;
import com.gamestore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 帖子服务类
 */
@Service
public class PostService {

    private static final int MAX_IMAGE_COUNT = 9;
    private static final Pattern SAFE_IMAGE_URL_PATTERN =
            Pattern.compile("^(https?://|/uploads/)[^\\s\"'<>]+$", Pattern.CASE_INSENSITIVE);
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 获取帖子列表（所有游戏或指定游戏）
     */
    public Page<PostResponse> getPosts(Long gameId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts;
        
        if (gameId != null && gameId > 0) {
            // 按游戏筛选
            posts = postRepository.findByGameIdAndStatusOrderByActivity(gameId, Post.PostStatus.PUBLISHED, pageable);
        } else {
            // 所有游戏
            posts = postRepository.findAllByStatusOrderByActivity(Post.PostStatus.PUBLISHED, pageable);
        }
        
        return posts.map(this::convertToPostResponse);
    }
    
    /**
     * 根据ID获取帖子详情
     */
    @Transactional
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException("帖子不存在"));
        
        if (post.getStatus() == Post.PostStatus.DELETED) {
            throw new CustomException("帖子已被删除");
        }
        
        // 增加浏览次数
        postRepository.incrementViewCount(id);
        post.setViewCount(post.getViewCount() + 1);
        
        return convertToPostResponse(post);
    }
    
    /**
     * 创建帖子
     */
    @Transactional
    public PostResponse createPost(CreatePostRequest request, Long userId) {
        // 验证输入
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new CustomException("标题不能为空");
        }
        if (request.getTitle().length() > 200) {
            throw new CustomException("标题长度不能超过200个字符");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new CustomException("内容不能为空");
        }
        
        // 验证用户存在
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("用户不存在"));
        
        // 如果指定了游戏ID，验证游戏存在
        if (request.getGameId() != null) {
            gameRepository.findById(request.getGameId())
                    .orElseThrow(() -> new CustomException("游戏不存在"));
        }
        
        Post post = new Post();
        post.setTitle(request.getTitle().trim());
        post.setContent(request.getContent().trim());
        post.setImages(normalizeImages(request.getImages()));
        post.setUserId(userId);
        post.setGameId(request.getGameId());
        post.setCategory(request.getCategory());
        post.setStatus(Post.PostStatus.PUBLISHED);
        
        Post savedPost = postRepository.save(post);
        return convertToPostResponse(savedPost);
    }
    
    /**
     * 获取帖子的评论列表
     */
    public Page<CommentResponse> getComments(Long postId, int page, int size) {
        // 验证帖子存在
        postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("帖子不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByPostIdAndParentIdIsNullAndStatusOrderByCreatedAtAsc(
                postId, Comment.CommentStatus.PUBLISHED, pageable);
        
        return comments.map(this::convertToCommentResponse);
    }
    
    /**
     * 创建评论
     */
    @Transactional
    public CommentResponse createComment(CreateCommentRequest request, Long userId) {
        // 验证输入
        if (request.getPostId() == null) {
            throw new CustomException("帖子ID不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new CustomException("评论内容不能为空");
        }
        
        // 验证用户存在
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("用户不存在"));
        
        // 验证帖子存在
        postRepository.findById(request.getPostId())
                .orElseThrow(() -> new CustomException("帖子不存在"));
        
        // 如果是回复评论，验证父评论存在
        if (request.getParentId() != null) {
            commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomException("父评论不存在"));
        }
        
        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setUserId(userId);
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent().trim());
        comment.setStatus(Comment.CommentStatus.PUBLISHED);
        
        Comment savedComment = commentRepository.save(comment);
        
        // 更新帖子的评论数和最后评论时间
        postRepository.incrementCommentCount(request.getPostId(), LocalDateTime.now());
        
        return convertToCommentResponse(savedComment);
    }
    
    /**
     * 删除帖子
     */
    @Transactional
    public void deletePost(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException("帖子不存在"));
        
        // 只有作者可以删除
        if (!post.getUserId().equals(userId)) {
            throw new CustomException("无权删除此帖子");
        }
        
        post.setStatus(Post.PostStatus.DELETED);
        postRepository.save(post);
    }
    
    /**
     * 删除评论
     */
    @Transactional
    public void deleteComment(Long id, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException("评论不存在"));
        
        // 只有作者可以删除
        if (!comment.getUserId().equals(userId)) {
            throw new CustomException("无权删除此评论");
        }
        
        comment.setStatus(Comment.CommentStatus.DELETED);
        commentRepository.save(comment);
        
        // 减少帖子的评论数
        postRepository.decrementCommentCount(comment.getPostId());
    }
    
    /**
     * 搜索帖子
     */
    public Page<PostResponse> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.searchPosts(keyword, Post.PostStatus.PUBLISHED, pageable);
        return posts.map(this::convertToPostResponse);
    }

    public List<PostResponse> getAdminPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
            .filter(post -> post.getStatus() != Post.PostStatus.DELETED)
            .map(this::convertToPostResponse)
            .collect(Collectors.toList());
    }

    public PostResponse getAdminPostById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new CustomException("帖子不存在"));
        return convertToPostResponse(post);
    }

    @Transactional
    public PostResponse setAdminPinned(Long id, boolean isPinned) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new CustomException("帖子不存在"));
        post.setIsPinned(isPinned);
        return convertToPostResponse(postRepository.save(post));
    }

    @Transactional
    public PostResponse setAdminFeatured(Long id, boolean isFeatured) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new CustomException("帖子不存在"));
        post.setIsFeatured(isFeatured);
        return convertToPostResponse(postRepository.save(post));
    }

    @Transactional
    public void adminDeletePost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new CustomException("帖子不存在"));
        post.setStatus(Post.PostStatus.DELETED);
        postRepository.save(post);
    }

    public Long getPostGameId(Long postId) {
        if (postId == null) {
            return null;
        }
        return postRepository.findById(postId)
            .map(Post::getGameId)
            .orElse(null);
    }
    
    /**
     * 转换Post实体为PostResponse
     */
    private PostResponse convertToPostResponse(Post post) {
        PostResponse response = PostResponse.fromEntity(post);
        
        // 添加用户信息
        userRepository.findById(post.getUserId()).ifPresent(user -> {
            response.setUsername(user.getUsername());
            response.setUserAvatar(user.getAvatar());
        });
        
        // 添加游戏信息
        if (post.getGameId() != null) {
            gameRepository.findById(post.getGameId()).ifPresent(game -> {
                response.setGameName(game.getName());
            });
        }
        
        return response;
    }
    
    /**
     * 转换Comment实体为CommentResponse
     */
    private CommentResponse convertToCommentResponse(Comment comment) {
        CommentResponse response = CommentResponse.fromEntity(comment);
        
        // 添加用户信息
        userRepository.findById(comment.getUserId()).ifPresent(user -> {
            response.setUsername(user.getUsername());
            response.setUserAvatar(user.getAvatar());
        });
        
        // 加载回复
        List<Comment> replies = commentRepository.findByParentIdAndStatusOrderByCreatedAtAsc(
                comment.getId(), Comment.CommentStatus.PUBLISHED);
        
        if (!replies.isEmpty()) {
            List<CommentResponse> replyResponses = replies.stream()
                    .map(this::convertToCommentResponse)
                    .collect(Collectors.toList());
            response.setReplies(replyResponses);
        }
        
        return response;
    }

    private String normalizeImages(String images) {
        if (images == null || images.isBlank()) {
            return null;
        }

        try {
            List<String> normalizedImages = objectMapper.readValue(images, new TypeReference<List<String>>() {})
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(url -> !url.isEmpty())
                    .filter(this::isSafeImageUrl)
                    .distinct()
                    .limit(MAX_IMAGE_COUNT)
                    .collect(Collectors.toList());

            if (normalizedImages.isEmpty()) {
                return null;
            }

            return objectMapper.writeValueAsString(normalizedImages);
        } catch (JsonProcessingException e) {
            throw new CustomException("图片数据格式不正确");
        }
    }

    private boolean isSafeImageUrl(String url) {
        return SAFE_IMAGE_URL_PATTERN.matcher(url).matches();
    }
}

