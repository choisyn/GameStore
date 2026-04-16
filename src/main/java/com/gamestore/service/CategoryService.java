package com.gamestore.service;

import com.gamestore.entity.Category;
import com.gamestore.exception.CustomException;
import com.gamestore.repository.CategoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public List<Category> getAllCategories() {
        return categoryRepository.findByStatus(Category.CategoryStatus.ACTIVE).stream()
                .sorted(Comparator
                        .comparing(Category::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Category::getId))
                .toList();
    }
    
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException("分类不存在"));
    }
    
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIdIsNullAndStatus(Category.CategoryStatus.ACTIVE);
    }
    
    public List<Category> getSubCategories(Long parentId) {
        return categoryRepository.findByParentIdAndStatus(parentId, Category.CategoryStatus.ACTIVE);
    }
    
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    /**
     * 创建分类
     */
    @Transactional
    public Category createCategory(Category category) {
        // 验证分类名称不能为空
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new CustomException("分类名称不能为空");
        }
        
        // 设置默认值
        if (category.getStatus() == null) {
            category.setStatus(Category.CategoryStatus.ACTIVE);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        
        return categoryRepository.save(category);
    }
    
    /**
     * 更新分类
     */
    @Transactional
    public Category updateCategory(Long id, Category categoryData) {
        Category category = getCategoryById(id);
        
        // 更新字段
        if (categoryData.getName() != null && !categoryData.getName().trim().isEmpty()) {
            category.setName(categoryData.getName());
        }
        if (categoryData.getDescription() != null) {
            category.setDescription(categoryData.getDescription());
        }
        if (categoryData.getIconUrl() != null) {
            category.setIconUrl(categoryData.getIconUrl());
        }
        if (categoryData.getSortOrder() != null) {
            category.setSortOrder(categoryData.getSortOrder());
        }
        if (categoryData.getStatus() != null) {
            category.setStatus(categoryData.getStatus());
        }
        if (categoryData.getParentId() != null) {
            category.setParentId(categoryData.getParentId());
        }
        
        return categoryRepository.save(category);
    }
    
    /**
     * 删除分类
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        
        // 检查是否有子分类
        List<Category> children = categoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new CustomException("该分类下有子分类，无法删除");
        }
        
        // 软删除：设置为INACTIVE状态
        category.setStatus(Category.CategoryStatus.INACTIVE);
        categoryRepository.save(category);
    }
    
    /**
     * 获取所有分类（包括禁用的）
     */
    public List<Category> getAllCategoriesIncludingInactive() {
        return categoryRepository.findAll();
    }
}
