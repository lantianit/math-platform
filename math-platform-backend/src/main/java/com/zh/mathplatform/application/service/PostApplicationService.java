package com.zh.mathplatform.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.post.entity.Post;

import java.util.List;

/**
 * 帖子应用服务接口
 */
public interface PostApplicationService {

    /**
     * 发布帖子
     */
    Post addPost(String title, String content, Long userId, String category, String tags, String images);

    /**
     * 更新帖子
     */
    Post updatePost(Long postId, String title, String content, String category, String tags, String images, Long userId);

    /**
     * 删除帖子
     */
    boolean deletePost(Long postId, Long userId);

    /**
     * 根据ID获取帖子详情
     */
    Post getPostById(Long postId);

    /**
     * 分页获取帖子列表
     */
    IPage<Post> listPostsByPage(Page<Post> page, String category, String searchText, String sortField, String sortOrder);

    /**
     * 根据用户ID获取帖子列表
     */
    List<Post> listPostsByUserId(Long userId);

    /**
     * 按用户分页获取帖子
     */
    IPage<Post> listPostsByUserIdPage(Page<Post> page, Long userId, String sortField, String sortOrder);

    /**
     * 处理帖子点赞
     */
    boolean togglePostLike(Long postId, Long userId);

    /**
     * 处理帖子收藏
     */
    boolean togglePostFavourite(Long postId, Long userId);

    /**
     * 增加帖子浏览量
     */
    void increasePostViewCount(Long postId);

    /**
     * 获取热门帖子
     */
    List<Post> getHotPosts(Integer limit);

    /**
     * 分页获取热门帖子
     */
    IPage<Post> getHotPosts(Page<Post> page);

    /**
     * 获取最新帖子
     */
    List<Post> getLatestPosts(Integer limit);

    /**
     * 分页获取最新帖子
     */
    IPage<Post> getLatestPosts(Page<Post> page);

    /**
     * 获取推荐帖子
     */
    IPage<Post> getRecommendPosts(Page<Post> page, Long userId);

    /**
     * 获取关注用户的帖子
     */
    IPage<Post> getFollowingPosts(Page<Post> page, Long userId);

    /**
     * 根据分类分页获取帖子
     */
    IPage<Post> getPostsByCategory(Page<Post> page, String category);
}
