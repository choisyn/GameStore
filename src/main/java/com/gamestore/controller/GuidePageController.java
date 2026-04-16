package com.gamestore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guides")
public class GuidePageController {

    @GetMapping
    public String guideIndex() {
        return "guides/index";
    }

    @GetMapping("/create")
    public String guideCreate() {
        return "guides/create";
    }

    @GetMapping("/{id}")
    public String guideDetail(@PathVariable Long id, Model model) {
        model.addAttribute("guideId", id);
        return "guides/detail";
    }
}
