package com.nynu.goule.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.nynu.goule.common.BaseController;
import com.nynu.goule.service.CategoryService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/manage/category")
public class CategoryController extends BaseController {

    @Resource
    private CategoryService categoryService;

    @RequestMapping("/all")
    public ModelAndView queryAllCategory(@RequestParam(value = "parentId", defaultValue = "0") String parentId) {
        return feedback(categoryService.queryAll(parentId));
    }

    @RequestMapping("/add")
    public ModelAndView addCategory(@RequestBody Map<String, Object> categoryMap) {
        return feedback(categoryService.addCategory(categoryMap));
    }

    @RequestMapping("/update")
    public ModelAndView updateCategory(@RequestBody Map<String, Object> categoryMap) {
        return feedback(categoryService.updateCategory(categoryMap));
    }

    @RequestMapping("/del")
    public ModelAndView delCategory(@RequestBody Map<String, Object> categoryMap) {
        return feedback(categoryService.delCategory(categoryMap));
    }

    @RequestMapping("/info")
    public ModelAndView categoryInfo(@RequestParam(value = "categoryId") String categoryId) {
        return feedback(categoryService.categoryInfo(categoryId));
    }
}
