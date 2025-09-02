package com.zh.mathplatform.interfaces.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.PostApplicationService;
import com.zh.mathplatform.domain.post.entity.Post;
import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.interfaces.vo.post.PostVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 搜索功能接口
 */
@RestController
@RequestMapping("/api/search")
@Slf4j
public class SearchController {

    @Autowired
    private PostApplicationService postApplicationService;

    /**
     * 搜索帖子
     */
    @PostMapping("/posts")
    public BaseResponse<IPage<PostVO>> searchPosts(@Valid @RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        // 创建分页对象
        Page<Post> page = new Page<>(searchRequest.getCurrent(), searchRequest.getPageSize());
        
        // 执行搜索
        IPage<Post> postPage = postApplicationService.listPostsByPage(
            page, 
            searchRequest.getCategory(),
            searchRequest.getKeyword(),
            searchRequest.getSortField(),
            searchRequest.getSortOrder()
        );
        
        // 转换为VO（这里需要实现转换逻辑）
        IPage<PostVO> postVOPage = convertToPostVOPage(postPage);
        
        return ResultUtils.success(postVOPage);
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    public BaseResponse<String[]> getSearchSuggestions(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResultUtils.success(new String[0]);
        }
        
        // TODO: 实现搜索建议逻辑，可以基于热门搜索词、标签等
        String[] suggestions = {
            keyword + " 教程",
            keyword + " 解题方法", 
            keyword + " 学习资料",
            keyword + " 考试重点"
        };
        
        return ResultUtils.success(suggestions);
    }

    /**
     * 获取热门搜索词
     */
    @GetMapping("/hot-keywords")
    public BaseResponse<String[]> getHotKeywords() {
        // TODO: 实现热门搜索词统计
        String[] hotKeywords = {
            "高等数学", "线性代数", "概率论", 
            "微积分", "数学建模", "离散数学",
            "数理统计", "复变函数", "数值分析"
        };
        
        return ResultUtils.success(hotKeywords);
    }

    // ===== 私有方法 =====

    /**
     * 转换Post分页为PostVO分页
     */
    private IPage<PostVO> convertToPostVOPage(IPage<Post> postPage) {
        // TODO: 实现Post到PostVO的转换逻辑
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        return postVOPage;
    }

    // ===== 内部类 =====

    /**
     * 搜索请求
     */
    @Data
    public static class SearchRequest implements Serializable {

        /**
         * 搜索关键词
         */
        @NotBlank(message = "搜索关键词不能为空")
        @Size(max = 100, message = "搜索关键词长度不能超过100字符")
        private String keyword;

        /**
         * 分类筛选
         */
        private String category;

        /**
         * 当前页号
         */
        private int current = 1;

        /**
         * 页面大小
         */
        private int pageSize = 10;

        /**
         * 排序字段
         */
        private String sortField = "createTime";

        /**
         * 排序顺序
         */
        private String sortOrder = "desc";

        private static final long serialVersionUID = 1L;
    }
}
