package com.gamestore.controller;

import com.gamestore.entity.Game;
import com.gamestore.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameDetailController {
    
    private final GameService gameService;
    
    public GameDetailController(GameService gameService) {
        this.gameService = gameService;
    }
    
    @GetMapping("/game/{id}")
    public String gameDetail(@PathVariable Long id, Model model) {
        Game game = gameService.getGameById(id);
        model.addAttribute("game", game);
        return "game-detail";
    }
}
