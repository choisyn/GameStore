package com.gamestore.repository;

import com.gamestore.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // 查找所有活跃分类
    List<Category> findByStatus(Category.CategoryStatus status);
    
    // 查找根分类（没有父分类）
    List<Category> findByParentIdIsNullAndStatus(Category.CategoryStatus status);
    
    // 查找子分类
    List<Category> findByParentIdAndStatus(Long parentId, Category.CategoryStatus status);
    
    // 查找所有子分类(包括非活跃的)
    List<Category> findByParentId(Long parentId);
}
