package com.gamestore.service;

import com.gamestore.entity.Category;
import com.gamestore.entity.Game;
import com.gamestore.exception.CustomException;
import com.gamestore.repository.CategoryRepository;
import com.gamestore.repository.GameRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final CategoryRepository categoryRepository;

    public GameService(GameRepository gameRepository, CategoryRepository categoryRepository) {
        this.gameRepository = gameRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<Game> getGames(Long categoryId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Game.GameStatus activeStatus = Game.GameStatus.ACTIVE;
        String normalizedKeyword = keyword == null ? null : keyword.trim();
        boolean hasKeyword = normalizedKeyword != null && !normalizedKeyword.isEmpty();

        if (categoryId != null && hasKeyword) {
            return rankAndPageGames(
                    gameRepository.findSearchCandidatesByCategoryId(categoryId, activeStatus),
                    normalizedKeyword,
                    pageable
            );
        } else if (categoryId != null) {
            return gameRepository.findByCategoryIdAndStatus(categoryId, activeStatus, pageable);
        } else if (hasKeyword) {
            return rankAndPageGames(
                    gameRepository.findByStatusOrderByCreatedAtDesc(activeStatus),
                    normalizedKeyword,
                    pageable
            );
        } else {
            return gameRepository.findByStatus(activeStatus, pageable);
        }
    }

    public Page<Game> filterGames(List<Long> categoryIds, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Game.GameStatus activeStatus = Game.GameStatus.ACTIVE;
        List<Long> normalizedCategoryIds = normalizeCategoryIds(categoryIds);
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        if (!normalizedCategoryIds.isEmpty() && hasKeyword) {
            return rankAndPageGames(
                    gameRepository.findSearchCandidatesByCategoryIds(normalizedCategoryIds, activeStatus),
                    keyword.trim(),
                    pageable
            );
        }
        if (!normalizedCategoryIds.isEmpty()) {
            return gameRepository.findByCategoryIdsAndStatus(normalizedCategoryIds, activeStatus, pageable);
        }
        if (hasKeyword) {
            return rankAndPageGames(
                    gameRepository.findByStatusOrderByCreatedAtDesc(activeStatus),
                    keyword.trim(),
                    pageable
            );
        }
        return gameRepository.findByStatus(activeStatus, pageable);
    }

    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new CustomException("Game not found"));
    }

    public List<Game> getFeaturedGames(int page, int size) {
        List<Game> featuredGames = gameRepository.findByIsFeaturedTrueAndStatus(Game.GameStatus.ACTIVE);

        int start = page * size;
        int end = Math.min(start + size, featuredGames.size());

        if (start >= featuredGames.size()) {
            return List.of();
        }

        return featuredGames.subList(start, end);
    }

    public Page<Game> searchGames(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        return rankAndPageGames(
                gameRepository.findByStatusOrderByCreatedAtDesc(Game.GameStatus.ACTIVE),
                normalizedKeyword,
                pageable
        );
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    /**
     * Create a game with optional category bindings.
     */
    @Transactional
    public Game createGame(Game game, List<Long> categoryIds) {
        Game savedGame = gameRepository.save(game);

        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : categoryIds) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CustomException("Category not found: " + categoryId));
                categories.add(category);
            }
            savedGame.setCategories(categories);
            savedGame = gameRepository.save(savedGame);
        }

        return savedGame;
    }

    /**
     * Update a game with optional category bindings.
     */
    @Transactional
    public Game updateGame(Long id, Game gameDetails, List<Long> categoryIds) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new CustomException("Game not found"));

        game.setName(gameDetails.getName());
        game.setDescription(gameDetails.getDescription());
        game.setDeveloper(gameDetails.getDeveloper());
        game.setPublisher(gameDetails.getPublisher());
        game.setReleaseDate(gameDetails.getReleaseDate());
        game.setPrice(gameDetails.getPrice());
        game.setDiscountPrice(gameDetails.getDiscountPrice());
        game.setImageUrl(gameDetails.getImageUrl());
        game.setGallery(gameDetails.getGallery());
        game.setSystemRequirements(gameDetails.getSystemRequirements());
        game.setTags(gameDetails.getTags());
        game.setIsFeatured(gameDetails.getIsFeatured());
        game.setStatus(gameDetails.getStatus());

        if (categoryIds != null) {
            game.clearCategories();
            if (!categoryIds.isEmpty()) {
                Set<Category> categories = new HashSet<>();
                for (Long categoryId : categoryIds) {
                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new CustomException("Category not found: " + categoryId));
                    categories.add(category);
                }
                game.setCategories(categories);
            }
        }

        return gameRepository.save(game);
    }

    /**
     * Delete a game by id.
     */
    @Transactional
    public void deleteGame(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new CustomException("Game not found"));
        gameRepository.delete(game);
    }

    /**
     * Find games by category.
     */
    public List<Game> getGamesByCategory(Long categoryId) {
        return gameRepository.findGamesByCategory(categoryId);
    }

    /**
     * Find a game by name for duplicate checks.
     */
    public Game getGameByName(String name) {
        return gameRepository.findByName(name);
    }

    /**
     * Find a game by name excluding the given id.
     */
    public Game getGameByNameExcludingId(String name, Long id) {
        return gameRepository.findByNameAndIdNot(name, id);
    }

    private List<Long> normalizeCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        return categoryIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private Page<Game> rankAndPageGames(List<Game> candidates, String keyword, Pageable pageable) {
        String normalizedKeyword = normalizeSearchText(keyword);
        if (normalizedKeyword.isEmpty()) {
            return toPage(candidates, pageable);
        }

        List<Game> rankedGames = candidates.stream()
                .map(game -> new SearchResult(game, calculateSearchScore(game, normalizedKeyword)))
                .filter(result -> result.score() > 0)
                .sorted(Comparator
                        .comparingInt(SearchResult::score).reversed()
                        .thenComparing((SearchResult result) -> getCreatedAtOrMin(result.game()), Comparator.reverseOrder())
                        .thenComparing(result -> String.valueOf(result.game().getDisplayName()), String.CASE_INSENSITIVE_ORDER))
                .map(SearchResult::game)
                .toList();

        return toPage(rankedGames, pageable);
    }

    private Page<Game> toPage(List<Game> games, Pageable pageable) {
        if (pageable.isUnpaged()) {
            return new PageImpl<>(games);
        }

        int start = (int) pageable.getOffset();
        if (start >= games.size()) {
            return new PageImpl<>(List.of(), pageable, games.size());
        }

        int end = Math.min(start + pageable.getPageSize(), games.size());
        return new PageImpl<>(games.subList(start, end), pageable, games.size());
    }

    private int calculateSearchScore(Game game, String normalizedKeyword) {
        String name = normalizeSearchText(game.getName());
        String displayName = normalizeSearchText(game.getDisplayName());
        String tags = normalizeSearchText(game.getTags());
        String developer = normalizeSearchText(game.getDeveloper());
        String publisher = normalizeSearchText(game.getPublisher());
        String description = normalizeSearchText(game.getDescription());

        List<String> primaryTexts = List.of(name, displayName);
        List<String> secondaryTexts = List.of(tags, developer, publisher, description);

        int score = 0;

        if (matchesExactly(primaryTexts, normalizedKeyword)) {
            score += 1200;
        }
        if (startsWith(primaryTexts, normalizedKeyword)) {
            score += 900;
        }
        if (contains(primaryTexts, normalizedKeyword)) {
            score += 760;
        }
        if (contains(secondaryTexts, normalizedKeyword)) {
            score += 520;
        }
        if (isOrderedSubsequence(normalizedKeyword, name) || isOrderedSubsequence(normalizedKeyword, displayName)) {
            score += 420;
        }

        List<String> fragments = buildKeywordFragments(normalizedKeyword);
        int primaryFragmentHits = countDistinctMatches(fragments, primaryTexts);
        int secondaryFragmentHits = countDistinctMatches(fragments, List.of(tags, developer, publisher));
        score += primaryFragmentHits * 140;
        score += secondaryFragmentHits * 70;

        List<String> characters = splitToSearchUnits(normalizedKeyword);
        int primaryCharacterHits = countDistinctMatches(characters, primaryTexts);
        int secondaryCharacterHits = countDistinctMatches(characters, List.of(tags, developer, publisher));
        score += primaryCharacterHits * 45;
        score += secondaryCharacterHits * 18;
        score += calculateOrderedUnitScore(characters, primaryTexts, 30, 15);
        score += calculateOrderedUnitScore(characters, List.of(tags, developer, publisher), 12, 4);

        if (score == 0) {
            return 0;
        }

        int keywordLength = normalizedKeyword.codePointCount(0, normalizedKeyword.length());
        if (keywordLength > 1
                && primaryCharacterHits == 0
                && primaryFragmentHits == 0
                && !contains(secondaryTexts, normalizedKeyword)) {
            return 0;
        }

        return score;
    }

    private boolean matchesExactly(List<String> texts, String keyword) {
        return texts.stream().anyMatch(keyword::equals);
    }

    private boolean startsWith(List<String> texts, String keyword) {
        return texts.stream().anyMatch(text -> !text.isEmpty() && text.startsWith(keyword));
    }

    private boolean contains(List<String> texts, String keyword) {
        return texts.stream().anyMatch(text -> !text.isEmpty() && text.contains(keyword));
    }

    private int countDistinctMatches(List<String> searchUnits, List<String> texts) {
        if (searchUnits.isEmpty()) {
            return 0;
        }

        int hitCount = 0;
        for (String unit : searchUnits) {
            if (unit.isEmpty()) {
                continue;
            }
            boolean matched = texts.stream().anyMatch(text -> !text.isEmpty() && text.contains(unit));
            if (matched) {
                hitCount++;
            }
        }
        return hitCount;
    }

    private int calculateOrderedUnitScore(List<String> searchUnits, List<String> texts, int firstWeight, int decay) {
        int score = 0;
        for (int index = 0; index < searchUnits.size(); index++) {
            String unit = searchUnits.get(index);
            if (unit.isEmpty()) {
                continue;
            }
            boolean matched = texts.stream().anyMatch(text -> !text.isEmpty() && text.contains(unit));
            if (matched) {
                score += Math.max(1, firstWeight - (index * decay));
            }
        }
        return score;
    }

    private List<String> buildKeywordFragments(String normalizedKeyword) {
        LinkedHashSet<String> fragments = new LinkedHashSet<>();
        List<String> units = splitToSearchUnits(normalizedKeyword);
        if (units.size() <= 2) {
            return List.of();
        }

        for (int size = Math.min(3, units.size() - 1); size >= 2; size--) {
            for (int index = 0; index <= units.size() - size; index++) {
                String fragment = String.join("", units.subList(index, index + size));
                if (!fragment.equals(normalizedKeyword)) {
                    fragments.add(fragment);
                }
            }
        }
        return new ArrayList<>(fragments);
    }

    private List<String> splitToSearchUnits(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        List<String> units = new ArrayList<>();
        text.codePoints()
                .mapToObj(codePoint -> new String(Character.toChars(codePoint)))
                .forEach(units::add);
        return units;
    }

    private boolean isOrderedSubsequence(String keyword, String text) {
        if (keyword.isEmpty() || text.isEmpty()) {
            return false;
        }

        int keywordIndex = 0;
        int[] keywordPoints = keyword.codePoints().toArray();
        int[] textPoints = text.codePoints().toArray();

        for (int textPoint : textPoints) {
            if (textPoint == keywordPoints[keywordIndex]) {
                keywordIndex++;
                if (keywordIndex == keywordPoints.length) {
                    return true;
                }
            }
        }
        return false;
    }

    private String normalizeSearchText(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        text.toLowerCase(Locale.ROOT)
                .codePoints()
                .filter(Character::isLetterOrDigit)
                .forEach(builder::appendCodePoint);
        return builder.toString();
    }

    private LocalDateTime getCreatedAtOrMin(Game game) {
        return game.getCreatedAt() == null ? LocalDateTime.MIN : game.getCreatedAt();
    }

    private record SearchResult(Game game, int score) {
    }
}
