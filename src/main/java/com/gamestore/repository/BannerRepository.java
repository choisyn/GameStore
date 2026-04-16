package com.gamestore.repository;

import com.gamestore.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 轮播图Repository
 */
@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    /**
     * 查找所有启用的轮播图,按排序顺序
     */
    List<Banner> findByIsActiveTrueOrderBySortOrderAsc();

    /**
     * 根据类型查找启用的轮播图
     */
    List<Banner> findByTypeAndIsActiveTrueOrderBySortOrderAsc(Banner.BannerType type);

    /**
     * 查找所有轮播图,按排序顺序
     */
    List<Banner> findAllByOrderBySortOrderAsc();
}

