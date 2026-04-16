package com.gamestore.repository;

import com.gamestore.entity.GameGuide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameGuideRepository extends JpaRepository<GameGuide, Long> {

    @Query("""
        SELECT gg FROM GameGuide gg
        WHERE gg.status = :status
          AND (:gameId IS NULL OR gg.gameId = :gameId)
          AND (:difficulty IS NULL OR gg.difficulty = :difficulty)
          AND (:featuredOnly = FALSE OR gg.isFeatured = TRUE)
          AND (
              :keyword IS NULL
              OR LOWER(gg.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(COALESCE(gg.summary, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(COALESCE(gg.tags, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(gg.game.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY gg.isFeatured DESC, COALESCE(gg.publishedAt, gg.createdAt) DESC
        """)
    Page<GameGuide> searchGuides(
        @Param("gameId") Long gameId,
        @Param("difficulty") GameGuide.GuideDifficulty difficulty,
        @Param("featuredOnly") boolean featuredOnly,
        @Param("keyword") String keyword,
        @Param("status") GameGuide.GuideStatus status,
        Pageable pageable
    );

    @Query("""
        SELECT gg FROM GameGuide gg
        WHERE gg.status = :status
          AND gg.isFeatured = TRUE
        ORDER BY COALESCE(gg.publishedAt, gg.createdAt) DESC, gg.viewCount DESC
        """)
    Page<GameGuide> findFeaturedGuides(
        @Param("status") GameGuide.GuideStatus status,
        Pageable pageable
    );

    long countByStatus(GameGuide.GuideStatus status);

    Optional<GameGuide> findByIdAndStatus(Long id, GameGuide.GuideStatus status);

    @Modifying
    @Query("UPDATE GameGuide gg SET gg.viewCount = COALESCE(gg.viewCount, 0) + 1 WHERE gg.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
