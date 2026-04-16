package com.gamestore.controller;

import com.gamestore.dto.request.AddToCartRequest;
import com.gamestore.dto.request.CheckoutRequest;
import com.gamestore.dto.request.UpdateCartItemRequest;
import com.gamestore.dto.response.ApiResponse;
import com.gamestore.dto.response.CartSummaryResponse;
import com.gamestore.dto.response.CheckoutOptionsResponse;
import com.gamestore.dto.response.LibraryGameResponse;
import com.gamestore.dto.response.OwnedDiscountCardResponse;
import com.gamestore.dto.response.OrderResponse;
import com.gamestore.dto.response.PointShopItemResponse;
import com.gamestore.dto.response.PointTransactionResponse;
import com.gamestore.entity.GameOrder;
import com.gamestore.entity.User;
import com.gamestore.service.CurrentUserService;
import com.gamestore.service.StoreService;
import com.gamestore.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;
    private final CurrentUserService currentUserService;

    public StoreController(StoreService storeService, CurrentUserService currentUserService) {
        this.storeService = storeService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> getCart(HttpServletRequest request) {
        User user = currentUserService.getCurrentUser(request);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        return ResponseUtil.success("获取购物车成功", storeService.getCartSummary(user.getId()));
    }

    @PostMapping("/cart/items")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> addToCart(
            @RequestBody AddToCartRequest request,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        CartSummaryResponse response = storeService.addToCart(user.getId(), request.getGameId(), request.getQuantity());
        return ResponseUtil.success("加入购物车成功", response);
    }

    @PutMapping("/cart/items/{itemId}")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        CartSummaryResponse response = storeService.updateCartItem(
            user.getId(),
            itemId,
            request.getQuantity(),
            request.getSelected()
        );
        return ResponseUtil.success("更新购物车成功", response);
    }

    @DeleteMapping("/cart/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            @PathVariable Long itemId,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        storeService.removeCartItem(user.getId(), itemId);
        return ResponseUtil.success("移除购物车成功", null);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        storeService.clearCart(user.getId());
        return ResponseUtil.success("清空购物车成功", null);
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @RequestBody(required = false) CheckoutRequest request,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }

        String paymentMethod = request == null || request.getPaymentMethod() == null ? "MOCK" : request.getPaymentMethod();
        try {
            OrderResponse order = storeService.checkout(
                user.getId(),
                GameOrder.PaymentMethod.valueOf(paymentMethod.toUpperCase()),
                request == null ? null : request.getDiscountCardId(),
                request == null ? 0 : request.getPointsToUse()
            );
            return ResponseUtil.success("结算成功", order);
        } catch (IllegalArgumentException ex) {
            return ResponseUtil.badRequest("支付方式不支持");
        }
    }

    @GetMapping("/checkout/options")
    public ResponseEntity<ApiResponse<CheckoutOptionsResponse>> getCheckoutOptions(HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        return ResponseUtil.success("获取结算配置成功", storeService.getCheckoutOptions(user.getId()));
    }

    @PostMapping("/games/{gameId}/claim")
    public ResponseEntity<ApiResponse<OrderResponse>> claimFreeGame(
            @PathVariable Long gameId,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        OrderResponse order = storeService.claimFreeGame(user.getId(), gameId);
        return ResponseUtil.success("领取成功", order);
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        return ResponseUtil.success("获取订单成功", storeService.getOrders(user.getId()));
    }

    @GetMapping("/library")
    public ResponseEntity<ApiResponse<List<LibraryGameResponse>>> getLibrary(HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        return ResponseUtil.success("获取游戏库成功", storeService.getLibrary(user.getId()));
    }

    @GetMapping("/points")
    public ResponseEntity<ApiResponse<List<PointTransactionResponse>>> getPoints(HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        return ResponseUtil.success("获取积分流水成功", storeService.getRecentPointTransactions(user.getId()));
    }

    @GetMapping("/point-shop/items")
    public ResponseEntity<ApiResponse<List<PointShopItemResponse>>> getPointShopItems() {
        return ResponseUtil.success("获取积分商品成功", storeService.getPointShopItems());
    }

    @PostMapping("/point-shop/items/{code}/redeem")
    public ResponseEntity<ApiResponse<OwnedDiscountCardResponse>> redeemPointShopItem(
            @PathVariable String code,
            HttpServletRequest httpRequest) {
        User user = currentUserService.getCurrentUser(httpRequest);
        if (user == null) {
            return ResponseUtil.unauthorized("请先登录");
        }
        return ResponseUtil.success("兑换成功", storeService.redeemPointShopItem(user.getId(), code));
    }
}
