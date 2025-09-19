package com.zh.mathplatform.interfaces.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.PostApplicationService;
import com.zh.mathplatform.domain.post.entity.Post;
import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.infrastructure.utils.PostVOConverter;
import com.zh.mathplatform.interfaces.constant.SearchRedisKeys;
import com.zh.mathplatform.interfaces.vo.post.PostVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Autowired
    private PostApplicationService postApplicationService;

    @Autowired
    private PostVOConverter postVOConverter;

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SearchRedisKeys searchRedisKeys;

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
        
        // 统计关键词热度
        recordHotKeyword(searchRequest.getKeyword());

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

        String[] suggestions = getSuggestionsFromRedis(keyword.trim(), 10);
        return ResultUtils.success(suggestions);
    }

    /**
     * 获取热门搜索词
     */
    @GetMapping("/hot-keywords")
    public BaseResponse<String[]> getHotKeywords() {
        String[] hotKeywords = getTopHotKeywords(10);
        return ResultUtils.success(hotKeywords);
    }

    // ===== 私有方法 =====

    /**
     * 生成搜索建议
     */
    private void recordHotKeyword(String keyword) {
        if (stringRedisTemplate == null) {
            return;
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        try {
            stringRedisTemplate.opsForZSet().incrementScore(searchRedisKeys.getHotKeywords(), keyword.trim(), 1.0);
            log.debug("记录热词成功: {}", keyword);
        } catch (Exception e) {
            log.warn("记录热词失败: {}, 错误: {}", keyword, e.getMessage());
        }
    }

    /**
     * 从 Redis 获取热门关键词
     */
    private String[] getTopHotKeywords(int limit) {
        if (stringRedisTemplate == null) {
            return new String[0];
        }
        try {
            ZSetOperations<String, String> zOps = stringRedisTemplate.opsForZSet();
            var tuples = zOps.reverseRangeWithScores(searchRedisKeys.getHotKeywords(), 0, Math.max(0, limit - 1));
            if (tuples == null || tuples.isEmpty()) {
                return new String[0];
            }
            return tuples.stream().map(ZSetOperations.TypedTuple::getValue).filter(java.util.Objects::nonNull).toArray(String[]::new);
        } catch (Exception e) {
            return new String[0];
        }
    }

    /**
     * 从 Redis TopN 中做前缀匹配的建议（简化实现）
     */
    private String[] getSuggestionsFromRedis(String prefix, int limit) {
        if (stringRedisTemplate == null) {
            return new String[0];
        }
        try {
            ZSetOperations<String, String> zOps = stringRedisTemplate.opsForZSet();
            var top = zOps.reverseRange(searchRedisKeys.getHotKeywords(), 0, 200);
            if (top == null) {
                return new String[0];
            }
            String lower = prefix.toLowerCase();
            return top.stream()
                .filter(k -> k != null && k.toLowerCase().startsWith(lower))
                .limit(limit)
                .toArray(String[]::new);
        } catch (Exception e) {
            return new String[0];
        }
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
