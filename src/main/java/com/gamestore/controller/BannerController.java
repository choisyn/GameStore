package com.gamestore.controller;

import com.gamestore.entity.Banner;
import com.gamestore.dto.response.ApiResponse;
import com.gamestore.service.BannerService;
import com.gamestore.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图控制器
 */
@RestController
@RequestMapping("/api/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 获取所有轮播图(管理员)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Banner>>> getAllBanners() {
        List<Banner> banners = bannerService.getAllBanners();
        return ResponseUtil.success("获取成功", banners);
    }

    /**
     * 获取所有启用的轮播图(前端)
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Banner>>> getActiveBanners() {
        List<Banner> banners = bannerService.getActiveBanners();
        return ResponseUtil.success("获取成功", banners);
    }

    /**
     * 根据类型获取启用的轮播图
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Banner>>> getBannersByType(@PathVariable String type) {
        Banner.BannerType bannerType = Banner.BannerType.valueOf(type.toUpperCase());
        List<Banner> banners = bannerService.getActiveBannersByType(bannerType);
        return ResponseUtil.success("获取成功", banners);
    }

    /**
     * 根据ID获取轮播图
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Banner>> getBannerById(@PathVariable Long id) {
        Banner banner = bannerService.getBannerById(id);
        return ResponseUtil.success("获取成功", banner);
    }

    /**
     * 创建轮播图
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Banner>> createBanner(@RequestBody Banner banner) {
        Banner created = bannerService.createBanner(banner);
        return ResponseUtil.success("创建成功", created);
    }

    /**
     * 更新轮播图
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Banner>> updateBanner(
            @PathVariable Long id,
            @RequestBody Banner banner) {
        Banner updated = bannerService.updateBanner(id, banner);
        return ResponseUtil.success("更新成功", updated);
    }

    /**
     * 删除轮播图
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseUtil.success("删除成功");
    }

    /**
     * 切换轮播图启用状态
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Banner>> toggleActive(@PathVariable Long id) {
        Banner banner = bannerService.toggleActive(id);
        return ResponseUtil.success("状态已更新", banner);
    }
}

