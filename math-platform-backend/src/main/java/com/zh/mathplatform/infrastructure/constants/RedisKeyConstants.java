package com.zh.mathplatform.infrastructure.constants;

/**
 * Redis Key 统一管理
 * 企业级架构 - 统一常量管理
 * 
 * @author zh
 */
public final class RedisKeyConstants {

    private RedisKeyConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 应用前缀 - 用于多应用环境隔离
     */
    private static final String APP_PREFIX = "math-platform";

    /**
     * 环境前缀 - 用于多环境隔离 (dev/test/prod)
     */
    private static final String ENV_PREFIX = "${spring.profiles.active:dev}";

    /**
     * 构建完整的 Redis Key
     * 格式: {app}:{env}:{domain}:{key}
     * 
     * @param domain 领域模块
     * @param key    业务键
     * @return 完整的 Redis Key
     */
    private static String buildKey(String domain, String key) {
        return String.format("%s:%s:%s:%s", APP_PREFIX, ENV_PREFIX, domain, key);
    }

    /**
     * 构建带用户ID的 Redis Key
     * 格式: {app}:{env}:{domain}:{key}:{userId}
     */
    private static String buildUserKey(String domain, String key, Long userId) {
        return String.format("%s:%s:%s:%s:%d", APP_PREFIX, ENV_PREFIX, domain, key, userId);
    }

    /**
     * 搜索领域相关 Redis Key
     */
    public static final class Search {
        private static final String DOMAIN = "search";

        /**
         * 热门搜索关键词 ZSet
         * Key: math-platform:dev:search:hot-keywords
         */
        public static final String HOT_KEYWORDS = buildKey(DOMAIN, "hot-keywords");

        /**
         * 搜索建议缓存 Hash
         * Key: math-platform:dev:search:suggestions
         */
        public static final String SUGGESTIONS_CACHE = buildKey(DOMAIN, "suggestions");

        /**
         * 用户搜索历史 List
         * Key: math-platform:dev:search:history:{userId}
         */
        public static String getUserSearchHistory(Long userId) {
            return buildUserKey(DOMAIN, "history", userId);
        }
    }

    /**
     * 通知领域相关 Redis Key
     */
    public static final class Notification {
        private static final String DOMAIN = "notify";

        /**
         * 用户通知列表 List
         * Key: math-platform:dev:notify:list:{userId}
         */
        public static String getUserNotifications(Long userId) {
            return buildUserKey(DOMAIN, "list", userId);
        }

        /**
         * 用户未读通知计数 String
         * Key: math-platform:dev:notify:unread:{userId}
         */
        public static String getUserUnreadCount(Long userId) {
            return buildUserKey(DOMAIN, "unread", userId);
        }

        /**
         * 全局通知广播 List
         * Key: math-platform:dev:notify:broadcast
         */
        public static final String GLOBAL_BROADCAST = buildKey(DOMAIN, "broadcast");
    }

    /**
     * 用户领域相关 Redis Key
     */
    public static final class User {
        private static final String DOMAIN = "user";

        /**
         * 用户在线状态 Hash
         * Key: math-platform:dev:user:online-status
         */
        public static final String ONLINE_STATUS = buildKey(DOMAIN, "online-status");

        /**
         * 用户权限缓存 Hash
         * Key: math-platform:dev:user:permissions:{userId}
         */
        public static String getUserPermissions(Long userId) {
            return buildUserKey(DOMAIN, "permissions", userId);
        }

        /**
         * 用户资料缓存 String
         * Key: math-platform:dev:user:profile:{userId}
         */
        public static String getUserProfile(Long userId) {
            return buildUserKey(DOMAIN, "profile", userId);
        }
    }

    /**
     * 社交领域相关 Redis Key
     */
    public static final class Social {
        private static final String DOMAIN = "social";

        /**
         * 用户关注列表 Set
         * Key: math-platform:dev:social:following:{userId}
         */
        public static String getUserFollowing(Long userId) {
            return buildUserKey(DOMAIN, "following", userId);
        }

        /**
         * 用户粉丝列表 Set
         * Key: math-platform:dev:social:followers:{userId}
         */
        public static String getUserFollowers(Long userId) {
            return buildUserKey(DOMAIN, "followers", userId);
        }

        /**
         * 帖子点赞用户集合 Set
         * Key: math-platform:dev:social:post-likes:{postId}
         */
        public static String getPostLikes(Long postId) {
            return buildUserKey(DOMAIN, "post-likes", postId);
        }
    }

    /**
     * 内容领域相关 Redis Key
     */
    public static final class Content {
        private static final String DOMAIN = "content";

        /**
         * 热门帖子排行 ZSet
         * Key: math-platform:dev:content:hot-posts
         */
        public static final String HOT_POSTS = buildKey(DOMAIN, "hot-posts");

        /**
         * 帖子浏览量计数 Hash
         * Key: math-platform:dev:content:post-views
         */
        public static final String POST_VIEW_COUNT = buildKey(DOMAIN, "post-views");

        /**
         * 用户发帖频率限制 String
         * Key: math-platform:dev:content:post-limit:{userId}
         */
        public static String getUserPostLimit(Long userId) {
            return buildUserKey(DOMAIN, "post-limit", userId);
        }
    }

    /**
     * 缓存领域相关 Redis Key
     */
    public static final class Cache {
        private static final String DOMAIN = "cache";

        /**
         * 系统配置缓存 Hash
         * Key: math-platform:dev:cache:system-config
         */
        public static final String SYSTEM_CONFIG = buildKey(DOMAIN, "system-config");

        /**
         * 热点数据缓存前缀
         * Key: math-platform:dev:cache:hotspot:{key}
         */
        public static String getHotspotCache(String key) {
            return buildKey(DOMAIN, "hotspot:" + key);
        }
    }

    /**
     * 分布式锁相关 Redis Key
     */
    public static final class Lock {
        private static final String DOMAIN = "lock";

        /**
         * 用户操作锁
         * Key: math-platform:dev:lock:user-action:{userId}
         */
        public static String getUserActionLock(Long userId) {
            return buildUserKey(DOMAIN, "user-action", userId);
        }

        /**
         * 帖子操作锁
         * Key: math-platform:dev:lock:post-action:{postId}
         */
        public static String getPostActionLock(Long postId) {
            return buildUserKey(DOMAIN, "post-action", postId);
        }
    }
}
