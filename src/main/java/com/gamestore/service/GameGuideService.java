package com.gamestore.service;

import com.gamestore.dto.request.CreateGameGuideRequest;
import com.gamestore.dto.response.GameGuideResponse;
import com.gamestore.entity.Game;
import com.gamestore.entity.GameGuide;
import com.gamestore.entity.User;
import com.gamestore.exception.CustomException;
import com.gamestore.repository.GameGuideRepository;
import com.gamestore.repository.GameRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameGuideService {

    private final GameGuideRepository gameGuideRepository;
    private final GameRepository gameRepository;

    public GameGuideService(GameGuideRepository gameGuideRepository, GameRepository gameRepository) {
        this.gameGuideRepository = gameGuideRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true)
    public List<GameGuideResponse> getGuides(
        Long gameId,
        GameGuide.GuideDifficulty difficulty,
        Boolean featuredOnly,
        String keyword,
        int page,
        int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GameGuide> guidePage = gameGuideRepository.searchGuides(
            gameId,
            difficulty,
            Boolean.TRUE.equals(featuredOnly),
            normalizeKeyword(keyword),
            GameGuide.GuideStatus.PUBLISHED,
            pageable
        );
        return guidePage.getContent().stream()
            .map(GameGuideResponse::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<GameGuideResponse> getFeaturedGuides(int size) {
        Pageable pageable = PageRequest.of(0, size);
        return gameGuideRepository.findFeaturedGuides(GameGuide.GuideStatus.PUBLISHED, pageable).getContent().stream()
            .map(GameGuideResponse::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public long getPublishedGuideCount() {
        return gameGuideRepository.countByStatus(GameGuide.GuideStatus.PUBLISHED);
    }

    @Transactional
    public GameGuideResponse createGuide(CreateGameGuideRequest request, User author) {
        if (author == null) {
            throw new CustomException("请先登录");
        }
        if (request.getGameId() == null) {
            throw new CustomException("请选择关联游戏");
        }

        Game game = gameRepository.findById(request.getGameId())
            .orElseThrow(() -> new CustomException("关联游戏不存在"));

        String title = normalizeRequiredText(request.getTitle(), "标题不能为空");
        if (title.length() < 5 || title.length() > 200) {
            throw new CustomException("标题长度需在 5 到 200 个字符之间");
        }

        String content = normalizeRequiredText(request.getContent(), "正文不能为空");
        if (content.length() < 20) {
            throw new CustomException("正文至少需要 20 个字符");
        }

        String summary = normalizeOptionalText(request.getSummary());
        if (summary != null && summary.length() > 500) {
            throw new CustomException("摘要不能超过 500 个字符");
        }

        String coverImageUrl = normalizeOptionalText(request.getCoverImageUrl());
        String tags = normalizeOptionalText(request.getTags());
        Integer estimatedMinutes = request.getEstimatedMinutes();
        if (estimatedMinutes == null || estimatedMinutes < 1) {
            estimatedMinutes = 10;
        }
        if (estimatedMinutes > 600) {
            throw new CustomException("预计时长不能超过 600 分钟");
        }

        GameGuide guide = new GameGuide();
        guide.setGameId(game.getId());
        guide.setAuthorId(author.getId());
        guide.setTitle(title);
        guide.setSummary(summary);
        guide.setContent(content);
        guide.setCoverImageUrl(coverImageUrl);
        guide.setDifficulty(request.getDifficulty() != null ? request.getDifficulty() : GameGuide.GuideDifficulty.BEGINNER);
        guide.setEstimatedMinutes(estimatedMinutes);
        guide.setTags(tags);
        guide.setStatus(GameGuide.GuideStatus.PUBLISHED);
        guide.setIsFeatured(false);

        GameGuide savedGuide = gameGuideRepository.save(guide);
        GameGuide persistedGuide = gameGuideRepository.findById(savedGuide.getId()).orElse(savedGuide);
        return GameGuideResponse.fromEntity(persistedGuide);
    }

    @Transactional
    public GameGuideResponse getGuideDetail(Long id) {
        GameGuide guide = gameGuideRepository.findByIdAndStatus(id, GameGuide.GuideStatus.PUBLISHED)
            .orElseThrow(() -> new CustomException("攻略不存在"));
        gameGuideRepository.incrementViewCount(id);
        guide.setViewCount((guide.getViewCount() == null ? 0 : guide.getViewCount()) + 1);
        return GameGuideResponse.fromEntity(guide);
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String normalized = keyword.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeRequiredText(String value, String message) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            throw new CustomException(message);
        }
        return normalized;
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
