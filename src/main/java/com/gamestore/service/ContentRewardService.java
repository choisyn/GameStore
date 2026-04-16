package com.gamestore.service;

import com.gamestore.dto.response.ContentRewardActionResponse;
import com.gamestore.dto.response.ContentRewardSummaryResponse;
import com.gamestore.dto.response.RewardSupporterResponse;
import com.gamestore.entity.ContentReward;
import com.gamestore.entity.GameGuide;
import com.gamestore.entity.PointTransaction;
import com.gamestore.entity.Post;
import com.gamestore.entity.User;
import com.gamestore.exception.CustomException;
import com.gamestore.repository.ContentRewardRepository;
import com.gamestore.repository.GameGuideRepository;
import com.gamestore.repository.PointTransactionRepository;
import com.gamestore.repository.PostRepository;
import com.gamestore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ContentRewardService {

    private final ContentRewardRepository contentRewardRepository;
    private final UserRepository userRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final PostRepository postRepository;
    private final GameGuideRepository gameGuideRepository;

    public ContentRewardService(
        ContentRewardRepository contentRewardRepository,
        UserRepository userRepository,
        PointTransactionRepository pointTransactionRepository,
        PostRepository postRepository,
        GameGuideRepository gameGuideRepository
    ) {
        this.contentRewardRepository = contentRewardRepository;
        this.userRepository = userRepository;
        this.pointTransactionRepository = pointTransactionRepository;
        this.postRepository = postRepository;
        this.gameGuideRepository = gameGuideRepository;
    }

    @Transactional
    public ContentRewardActionResponse rewardForumPost(Long postId, Long giverUserId, Integer points) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new CustomException("帖子不存在"));
        if (post.getStatus() == Post.PostStatus.DELETED) {
            throw new CustomException("帖子不存在");
        }
        return rewardTarget(
            ContentReward.TargetType.FORUM_POST,
            postId,
            giverUserId,
            post.getUserId(),
            points,
            "帖子《" + post.getTitle() + "》"
        );
    }

    @Transactional
    public ContentRewardActionResponse rewardGuide(Long guideId, Long giverUserId, Integer points) {
        GameGuide guide = gameGuideRepository.findByIdAndStatus(guideId, GameGuide.GuideStatus.PUBLISHED)
            .orElseThrow(() -> new CustomException("攻略不存在"));
        return rewardTarget(
            ContentReward.TargetType.GAME_GUIDE,
            guideId,
            giverUserId,
            guide.getAuthorId(),
            points,
            "攻略《" + guide.getTitle() + "》"
        );
    }

    @Transactional(readOnly = true)
    public ContentRewardSummaryResponse getForumPostRewardPreview(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new CustomException("帖子不存在"));
        if (post.getStatus() == Post.PostStatus.DELETED) {
            throw new CustomException("帖子不存在");
        }
        return buildSummary(ContentReward.TargetType.FORUM_POST, postId, 3);
    }

    @Transactional(readOnly = true)
    public ContentRewardSummaryResponse getForumPostRewardAll(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new CustomException("帖子不存在"));
        if (post.getStatus() == Post.PostStatus.DELETED) {
            throw new CustomException("帖子不存在");
        }
        return buildSummary(ContentReward.TargetType.FORUM_POST, postId, Integer.MAX_VALUE);
    }

    @Transactional(readOnly = true)
    public ContentRewardSummaryResponse getGuideRewardPreview(Long guideId) {
        gameGuideRepository.findByIdAndStatus(guideId, GameGuide.GuideStatus.PUBLISHED)
            .orElseThrow(() -> new CustomException("攻略不存在"));
        return buildSummary(ContentReward.TargetType.GAME_GUIDE, guideId, 3);
    }

    @Transactional(readOnly = true)
    public ContentRewardSummaryResponse getGuideRewardAll(Long guideId) {
        gameGuideRepository.findByIdAndStatus(guideId, GameGuide.GuideStatus.PUBLISHED)
            .orElseThrow(() -> new CustomException("攻略不存在"));
        return buildSummary(ContentReward.TargetType.GAME_GUIDE, guideId, Integer.MAX_VALUE);
    }

    private ContentRewardActionResponse rewardTarget(
        ContentReward.TargetType targetType,
        Long targetId,
        Long giverUserId,
        Long receiverUserId,
        Integer points,
        String targetLabel
    ) {
        if (giverUserId == null) {
            throw new CustomException("请先登录");
        }
        if (points == null || points <= 0) {
            throw new CustomException("打赏积分必须大于 0");
        }
        if (points > 50000) {
            throw new CustomException("单次打赏积分不能超过 50000");
        }
        if (giverUserId.equals(receiverUserId)) {
            throw new CustomException("不能给自己打赏");
        }

        User giver = getUserOrThrow(giverUserId);
        User receiver = getUserOrThrow(receiverUserId);

        int giverBalance = giver.getPoints() == null ? 0 : giver.getPoints();
        if (giverBalance < points) {
            throw new CustomException("积分余额不足");
        }

        int receiverBalance = receiver.getPoints() == null ? 0 : receiver.getPoints();
        giver.setPoints(giverBalance - points);
        receiver.setPoints(receiverBalance + points);
        userRepository.save(giver);
        userRepository.save(receiver);

        ContentReward reward = new ContentReward();
        reward.setTargetType(targetType);
        reward.setTargetId(targetId);
        reward.setGiverUserId(giverUserId);
        reward.setReceiverUserId(receiverUserId);
        reward.setPointsAmount(points);
        contentRewardRepository.save(reward);

        savePointTransaction(
            giverUserId,
            -points,
            giver.getPoints(),
            PointTransaction.TransactionType.SPEND,
            "打赏" + targetLabel
        );
        savePointTransaction(
            receiverUserId,
            points,
            receiver.getPoints(),
            PointTransaction.TransactionType.EARN,
            "收到来自 " + giver.getUsername() + " 的打赏：" + targetLabel
        );

        ContentRewardSummaryResponse summary = buildSummary(targetType, targetId, 3);
        ContentRewardActionResponse response = new ContentRewardActionResponse();
        response.setRewardedPoints(points);
        response.setSenderPointsBalance(giver.getPoints());
        response.setReceiverPointsBalance(receiver.getPoints());
        response.setTotalRewardPoints(summary.getTotalRewardPoints());
        response.setSupporterCount(summary.getSupporterCount());
        return response;
    }

    private ContentRewardSummaryResponse buildSummary(ContentReward.TargetType targetType, Long targetId, int limit) {
        List<ContentReward> rewards = contentRewardRepository.findByTargetTypeAndTargetIdOrderByCreatedAtAsc(targetType, targetId);
        Map<Long, RewardAggregation> aggregationMap = new LinkedHashMap<>();
        int totalRewardPoints = 0;

        for (ContentReward reward : rewards) {
            totalRewardPoints += reward.getPointsAmount() == null ? 0 : reward.getPointsAmount();
            RewardAggregation aggregation = aggregationMap.computeIfAbsent(
                reward.getGiverUserId(),
                userId -> new RewardAggregation(userId, reward.getCreatedAt())
            );
            aggregation.totalPoints += reward.getPointsAmount() == null ? 0 : reward.getPointsAmount();
        }

        Map<Long, User> userMap = userRepository.findAllById(aggregationMap.keySet()).stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

        List<RewardSupporterResponse> supporters = new ArrayList<>();
        List<RewardAggregation> sortedAggregations = aggregationMap.values().stream()
            .sorted(
                Comparator.comparingInt(RewardAggregation::getTotalPoints).reversed()
                    .thenComparing(RewardAggregation::getFirstRewardAt)
                    .thenComparing(RewardAggregation::getUserId)
            )
            .collect(Collectors.toList());

        for (RewardAggregation aggregation : sortedAggregations) {
            User user = userMap.get(aggregation.userId);
            RewardSupporterResponse response = new RewardSupporterResponse();
            response.setUserId(aggregation.userId);
            response.setUsername(user != null ? user.getUsername() : "匿名用户");
            response.setAvatar(user != null ? user.getAvatar() : null);
            response.setTotalPoints(aggregation.totalPoints);
            response.setFirstRewardAt(aggregation.firstRewardAt);
            supporters.add(response);
            if (supporters.size() >= limit) {
                break;
            }
        }

        ContentRewardSummaryResponse summary = new ContentRewardSummaryResponse();
        summary.setTotalRewardPoints(totalRewardPoints);
        summary.setSupporterCount(aggregationMap.size());
        summary.setSupporters(supporters);
        return summary;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException("用户不存在"));
    }

    private void savePointTransaction(
        Long userId,
        int changeAmount,
        int balanceAfter,
        PointTransaction.TransactionType type,
        String description
    ) {
        PointTransaction transaction = new PointTransaction();
        transaction.setUserId(userId);
        transaction.setChangeAmount(changeAmount);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setType(type);
        transaction.setDescription(description);
        pointTransactionRepository.save(transaction);
    }

    private static class RewardAggregation {
        private final Long userId;
        private final LocalDateTime firstRewardAt;
        private int totalPoints = 0;

        private RewardAggregation(Long userId, LocalDateTime firstRewardAt) {
            this.userId = userId;
            this.firstRewardAt = firstRewardAt;
        }

        private Long getUserId() {
            return userId;
        }

        private LocalDateTime getFirstRewardAt() {
            return firstRewardAt;
        }

        private int getTotalPoints() {
            return totalPoints;
        }
    }
}
