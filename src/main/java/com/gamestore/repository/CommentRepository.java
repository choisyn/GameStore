package com.gamestore.repository;

import com.gamestore.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论Repository
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countByStatus(Comment.CommentStatus status);
    
    /**
     * 根据帖子ID查询评论，按时间排序
     */
    Page<Comment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, Comment.CommentStatus status, Pageable pageable);
    
    /**
     * 查询帖子的顶级评论（没有父评论）
     */
    Page<Comment> findByPostIdAndParentIdIsNullAndStatusOrderByCreatedAtAsc(Long postId, Comment.CommentStatus status, Pageable pageable);
    
    /**
     * 查询子评论
     */
    List<Comment> findByParentIdAndStatusOrderByCreatedAtAsc(Long parentId, Comment.CommentStatus status);
    
    /**
     * 统计帖子的评论数
     */
    long countByPostIdAndStatus(Long postId, Comment.CommentStatus status);
    
    /**
     * 根据用户ID查询评论
     */
    Page<Comment> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Comment.CommentStatus status, Pageable pageable);

    long countByUserIdAndStatus(Long userId, Comment.CommentStatus status);
    
    /**
     * 增加点赞数
     */
    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :id")
    void incrementLikeCount(@Param("id") Long id);
    
    /**
     * 减少点赞数
     */
    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = GREATEST(c.likeCount - 1, 0) WHERE c.id = :id")
    void decrementLikeCount(@Param("id") Long id);
}

