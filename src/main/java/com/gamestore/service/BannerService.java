package com.gamestore.service;

import com.gamestore.entity.Banner;
import com.gamestore.exception.CustomException;
import com.gamestore.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 轮播图服务层
 */
@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    /**
     * 获取所有轮播图
     */
    public List<Banner> getAllBanners() {
        return bannerRepository.findAllByOrderBySortOrderAsc();
    }

    /**
     * 获取所有启用的轮播图
     */
    public List<Banner> getActiveBanners() {
        return bannerRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    /**
     * 根据类型获取启用的轮播图
     */
    public List<Banner> getActiveBannersByType(Banner.BannerType type) {
        return bannerRepository.findByTypeAndIsActiveTrueOrderBySortOrderAsc(type);
    }

    /**
     * 根据ID获取轮播图
     */
    public Banner getBannerById(Long id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new CustomException("轮播图不存在"));
    }

    /**
     * 创建轮播图
     */
    @Transactional
    public Banner createBanner(Banner banner) {
        // 验证必填字段
        if (banner.getTitle() == null || banner.getTitle().trim().isEmpty()) {
            throw new CustomException("标题不能为空");
        }
        if (banner.getImageUrl() == null || banner.getImageUrl().trim().isEmpty()) {
            throw new CustomException("图片URL不能为空");
        }

        // 设置默认值
        if (banner.getSortOrder() == null) {
            banner.setSortOrder(0);
        }
        if (banner.getIsActive() == null) {
            banner.setIsActive(true);
        }
        if (banner.getType() == null) {
            banner.setType(Banner.BannerType.HOME);
        }

        return bannerRepository.save(banner);
    }

    /**
     * 更新轮播图
     */
    @Transactional
    public Banner updateBanner(Long id, Banner bannerData) {
        Banner banner = getBannerById(id);

        // 更新字段
        if (bannerData.getTitle() != null) {
            banner.setTitle(bannerData.getTitle());
        }
        if (bannerData.getImageUrl() != null) {
            banner.setImageUrl(bannerData.getImageUrl());
        }
        if (bannerData.getLinkUrl() != null) {
            banner.setLinkUrl(bannerData.getLinkUrl());
        }
        if (bannerData.getDescription() != null) {
            banner.setDescription(bannerData.getDescription());
        }
        if (bannerData.getSortOrder() != null) {
            banner.setSortOrder(bannerData.getSortOrder());
        }
        if (bannerData.getIsActive() != null) {
            banner.setIsActive(bannerData.getIsActive());
        }
        if (bannerData.getType() != null) {
            banner.setType(bannerData.getType());
        }

        return bannerRepository.save(banner);
    }

    /**
     * 删除轮播图
     */
    @Transactional
    public void deleteBanner(Long id) {
        Banner banner = getBannerById(id);
        bannerRepository.delete(banner);
    }

    /**
     * 切换轮播图启用状态
     */
    @Transactional
    public Banner toggleActive(Long id) {
        Banner banner = getBannerById(id);
        banner.setIsActive(!banner.getIsActive());
        return bannerRepository.save(banner);
    }
}

