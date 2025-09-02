package com.zh.mathplatform.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zh.mathplatform.domain.social.entity.PostFavourite;
import com.zh.mathplatform.domain.social.entity.PostLike;
import com.zh.mathplatform.domain.social.entity.UserFollow;
import com.zh.mathplatform.domain.social.repository.SocialRepository;
import com.zh.mathplatform.infrastructure.mapper.PostFavouriteMapper;
import com.zh.mathplatform.infrastructure.mapper.PostLikeMapper;
import com.zh.mathplatform.infrastructure.mapper.UserFollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 社交交互仓储实现
 */
@Repository
public class SocialRepositoryImpl implements SocialRepository {

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private PostFavouriteMapper postFavouriteMapper;

    @Autowired
    private UserFollowMapper userFollowMapper;

    // ===== PostLike 相关方法 =====

    @Override
    public boolean savePostLike(PostLike postLike) {
        return postLikeMapper.insert(postLike) > 0;
    }

    @Override
    public PostLike findPostLikeById(Long id) {
        return postLikeMapper.selectById(id);
    }

    @Override
    public PostLike findPostLikeByPostIdAndUserId(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
               .eq(PostLike::getUserId, userId)
               .eq(PostLike::getIsDelete, 0);
        return postLikeMapper.selectOne(wrapper);
    }

    @Override
    public List<PostLike> findPostLikesByCondition(LambdaQueryWrapper<PostLike> wrapper) {
        return postLikeMapper.selectList(wrapper);
    }

    @Override
    public boolean removePostLikeById(Long id) {
        return postLikeMapper.deleteById(id) > 0;
    }

    @Override
    public boolean removePostLikesByPostId(Long postId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId);
        return postLikeMapper.delete(wrapper) > 0;
    }

    @Override
    public List<Long> findLikedPostIdsByUserId(Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getUserId, userId)
               .eq(PostLike::getIsDelete, 0)
               .select(PostLike::getPostId);
        return postLikeMapper.selectObjs(wrapper).stream()
                .map(obj -> (Long) obj)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public boolean isPostLikedByUser(Long postId, Long userId) {
        PostLike postLike = findPostLikeByPostIdAndUserId(postId, userId);
        return postLike != null && !postLike.isDeleted();
    }

    // ===== PostFavourite 相关方法 =====

    @Override
    public boolean savePostFavourite(PostFavourite postFavourite) {
        return postFavouriteMapper.insert(postFavourite) > 0;
    }

    @Override
    public PostFavourite findPostFavouriteById(Long id) {
        return postFavouriteMapper.selectById(id);
    }

    @Override
    public PostFavourite findPostFavouriteByPostIdAndUserId(Long postId, Long userId) {
        LambdaQueryWrapper<PostFavourite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavourite::getPostId, postId)
               .eq(PostFavourite::getUserId, userId)
               .eq(PostFavourite::getIsDelete, 0);
        return postFavouriteMapper.selectOne(wrapper);
    }

    @Override
    public List<PostFavourite> findPostFavouritesByCondition(LambdaQueryWrapper<PostFavourite> wrapper) {
        return postFavouriteMapper.selectList(wrapper);
    }

    @Override
    public boolean removePostFavouriteById(Long id) {
        return postFavouriteMapper.deleteById(id) > 0;
    }

    @Override
    public boolean removePostFavouritesByPostId(Long postId) {
        LambdaQueryWrapper<PostFavourite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavourite::getPostId, postId);
        return postFavouriteMapper.delete(wrapper) > 0;
    }

    @Override
    public List<Long> findFavouritePostIdsByUserId(Long userId) {
        LambdaQueryWrapper<PostFavourite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavourite::getUserId, userId)
               .eq(PostFavourite::getIsDelete, 0)
               .select(PostFavourite::getPostId);
        return postFavouriteMapper.selectObjs(wrapper).stream()
                .map(obj -> (Long) obj)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public boolean isPostFavouritedByUser(Long postId, Long userId) {
        PostFavourite postFavourite = findPostFavouriteByPostIdAndUserId(postId, userId);
        return postFavourite != null && !postFavourite.isDeleted();
    }

    // ===== UserFollow 相关方法 =====

    @Override
    public boolean saveUserFollow(UserFollow userFollow) {
        return userFollowMapper.insert(userFollow) > 0;
    }

    @Override
    public UserFollow findUserFollowById(Long id) {
        return userFollowMapper.selectById(id);
    }

    @Override
    public UserFollow findUserFollowByFollowerAndFollowing(Long followerId, Long followingId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, followerId)
               .eq(UserFollow::getFollowingId, followingId)
               .eq(UserFollow::getIsDelete, 0);
        return userFollowMapper.selectOne(wrapper);
    }

    @Override
    public List<UserFollow> findUserFollowsByCondition(LambdaQueryWrapper<UserFollow> wrapper) {
        return userFollowMapper.selectList(wrapper);
    }

    @Override
    public boolean removeUserFollowById(Long id) {
        return userFollowMapper.deleteById(id) > 0;
    }

    @Override
    public List<Long> findFollowingIdsByFollowerId(Long followerId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, followerId)
               .eq(UserFollow::getIsDelete, 0)
               .select(UserFollow::getFollowingId);
        return userFollowMapper.selectObjs(wrapper).stream()
                .map(obj -> (Long) obj)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Long> findFollowerIdsByFollowingId(Long followingId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowingId, followingId)
               .eq(UserFollow::getIsDelete, 0)
               .select(UserFollow::getFollowerId);
        return userFollowMapper.selectObjs(wrapper).stream()
                .map(obj -> (Long) obj)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public boolean isUserFollowedByUser(Long followerId, Long followingId) {
        UserFollow userFollow = findUserFollowByFollowerAndFollowing(followerId, followingId);
        return userFollow != null && !userFollow.isDeleted();
    }

    @Override
    public Long countFollowingByUserId(Long userId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, userId)
               .eq(UserFollow::getIsDelete, 0);
        return userFollowMapper.selectCount(wrapper).longValue();
    }

    @Override
    public Long countFollowersByUserId(Long userId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowingId, userId)
               .eq(UserFollow::getIsDelete, 0);
        return userFollowMapper.selectCount(wrapper).longValue();
    }
}
