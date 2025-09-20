package com.zh.mathplatform.interfaces.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;
import cn.hutool.json.JSONUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

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
     * Caffeine本地缓存 - 热门搜索词缓存
     * 容量：100个缓存项
     * 过期时间：写入后5分钟过期
     * 初始容量：10个
     */
    private final Cache<String, String> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(10)
            .maximumSize(100L)
            .expireAfterWrite(5L, TimeUnit.MINUTES)
            .recordStats() // 启用统计，便于监控缓存效果
            .build();

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
     * 获取热门搜索词 - 多级缓存实现
     * 缓存策略：Caffeine本地缓存 -> Redis分布式缓存 -> 数据库查询
     */
    @GetMapping("/hot-keywords")
    public BaseResponse<String[]> getHotKeywords() {
        // 构建缓存key
        String cacheKey = "hot_keywords_top_10";
        
        // 1. 优先从本地缓存（Caffeine）中读取
        String cachedValue = LOCAL_CACHE.getIfPresent(cacheKey);
        if (cachedValue != null) {
            log.debug("热门搜索词命中本地缓存");
            String[] hotKeywords = deserializeFromJson(cachedValue);
            if (hotKeywords.length > 0) {
                return ResultUtils.success(hotKeywords);
            } else {
                log.warn("本地缓存数据为空，清除缓存");
                LOCAL_CACHE.invalidate(cacheKey);
            }
        }
        
        // 2. 本地缓存未命中，查询Redis分布式缓存
        if (stringRedisTemplate != null) {
            try {
                ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
                String redisValue = valueOps.get(searchRedisKeys.getHotKeywords() + ":cache");
                
                if (redisValue != null) {
                    log.debug("热门搜索词命中Redis缓存，更新本地缓存");
                    String[] hotKeywords = deserializeFromJson(redisValue);
                    if (hotKeywords.length > 0) {
                        // Redis命中且解析成功，存入本地缓存并返回
                        LOCAL_CACHE.put(cacheKey, redisValue);
                        return ResultUtils.success(hotKeywords);
                    } else {
                        log.warn("Redis缓存数据为空，清除Redis缓存");
                        stringRedisTemplate.delete(searchRedisKeys.getHotKeywords() + ":cache");
                    }
                }
            } catch (Exception e) {
                log.warn("查询Redis缓存失败: {}", e.getMessage());
            }
        }
        
        // 3. Redis也未命中，从原始数据源（ZSet）查询
        log.debug("缓存全部未命中，从Redis ZSet查询热门搜索词");
        String[] hotKeywords = getTopHotKeywordsFromZSet(10);
        
        // 4. 更新多级缓存
        String cacheValue = serializeToJson(hotKeywords);
        
        // 更新本地缓存
        LOCAL_CACHE.put(cacheKey, cacheValue);
        
        // 更新Redis缓存，设置过期时间为5分钟
        if (stringRedisTemplate != null) {
            try {
                ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
                valueOps.set(searchRedisKeys.getHotKeywords() + ":cache", cacheValue, 5, TimeUnit.MINUTES);
                log.debug("热门搜索词已更新到Redis缓存");
            } catch (Exception e) {
                log.warn("更新Redis缓存失败: {}", e.getMessage());
            }
        }
        
        return ResultUtils.success(hotKeywords);
    }

    /**
     * 获取缓存统计信息（管理员接口）
     */
    @GetMapping("/cache/stats")
    public BaseResponse<Object> getCacheStats() {
        var stats = LOCAL_CACHE.stats();
        var statsMap = new java.util.HashMap<String, Object>();
        statsMap.put("hitCount", stats.hitCount());
        statsMap.put("missCount", stats.missCount());
        statsMap.put("hitRate", String.format("%.2f%%", stats.hitRate() * 100));
        statsMap.put("evictionCount", stats.evictionCount());
        statsMap.put("averageLoadPenalty", stats.averageLoadPenalty() / 1_000_000.0 + "ms");
        statsMap.put("estimatedSize", LOCAL_CACHE.estimatedSize());
        
        log.info("缓存统计信息: {}", statsMap);
        return ResultUtils.success(statsMap);
    }

    /**
     * 手动刷新缓存（管理员接口）
     */
    @PostMapping("/cache/refresh")
    public BaseResponse<?> refreshCache() {
        try {
            // 清空本地缓存
            LOCAL_CACHE.invalidateAll();
            
            // 清空Redis缓存
            if (stringRedisTemplate != null) {
                stringRedisTemplate.delete(searchRedisKeys.getHotKeywords() + ":cache");
            }
            
            log.info("缓存已手动刷新");
            return ResultUtils.success("缓存刷新成功");
        } catch (Exception e) {
            log.error("缓存刷新失败", e);
            return ResultUtils.error(com.zh.mathplatform.infrastructure.exception.ErrorCode.SYSTEM_ERROR, "缓存刷新失败: " + e.getMessage());
        }
    }

    /**
     * 测试JSON序列化（调试接口）
     */
    @GetMapping("/cache/test")
    public BaseResponse<?> testSerialization() {
        String[] testArray = {"数学", "算法", "微积分"};
        String json = serializeToJson(testArray);
        String[] result = deserializeFromJson(json);
        
        var testResult = new java.util.HashMap<String, Object>();
        testResult.put("original", testArray);
        testResult.put("serialized", json);
        testResult.put("deserialized", result);
        testResult.put("success", java.util.Arrays.equals(testArray, result));
        
        return ResultUtils.success(testResult);
    }

    // ===== 私有方法 =====

    /**
     * 安全的JSON序列化
     */
    private String serializeToJson(String[] array) {
        try {
            if (array == null || array.length == 0) {
                return "[]";
            }
            return JSONUtil.toJsonStr(array);
        } catch (Exception e) {
            log.error("JSON序列化失败", e);
            return "[]";
        }
    }

    /**
     * 安全的JSON反序列化
     */
    private String[] deserializeFromJson(String json) {
        try {
            if (json == null || json.trim().isEmpty()) {
                return new String[0];
            }
            // 验证JSON格式
            if (!json.trim().startsWith("[") || !json.trim().endsWith("]")) {
                log.warn("JSON格式不正确，期望数组格式: {}", json);
                return new String[0];
            }
            return JSONUtil.toBean(json, String[].class);
        } catch (Exception e) {
            log.error("JSON反序列化失败: {}", json, e);
            return new String[0];
        }
    }

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
     * 从 Redis ZSet 获取热门关键词（原始数据源）
     */
    private String[] getTopHotKeywordsFromZSet(int limit) {
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
            log.warn("从Redis ZSet查询热门关键词失败: {}", e.getMessage());
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
