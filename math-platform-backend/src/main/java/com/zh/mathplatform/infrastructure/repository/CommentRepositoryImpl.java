package com.zh.mathplatform.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.comment.entity.Comment;
import com.zh.mathplatform.domain.comment.repository.CommentRepository;
import com.zh.mathplatform.infrastructure.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论仓储实现
 */
@Repository
public class CommentRepositoryImpl implements CommentRepository {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public boolean save(Comment comment) {
        return commentMapper.insert(comment) > 0;
    }

    @Override
    public Comment findById(Long id) {
        return commentMapper.selectById(id);
    }

    @Override
    public List<Comment> findByCondition(LambdaQueryWrapper<Comment> wrapper) {
        return commentMapper.selectList(wrapper);
    }

    @Override
    public IPage<Comment> findByPage(Page<Comment> page, LambdaQueryWrapper<Comment> wrapper) {
        return commentMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean updateById(Comment comment) {
        return commentMapper.updateById(comment) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return commentMapper.deleteById(id) > 0;
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
               .eq(Comment::getIsDelete, 0)
               .isNull(Comment::getParentId) // 只查询根评论
               .orderByDesc(Comment::getCreateTime);
        return commentMapper.selectList(wrapper);
    }

    @Override
    public IPage<Comment> findByPostIdPage(Page<Comment> page, Long postId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
               .eq(Comment::getIsDelete, 0)
               .isNull(Comment::getParentId) // 只查询根评论
               .orderByDesc(Comment::getCreateTime);
        return commentMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Comment> findByUserId(Long userId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getUserId, userId)
               .eq(Comment::getIsDelete, 0)
               .orderByDesc(Comment::getCreateTime);
        return commentMapper.selectList(wrapper);
    }

    @Override
    public List<Comment> findByParentId(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId)
               .eq(Comment::getIsDelete, 0)
               .orderByAsc(Comment::getCreateTime);
        return commentMapper.selectList(wrapper);
    }

    @Override
    public boolean increaseLikeCount(Long commentId) {
        LambdaUpdateWrapper<Comment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Comment::getId, commentId)
               .setSql("like_count = like_count + 1");
        return commentMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean decreaseLikeCount(Long commentId) {
        LambdaUpdateWrapper<Comment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Comment::getId, commentId)
               .gt(Comment::getLikeCount, 0)
               .setSql("like_count = like_count - 1");
        return commentMapper.update(null, wrapper) > 0;
    }
}
