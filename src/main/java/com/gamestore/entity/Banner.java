package com.gamestore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 首页轮播图实体类
 */
@Entity
@Table(name = "banners")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;  // 标题

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;  // 图片URL

    @Column(name = "link_url", length = 500)
    private String linkUrl;  // 点击跳转链接

    @Column(columnDefinition = "TEXT")
    private String description;  // 描述

    @Column(name = "sort_order")
    private Integer sortOrder = 0;  // 排序顺序,数字越小越靠前

    @Column(name = "is_active")
    private Boolean isActive = true;  // 是否启用

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BannerType type = BannerType.HOME;  // 轮播图类型

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 枚举：轮播图类型
    public enum BannerType {
        HOME,       // 首页轮播图
        COMMUNITY,  // 社区页轮播图
        CUSTOM      // 自定义
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Banner() {
    }

    public Banner(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public BannerType getType() {
        return type;
    }

    public void setType(BannerType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

