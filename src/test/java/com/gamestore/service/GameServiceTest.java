package com.gamestore.service;

import com.gamestore.entity.Game;
import com.gamestore.repository.CategoryRepository;
import com.gamestore.repository.GameRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void filterGamesUsesTrimmedKeywordForExpandedSearch() {
        when(gameRepository.findByStatusOrderByCreatedAtDesc(eq(Game.GameStatus.ACTIVE)))
                .thenReturn(List.of(createGame("Marvel's Spider-Man 2", "动作冒险", 1)));

        gameService.filterGames(null, "  Spider-Man  ", 0, 12);

        verify(gameRepository).findByStatusOrderByCreatedAtDesc(eq(Game.GameStatus.ACTIVE));
        verify(gameRepository, never()).findByStatus(any(), any(Pageable.class));
    }

    @Test
    void filterGamesUsesCategorySearchWhenKeywordAndCategoriesArePresent() {
        when(gameRepository.findSearchCandidatesByCategoryIds(
                eq(List.of(2L, 5L)),
                eq(Game.GameStatus.ACTIVE)
        )).thenReturn(List.of(createGame("艾尔登法环", "RPG", 2)));

        gameService.filterGames(List.of(2L, 5L, 2L), "  RPG  ", 1, 12);

        verify(gameRepository).findSearchCandidatesByCategoryIds(
                eq(List.of(2L, 5L)),
                eq(Game.GameStatus.ACTIVE)
        );
    }

    @Test
    void searchGamesUsesTrimmedKeyword() {
        when(gameRepository.findByStatusOrderByCreatedAtDesc(eq(Game.GameStatus.ACTIVE)))
                .thenReturn(List.of(createGame("赛博朋克2077", "开放世界", 3)));

        gameService.searchGames(" 2077 ", 0, 10);

        verify(gameRepository).findByStatusOrderByCreatedAtDesc(eq(Game.GameStatus.ACTIVE));
    }

    @Test
    void searchGamesMatchesAbbreviationBeforeSingleCharacterMatches() {
        Game darkSouls = createGame("黑暗之魂3", "魂系动作角色扮演", 10);
        Game blackMyth = createGame("黑神话：悟空", "动作角色扮演", 5);
        Game soulWorker = createGame("灵魂行者", "二次元动作", 1);

        when(gameRepository.findByStatusOrderByCreatedAtDesc(eq(Game.GameStatus.ACTIVE)))
                .thenReturn(List.of(blackMyth, soulWorker, darkSouls));

        Page<Game> result = gameService.searchGames("黑魂", 0, 10);

        Assertions.assertEquals(
                List.of("黑暗之魂3", "黑神话：悟空", "灵魂行者"),
                result.getContent().stream().map(Game::getName).toList()
        );
    }

    @Test
    void searchGamesFallsBackToSingleCharacterMatches() {
        Game darkSouls = createGame("黑暗之魂3", "魂系动作角色扮演", 10);
        Game soulWorker = createGame("灵魂行者", "动作", 5);
        Game witcher = createGame("巫师3", "奇幻角色扮演", 1);

        when(gameRepository.findByStatusOrderByCreatedAtDesc(eq(Game.GameStatus.ACTIVE)))
                .thenReturn(List.of(witcher, soulWorker, darkSouls));

        Page<Game> result = gameService.searchGames("魂", 0, 10);

        Assertions.assertEquals(
                List.of("黑暗之魂3", "灵魂行者"),
                result.getContent().stream().map(Game::getName).toList()
        );
    }

    @Test
    void filterGamesAppliesManualPaginationAfterRanking() {
        when(gameRepository.findSearchCandidatesByCategoryIds(eq(List.of(9L)), eq(Game.GameStatus.ACTIVE)))
                .thenReturn(List.of(
                        createGame("黑暗之魂3", "魂系动作角色扮演", 3),
                        createGame("黑神话：悟空", "动作角色扮演", 2),
                        createGame("灵魂行者", "动作", 1)
                ));

        Page<Game> result = gameService.filterGames(List.of(9L), "黑魂", 0, 2);

        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(3, result.getTotalElements());
        Assertions.assertEquals("黑暗之魂3", result.getContent().get(0).getName());
    }

    private Game createGame(String name, String tags, int createdAtDaysAgo) {
        Game game = new Game();
        game.setName(name);
        game.setDescription(name + " 的游戏介绍");
        game.setTags(tags);
        game.setCreatedAt(LocalDateTime.now().minusDays(createdAtDaysAgo));
        return game;
    }
}
