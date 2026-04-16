package com.gamestore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 社区页面控制器
 */
@Controller
@RequestMapping("/community")
public class CommunityPageController {

    /**
     * 社区首页
     */
    @GetMapping
    public String communityIndex() {
        return "community/index";
    }

    /**
     * 板块页面 - 暂时禁用(模板未创建)
     */
    /*
    @GetMapping("/section/{id}")
    public String sectionPage(@PathVariable Long id, Model model) {
        model.addAttribute("sectionId", id);
        return "community/section";
    }
    */

    /**
     * 帖子详情页 - 暂时禁用(模板未创建)
     */
    /*
    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable Long id, Model model) {
        model.addAttribute("postId", id);
        return "community/post-detail";
    }
    */

    /**
     * 发帖页面 - 暂时禁用(模板未创建)
     */
    /*
    @GetMapping("/create-post")
    public String createPostPage() {
        return "community/create-post";
    }
    */
}

