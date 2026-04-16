package com.gamestore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gamestore.dto.response.ApiResponse;
import com.gamestore.dto.response.GameWithCategoriesDTO;
import com.gamestore.entity.Game;
import com.gamestore.service.GameService;
import com.gamestore.util.ResponseUtil;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/games")
public class GameController {
    
    private final GameService gameService;
    
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Game>>> getGames(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Game> gamesPage = gameService.getGames(categoryId, keyword, page, size);
        return ResponseUtil.success("获取游戏列表成功", gamesPage.getContent());
    }
    
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<GameWithCategoriesDTO>>> filterGames(
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Page<Game> gamesPage = gameService.filterGames(categoryIds, keyword, page, size);
        List<GameWithCategoriesDTO> dtos = gamesPage.getContent().stream()
                .map(GameWithCategoriesDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseUtil.success("Filtered games fetched successfully", dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Game>> getGame(@PathVariable Long id) {
        Game game = gameService.getGameById(id);
        return ResponseUtil.success("获取游戏详情成功", game);
    }
    
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<Game>>> getFeaturedGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<Game> games = gameService.getFeaturedGames(page, size);
        return ResponseUtil.success("获取推荐游戏成功", games);
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Game>>> searchGames(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Game> gamesPage = gameService.searchGames(keyword, page, size);
        return ResponseUtil.success("搜索游戏成功", gamesPage.getContent());
    }
    
    /**
     * 获取游戏(包含分类信息)
     */
    @GetMapping("/{id}/with-categories")
    public ResponseEntity<ApiResponse<GameWithCategoriesDTO>> getGameWithCategories(@PathVariable Long id) {
        Game game = gameService.getGameById(id);
        GameWithCategoriesDTO dto = GameWithCategoriesDTO.fromEntity(game);
        return ResponseUtil.success("获取游戏详情成功", dto);
    }
    
    /**
     * 创建游戏(支持多分类)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<GameWithCategoriesDTO>> createGame(@RequestBody Map<String, Object> request) {
        Game game = new Game();
        populateGameFromRequest(game, request);
        
        // 检查游戏名称是否已存在
        if (game.getName() != null && !game.getName().trim().isEmpty()) {
            Game existingGame = gameService.getGameByName(game.getName());
            if (existingGame != null) {
                return ResponseUtil.error("游戏名称已存在: " + game.getName());
            }
        }
        
        // 处理categoryIds,将Integer转换为Long
        List<Long> categoryIds = null;
        Object categoryIdsObj = request.get("categoryIds");
        if (categoryIdsObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<?> rawList = (List<?>) categoryIdsObj;
            categoryIds = rawList.stream()
                .map(obj -> {
                    if (obj instanceof Number) {
                        return ((Number) obj).longValue();
                    }
                    return null;
                })
                .filter(categoryId -> categoryId != null)
                .collect(java.util.stream.Collectors.toList());
        }
        
        Game savedGame = gameService.createGame(game, categoryIds);
        GameWithCategoriesDTO dto = GameWithCategoriesDTO.fromEntity(savedGame);
        return ResponseUtil.success("创建游戏成功", dto);
    }
    
    /**
     * 更新游戏(支持多分类)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GameWithCategoriesDTO>> updateGame(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> request) {
        
        try {
            System.out.println("=== 更新游戏请求 ===");
            System.out.println("游戏ID: " + id);
            System.out.println("请求数据: " + request);
            
            Game gameDetails = new Game();
            populateGameFromRequest(gameDetails, request);
            
            // 检查游戏名称是否与其他游戏重复
            if (gameDetails.getName() != null && !gameDetails.getName().trim().isEmpty()) {
                Game existingGame = gameService.getGameByNameExcludingId(gameDetails.getName(), id);
                if (existingGame != null) {
                    return ResponseUtil.error("游戏名称已被其他游戏使用: " + gameDetails.getName());
                }
            }
            
            // 处理categoryIds,将Integer转换为Long
            List<Long> categoryIds = null;
            Object categoryIdsObj = request.get("categoryIds");
            if (categoryIdsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<?> rawList = (List<?>) categoryIdsObj;
                categoryIds = rawList.stream()
                    .map(obj -> {
                        if (obj instanceof Number) {
                            return ((Number) obj).longValue();
                        }
                        return null;
                    })
                    .filter(categoryId -> categoryId != null)
                    .collect(java.util.stream.Collectors.toList());
            }
            System.out.println("分类IDs: " + categoryIds);
            
            Game updatedGame = gameService.updateGame(id, gameDetails, categoryIds);
            GameWithCategoriesDTO dto = GameWithCategoriesDTO.fromEntity(updatedGame);
            
            System.out.println("=== 更新游戏成功 ===");
            return ResponseUtil.success("更新游戏成功", dto);
        } catch (Exception e) {
            System.err.println("=== 更新游戏失败 ===");
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * 删除游戏
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseUtil.success("删除游戏成功");
    }
    
    /**
     * 根据分类查询游戏
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<GameWithCategoriesDTO>>> getGamesByCategory(@PathVariable Long categoryId) {
        List<Game> games = gameService.getGamesByCategory(categoryId);
        List<GameWithCategoriesDTO> dtos = games.stream()
                .map(GameWithCategoriesDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseUtil.success("获取分类游戏成功", dtos);
    }
    
    /**
     * 辅助方法: 从请求填充Game对象
     */
    private void populateGameFromRequest(Game game, Map<String, Object> request) {
        if (request.containsKey("name")) {
            game.setName((String) request.get("name"));
        }
        if (request.containsKey("description")) {
            game.setDescription((String) request.get("description"));
        }
        if (request.containsKey("developer")) {
            game.setDeveloper((String) request.get("developer"));
        }
        if (request.containsKey("publisher")) {
            game.setPublisher((String) request.get("publisher"));
        }
        if (request.containsKey("price")) {
            game.setPrice(new java.math.BigDecimal(request.get("price").toString()));
        }
        if (request.containsKey("discountPrice")) {
            Object discountPrice = request.get("discountPrice");
            if (discountPrice != null) {
                game.setDiscountPrice(new java.math.BigDecimal(discountPrice.toString()));
            }
        }
        if (request.containsKey("imageUrl")) {
            game.setImageUrl((String) request.get("imageUrl"));
        }
        if (request.containsKey("gallery")) {
            game.setGallery((String) request.get("gallery"));
        }
        if (request.containsKey("systemRequirements")) {
            game.setSystemRequirements((String) request.get("systemRequirements"));
        }
        if (request.containsKey("tags")) {
            game.setTags((String) request.get("tags"));
        }
        if (request.containsKey("isFeatured")) {
            game.setIsFeatured((Boolean) request.get("isFeatured"));
        }
        if (request.containsKey("status")) {
            game.setStatus(Game.GameStatus.valueOf((String) request.get("status")));
        }
        if (request.containsKey("releaseDate")) {
            Object releaseDate = request.get("releaseDate");
            if (releaseDate != null && !releaseDate.toString().isEmpty()) {
                game.setReleaseDate(java.time.LocalDate.parse(releaseDate.toString()));
            }
        }
    }
}
