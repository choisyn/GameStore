package com.gamestore.controller;

import com.gamestore.dto.response.ApiResponse;
import com.gamestore.entity.Game;
import com.gamestore.service.GameService;
import com.gamestore.util.ResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台游戏管理API
 */
@RestController
@RequestMapping("/api/admin/games")
public class AdminGameController {

    private final GameService gameService;

    public AdminGameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * 获取所有游戏（支持搜索）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Game>>> getAllGames(
            @RequestParam(required = false) String keyword) {
        
        List<Game> games;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 搜索游戏 - 使用分页但获取大量数据以便前端分页
            Page<Game> gamesPage = gameService.searchGames(keyword, 0, 1000);
            games = gamesPage.getContent();
        } else {
            // 获取所有游戏
            games = gameService.getAllGames();
        }
        
        return ResponseUtil.success("获取成功", games);
    }

    /**
     * 获取游戏详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Game>> getGame(@PathVariable Long id) {
        Game game = gameService.getGameById(id);
        return ResponseUtil.success("获取成功", game);
    }

    /**
     * 创建游戏
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Game>> createGame(@RequestBody Game game) {
        Game savedGame = gameService.saveGame(game);
        return ResponseUtil.success("创建成功", savedGame);
    }

    /**
     * 更新游戏
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Game>> updateGame(
            @PathVariable Long id,
            @RequestBody Game game) {
        
        game.setId(id);
        Game updatedGame = gameService.saveGame(game);
        return ResponseUtil.success("更新成功", updatedGame);
    }

    /**
     * 删除游戏
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGame(@PathVariable Long id) {
        Game game = gameService.getGameById(id);
        game.setStatus(Game.GameStatus.INACTIVE);
        gameService.saveGame(game);
        return ResponseUtil.success("删除成功", null);
    }

    /**
     * 批量删除游戏
     */
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchDeleteGames(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            Game game = gameService.getGameById(id);
            game.setStatus(Game.GameStatus.INACTIVE);
            gameService.saveGame(game);
        }
        return ResponseUtil.success("批量删除成功", null);
    }
}

