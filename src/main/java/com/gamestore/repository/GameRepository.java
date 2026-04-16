package com.gamestore.repository;

import com.gamestore.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    long countByStatus(Game.GameStatus status);
    
    // 查找精选游戏（无分页）
    List<Game> findByIsFeaturedTrueAndStatus(Game.GameStatus status);
    
    // 查找精选游戏（分页）
    Page<Game> findByIsFeaturedTrueAndStatus(Game.GameStatus status, Pageable pageable);
    
    // 按分类查询游戏
    Page<Game> findByCategoryIdAndStatus(Long categoryId, Game.GameStatus status, Pageable pageable);
    
    // 按状态查询游戏
    Page<Game> findByStatus(Game.GameStatus status, Pageable pageable);

    List<Game> findByStatusOrderByCreatedAtDesc(Game.GameStatus status);
    
    // 搜索游戏（按名称或描述）
    @Query("SELECT g FROM Game g WHERE g.status = :status AND (LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Game> searchGames(@Param("keyword") String keyword, @Param("status") Game.GameStatus status, Pageable pageable);

    @Query("""
        SELECT g FROM Game g
        WHERE g.status = :status
          AND (
              LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(COALESCE(g.tags, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(COALESCE(g.developer, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(COALESCE(g.publisher, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        """)
    Page<Game> searchGamesExpanded(@Param("keyword") String keyword, @Param("status") Game.GameStatus status, Pageable pageable);
    
    // 按分类和关键词搜索
    @Query("SELECT g FROM Game g WHERE g.categoryId = :categoryId AND g.status = :status AND (LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Game> searchGamesByCategory(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, @Param("status") Game.GameStatus status, Pageable pageable);

    @Query(
        value = """
            SELECT DISTINCT g FROM Game g
            JOIN g.categories c
            WHERE c.id IN :categoryIds
              AND g.status = :status
            """,
        countQuery = """
            SELECT COUNT(DISTINCT g) FROM Game g
            JOIN g.categories c
            WHERE c.id IN :categoryIds
              AND g.status = :status
            """
    )
    Page<Game> findByCategoryIdsAndStatus(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("status") Game.GameStatus status,
            Pageable pageable);

    @Query(
        value = """
            SELECT DISTINCT g FROM Game g
            JOIN g.categories c
            WHERE c.id IN :categoryIds
              AND g.status = :status
              AND (
                  LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(COALESCE(g.tags, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(COALESCE(g.developer, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(COALESCE(g.publisher, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """,
        countQuery = """
            SELECT COUNT(DISTINCT g) FROM Game g
            JOIN g.categories c
            WHERE c.id IN :categoryIds
              AND g.status = :status
              AND (
                  LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(COALESCE(g.tags, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(COALESCE(g.developer, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(COALESCE(g.publisher, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """
    )
    Page<Game> searchGamesByCategoryIds(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("keyword") String keyword,
            @Param("status") Game.GameStatus status,
            Pageable pageable);

    @Query("""
        SELECT DISTINCT g FROM Game g
        LEFT JOIN g.categories c
        WHERE g.status = :status
          AND (g.categoryId = :categoryId OR c.id = :categoryId)
        ORDER BY g.createdAt DESC
        """)
    List<Game> findSearchCandidatesByCategoryId(
            @Param("categoryId") Long categoryId,
            @Param("status") Game.GameStatus status);

    @Query("""
        SELECT DISTINCT g FROM Game g
        LEFT JOIN g.categories c
        WHERE g.status = :status
          AND (g.categoryId IN :categoryIds OR c.id IN :categoryIds)
        ORDER BY g.createdAt DESC
        """)
    List<Game> findSearchCandidatesByCategoryIds(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("status") Game.GameStatus status);
    
    // 根据分类查询游戏(支持多对多关系)
    @Query("SELECT DISTINCT g FROM Game g JOIN g.categories c WHERE c.id = :categoryId AND g.status = 'ACTIVE'")
    List<Game> findGamesByCategory(@Param("categoryId") Long categoryId);
    
    // 根据分类查询游戏(分页,支持多对多关系)
    @Query("SELECT DISTINCT g FROM Game g JOIN g.categories c WHERE c.id = :categoryId AND g.status = :status")
    Page<Game> findGamesByCategoryPaged(@Param("categoryId") Long categoryId, @Param("status") Game.GameStatus status, Pageable pageable);
    
    // 根据游戏名称查找(用于检查重复)
    Game findByName(String name);
    
    // 根据名称查找(排除指定ID,用于更新时检查)
    Game findByNameAndIdNot(String name, Long id);
}
