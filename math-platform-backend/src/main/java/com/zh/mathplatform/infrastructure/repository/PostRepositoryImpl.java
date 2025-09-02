package com.zh.mathplatform.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.post.entity.Post;
import com.zh.mathplatform.domain.post.repository.PostRepository;
import com.zh.mathplatform.infrastructure.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 帖子仓储实现
 */
@Repository
public class PostRepositoryImpl implements PostRepository {

    @Autowired
    private PostMapper postMapper;

    @Override
    public boolean save(Post post) {
        return postMapper.insert(post) > 0;
    }

    @Override
    public Post findById(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public List<Post> findByCondition(LambdaQueryWrapper<Post> wrapper) {
        return postMapper.selectList(wrapper);
    }

    @Override
    public IPage<Post> findByPage(Page<Post> page, LambdaQueryWrapper<Post> wrapper) {
        return postMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean updateById(Post post) {
        return postMapper.updateById(post) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return postMapper.deleteById(id) > 0;
    }

    @Override
    public List<Post> findByUserId(Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, userId)
               .eq(Post::getIsDelete, 0)
               .orderByDesc(Post::getCreateTime);
        return postMapper.selectList(wrapper);
    }

    @Override
    public List<Post> findByCategory(String category) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getCategory, category)
               .eq(Post::getIsDelete, 0)
               .orderByDesc(Post::getCreateTime);
        return postMapper.selectList(wrapper);
    }

    @Override
    public boolean increaseViewCount(Long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
               .setSql("view_count = view_count + 1");
        return postMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean increaseLikeCount(Long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
               .setSql("like_count = like_count + 1");
        return postMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean decreaseLikeCount(Long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
               .gt(Post::getLikeCount, 0)
               .setSql("like_count = like_count - 1");
        return postMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean increaseCommentCount(Long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
               .setSql("comment_count = comment_count + 1");
        return postMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean decreaseCommentCount(Long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
               .gt(Post::getCommentCount, 0)
               .setSql("comment_count = comment_count - 1");
        return postMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean increaseFavouriteCount(Long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
               .setSql("favourite_count = favourite_count + 1");
        return postMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean decreaseFavouriteCount(Long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
               .gt(Post::getFavouriteCount, 0)
               .setSql("favourite_count = favourite_count - 1");
        return postMapper.update(null, wrapper) > 0;
    }

    @Override
    public IPage<Post> findByConditionPage(Page<Post> page, LambdaQueryWrapper<Post> wrapper) {
        return postMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<Post> findFollowingUserPosts(Page<Post> page, Long userId) {
        // 这里需要联表查询，查询关注用户的帖子
        // 简化实现，实际应该使用自定义SQL
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getIsDelete, 0)
               .orderByDesc(Post::getCreateTime);
        // TODO: 实现关注用户帖子的联表查询
        return postMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean increaseShareCount(Long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
               .setSql("share_count = share_count + 1");
        return postMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean updateQualityScore(Long postId, Double score) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
               .set(Post::getQualityScore, score);
        return postMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean updateHotScore(Long postId, Double score) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
               .set(Post::getHotScore, score);
        return postMapper.update(null, wrapper) > 0;
    }
}
