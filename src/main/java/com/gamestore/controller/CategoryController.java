package com.gamestore.controller;

import com.gamestore.dto.response.ApiResponse;
import com.gamestore.entity.Category;
import com.gamestore.service.CategoryService;
import com.gamestore.util.ResponseUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseUtil.success("获取分类列表成功", categories);
    }
    
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<Category>>> getRootCategories() {
        List<Category> categories = categoryService.getRootCategories();
        return ResponseUtil.success("获取根分类成功", categories);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategory(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseUtil.success("获取分类详情成功", category);
    }
    
    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse<List<Category>>> getSubCategories(@PathVariable Long id) {
        List<Category> categories = categoryService.getSubCategories(id);
        return ResponseUtil.success("获取子分类成功", categories);
    }
    
    /**
     * 创建分类
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return ResponseUtil.success("创建分类成功", created);
    }
    
    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category) {
        Category updated = categoryService.updateCategory(id, category);
        return ResponseUtil.success("更新分类成功", updated);
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseUtil.success("删除分类成功");
    }
}
