package com.zh.mathplatform.interfaces.constant;

import com.zh.mathplatform.infrastructure.config.RedisKeyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 搜索功能 Redis Key 管理
 * 接口层常量管理
 * 
 * @author zh
 */
@Component
public class SearchRedisKeys {

    private static final String DOMAIN = "search";

    @Autowired
    private RedisKeyConfig redisKeyConfig;

    /**
     * 获取热门搜索关键词 Key
     * Key: math-platform:dev:search:hot-keywords
     */
    public String getHotKeywords() {
        return redisKeyConfig.buildKey(DOMAIN, "hot-keywords");
    }

    /**
     * 获取搜索建议缓存 Key
     * Key: math-platform:dev:search:suggestions
     */
    public String getSuggestionCache() {
        return redisKeyConfig.buildKey(DOMAIN, "suggestions");
    }

    /**
     * 获取用户搜索历史 Key
     * Key: math-platform:dev:search:history:{userId}
     */
    public String getUserSearchHistory(Long userId) {
        return redisKeyConfig.buildKeyWithId(DOMAIN, "history", userId);
    }

    /**
     * 获取搜索统计 Key
     * Key: math-platform:dev:search:stats:{date}
     */
    public String getSearchStats(String date) {
        return redisKeyConfig.buildKeyWithId(DOMAIN, "stats", date);
    }

    /**
     * 获取搜索结果缓存 Key
     * Key: math-platform:dev:search:result:{keyword}:{page}
     */
    public String getSearchResultCache(String keyword, int page) {
        return redisKeyConfig.buildKeyWithParams(DOMAIN, "result", keyword, page);
    }
}
