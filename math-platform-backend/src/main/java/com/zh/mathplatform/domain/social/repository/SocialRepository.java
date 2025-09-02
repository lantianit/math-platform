package com.zh.mathplatform.domain.social.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zh.mathplatform.domain.social.entity.PostFavourite;
import com.zh.mathplatform.domain.social.entity.PostLike;
import com.zh.mathplatform.domain.social.entity.UserFollow;

import java.util.List;

/**
 * 社交交互仓储接口
 */
public interface SocialRepository {

    // ===== PostLike 相关方法 =====

    /**
     * 保存帖子点赞
     */
    boolean savePostLike(PostLike postLike);

    /**
     * 根据ID查询帖子点赞
     */
    PostLike findPostLikeById(Long id);

    /**
     * 根据帖子ID和用户ID查询点赞记录
     */
    PostLike findPostLikeByPostIdAndUserId(Long postId, Long userId);

    /**
     * 根据条件查询点赞列表
     */
    List<PostLike> findPostLikesByCondition(LambdaQueryWrapper<PostLike> wrapper);

    /**
     * 删除帖子点赞
     */
    boolean removePostLikeById(Long id);

    /**
     * 根据帖子ID删除点赞记录
     */
    boolean removePostLikesByPostId(Long postId);

    /**
     * 根据用户ID查询点赞的帖子ID列表
     */
    List<Long> findLikedPostIdsByUserId(Long userId);

    /**
     * 检查用户是否已点赞
     */
    boolean isPostLikedByUser(Long postId, Long userId);

    // ===== PostFavourite 相关方法 =====

    /**
     * 保存帖子收藏
     */
    boolean savePostFavourite(PostFavourite postFavourite);

    /**
     * 根据ID查询帖子收藏
     */
    PostFavourite findPostFavouriteById(Long id);

    /**
     * 根据帖子ID和用户ID查询收藏记录
     */
    PostFavourite findPostFavouriteByPostIdAndUserId(Long postId, Long userId);

    /**
     * 根据条件查询收藏列表
     */
    List<PostFavourite> findPostFavouritesByCondition(LambdaQueryWrapper<PostFavourite> wrapper);

    /**
     * 删除帖子收藏
     */
    boolean removePostFavouriteById(Long id);

    /**
     * 根据帖子ID删除收藏记录
     */
    boolean removePostFavouritesByPostId(Long postId);

    /**
     * 根据用户ID查询收藏的帖子ID列表
     */
    List<Long> findFavouritePostIdsByUserId(Long userId);

    /**
     * 检查用户是否已收藏
     */
    boolean isPostFavouritedByUser(Long postId, Long userId);

    // ===== UserFollow 相关方法 =====

    /**
     * 保存用户关注
     */
    boolean saveUserFollow(UserFollow userFollow);

    /**
     * 根据ID查询用户关注
     */
    UserFollow findUserFollowById(Long id);

    /**
     * 根据关注者和被关注者ID查询关注记录
     */
    UserFollow findUserFollowByFollowerAndFollowing(Long followerId, Long followingId);

    /**
     * 根据条件查询关注列表
     */
    List<UserFollow> findUserFollowsByCondition(LambdaQueryWrapper<UserFollow> wrapper);

    /**
     * 删除用户关注
     */
    boolean removeUserFollowById(Long id);

    /**
     * 根据关注者ID查询关注的用户ID列表
     */
    List<Long> findFollowingIdsByFollowerId(Long followerId);

    /**
     * 根据被关注者ID查询粉丝ID列表
     */
    List<Long> findFollowerIdsByFollowingId(Long followingId);

    /**
     * 检查用户是否已关注
     */
    boolean isUserFollowedByUser(Long followerId, Long followingId);

    /**
     * 获取用户关注数
     */
    Long countFollowingByUserId(Long userId);

    /**
     * 获取用户粉丝数
     */
    Long countFollowersByUserId(Long userId);
}
