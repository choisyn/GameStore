package com.gamestore.repository;

import com.gamestore.entity.CommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    long countByStatus(CommunityComment.CommentStatus status);
    
    // 按帖子查询评论（分页）
    Page<CommunityComment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, CommunityComment.CommentStatus status, Pageable pageable);
    
    // 按帖子查询评论（不分页）
    List<CommunityComment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, CommunityComment.CommentStatus status);
    
    // 按用户查询评论
    Page<CommunityComment> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, CommunityComment.CommentStatus status, Pageable pageable);

    long countByUserIdAndStatus(Long userId, CommunityComment.CommentStatus status);
    
    // 查询某评论的回复
    List<CommunityComment> findByParentIdAndStatusOrderByCreatedAtAsc(Long parentId, CommunityComment.CommentStatus status);
    
    // 统计帖子评论数
    long countByPostIdAndStatus(Long postId, CommunityComment.CommentStatus status);
}

