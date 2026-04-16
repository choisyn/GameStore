package com.gamestore.repository;

import com.gamestore.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    long countByStatus(CommunityPost.PostStatus status);
    
    // 按板块和状态分页查询（按置顶和最后评论时间排序）
    @Query("SELECT p FROM CommunityPost p WHERE p.sectionId = :sectionId AND p.status = :status " +
           "ORDER BY p.isPinned DESC, p.lastCommentAt DESC NULLS LAST, p.createdAt DESC")
    Page<CommunityPost> findBySectionIdAndStatus(@Param("sectionId") Long sectionId, 
                                                   @Param("status") CommunityPost.PostStatus status, 
                                                   Pageable pageable);
    
    // 查询精华帖子
    Page<CommunityPost> findByIsEssenceTrueAndStatusOrderByCreatedAtDesc(CommunityPost.PostStatus status, Pageable pageable);
    
    // 按用户查询
    Page<CommunityPost> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, CommunityPost.PostStatus status, Pageable pageable);

    long countByUserIdAndStatus(Long userId, CommunityPost.PostStatus status);
    
    // 搜索帖子
    @Query("SELECT p FROM CommunityPost p WHERE p.status = :status AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CommunityPost> searchPosts(@Param("keyword") String keyword, 
                                     @Param("status") CommunityPost.PostStatus status, 
                                     Pageable pageable);
    
    // 增加浏览次数
    @Modifying
    @Query("UPDATE CommunityPost p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);
    
    // 更新评论数和最后评论时间
    @Modifying
    @Query("UPDATE CommunityPost p SET p.commentCount = p.commentCount + :delta, " +
           "p.lastCommentAt = CURRENT_TIMESTAMP WHERE p.id = :postId")
    void updateCommentCount(@Param("postId") Long postId, @Param("delta") int delta);
}

