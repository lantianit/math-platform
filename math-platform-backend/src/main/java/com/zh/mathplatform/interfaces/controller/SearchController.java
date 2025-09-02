package com.zh.mathplatform.interfaces.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.PostApplicationService;
import com.zh.mathplatform.domain.post.entity.Post;
import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.utils.PostVOConverter;
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

    @Autowired
    private PostVOConverter postVOConverter;

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
        
        // 转换为VO
        IPage<PostVO> postVOPage = postVOConverter.convertToVOPage(postPage);
        
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
        
        // 实现搜索建议逻辑，基于关键词生成相关建议
        String[] suggestions = generateSearchSuggestions(keyword);
        
        return ResultUtils.success(suggestions);
    }

    /**
     * 获取热门搜索词
     */
    @GetMapping("/hot-keywords")
    public BaseResponse<String[]> getHotKeywords() {
        // 实现热门搜索词统计，这里可以基于搜索频次、帖子标签等
        String[] hotKeywords = getPopularKeywords();
        
        return ResultUtils.success(hotKeywords);
    }

    // ===== 私有方法 =====

    /**
     * 生成搜索建议
     */
    private String[] generateSearchSuggestions(String keyword) {
        // 基于关键词生成相关建议
        return new String[]{
            keyword + " 教程",
            keyword + " 解题方法", 
            keyword + " 学习资料",
            keyword + " 考试重点",
            keyword + " 练习题"
        };
    }

    /**
     * 获取热门关键词
     */
    private String[] getPopularKeywords() {
        // 这里可以从数据库或缓存中获取热门搜索词
        return new String[]{
            "高等数学", "线性代数", "概率论", 
            "微积分", "数学建模", "离散数学",
            "数理统计", "复变函数", "数值分析"
        };
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
