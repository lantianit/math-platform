package com.zh.mathplatform.domain.post.service;

import com.zh.mathplatform.domain.post.entity.Post;

/**
 * 帖子领域服务接口
 */
public interface PostDomainService {

    /**
     * 创建帖子
     */
    Post createPost(String title, String content, Long userId, String category, String tags, String images);

    /**
     * 更新帖子
     */
    Post updatePost(Post post, String title, String content, String category, String tags, String images);

    /**
     * 删除帖子
     */
    void deletePost(Post post);

    /**
     * 增加浏览量
     */
    void increaseViewCount(Post post);

    /**
     * 处理点赞
     */
    void handleLike(Post post, boolean isLike);

    /**
     * 处理收藏
     */
    void handleFavourite(Post post, boolean isFavourite);

    /**
     * 处理评论
     */
    void handleComment(Post post, boolean isAddComment);
}
