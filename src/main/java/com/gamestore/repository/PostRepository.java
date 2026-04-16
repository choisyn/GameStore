package com.gamestore.repository;

import com.gamestore.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 帖子Repository
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    long countByStatus(Post.PostStatus status);

    long countByUserIdAndStatus(Long userId, Post.PostStatus status);

    long countByGameIdAndStatus(Long gameId, Post.PostStatus status);
    
    /**
     * 查询所有已发布的帖子，按最新活跃时间排序（置顶优先）
     */
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "ORDER BY p.isPinned DESC, " +
           "CASE WHEN p.lastCommentAt IS NULL THEN p.createdAt ELSE p.lastCommentAt END DESC")
    Page<Post> findAllByStatusOrderByActivity(@Param("status") Post.PostStatus status, Pageable pageable);
    
    /**
     * 根据游戏ID查询帖子，按最新活跃时间排序
     */
    @Query("SELECT p FROM Post p WHERE p.gameId = :gameId AND p.status = :status " +
           "ORDER BY p.isPinned DESC, " +
           "CASE WHEN p.lastCommentAt IS NULL THEN p.createdAt ELSE p.lastCommentAt END DESC")
    Page<Post> findByGameIdAndStatusOrderByActivity(@Param("gameId") Long gameId, 
                                                      @Param("status") Post.PostStatus status, 
                                                      Pageable pageable);
    
    /**
     * 根据用户ID查询帖子
     */
    Page<Post> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Post.PostStatus status, Pageable pageable);
    
    /**
     * 搜索帖子（标题和内容）
     */
    @Query("SELECT p FROM Post p WHERE p.status = :status AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY p.isPinned DESC, " +
           "CASE WHEN p.lastCommentAt IS NULL THEN p.createdAt ELSE p.lastCommentAt END DESC")
    Page<Post> searchPosts(@Param("keyword") String keyword, 
                           @Param("status") Post.PostStatus status, 
                           Pageable pageable);
    
    /**
     * 增加浏览次数
     */
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);
    
    /**
     * 增加评论数
     */
    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + 1, p.lastCommentAt = :commentTime WHERE p.id = :id")
    void incrementCommentCount(@Param("id") Long id, @Param("commentTime") java.time.LocalDateTime commentTime);
    
    /**
     * 减少评论数
     */
    @Modifying
    @Query("UPDATE Post p SET p.commentCount = GREATEST(p.commentCount - 1, 0) WHERE p.id = :id")
    void decrementCommentCount(@Param("id") Long id);
    
    /**
     * 增加点赞数
     */
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :id")
    void incrementLikeCount(@Param("id") Long id);
    
    /**
     * 减少点赞数
     */
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = GREATEST(p.likeCount - 1, 0) WHERE p.id = :id")
    void decrementLikeCount(@Param("id") Long id);
}

