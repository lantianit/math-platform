package com.zh.mathplatform.domain.post.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.post.entity.Post;

import java.util.List;

/**
 * 帖子仓储接口
 */
public interface PostRepository {

    /**
     * 保存帖子
     */
    boolean save(Post post);

    /**
     * 根据ID查询帖子
     */
    Post findById(Long id);

    /**
     * 根据条件查询帖子列表
     */
    List<Post> findByCondition(LambdaQueryWrapper<Post> wrapper);

    /**
     * 分页查询帖子
     */
    IPage<Post> findByPage(Page<Post> page, LambdaQueryWrapper<Post> wrapper);

    /**
     * 更新帖子
     */
    boolean updateById(Post post);

    /**
     * 根据ID删除帖子
     */
    boolean removeById(Long id);

    /**
     * 根据用户ID查询帖子列表
     */
    List<Post> findByUserId(Long userId);

    /**
     * 根据分类查询帖子列表
     */
    List<Post> findByCategory(String category);

    /**
     * 增加浏览量
     */
    boolean increaseViewCount(Long postId);

    /**
     * 增加点赞数
     */
    boolean increaseLikeCount(Long postId);

    /**
     * 减少点赞数
     */
    boolean decreaseLikeCount(Long postId);

    /**
     * 增加评论数
     */
    boolean increaseCommentCount(Long postId);

    /**
     * 减少评论数
     */
    boolean decreaseCommentCount(Long postId);

    /**
     * 增加收藏数
     */
    boolean increaseFavouriteCount(Long postId);

    /**
     * 减少收藏数
     */
    boolean decreaseFavouriteCount(Long postId);
}
