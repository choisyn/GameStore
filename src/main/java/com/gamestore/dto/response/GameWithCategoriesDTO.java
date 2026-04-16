package com.gamestore.dto.response;

import com.gamestore.entity.Category;
import com.gamestore.entity.Game;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 游戏响应DTO(包含分类列表)
 */
public class GameWithCategoriesDTO {
    
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private Long categoryId;  // 主分类ID(向后兼容)
    private List<Long> categoryIds;  // 所有分类ID列表
    private List<String> categoryNames;  // 所有分类名称列表
    private String developer;
    private String publisher;
    private LocalDate releaseDate;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String imageUrl;
    private String gallery;
    private String systemRequirements;
    private String tags;
    private BigDecimal rating;
    private Integer ratingCount;
    private Integer downloadCount;
    private Boolean isFeatured;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 从Game实体创建DTO
    public static GameWithCategoriesDTO fromEntity(Game game) {
        GameWithCategoriesDTO dto = new GameWithCategoriesDTO();
        dto.id = game.getId();
        dto.name = game.getName();
        dto.displayName = game.getDisplayName();
        dto.description = game.getDescription();
        dto.categoryId = game.getCategoryId();
        
        // 提取分类ID和名称
        if (game.getCategories() != null && !game.getCategories().isEmpty()) {
            dto.categoryIds = game.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
            dto.categoryNames = game.getCategories().stream()
                    .map(Category::getName)
                    .collect(Collectors.toList());
        }
        
        dto.developer = game.getDeveloper();
        dto.publisher = game.getPublisher();
        dto.releaseDate = game.getReleaseDate();
        dto.price = game.getPrice();
        dto.discountPrice = game.getDiscountPrice();
        dto.imageUrl = game.getImageUrl();
        dto.gallery = game.getGallery();
        dto.systemRequirements = game.getSystemRequirements();
        dto.tags = game.getTags();
        dto.rating = game.getRating();
        dto.ratingCount = game.getRatingCount();
        dto.downloadCount = game.getDownloadCount();
        dto.isFeatured = game.getIsFeatured();
        dto.status = game.getStatus().name();
        dto.createdAt = game.getCreatedAt();
        dto.updatedAt = game.getUpdatedAt();
        
        return dto;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public List<Long> getCategoryIds() {
        return categoryIds;
    }
    
    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
    
    public List<String> getCategoryNames() {
        return categoryNames;
    }
    
    public void setCategoryNames(List<String> categoryNames) {
        this.categoryNames = categoryNames;
    }
    
    public String getDeveloper() {
        return developer;
    }
    
    public void setDeveloper(String developer) {
        this.developer = developer;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }
    
    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getGallery() {
        return gallery;
    }
    
    public void setGallery(String gallery) {
        this.gallery = gallery;
    }
    
    public String getSystemRequirements() {
        return systemRequirements;
    }
    
    public void setSystemRequirements(String systemRequirements) {
        this.systemRequirements = systemRequirements;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public BigDecimal getRating() {
        return rating;
    }
    
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
    
    public Integer getRatingCount() {
        return ratingCount;
    }
    
    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }
    
    public Integer getDownloadCount() {
        return downloadCount;
    }
    
    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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

