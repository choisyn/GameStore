package com.gamestore.service;

import com.gamestore.dto.response.BadgeResponse;
import com.gamestore.dto.response.CheckInResponse;
import com.gamestore.dto.response.DecisionInsightResponse;
import com.gamestore.dto.response.GrowthDashboardResponse;
import com.gamestore.dto.response.GrowthTaskResponse;
import com.gamestore.dto.response.RecommendationReasonDetail;
import com.gamestore.dto.response.RecommendationResponse;
import com.gamestore.entity.CartItem;
import com.gamestore.entity.Category;
import com.gamestore.entity.CommunityComment;
import com.gamestore.entity.CommunityPost;
import com.gamestore.entity.CommunitySection;
import com.gamestore.entity.Game;
import com.gamestore.entity.PointTransaction;
import com.gamestore.entity.Post;
import com.gamestore.entity.User;
import com.gamestore.entity.UserBadge;
import com.gamestore.entity.UserBehaviorLog;
import com.gamestore.entity.UserGame;
import com.gamestore.exception.CustomException;
import com.gamestore.repository.CartItemRepository;
import com.gamestore.repository.CategoryRepository;
import com.gamestore.repository.CommentRepository;
import com.gamestore.repository.CommunityCommentRepository;
import com.gamestore.repository.CommunityPostRepository;
import com.gamestore.repository.CommunitySectionRepository;
import com.gamestore.repository.GameOrderRepository;
import com.gamestore.repository.GameRepository;
import com.gamestore.repository.PointTransactionRepository;
import com.gamestore.repository.PostRepository;
import com.gamestore.repository.UserBadgeRepository;
import com.gamestore.repository.UserBehaviorLogRepository;
import com.gamestore.repository.UserGameRepository;
import com.gamestore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class InnovationService {

    private static final int CHECK_IN_REWARD_POINTS = 30;
    private static final long RECOMMENDATION_CATALOG_TTL_MILLIS = 5 * 60 * 1000L;
    private static final long USER_RECOMMENDATION_TTL_MILLIS = 45 * 1000L;
    private static final long GUEST_RECOMMENDATION_CACHE_KEY = -1L;
    private static final int MAX_RECOMMENDATION_CANDIDATES = 96;

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final UserGameRepository userGameRepository;
    private final CartItemRepository cartItemRepository;
    private final GameOrderRepository gameOrderRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommunitySectionRepository communitySectionRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final UserBehaviorLogRepository userBehaviorLogRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final Object recommendationCatalogMonitor = new Object();
    private volatile RecommendationCatalog recommendationCatalog = RecommendationCatalog.empty();
    private volatile long recommendationCatalogExpiresAt = 0L;
    private final Map<Long, CachedRecommendationList> recommendationCache = new ConcurrentHashMap<>();

    public InnovationService(
            GameRepository gameRepository,
            UserRepository userRepository,
            UserGameRepository userGameRepository,
            CartItemRepository cartItemRepository,
            GameOrderRepository gameOrderRepository,
            CategoryRepository categoryRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            CommunityPostRepository communityPostRepository,
            CommunitySectionRepository communitySectionRepository,
            CommunityCommentRepository communityCommentRepository,
            PointTransactionRepository pointTransactionRepository,
            UserBehaviorLogRepository userBehaviorLogRepository,
            UserBadgeRepository userBadgeRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.userGameRepository = userGameRepository;
        this.cartItemRepository = cartItemRepository;
        this.gameOrderRepository = gameOrderRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.communityPostRepository = communityPostRepository;
        this.communitySectionRepository = communitySectionRepository;
        this.communityCommentRepository = communityCommentRepository;
        this.pointTransactionRepository = pointTransactionRepository;
        this.userBehaviorLogRepository = userBehaviorLogRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    public List<RecommendationResponse> getRecommendations(Long userId, int size) {
        return getRecommendations(userId, size, 0);
    }

    public List<RecommendationResponse> getRecommendations(Long userId, int size, int batch) {
        int safeSize = Math.max(1, size);
        int safeBatch = Math.max(0, batch);
        long offset = (long) safeSize * safeBatch;
        List<RecommendationResponse> rankedRecommendations = getOrBuildRecommendationList(userId);
        if (rankedRecommendations.isEmpty()) {
            return List.of();
        }
        return rankedRecommendations.stream()
            .skip(offset)
            .limit(safeSize)
            .toList();
    }

    public DecisionInsightResponse getDecisionInsight(Long userId, Long gameId) {
        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new CustomException("游戏不存在"));

        Map<String, Integer> preferenceScores = buildPreferenceScores(userId);
        Map<Long, String> categoryNameCache = new HashMap<>();
        Set<String> gameKeywords = collectGameKeywords(game, categoryNameCache);
        List<String> matchedTags = findTopMatchedKeywords(preferenceScores, gameKeywords, 3);

        long forumPostCount = postRepository.countByGameIdAndStatus(gameId, Post.PostStatus.PUBLISHED);
        int interestMatchScore = clamp(
            preferenceScores.isEmpty()
                ? 56
                : 28 + calculatePreferenceMatchScore(preferenceScores, gameKeywords) * 4,
            35,
            98
        );

        int communityHeatScore = clamp(
            20
                + Math.min(30, (int) forumPostCount * 8)
                + Math.min(25, safeInt(game.getRatingCount()) / 5)
                + Math.min(25, safeInt(game.getDownloadCount()) / 30),
            25,
            95
        );

        BigDecimal currentPrice = resolveCurrentPrice(game);
        int discountPercent = resolveDiscountPercent(game);
        int valueScore = clamp(
            42 + safeRatingScore(game) + Math.min(22, discountPercent) + priceAffordabilityBonus(currentPrice),
            35,
            96
        );

        int supportScore = clamp(
            35
                + (hasText(game.getSystemRequirements()) ? 18 : 0)
                + (hasText(game.getDeveloper()) ? 10 : 0)
                + (hasText(game.getPublisher()) ? 7 : 0)
                + Math.min(20, gameKeywords.size() * 2),
            35,
            90
        );

        int overallScore = clamp(
            (int) Math.round(
                interestMatchScore * 0.35
                    + communityHeatScore * 0.25
                    + valueScore * 0.25
                    + supportScore * 0.15
            ),
            40,
            97
        );

        List<String> reasons = new ArrayList<>();
        if (!matchedTags.isEmpty()) {
            reasons.add("与你近期偏好的 " + String.join(" / ", matchedTags) + " 类型高度相关");
        }
        if (discountPercent > 0) {
            reasons.add("当前优惠力度约为 " + discountPercent + "%，性价比表现更好");
        }
        if (forumPostCount > 0) {
            reasons.add("平台内已有 " + forumPostCount + " 条相关讨论，可辅助购前判断");
        }
        if (safeInt(game.getRatingCount()) > 0) {
            reasons.add("当前综合评分为 " + safeScale(game.getRating()) + " / 5，用户口碑较稳定");
        }
        if (reasons.isEmpty()) {
            reasons.add("该游戏当前热度、资料完整度和价格表现整体均衡");
        }

        String summary;
        String suggestedAction;
        if (overallScore >= 82) {
            summary = "这款游戏与当前账号兴趣画像匹配度较高，属于可优先考虑的候选项。";
            suggestedAction = "建议直接加入购物车，或结合社区讨论后完成购买。";
        } else if (overallScore >= 68) {
            summary = "它在口碑与价格之间表现均衡，适合先收藏、观察活动或阅读讨论后再决定。";
            suggestedAction = "建议先查看讨论广场的攻略与测评，再决定是否下单。";
        } else {
            summary = "当前这款游戏与已有兴趣偏好重合度一般，更适合作为补充型选择。";
            suggestedAction = "建议优先关注推荐区内更匹配的作品，或等待更合适的折扣。";
        }

        String caution = hasText(game.getSystemRequirements())
            ? "下单前建议再确认系统需求与设备配置是否匹配。"
            : "当前游戏缺少完整系统需求说明，下单前建议结合外部资料核验配置。";

        return new DecisionInsightResponse(
            gameId,
            overallScore,
            interestMatchScore,
            communityHeatScore,
            valueScore,
            supportScore,
            summary,
            suggestedAction,
            caution,
            reasons,
            matchedTags
        );
    }

    public GrowthDashboardResponse getGrowthDashboard(Long userId) {
        evaluateBadges(userId);

        Map<String, Integer> preferenceScores = buildPreferenceScores(userId);
        List<UserBehaviorLog> recentLogs = userBehaviorLogRepository.findTop300ByUserIdOrderByCreatedAtDesc(userId);
        List<UserBadge> badges = userBadgeRepository.findByUserIdOrderByEarnedAtDesc(userId);
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();

        int todayViewedGames = (int) recentLogs.stream()
            .filter(log -> log.getBehaviorType() == UserBehaviorLog.BehaviorType.VIEW_GAME)
            .filter(log -> !log.getCreatedAt().isBefore(todayStart))
            .map(UserBehaviorLog::getGameId)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(HashSet::new))
            .size();

        int todayDiscussionCount = (int) recentLogs.stream()
            .filter(log -> !log.getCreatedAt().isBefore(todayStart))
            .filter(log -> log.getBehaviorType() == UserBehaviorLog.BehaviorType.COMMENT_FORUM_POST
                || log.getBehaviorType() == UserBehaviorLog.BehaviorType.COMMENT_COMMUNITY_POST)
            .count();

        boolean checkedInToday = !userBehaviorLogRepository
            .findByUserIdAndBehaviorTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                userId,
                UserBehaviorLog.BehaviorType.DAILY_CHECK_IN,
                todayStart,
                tomorrowStart
            )
            .isEmpty();

        long forumPostCount = postRepository.countByUserIdAndStatus(userId, Post.PostStatus.PUBLISHED);
        long forumCommentCount = commentRepository.countByUserIdAndStatus(
            userId,
            com.gamestore.entity.Comment.CommentStatus.PUBLISHED
        );
        long communityPostCount = communityPostRepository.countByUserIdAndStatus(userId, CommunityPost.PostStatus.PUBLISHED);
        long communityCommentCount = communityCommentRepository.countByUserIdAndStatus(
            userId,
            CommunityComment.CommentStatus.PUBLISHED
        );
        long totalPostCount = forumPostCount + communityPostCount;
        long totalCommentCount = forumCommentCount + communityCommentCount;
        long libraryCount = userGameRepository.countByUserId(userId);
        long orderCount = gameOrderRepository.countByUserId(userId);

        int contributionScore = (int) (
            libraryCount * 28
                + orderCount * 20
                + totalPostCount * 16
                + totalCommentCount * 8
                + badges.size() * 18
                + Math.min(60, preferenceScores.size() * 6)
        );

        int level = Math.max(1, contributionScore / 120 + 1);
        int currentLevelStart = (level - 1) * 120;
        int nextLevelScore = level * 120;
        int progressPercent = Math.min(
            100,
            (int) Math.round((contributionScore - currentLevelStart) * 100.0 / Math.max(1, nextLevelScore - currentLevelStart))
        );

        List<GrowthTaskResponse> tasks = List.of(
            new GrowthTaskResponse("每日签到", "完成签到可保持活跃节奏，并稳定积累积分。", checkedInToday ? 1 : 0, 1, checkedInToday, "建议每天首次登录后先完成签到"),
            new GrowthTaskResponse("今日浏览 3 款游戏", "通过浏览行为补全兴趣画像，让推荐更贴近你。", todayViewedGames, 3, todayViewedGames >= 3, "优先浏览标签明确、风格稳定的作品"),
            new GrowthTaskResponse("今日参与 2 次讨论", "讨论参与越多，系统越能识别你的偏好方向。", todayDiscussionCount, 2, todayDiscussionCount >= 2, "可以在讨论广场或社区板块留言"),
            new GrowthTaskResponse("累计发布 3 篇内容", "持续输出攻略、评测和提问，更容易形成个人标签。", (int) totalPostCount, 3, totalPostCount >= 3, "优先发布与你常购类型相关的内容"),
            new GrowthTaskResponse("累计拥有 5 款游戏", "游戏库越完整，系统的购前判断越准确。", (int) libraryCount, 5, libraryCount >= 5, "可先从价格较低或免费作品开始补足画像")
        );

        List<String> interestKeywords = preferenceScores.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .map(Map.Entry::getKey)
            .toList();

        return new GrowthDashboardResponse(
            level,
            resolveLevelTitle(level),
            contributionScore,
            nextLevelScore,
            progressPercent,
            checkedInToday,
            calculateCheckInStreak(userId),
            interestKeywords,
            tasks,
            badges.stream()
                .map(badge -> new BadgeResponse(badge.getCode(), badge.getName(), badge.getDescription(), badge.getEarnedAt()))
                .toList()
        );
    }

    @Transactional
    public CheckInResponse checkIn(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        boolean alreadyCheckedIn = !userBehaviorLogRepository
            .findByUserIdAndBehaviorTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                userId,
                UserBehaviorLog.BehaviorType.DAILY_CHECK_IN,
                start,
                end
            )
            .isEmpty();
        if (alreadyCheckedIn) {
            throw new CustomException("今天已经签到过了");
        }

        changeUserPoints(userId, CHECK_IN_REWARD_POINTS, "每日签到奖励");
        recordBehavior(userId, UserBehaviorLog.BehaviorType.DAILY_CHECK_IN, null, null, "每日签到");
        User user = getRequiredUser(userId);
        int currentPoints = user.getPoints() == null ? 0 : user.getPoints();
        return new CheckInResponse(CHECK_IN_REWARD_POINTS, currentPoints, calculateCheckInStreak(userId));
    }

    @Transactional
    public void recordBehavior(Long userId, UserBehaviorLog.BehaviorType behaviorType, Long gameId, Long referenceId, String detail) {
        if (userId == null || behaviorType == null) {
            return;
        }

        UserBehaviorLog log = new UserBehaviorLog();
        log.setUserId(userId);
        log.setBehaviorType(behaviorType);
        log.setGameId(gameId);
        log.setReferenceId(referenceId);
        log.setDetail(detail);
        userBehaviorLogRepository.save(log);
        invalidateRecommendationCache(userId);
        evaluateBadges(userId);
    }

    private Map<String, Integer> buildPreferenceScores(Long userId) {
        return buildPreferenceScores(userId, getRecommendationCatalog());
    }

    private Map<String, Integer> buildPreferenceScores(Long userId, RecommendationCatalog catalog) {
        if (userId == null) {
            return Map.of();
        }

        RecommendationCatalog safeCatalog = catalog == null ? RecommendationCatalog.empty() : catalog;
        Set<String> knownGameKeywords = safeCatalog.knownKeywords();

        Map<String, Integer> scores = new HashMap<>();
        addGamePreferences(
            scores,
            userGameRepository.findByUserIdOrderByAcquiredAtDesc(userId)
                .stream()
                .map(UserGame::getGameId)
                .collect(Collectors.toSet()),
            6,
            safeCatalog
        );
        addGamePreferences(
            scores,
            cartItemRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(CartItem::getGameId)
                .collect(Collectors.toSet()),
            4,
            safeCatalog
        );

        Map<Long, Set<String>> forumKeywordCache = new HashMap<>();
        Map<Long, Set<String>> communityKeywordCache = new HashMap<>();
        userBehaviorLogRepository.findTop300ByUserIdOrderByCreatedAtDesc(userId).forEach(log -> {
            int gameWeight = switch (log.getBehaviorType()) {
                case VIEW_GAME -> 2;
                case ADD_TO_CART -> 4;
                case PURCHASE_GAME, CLAIM_FREE_GAME -> 6;
                case CREATE_FORUM_POST, COMMENT_FORUM_POST -> 3;
                case CREATE_COMMUNITY_POST, COMMENT_COMMUNITY_POST -> 2;
                default -> 0;
            };

            if (gameWeight > 0 && log.getGameId() != null) {
                addGamePreferences(
                    scores,
                    Set.of(log.getGameId()),
                    gameWeight,
                    safeCatalog
                );
            }

            int communityWeight = switch (log.getBehaviorType()) {
                case VIEW_FORUM_POST, VIEW_COMMUNITY_POST -> 2;
                case CREATE_FORUM_POST, COMMENT_FORUM_POST -> 3;
                case CREATE_COMMUNITY_POST, COMMENT_COMMUNITY_POST -> 3;
                default -> 0;
            };
            if (communityWeight <= 0) {
                return;
            }

            Set<String> communityKeywords = switch (log.getBehaviorType()) {
                case VIEW_FORUM_POST, CREATE_FORUM_POST, COMMENT_FORUM_POST ->
                    collectForumKeywords(log, knownGameKeywords, safeCatalog, forumKeywordCache);
                case VIEW_COMMUNITY_POST, CREATE_COMMUNITY_POST, COMMENT_COMMUNITY_POST ->
                    collectCommunityKeywords(log, knownGameKeywords, safeCatalog, communityKeywordCache);
                default -> Set.of();
            };

            addKeywordPreferences(scores, communityKeywords, communityWeight);

            List<Long> relatedGameIds = findMatchingGameIds(communityKeywords, safeCatalog, 4);
            if (!relatedGameIds.isEmpty()) {
                addGamePreferences(
                    scores,
                    relatedGameIds,
                    Math.max(1, communityWeight - 1),
                    safeCatalog
                );
            }
        });

        return scores;
    }

    private void addGamePreferences(
            Map<String, Integer> scores,
            Collection<Long> gameIds,
            int weight,
            RecommendationCatalog catalog) {
        if (gameIds == null || gameIds.isEmpty()) {
            return;
        }
        gameIds.stream()
            .map(gameId -> catalog.gamesById().get(gameId))
            .filter(Objects::nonNull)
            .forEach(game -> addKeywordPreferences(
                scores,
                game.keywords(),
                weight
            ));
    }

    private ScoredRecommendation toRecommendation(
            CatalogGame catalogGame,
            Map<String, Integer> preferenceScores) {
        Game game = catalogGame.game();
        Set<String> gameKeywords = catalogGame.keywords();
        List<String> matchedTags = findTopMatchedKeywords(preferenceScores, gameKeywords, 2);

        int tagScore = calculatePreferenceMatchScore(preferenceScores, gameKeywords);
        int totalScore = Math.max(38, Math.min(98, tagScore * 5 + catalogGame.baseScore()));
        List<RecommendationReasonDetail> detailReasons = buildRecommendationDetails(catalogGame, matchedTags, totalScore);

        boolean useCommunityAwareReasons = true;
        if (useCommunityAwareReasons) {
            String primaryReason;
            String secondaryReason;
            if (!matchedTags.isEmpty()) {
                primaryReason = "因为你最近在社区和商城里持续关注了「" + matchedTags.get(0) + "」相关内容";
                secondaryReason = matchedTags.size() > 1
                    ? "同时它和你浏览帖子里高频出现的「" + matchedTags.get(1) + "」关键词也比较接近"
                    : "系统已综合购买、加购、浏览和社区讨论行为生成推荐";
            } else if (catalogGame.discountPercent() >= 20) {
                primaryReason = "因为当前折扣力度较高，适合作为高性价比候选";
                secondaryReason = "它在价格与口碑之间保持了比较均衡的表现";
            } else {
                primaryReason = "因为它在当前平台内的热度和口碑都比较稳定";
                secondaryReason = Boolean.TRUE.equals(game.getIsFeatured())
                    ? "当前仍处于平台精选推荐位"
                    : "适合作为你下一款尝试的补充型作品";
            }

            return new ScoredRecommendation(
                totalScore,
                new RecommendationResponse(
                    game.getId(),
                    game.getName(),
                    game.getDisplayName(),
                    game.getDescription(),
                    game.getImageUrl(),
                    game.getTags(),
                    game.getPrice(),
                    game.getDiscountPrice(),
                    game.getRating(),
                    game.getRatingCount(),
                    totalScore,
                    primaryReason,
                    secondaryReason,
                    detailReasons
                )
            );
        }

        String primaryReason;
        String secondaryReason;
        if (!matchedTags.isEmpty()) {
            primaryReason = "因为你最近关注了 " + matchedTags.get(0) + " 类内容";
            secondaryReason = matchedTags.size() > 1
                ? "同时与 " + matchedTags.get(1) + " 标签也较为接近"
                : "系统已综合购买、浏览和社区行为生成推荐";
        } else if (catalogGame.discountPercent() >= 20) {
            primaryReason = "因为当前折扣力度较高，适合作为性价比候选";
            secondaryReason = "它在价格与口碑之间表现较均衡";
        } else {
            primaryReason = "因为它在当前平台中热度和口碑都比较稳定";
            secondaryReason = Boolean.TRUE.equals(game.getIsFeatured())
                ? "当前还处于平台精选推荐位"
                : "适合作为你下一款尝试的补充型作品";
        }

        return new ScoredRecommendation(
            totalScore,
            new RecommendationResponse(
                game.getId(),
                game.getName(),
                game.getDisplayName(),
                game.getDescription(),
                game.getImageUrl(),
                game.getTags(),
                game.getPrice(),
                game.getDiscountPrice(),
                game.getRating(),
                game.getRatingCount(),
                totalScore,
                primaryReason,
                secondaryReason,
                detailReasons
            )
        );
    }

    private List<RecommendationReasonDetail> buildRecommendationDetails(
            CatalogGame catalogGame,
            List<String> matchedTags,
            int totalScore) {
        Game game = catalogGame.game();
        List<RecommendationReasonDetail> details = new ArrayList<>();

        if (!matchedTags.isEmpty()) {
            details.add(new RecommendationReasonDetail(
                "兴趣匹配",
                "系统识别到你近期在商城浏览、加购、购买以及社区讨论中，多次关注了 "
                    + String.join(" / ", matchedTags)
                    + " 相关关键词，因此提升了这类游戏的匹配权重。"
            ));
        }

        String categoryName = catalogGame.categoryName();
        if (hasText(categoryName) || hasText(game.getTags())) {
            StringBuilder explanation = new StringBuilder("该游戏的");
            if (hasText(categoryName)) {
                explanation.append("类型归属为 ").append(categoryName);
            }
            if (hasText(game.getTags())) {
                if (hasText(categoryName)) {
                    explanation.append("，");
                }
                explanation.append("标签包含 ").append(game.getTags());
            }
            explanation.append("，与当前画像中的兴趣词存在较强重合。");
            details.add(new RecommendationReasonDetail("标签与类型关联", explanation.toString()));
        }

        int discountPercent = catalogGame.discountPercent();
        if (discountPercent > 0) {
            details.add(new RecommendationReasonDetail(
                "价格优势",
                "当前价格相较原价约优惠 " + discountPercent + "%，系统会将价格吸引力一并纳入推荐评分。"
            ));
        }

        if (safeInt(game.getRatingCount()) > 0 || safeInt(game.getDownloadCount()) > 0) {
            details.add(new RecommendationReasonDetail(
                "口碑与热度",
                "该游戏当前累计评价 "
                    + safeInt(game.getRatingCount())
                    + " 条，热度统计 "
                    + safeInt(game.getDownloadCount())
                    + " 次，说明它在平台内具备一定讨论度和稳定反馈。"
            ));
        }

        if (Boolean.TRUE.equals(game.getIsFeatured())) {
            details.add(new RecommendationReasonDetail(
                "平台精选加权",
                "该游戏当前处于平台精选展示位，说明它在内容质量、热度或活动价值上具备额外优势。"
            ));
        }

        details.add(new RecommendationReasonDetail(
            "推荐分说明",
            "当前综合推荐度为 "
                + totalScore
                + " 分，主要由兴趣匹配、社区内容联动、价格、口碑和平台热度共同计算得出。"
        ));

        return details;
    }

    private Set<String> collectForumKeywords(
            UserBehaviorLog log,
            Set<String> knownGameKeywords,
            RecommendationCatalog catalog,
            Map<Long, Set<String>> forumKeywordCache) {
        Set<String> keywords = new LinkedHashSet<>();
        if (log.getReferenceId() != null) {
            keywords.addAll(forumKeywordCache.computeIfAbsent(
                log.getReferenceId(),
                postId -> loadForumKeywords(postId, knownGameKeywords, catalog)
            ));
        }
        if (keywords.isEmpty() && hasText(log.getDetail())) {
            keywords.addAll(matchKeywordsFromTexts(List.of(log.getDetail()), knownGameKeywords));
        }
        return keywords;
    }

    private Set<String> collectCommunityKeywords(
            UserBehaviorLog log,
            Set<String> knownGameKeywords,
            RecommendationCatalog catalog,
            Map<Long, Set<String>> communityKeywordCache) {
        Set<String> keywords = new LinkedHashSet<>();
        if (log.getReferenceId() != null) {
            keywords.addAll(communityKeywordCache.computeIfAbsent(
                log.getReferenceId(),
                postId -> loadCommunityKeywords(postId, knownGameKeywords, catalog)
            ));
        }
        if (keywords.isEmpty() && hasText(log.getDetail())) {
            keywords.addAll(matchKeywordsFromTexts(List.of(log.getDetail()), knownGameKeywords));
        }
        return keywords;
    }

    private Set<String> loadForumKeywords(
            Long postId,
            Set<String> knownGameKeywords,
            RecommendationCatalog catalog) {
        return postRepository.findById(postId)
            .map(post -> {
                Set<String> keywords = new LinkedHashSet<>(matchKeywordsFromTexts(
                    List.of(post.getCategory(), post.getTitle(), post.getContent()),
                    knownGameKeywords
                ));
                if (post.getGameId() != null) {
                    CatalogGame relatedGame = catalog.gamesById().get(post.getGameId());
                    if (relatedGame != null) {
                        keywords.addAll(relatedGame.keywords());
                    }
                }
                return keywords;
            })
            .orElseGet(LinkedHashSet::new);
    }

    private Set<String> loadCommunityKeywords(
            Long postId,
            Set<String> knownGameKeywords,
            RecommendationCatalog catalog) {
        return communityPostRepository.findById(postId)
            .map(post -> {
                List<String> texts = new ArrayList<>();
                texts.add(post.getTitle());
                texts.add(post.getContent());
                texts.addAll(resolveCommunitySectionTexts(post.getSectionId()));

                Set<String> keywords = new LinkedHashSet<>(matchKeywordsFromTexts(texts, knownGameKeywords));
                findMatchingGameIds(keywords, catalog, 2).stream()
                    .map(gameId -> catalog.gamesById().get(gameId))
                    .filter(Objects::nonNull)
                    .forEach(game -> keywords.addAll(game.keywords()));
                return keywords;
            })
            .orElseGet(LinkedHashSet::new);
    }

    private List<String> resolveCommunitySectionTexts(Long sectionId) {
        if (sectionId == null) {
            return List.of();
        }
        return communitySectionRepository.findById(sectionId)
            .map(section -> {
                List<String> texts = new ArrayList<>();
                texts.add(section.getName());
                texts.add(section.getDescription());
                return texts;
            })
            .orElseGet(List::of);
    }

    private List<Long> findMatchingGameIds(
            Set<String> communityKeywords,
            RecommendationCatalog catalog,
            int limit) {
        if (communityKeywords == null || communityKeywords.isEmpty() || catalog.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> matchScores = new HashMap<>();
        communityKeywords.forEach(keyword -> resolveCandidateGameIdsForKeyword(keyword, catalog)
            .forEach(gameId -> matchScores.merge(gameId, 1, Integer::sum)));

        return matchScores.entrySet().stream()
            .sorted(Comparator
                .<Map.Entry<Long, Integer>>comparingInt(Map.Entry::getValue)
                .reversed()
                .thenComparing(
                    entry -> catalog.gamesById().get(entry.getKey()).baseScore(),
                    Comparator.reverseOrder()
                ))
            .limit(Math.max(1, limit))
            .map(Map.Entry::getKey)
            .toList();
    }

    private Set<Long> resolveCandidateGameIdsForKeyword(String keyword, RecommendationCatalog catalog) {
        if (catalog.isEmpty() || !hasText(keyword)) {
            return Set.of();
        }

        Set<Long> matchedIds = new LinkedHashSet<>();
        String normalizedKeyword = normalizeKeyword(keyword);
        if (hasText(normalizedKeyword)) {
            matchedIds.addAll(catalog.keywordToGameIds().getOrDefault(normalizedKeyword, Set.of()));
        }

        String searchKey = toSearchKey(keyword);
        if (!hasText(searchKey)) {
            return matchedIds;
        }

        matchedIds.addAll(catalog.searchKeyToGameIds().getOrDefault(searchKey, Set.of()));
        if (!matchedIds.isEmpty()) {
            return matchedIds;
        }

        catalog.searchKeyToGameIds().forEach((candidateKey, gameIds) -> {
            if (candidateKey.contains(searchKey) || searchKey.contains(candidateKey)) {
                matchedIds.addAll(gameIds);
            }
        });
        return matchedIds;
    }

    private int countKeywordMatches(Set<String> sourceKeywords, Set<String> targetKeywords) {
        if (sourceKeywords == null || sourceKeywords.isEmpty() || targetKeywords == null || targetKeywords.isEmpty()) {
            return 0;
        }
        return (int) sourceKeywords.stream()
            .filter(keyword -> matchesAnyKeyword(keyword, targetKeywords))
            .count();
    }

    private int calculatePreferenceMatchScore(Map<String, Integer> preferenceScores, Set<String> candidateKeywords) {
        if (preferenceScores.isEmpty() || candidateKeywords.isEmpty()) {
            return 0;
        }
        return preferenceScores.entrySet().stream()
            .filter(entry -> matchesAnyKeyword(entry.getKey(), candidateKeywords))
            .mapToInt(Map.Entry::getValue)
            .sum();
    }

    private List<String> findTopMatchedKeywords(
            Map<String, Integer> preferenceScores,
            Set<String> candidateKeywords,
            int limit) {
        if (preferenceScores.isEmpty() || candidateKeywords.isEmpty()) {
            return List.of();
        }
        return preferenceScores.entrySet().stream()
            .filter(entry -> matchesAnyKeyword(entry.getKey(), candidateKeywords))
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .distinct()
            .limit(Math.max(1, limit))
            .toList();
    }

    private boolean matchesAnyKeyword(String sourceKeyword, Set<String> candidateKeywords) {
        return candidateKeywords.stream().anyMatch(candidate -> keywordsOverlap(sourceKeyword, candidate));
    }

    private boolean keywordsOverlap(String left, String right) {
        String leftSearchKey = toSearchKey(left);
        String rightSearchKey = toSearchKey(right);
        if (leftSearchKey.length() < 2 || rightSearchKey.length() < 2) {
            return false;
        }
        return leftSearchKey.equals(rightSearchKey)
            || leftSearchKey.contains(rightSearchKey)
            || rightSearchKey.contains(leftSearchKey);
    }

    private Set<String> collectGameKeywords(Game game, Map<Long, String> categoryNameCache) {
        return collectGameKeywords(game, new HashMap<>(), categoryNameCache);
    }

    private Set<String> collectGameKeywords(
            Game game,
            Map<Long, Set<String>> gameKeywordCache,
            Map<Long, String> categoryNameCache) {
        if (game == null || game.getId() == null) {
            return Set.of();
        }
        return gameKeywordCache.computeIfAbsent(game.getId(), gameId -> {
            Set<String> keywords = new LinkedHashSet<>();
            keywords.addAll(normalizeTags(game.getTags()));
            addKeywordIfPresent(keywords, game.getName());
            addKeywordIfPresent(keywords, game.getDeveloper());
            addKeywordIfPresent(keywords, game.getPublisher());
            addKeywordIfPresent(keywords, resolveCategoryName(game.getCategoryId(), categoryNameCache));
            return keywords;
        });
    }

    private String resolveCategoryName(Long categoryId, Map<Long, String> categoryNameCache) {
        if (categoryId == null) {
            return null;
        }
        return categoryNameCache.computeIfAbsent(
            categoryId,
            id -> categoryRepository.findById(id).map(Category::getName).orElse(null)
        );
    }

    private void addKeywordPreferences(Map<String, Integer> scores, Collection<String> keywords, int weight) {
        if (keywords == null || keywords.isEmpty() || weight <= 0) {
            return;
        }
        keywords.stream()
            .map(this::normalizeKeyword)
            .filter(this::isMeaningfulKeyword)
            .forEach(keyword -> scores.merge(keyword, weight, Integer::sum));
    }

    private Set<String> matchKeywordsFromTexts(Collection<String> texts, Set<String> knownKeywords) {
        if (texts == null || texts.isEmpty() || knownKeywords == null || knownKeywords.isEmpty()) {
            return Set.of();
        }

        String searchableText = texts.stream()
            .filter(this::hasText)
            .map(this::toSearchKey)
            .collect(Collectors.joining(" "));
        if (!hasText(searchableText)) {
            return Set.of();
        }

        return knownKeywords.stream()
            .map(this::normalizeKeyword)
            .filter(this::isMeaningfulKeyword)
            .filter(keyword -> searchableText.contains(toSearchKey(keyword)))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void addKeywordIfPresent(Set<String> keywords, String keyword) {
        String normalized = normalizeKeyword(keyword);
        if (isMeaningfulKeyword(normalized)) {
            keywords.add(normalized);
        }
    }

    private String normalizeKeyword(String value) {
        if (!hasText(value)) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT)
            .replace('\u3000', ' ')
            .trim()
            .replaceAll("\\s+", " ");
    }

    private String toSearchKey(String value) {
        return normalizeKeyword(value)
            .replaceAll("[\\s\\p{Punct}，。！？；：“”‘’（）【】《》、]+", "");
    }

    private boolean isMeaningfulKeyword(String keyword) {
        String searchKey = toSearchKey(keyword);
        if (searchKey.length() < 2) {
            return false;
        }
        return !Set.of("游戏", "玩家", "社区", "帖子", "内容", "讨论", "分享", "平台").contains(searchKey);
    }

    private void evaluateBadges(Long userId) {
        if (userId == null) {
            return;
        }

        long libraryCount = userGameRepository.countByUserId(userId);
        long orderCount = gameOrderRepository.countByUserId(userId);
        long totalPosts = postRepository.countByUserIdAndStatus(userId, Post.PostStatus.PUBLISHED)
            + communityPostRepository.countByUserIdAndStatus(userId, CommunityPost.PostStatus.PUBLISHED);
        long totalComments = commentRepository.countByUserIdAndStatus(
            userId,
            com.gamestore.entity.Comment.CommentStatus.PUBLISHED
        ) + communityCommentRepository.countByUserIdAndStatus(userId, CommunityComment.CommentStatus.PUBLISHED);
        long distinctViewedGames = userBehaviorLogRepository.findTop300ByUserIdOrderByCreatedAtDesc(userId).stream()
            .filter(log -> log.getBehaviorType() == UserBehaviorLog.BehaviorType.VIEW_GAME)
            .map(UserBehaviorLog::getGameId)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(HashSet::new))
            .size();
        int streak = calculateCheckInStreak(userId);

        awardBadgeIfNeeded(userId, "COMMUNITY_VOICE", "社区发声者", "已完成首次社区内容发布", totalPosts >= 1);
        awardBadgeIfNeeded(userId, "DISCUSSION_DRIVER", "讨论活跃者", "累计评论达到 5 次，具备持续互动能力", totalComments >= 5);
        awardBadgeIfNeeded(userId, "GAME_EXPLORER", "兴趣探索家", "已浏览并研究多款游戏，兴趣画像开始清晰", distinctViewedGames >= 6);
        awardBadgeIfNeeded(userId, "LIBRARY_COLLECTOR", "游戏收藏家", "游戏库累计达到 5 款，具备稳定消费特征", libraryCount >= 5);
        awardBadgeIfNeeded(userId, "DECISION_MAKER", "购前决策官", "累计完成 3 笔订单，形成较成熟的购买偏好", orderCount >= 3);
        awardBadgeIfNeeded(userId, "CHECKIN_STREAK", "坚持打卡者", "已连续签到 3 天，活跃度表现稳定", streak >= 3);
    }

    private void awardBadgeIfNeeded(Long userId, String code, String name, String description, boolean unlocked) {
        if (!unlocked || userBadgeRepository.existsByUserIdAndCode(userId, code)) {
            return;
        }

        UserBadge badge = new UserBadge();
        badge.setUserId(userId);
        badge.setCode(code);
        badge.setName(name);
        badge.setDescription(description);
        userBadgeRepository.save(badge);
    }

    private int calculateCheckInStreak(Long userId) {
        List<LocalDate> dates = userBehaviorLogRepository.findByUserIdAndBehaviorTypeOrderByCreatedAtDesc(
                userId,
                UserBehaviorLog.BehaviorType.DAILY_CHECK_IN
            ).stream()
            .map(log -> log.getCreatedAt().toLocalDate())
            .distinct()
            .toList();

        if (dates.isEmpty()) {
            return 0;
        }

        Set<LocalDate> dateSet = new HashSet<>(dates);
        LocalDate cursor = dateSet.contains(LocalDate.now()) ? LocalDate.now() : dates.get(0);
        int streak = 0;
        while (dateSet.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private void changeUserPoints(Long userId, int delta, String description) {
        User user = getRequiredUser(userId);
        int currentPoints = user.getPoints() == null ? 0 : user.getPoints();
        int newPoints = currentPoints + delta;
        if (newPoints < 0) {
            throw new CustomException("积分余额不足");
        }

        user.setPoints(newPoints);
        userRepository.save(user);

        PointTransaction transaction = new PointTransaction();
        transaction.setUserId(userId);
        transaction.setChangeAmount(delta);
        transaction.setBalanceAfter(newPoints);
        transaction.setType(delta >= 0 ? PointTransaction.TransactionType.EARN : PointTransaction.TransactionType.SPEND);
        transaction.setDescription(description);
        pointTransactionRepository.save(transaction);
    }

    private User getRequiredUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException("用户不存在"));
    }

    private List<RecommendationResponse> getOrBuildRecommendationList(Long userId) {
        long cacheKey = resolveRecommendationCacheKey(userId);
        long now = System.currentTimeMillis();
        CachedRecommendationList cached = recommendationCache.get(cacheKey);
        if (cached != null && cached.expiresAtMillis() > now) {
            return cached.recommendations();
        }

        List<RecommendationResponse> recommendations = buildRecommendationList(userId);
        recommendationCache.put(
            cacheKey,
            new CachedRecommendationList(recommendations, now + USER_RECOMMENDATION_TTL_MILLIS)
        );
        return recommendations;
    }

    private List<RecommendationResponse> buildRecommendationList(Long userId) {
        RecommendationCatalog catalog = getRecommendationCatalog();
        if (catalog.isEmpty()) {
            return List.of();
        }

        Map<String, Integer> preferenceScores = buildPreferenceScores(userId, catalog);
        Set<Long> excludedGameIds = resolveExcludedGameIds(userId);
        List<Long> candidateIds = preferenceScores.isEmpty()
            ? catalog.defaultRankedGameIds()
            : selectRecommendationCandidateIds(preferenceScores, excludedGameIds, catalog);

        List<ScoredRecommendation> scoredRecommendations = candidateIds.stream()
            .filter(gameId -> !excludedGameIds.contains(gameId))
            .map(catalog.gamesById()::get)
            .filter(Objects::nonNull)
            .map(candidate -> toRecommendation(candidate, preferenceScores))
            .toList();

        List<ScoredRecommendation> rankedRecommendations = preferenceScores.isEmpty()
            ? scoredRecommendations
            : scoredRecommendations.stream()
                .sorted(Comparator.comparingInt(ScoredRecommendation::score).reversed())
                .toList();

        return deduplicateRecommendations(rankedRecommendations).stream()
            .map(ScoredRecommendation::response)
            .toList();
    }

    private List<Long> selectRecommendationCandidateIds(
            Map<String, Integer> preferenceScores,
            Set<Long> excludedGameIds,
            RecommendationCatalog catalog) {
        if (catalog.isEmpty()) {
            return List.of();
        }

        int targetCandidateCount = Math.min(
            catalog.gamesById().size(),
            Math.max(36, Math.min(MAX_RECOMMENDATION_CANDIDATES, Math.max(48, preferenceScores.size() * 6)))
        );

        LinkedHashSet<Long> candidateIds = new LinkedHashSet<>();
        List<String> topKeywords = preferenceScores.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(12)
            .map(Map.Entry::getKey)
            .toList();

        for (String keyword : topKeywords) {
            if (candidateIds.size() >= targetCandidateCount) {
                break;
            }
            for (Long gameId : resolveCandidateGameIdsForKeyword(keyword, catalog)) {
                if (!excludedGameIds.contains(gameId)) {
                    candidateIds.add(gameId);
                }
                if (candidateIds.size() >= targetCandidateCount) {
                    break;
                }
            }
        }

        for (Long gameId : catalog.defaultRankedGameIds()) {
            if (candidateIds.size() >= targetCandidateCount) {
                break;
            }
            if (!excludedGameIds.contains(gameId)) {
                candidateIds.add(gameId);
            }
        }
        return new ArrayList<>(candidateIds);
    }

    private RecommendationCatalog getRecommendationCatalog() {
        long now = System.currentTimeMillis();
        RecommendationCatalog currentCatalog = recommendationCatalog;
        if (now < recommendationCatalogExpiresAt) {
            return currentCatalog;
        }

        synchronized (recommendationCatalogMonitor) {
            if (now < recommendationCatalogExpiresAt) {
                return recommendationCatalog;
            }
            recommendationCatalog = rebuildRecommendationCatalog();
            recommendationCatalogExpiresAt = now + RECOMMENDATION_CATALOG_TTL_MILLIS;
            recommendationCache.clear();
            return recommendationCatalog;
        }
    }

    private RecommendationCatalog rebuildRecommendationCatalog() {
        List<Game> activeGames = gameRepository.findByStatusOrderByCreatedAtDesc(Game.GameStatus.ACTIVE);
        if (activeGames.isEmpty()) {
            return RecommendationCatalog.empty();
        }

        Map<Long, String> categoryNames = activeGames.stream()
            .map(Game::getCategoryId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.collectingAndThen(
                Collectors.toList(),
                categoryIds -> categoryRepository.findAllById(categoryIds).stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName, (left, right) -> left, HashMap::new))
            ));

        Map<Long, CatalogGame> gamesById = new LinkedHashMap<>();
        Map<String, Set<Long>> keywordToGameIds = new HashMap<>();
        Map<String, Set<Long>> searchKeyToGameIds = new HashMap<>();

        for (Game game : activeGames) {
            String categoryName = categoryNames.get(game.getCategoryId());
            Set<String> keywords = new LinkedHashSet<>();
            keywords.addAll(normalizeTags(game.getTags()));
            addKeywordIfPresent(keywords, game.getName());
            addKeywordIfPresent(keywords, game.getDeveloper());
            addKeywordIfPresent(keywords, game.getPublisher());
            addKeywordIfPresent(keywords, categoryName);

            int popularityScore = Math.min(18, safeInt(game.getDownloadCount()) / 40)
                + Math.min(16, safeInt(game.getRatingCount()) / 8);
            int featuredScore = Boolean.TRUE.equals(game.getIsFeatured()) ? 6 : 0;
            int discountPercent = resolveDiscountPercent(game);
            int discountScore = discountPercent > 0 ? Math.min(12, discountPercent) : 0;
            int baseScore = Math.max(38, Math.min(98, safeRatingScore(game) + popularityScore + featuredScore + discountScore));

            CatalogGame catalogGame = new CatalogGame(
                game,
                new LinkedHashSet<>(keywords),
                categoryName,
                baseScore,
                discountPercent
            );
            gamesById.put(game.getId(), catalogGame);

            for (String keyword : keywords) {
                keywordToGameIds.computeIfAbsent(keyword, key -> new LinkedHashSet<>()).add(game.getId());
                String searchKey = toSearchKey(keyword);
                if (hasText(searchKey)) {
                    searchKeyToGameIds.computeIfAbsent(searchKey, key -> new LinkedHashSet<>()).add(game.getId());
                }
            }
        }

        List<Long> defaultRankedGameIds = gamesById.values().stream()
            .sorted(Comparator
                .comparingInt(CatalogGame::baseScore)
                .reversed()
                .thenComparing(
                    catalogGame -> catalogGame.game().getCreatedAt(),
                    Comparator.nullsLast(Comparator.reverseOrder())
                ))
            .map(catalogGame -> catalogGame.game().getId())
            .toList();

        return new RecommendationCatalog(
            gamesById,
            keywordToGameIds,
            searchKeyToGameIds,
            defaultRankedGameIds,
            new LinkedHashSet<>(keywordToGameIds.keySet())
        );
    }

    private Set<Long> resolveExcludedGameIds(Long userId) {
        if (userId == null) {
            return Set.of();
        }
        return userGameRepository.findByUserIdOrderByAcquiredAtDesc(userId).stream()
            .map(UserGame::getGameId)
            .collect(Collectors.toSet());
    }

    private void invalidateRecommendationCache(Long userId) {
        recommendationCache.remove(resolveRecommendationCacheKey(userId));
    }

    private long resolveRecommendationCacheKey(Long userId) {
        return userId == null ? GUEST_RECOMMENDATION_CACHE_KEY : userId;
    }

    private List<String> normalizeTags(String rawTags) {
        if (!hasText(rawTags)) {
            return List.of();
        }
        return List.of(rawTags.split("[,，/]")).stream()
            .map(String::trim)
            .filter(tag -> !tag.isEmpty())
            .map(String::toLowerCase)
            .distinct()
            .toList();
    }

    private List<ScoredRecommendation> deduplicateRecommendations(List<ScoredRecommendation> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return List.of();
        }

        Map<String, ScoredRecommendation> uniqueRecommendations = new LinkedHashMap<>();
        for (ScoredRecommendation recommendation : recommendations) {
            if (recommendation == null || recommendation.response() == null) {
                continue;
            }
            uniqueRecommendations.putIfAbsent(buildRecommendationUniqueKey(recommendation.response()), recommendation);
        }
        return new ArrayList<>(uniqueRecommendations.values());
    }

    private String buildRecommendationUniqueKey(RecommendationResponse response) {
        String normalizedName = normalizeKeyword(response.name());
        if (hasText(normalizedName)) {
            return normalizedName;
        }
        return response.id() == null ? "" : "id:" + response.id();
    }

    private BigDecimal resolveCurrentPrice(Game game) {
        if (game.getDiscountPrice() != null && game.getDiscountPrice().compareTo(BigDecimal.ZERO) >= 0) {
            return game.getDiscountPrice();
        }
        return game.getPrice() == null ? BigDecimal.ZERO : game.getPrice();
    }

    private int resolveDiscountPercent(Game game) {
        if (game.getPrice() == null || game.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        BigDecimal currentPrice = resolveCurrentPrice(game);
        if (currentPrice.compareTo(game.getPrice()) >= 0) {
            return 0;
        }
        return game.getPrice()
            .subtract(currentPrice)
            .multiply(BigDecimal.valueOf(100))
            .divide(game.getPrice(), 0, RoundingMode.DOWN)
            .intValue();
    }

    private int safeRatingScore(Game game) {
        BigDecimal rating = game.getRating() == null ? BigDecimal.ZERO : game.getRating();
        return rating.multiply(BigDecimal.valueOf(6)).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private int priceAffordabilityBonus(BigDecimal currentPrice) {
        if (currentPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 20;
        }
        if (currentPrice.compareTo(new BigDecimal("68")) <= 0) {
            return 14;
        }
        if (currentPrice.compareTo(new BigDecimal("128")) <= 0) {
            return 8;
        }
        return 2;
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String safeScale(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(1, RoundingMode.HALF_UP).toPlainString();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private String resolveLevelTitle(int level) {
        if (level >= 8) {
            return "策略型策展者";
        }
        if (level >= 6) {
            return "高活跃玩家";
        }
        if (level >= 4) {
            return "兴趣成长者";
        }
        if (level >= 2) {
            return "社区探索者";
        }
        return "新晋玩家";
    }

    private record ScoredRecommendation(int score, RecommendationResponse response) {
    }

    private record CachedRecommendationList(List<RecommendationResponse> recommendations, long expiresAtMillis) {
    }

    private record CatalogGame(
            Game game,
            Set<String> keywords,
            String categoryName,
            int baseScore,
            int discountPercent) {
    }

    private record RecommendationCatalog(
            Map<Long, CatalogGame> gamesById,
            Map<String, Set<Long>> keywordToGameIds,
            Map<String, Set<Long>> searchKeyToGameIds,
            List<Long> defaultRankedGameIds,
            Set<String> knownKeywords) {

        private static RecommendationCatalog empty() {
            return new RecommendationCatalog(Map.of(), Map.of(), Map.of(), List.of(), Set.of());
        }

        private boolean isEmpty() {
            return gamesById.isEmpty();
        }
    }
}
