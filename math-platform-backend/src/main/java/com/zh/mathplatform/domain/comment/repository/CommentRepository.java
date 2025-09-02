package com.zh.mathplatform.domain.comment.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.comment.entity.Comment;

import java.util.List;

/**
 * 评论仓储接口
 */
public interface CommentRepository {

    /**
     * 保存评论
     */
    boolean save(Comment comment);

    /**
     * 根据ID查询评论
     */
    Comment findById(Long id);

    /**
     * 根据条件查询评论列表
     */
    List<Comment> findByCondition(LambdaQueryWrapper<Comment> wrapper);

    /**
     * 分页查询评论
     */
    IPage<Comment> findByPage(Page<Comment> page, LambdaQueryWrapper<Comment> wrapper);

    /**
     * 更新评论
     */
    boolean updateById(Comment comment);

    /**
     * 根据ID删除评论
     */
    boolean removeById(Long id);

    /**
     * 根据帖子ID查询评论列表
     */
    List<Comment> findByPostId(Long postId);

    /**
     * 根据帖子ID分页查询评论
     */
    IPage<Comment> findByPostIdPage(Page<Comment> page, Long postId);

    /**
     * 根据用户ID查询评论列表
     */
    List<Comment> findByUserId(Long userId);

    /**
     * 根据父评论ID查询回复列表
     */
    List<Comment> findByParentId(Long parentId);

    /**
     * 增加点赞数
     */
    boolean increaseLikeCount(Long commentId);

    /**
     * 减少点赞数
     */
    boolean decreaseLikeCount(Long commentId);
}
