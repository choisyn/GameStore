package com.gamestore.service;

import com.gamestore.dto.response.CartItemResponse;
import com.gamestore.dto.response.CartSummaryResponse;
import com.gamestore.dto.response.CheckoutOptionsResponse;
import com.gamestore.dto.response.LibraryGameResponse;
import com.gamestore.dto.response.OwnedDiscountCardResponse;
import com.gamestore.dto.response.OrderItemResponse;
import com.gamestore.dto.response.OrderResponse;
import com.gamestore.dto.response.PointShopItemResponse;
import com.gamestore.dto.response.PointTransactionResponse;
import com.gamestore.util.GameNameFormatter;
import com.gamestore.entity.CartItem;
import com.gamestore.entity.Game;
import com.gamestore.entity.GameOrder;
import com.gamestore.entity.GameOrderItem;
import com.gamestore.entity.PointTransaction;
import com.gamestore.entity.User;
import com.gamestore.entity.UserDiscountCard;
import com.gamestore.entity.UserGame;
import com.gamestore.exception.CustomException;
import com.gamestore.repository.CartItemRepository;
import com.gamestore.repository.GameOrderItemRepository;
import com.gamestore.repository.GameOrderRepository;
import com.gamestore.repository.GameRepository;
import com.gamestore.repository.PointTransactionRepository;
import com.gamestore.repository.UserDiscountCardRepository;
import com.gamestore.repository.UserGameRepository;
import com.gamestore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class StoreService {

    private static final DateTimeFormatter ORDER_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final int POINTS_EARN_RATE = 2;
    private static final int POINTS_REDEEM_RATE = 100;

    private static final List<PointShopDefinition> POINT_SHOP_CATALOG = List.of(
        new PointShopDefinition(
            "CARD_90_CAP_200",
            "9折消费卡",
            "结算时立减订单金额的10%，单次最高优惠200元，更适合1000元以上订单。",
            10000,
            new BigDecimal("0.10"),
            new BigDecimal("200.00")
        ),
        new PointShopDefinition(
            "CARD_80_CAP_100",
            "8折消费卡",
            "结算时立减订单金额的20%，单次最高优惠100元，更适合250元以上订单。",
            5000,
            new BigDecimal("0.20"),
            new BigDecimal("100.00")
        ),
        new PointShopDefinition(
            "CARD_70_CAP_50",
            "7折消费卡",
            "结算时立减订单金额的30%，单次最高优惠50元，更适合85元以上订单。",
            2500,
            new BigDecimal("0.30"),
            new BigDecimal("50.00")
        )
    );

    private static final Map<String, PointShopDefinition> POINT_SHOP_LOOKUP = POINT_SHOP_CATALOG.stream()
        .collect(Collectors.toMap(
            PointShopDefinition::code,
            item -> item,
            (left, right) -> left,
            LinkedHashMap::new
        ));

    private final CartItemRepository cartItemRepository;
    private final GameRepository gameRepository;
    private final UserGameRepository userGameRepository;
    private final GameOrderRepository gameOrderRepository;
    private final GameOrderItemRepository gameOrderItemRepository;
    private final UserRepository userRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final UserDiscountCardRepository userDiscountCardRepository;
    private final InnovationService innovationService;

    public StoreService(
            CartItemRepository cartItemRepository,
            GameRepository gameRepository,
            UserGameRepository userGameRepository,
            GameOrderRepository gameOrderRepository,
            GameOrderItemRepository gameOrderItemRepository,
            UserRepository userRepository,
            PointTransactionRepository pointTransactionRepository,
            UserDiscountCardRepository userDiscountCardRepository,
            InnovationService innovationService) {
        this.cartItemRepository = cartItemRepository;
        this.gameRepository = gameRepository;
        this.userGameRepository = userGameRepository;
        this.gameOrderRepository = gameOrderRepository;
        this.gameOrderItemRepository = gameOrderItemRepository;
        this.userRepository = userRepository;
        this.pointTransactionRepository = pointTransactionRepository;
        this.userDiscountCardRepository = userDiscountCardRepository;
        this.innovationService = innovationService;
    }

    public CartSummaryResponse getCartSummary(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtDesc(userId);
        Map<Long, Game> gameMap = loadGames(cartItems.stream().map(CartItem::getGameId).toList());
        Set<Long> ownedGameIds = getOwnedGameIds(userId);

        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;
        boolean hasOwnedItems = false;

        for (CartItem cartItem : cartItems) {
            Game game = gameMap.get(cartItem.getGameId());
            if (game == null) {
                continue;
            }

            boolean alreadyOwned = ownedGameIds.contains(cartItem.getGameId());
            BigDecimal unitPrice = resolveCurrentPrice(game);
            BigDecimal subtotal = unitPrice;

            if (Boolean.TRUE.equals(cartItem.getSelected()) && !alreadyOwned) {
                totalAmount = totalAmount.add(subtotal);
                totalItems += 1;
            }
            hasOwnedItems = hasOwnedItems || alreadyOwned;

            items.add(new CartItemResponse(
                cartItem.getId(),
                game.getId(),
                game.getName(),
                game.getDisplayName(),
                game.getImageUrl(),
                game.getDescription(),
                unitPrice,
                1,
                subtotal,
                cartItem.getSelected(),
                alreadyOwned
            ));
        }

        return new CartSummaryResponse(items, totalItems, totalAmount, hasOwnedItems);
    }

    @Transactional
    public CartSummaryResponse addToCart(Long userId, Long gameId, Integer quantity) {
        Game game = getActiveGame(gameId);
        if (userGameRepository.existsByUserIdAndGameId(userId, gameId)) {
            throw new CustomException("该游戏已在你的游戏库中");
        }

        CartItem cartItem = cartItemRepository.findByUserIdAndGameId(userId, gameId)
            .orElseGet(CartItem::new);

        cartItem.setUserId(userId);
        cartItem.setGameId(gameId);
        cartItem.setQuantity(1);
        cartItem.setSelected(true);
        cartItem.setUnitPrice(resolveCurrentPrice(game));
        cartItemRepository.save(cartItem);
        innovationService.recordBehavior(
            userId,
            com.gamestore.entity.UserBehaviorLog.BehaviorType.ADD_TO_CART,
            gameId,
            cartItem.getId(),
            "加入购物车"
        );

        return getCartSummary(userId);
    }

    @Transactional
    public CartSummaryResponse updateCartItem(Long userId, Long itemId, Integer quantity, Boolean selected) {
        CartItem cartItem = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new CustomException("购物车项目不存在"));
        if (!cartItem.getUserId().equals(userId)) {
            throw new CustomException("无权修改该购物车项目");
        }

        if (quantity != null) {
            cartItem.setQuantity(1);
        }
        if (selected != null) {
            cartItem.setSelected(selected);
        }

        Game game = getActiveGame(cartItem.getGameId());
        cartItem.setUnitPrice(resolveCurrentPrice(game));
        cartItemRepository.save(cartItem);
        return getCartSummary(userId);
    }

    @Transactional
    public void removeCartItem(Long userId, Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new CustomException("购物车项目不存在"));
        if (!cartItem.getUserId().equals(userId)) {
            throw new CustomException("无权删除该购物车项目");
        }
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    @Transactional
    public OrderResponse checkout(Long userId, GameOrder.PaymentMethod paymentMethod, Long discountCardId, Integer pointsToUse) {
        List<CartItem> selectedItems = cartItemRepository.findByUserIdAndSelectedTrueOrderByCreatedAtDesc(userId);
        if (selectedItems.isEmpty()) {
            throw new CustomException("请选择要结算的商品");
        }

        Map<Long, Game> gameMap = loadGames(selectedItems.stream().map(CartItem::getGameId).toList());
        Set<Long> ownedGameIds = getOwnedGameIds(userId);
        List<CartItem> payableItems = selectedItems.stream()
            .filter(item -> gameMap.containsKey(item.getGameId()))
            .filter(item -> !ownedGameIds.contains(item.getGameId()))
            .toList();

        if (payableItems.isEmpty()) {
            throw new CustomException("请选择未拥有的游戏进行结算");
        }

        BigDecimal totalAmount = payableItems.stream()
            .map(item -> resolveCurrentPrice(gameMap.get(item.getGameId())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        User user = getUserOrThrow(userId);
        UserDiscountCard discountCard = resolveAvailableDiscountCard(userId, discountCardId);
        CheckoutBreakdown breakdown = calculateCheckoutBreakdown(
            totalAmount,
            discountCard,
            pointsToUse,
            user.getPoints() == null ? 0 : user.getPoints()
        );

        GameOrder order = buildOrder(userId, totalAmount, paymentMethod, breakdown);
        List<GameOrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : payableItems) {
            Game game = gameMap.get(cartItem.getGameId());
            if (game == null) {
                continue;
            }

            BigDecimal unitPrice = resolveCurrentPrice(game);
            GameOrderItem orderItem = buildOrderItem(order.getId(), game, unitPrice, 1);
            orderItems.add(orderItem);
            createUserGame(userId, game, order.getId(), unitPrice);
            innovationService.recordBehavior(
                userId,
                com.gamestore.entity.UserBehaviorLog.BehaviorType.PURCHASE_GAME,
                game.getId(),
                order.getId(),
                "完成订单购买"
            );
        }

        gameOrderItemRepository.saveAll(orderItems);

        if (breakdown.pointsUsed() > 0) {
            spendPoints(userId, breakdown.pointsUsed(), "订单 " + order.getOrderNo() + " 使用积分抵扣", order.getId());
        }
        if (discountCard != null) {
            markDiscountCardUsed(discountCard, order.getId());
        }
        grantPoints(userId, order);

        cartItemRepository.deleteAll(payableItems);
        return toOrderResponse(order, orderItems);
    }

    @Transactional
    public OrderResponse claimFreeGame(Long userId, Long gameId) {
        Game game = getActiveGame(gameId);
        if (resolveCurrentPrice(game).compareTo(BigDecimal.ZERO) > 0) {
            throw new CustomException("该游戏不是免费游戏，不能直接领取");
        }
        validateNotOwned(userId, List.of(game));

        GameOrder order = buildOrder(
            userId,
            BigDecimal.ZERO,
            GameOrder.PaymentMethod.FREE,
            new CheckoutBreakdown(null, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO)
        );
        GameOrderItem orderItem = buildOrderItem(order.getId(), game, BigDecimal.ZERO, 1);
        gameOrderItemRepository.save(orderItem);
        createUserGame(userId, game, order.getId(), BigDecimal.ZERO);
        cartItemRepository.findByUserIdAndGameId(userId, gameId).ifPresent(cartItemRepository::delete);
        innovationService.recordBehavior(
            userId,
            com.gamestore.entity.UserBehaviorLog.BehaviorType.CLAIM_FREE_GAME,
            gameId,
            order.getId(),
            "领取免费游戏"
        );

        return toOrderResponse(order, List.of(orderItem));
    }

    public List<OrderResponse> getOrders(Long userId) {
        return gameOrderRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toOrderResponse)
            .toList();
    }

    public List<LibraryGameResponse> getLibrary(Long userId) {
        List<UserGame> userGames = userGameRepository.findByUserIdOrderByAcquiredAtDesc(userId);
        Map<Long, Game> gameMap = loadGames(userGames.stream().map(UserGame::getGameId).toList());

        return userGames.stream()
            .map(userGame -> {
                Game game = gameMap.get(userGame.getGameId());
                if (game == null) {
                    return null;
                }
                return new LibraryGameResponse(
                    game.getId(),
                    game.getName(),
                    game.getDisplayName(),
                    game.getImageUrl(),
                    game.getDescription(),
                    game.getDeveloper(),
                    userGame.getAcquiredPrice(),
                    userGame.getAcquiredAt(),
                    game.getRating()
                );
            })
            .filter(item -> item != null)
            .toList();
    }

    public List<PointTransactionResponse> getRecentPointTransactions(Long userId) {
        return pointTransactionRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(tx -> new PointTransactionResponse(
                tx.getId(),
                tx.getChangeAmount(),
                tx.getBalanceAfter(),
                tx.getType().name(),
                tx.getDescription(),
                tx.getCreatedAt()
            ))
            .toList();
    }

    public CheckoutOptionsResponse getCheckoutOptions(Long userId) {
        User user = getUserOrThrow(userId);
        List<OwnedDiscountCardResponse> availableCards = userDiscountCardRepository
            .findByUserIdAndStatusOrderByCreatedAtDesc(userId, UserDiscountCard.CardStatus.AVAILABLE)
            .stream()
            .map(this::toOwnedDiscountCardResponse)
            .toList();

        return new CheckoutOptionsResponse(
            user.getPoints() == null ? 0 : user.getPoints(),
            POINTS_EARN_RATE,
            POINTS_REDEEM_RATE,
            availableCards
        );
    }

    public List<PointShopItemResponse> getPointShopItems() {
        return POINT_SHOP_CATALOG.stream()
            .map(PointShopDefinition::toResponse)
            .toList();
    }

    @Transactional
    public OwnedDiscountCardResponse redeemPointShopItem(Long userId, String code) {
        PointShopDefinition definition = getPointShopDefinition(code);

        spendPoints(userId, definition.pointsCost(), "兑换积分商品：" + definition.cardName(), null);

        UserDiscountCard card = new UserDiscountCard();
        card.setUserId(userId);
        card.setSourceCode(definition.code());
        card.setCardName(definition.cardName());
        card.setDescription(definition.description());
        card.setPointsCost(definition.pointsCost());
        card.setDiscountRate(definition.discountRate());
        card.setMaxDiscountAmount(definition.maxDiscountAmount());
        card.setStatus(UserDiscountCard.CardStatus.AVAILABLE);
        innovationService.recordBehavior(
            userId,
            com.gamestore.entity.UserBehaviorLog.BehaviorType.REDEEM_POINT_ITEM,
            null,
            null,
            definition.cardName()
        );

        return toOwnedDiscountCardResponse(userDiscountCardRepository.save(card));
    }

    public long countOrders(Long userId) {
        return gameOrderRepository.countByUserId(userId);
    }

    public long countLibraryGames(Long userId) {
        return userGameRepository.countByUserId(userId);
    }

    public long countTotalOrders() {
        return gameOrderRepository.count();
    }

    public BigDecimal getTotalPaidAmount() {
        BigDecimal amount = gameOrderRepository.sumPayableAmountByStatus(GameOrder.OrderStatus.PAID);
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private GameOrder buildOrder(
            Long userId,
            BigDecimal totalAmount,
            GameOrder.PaymentMethod paymentMethod,
            CheckoutBreakdown breakdown) {
        GameOrder order = new GameOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setCouponName(breakdown.couponName());
        order.setCouponDiscountAmount(breakdown.couponDiscountAmount());
        order.setPointsUsed(breakdown.pointsUsed());
        order.setPointsDiscountAmount(breakdown.pointsDiscountAmount());
        order.setPayableAmount(breakdown.payableAmount());
        order.setPointsEarned(calculateEarnedPoints(breakdown.payableAmount()));
        order.setStatus(GameOrder.OrderStatus.PAID);
        order.setPaymentMethod(
            breakdown.payableAmount().compareTo(BigDecimal.ZERO) == 0
                ? GameOrder.PaymentMethod.FREE
                : paymentMethod
        );
        order.setPaidAt(LocalDateTime.now());
        return gameOrderRepository.save(order);
    }

    private GameOrderItem buildOrderItem(Long orderId, Game game, BigDecimal unitPrice, int quantity) {
        GameOrderItem orderItem = new GameOrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setGameId(game.getId());
        orderItem.setGameName(game.getName());
        orderItem.setGameImageUrl(game.getImageUrl());
        orderItem.setUnitPrice(unitPrice);
        orderItem.setQuantity(quantity);
        orderItem.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        return orderItem;
    }

    private void createUserGame(Long userId, Game game, Long orderId, BigDecimal acquiredPrice) {
        if (userGameRepository.existsByUserIdAndGameId(userId, game.getId())) {
            return;
        }

        UserGame userGame = new UserGame();
        userGame.setUserId(userId);
        userGame.setGameId(game.getId());
        userGame.setOrderId(orderId);
        userGame.setAcquiredPrice(acquiredPrice);
        userGameRepository.save(userGame);

        game.setDownloadCount((game.getDownloadCount() == null ? 0 : game.getDownloadCount()) + 1);
        gameRepository.save(game);
    }

    private void grantPoints(Long userId, GameOrder order) {
        int pointsToAdd = order.getPointsEarned() == null ? 0 : order.getPointsEarned();
        if (pointsToAdd <= 0) {
            return;
        }

        changeUserPoints(
            userId,
            pointsToAdd,
            PointTransaction.TransactionType.EARN,
            "订单 " + order.getOrderNo() + " 支付完成，获得积分",
            order.getId()
        );
    }

    private OrderResponse toOrderResponse(GameOrder order) {
        List<GameOrderItem> items = gameOrderItemRepository.findByOrderIdOrderByIdAsc(order.getId());
        return toOrderResponse(order, items);
    }

    private OrderResponse toOrderResponse(GameOrder order, List<GameOrderItem> items) {
        List<OrderItemResponse> itemResponses = items.stream()
            .map(item -> new OrderItemResponse(
                item.getGameId(),
                item.getGameName(),
                GameNameFormatter.toDisplayName(item.getGameName()),
                item.getGameImageUrl(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal()
            ))
            .toList();

        return new OrderResponse(
            order.getId(),
            order.getOrderNo(),
            order.getStatus().name(),
            order.getPaymentMethod().name(),
            order.getTotalAmount(),
            order.getCouponName(),
            order.getCouponDiscountAmount(),
            order.getPointsUsed(),
            order.getPointsDiscountAmount(),
            order.getPayableAmount(),
            order.getPointsEarned(),
            order.getPaidAt(),
            order.getCreatedAt(),
            itemResponses
        );
    }

    private Set<Long> getOwnedGameIds(Long userId) {
        return userGameRepository.findByUserIdOrderByAcquiredAtDesc(userId)
            .stream()
            .map(UserGame::getGameId)
            .collect(Collectors.toCollection(HashSet::new));
    }

    private Map<Long, Game> loadGames(Collection<Long> gameIds) {
        if (gameIds == null || gameIds.isEmpty()) {
            return Map.of();
        }
        return gameRepository.findAllById(gameIds)
            .stream()
            .collect(Collectors.toMap(Game::getId, game -> game, (left, right) -> left, HashMap::new));
    }

    private Game getActiveGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new CustomException("游戏不存在"));
        if (game.getStatus() != Game.GameStatus.ACTIVE) {
            throw new CustomException("该游戏当前不可购买");
        }
        return game;
    }

    private void validateNotOwned(Long userId, Collection<Game> games) {
        List<String> ownedGames = games.stream()
            .filter(game -> userGameRepository.existsByUserIdAndGameId(userId, game.getId()))
            .map(Game::getName)
            .sorted(Comparator.naturalOrder())
            .toList();
        if (!ownedGames.isEmpty()) {
            throw new CustomException("以下游戏已在你的游戏库中：" + String.join("、", ownedGames));
        }
    }

    private BigDecimal resolveCurrentPrice(Game game) {
        if (game.getDiscountPrice() != null && game.getDiscountPrice().compareTo(BigDecimal.ZERO) >= 0) {
            return game.getDiscountPrice();
        }
        return game.getPrice() == null ? BigDecimal.ZERO : game.getPrice();
    }

    private String generateOrderNo() {
        return "GS" + LocalDateTime.now().format(ORDER_TIME_FORMAT) + ThreadLocalRandom.current().nextInt(100, 999);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException("用户不存在"));
    }

    private UserDiscountCard resolveAvailableDiscountCard(Long userId, Long discountCardId) {
        if (discountCardId == null) {
            return null;
        }

        UserDiscountCard card = userDiscountCardRepository.findByIdAndUserId(discountCardId, userId)
            .orElseThrow(() -> new CustomException("所选消费卡不存在"));
        if (card.getStatus() != UserDiscountCard.CardStatus.AVAILABLE) {
            throw new CustomException("所选消费卡已失效或已使用");
        }
        return card;
    }

    private CheckoutBreakdown calculateCheckoutBreakdown(
            BigDecimal totalAmount,
            UserDiscountCard discountCard,
            Integer requestedPoints,
            Integer currentPointsBalance) {
        BigDecimal couponDiscountAmount = BigDecimal.ZERO;
        String couponName = null;

        if (discountCard != null) {
            BigDecimal rawDiscount = totalAmount.multiply(discountCard.getDiscountRate()).setScale(2, RoundingMode.HALF_UP);
            couponDiscountAmount = rawDiscount.min(discountCard.getMaxDiscountAmount()).min(totalAmount);
            if (couponDiscountAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new CustomException("当前消费卡无法用于本次结算");
            }
            couponName = discountCard.getCardName();
        }

        BigDecimal amountAfterCoupon = totalAmount.subtract(couponDiscountAmount).max(BigDecimal.ZERO);
        int safeRequestedPoints = Math.max(requestedPoints == null ? 0 : requestedPoints, 0);
        int safeBalance = Math.max(currentPointsBalance == null ? 0 : currentPointsBalance, 0);
        int maxPointsForThisOrder = amountAfterCoupon
            .multiply(BigDecimal.valueOf(POINTS_REDEEM_RATE))
            .setScale(0, RoundingMode.DOWN)
            .intValue();
        int pointsUsed = Math.min(safeRequestedPoints, Math.min(safeBalance, maxPointsForThisOrder));
        BigDecimal pointsDiscountAmount = calculatePointsDiscountAmount(pointsUsed);
        BigDecimal payableAmount = amountAfterCoupon.subtract(pointsDiscountAmount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        return new CheckoutBreakdown(
            couponName,
            couponDiscountAmount,
            pointsUsed,
            pointsDiscountAmount,
            payableAmount
        );
    }

    private int calculateEarnedPoints(BigDecimal payableAmount) {
        if (payableAmount == null || payableAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return payableAmount
            .multiply(BigDecimal.valueOf(POINTS_EARN_RATE))
            .setScale(0, RoundingMode.DOWN)
            .intValue();
    }

    private BigDecimal calculatePointsDiscountAmount(int pointsUsed) {
        if (pointsUsed <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(pointsUsed)
            .divide(BigDecimal.valueOf(POINTS_REDEEM_RATE), 2, RoundingMode.DOWN);
    }

    private void spendPoints(Long userId, int pointsToSpend, String description, Long orderId) {
        if (pointsToSpend <= 0) {
            return;
        }

        changeUserPoints(
            userId,
            -pointsToSpend,
            PointTransaction.TransactionType.SPEND,
            description,
            orderId
        );
    }

    private void changeUserPoints(Long userId, int delta, PointTransaction.TransactionType type, String description, Long orderId) {
        if (delta == 0) {
            return;
        }

        User user = getUserOrThrow(userId);
        int currentBalance = user.getPoints() == null ? 0 : user.getPoints();
        int newBalance = currentBalance + delta;
        if (newBalance < 0) {
            throw new CustomException("积分余额不足");
        }

        user.setPoints(newBalance);
        userRepository.save(user);

        PointTransaction transaction = new PointTransaction();
        transaction.setUserId(userId);
        transaction.setChangeAmount(delta);
        transaction.setBalanceAfter(newBalance);
        transaction.setType(type);
        transaction.setDescription(description);
        transaction.setOrderId(orderId);
        pointTransactionRepository.save(transaction);
    }

    private void markDiscountCardUsed(UserDiscountCard card, Long orderId) {
        card.setStatus(UserDiscountCard.CardStatus.USED);
        card.setUsedOrderId(orderId);
        card.setUsedAt(LocalDateTime.now());
        userDiscountCardRepository.save(card);
    }

    private OwnedDiscountCardResponse toOwnedDiscountCardResponse(UserDiscountCard card) {
        return new OwnedDiscountCardResponse(
            card.getId(),
            card.getSourceCode(),
            card.getCardName(),
            card.getDescription(),
            card.getPointsCost(),
            card.getDiscountRate(),
            card.getMaxDiscountAmount(),
            card.getCreatedAt()
        );
    }

    private PointShopDefinition getPointShopDefinition(String code) {
        PointShopDefinition definition = POINT_SHOP_LOOKUP.get(code);
        if (definition == null) {
            throw new CustomException("积分商品不存在");
        }
        return definition;
    }

    private record PointShopDefinition(
        String code,
        String cardName,
        String description,
        Integer pointsCost,
        BigDecimal discountRate,
        BigDecimal maxDiscountAmount
    ) {
        PointShopItemResponse toResponse() {
            return new PointShopItemResponse(code, cardName, description, pointsCost, discountRate, maxDiscountAmount);
        }
    }

    private record CheckoutBreakdown(
        String couponName,
        BigDecimal couponDiscountAmount,
        Integer pointsUsed,
        BigDecimal pointsDiscountAmount,
        BigDecimal payableAmount
    ) {
    }
}
