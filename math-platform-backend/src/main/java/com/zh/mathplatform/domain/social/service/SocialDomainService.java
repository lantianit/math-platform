package com.zh.mathplatform.domain.social.service;

import com.zh.mathplatform.domain.social.entity.PostFavourite;
import com.zh.mathplatform.domain.social.entity.PostLike;
import com.zh.mathplatform.domain.social.entity.UserFollow;

import java.util.List;

/**
 * 社交交互领域服务接口
 */
public interface SocialDomainService {

    // ===== 点赞相关 =====

    /**
     * 创建帖子点赞
     */
    PostLike createPostLike(Long postId, Long userId);

    /**
     * 删除帖子点赞
     */
    void deletePostLike(PostLike postLike);

    /**
     * 处理帖子点赞/取消点赞
     */
    boolean togglePostLike(Long postId, Long userId);

    /**
     * 检查用户是否已点赞
     */
    boolean isPostLikedByUser(Long postId, Long userId);

    /**
     * 获取用户点赞的帖子ID列表
     */
    List<Long> getLikedPostIdsByUser(Long userId);

    // ===== 收藏相关 =====

    /**
     * 创建帖子收藏
     */
    PostFavourite createPostFavourite(Long postId, Long userId);

    /**
     * 删除帖子收藏
     */
    void deletePostFavourite(PostFavourite postFavourite);

    /**
     * 处理帖子收藏/取消收藏
     */
    boolean togglePostFavourite(Long postId, Long userId);

    /**
     * 检查用户是否已收藏
     */
    boolean isPostFavouritedByUser(Long postId, Long userId);

    /**
     * 获取用户收藏的帖子ID列表
     */
    List<Long> getFavouritePostIdsByUser(Long userId);

    // ===== 关注相关 =====

    /**
     * 创建用户关注
     */
    UserFollow createUserFollow(Long followerId, Long followingId);

    /**
     * 删除用户关注
     */
    void deleteUserFollow(UserFollow userFollow);

    /**
     * 处理用户关注/取消关注
     */
    boolean toggleUserFollow(Long followerId, Long followingId);

    /**
     * 检查用户是否已关注
     */
    boolean isUserFollowedByUser(Long followerId, Long followingId);

    /**
     * 获取用户关注的用户ID列表
     */
    List<Long> getFollowingIdsByUser(Long userId);

    /**
     * 获取用户的粉丝ID列表
     */
    List<Long> getFollowerIdsByUser(Long userId);

    /**
     * 获取用户关注数
     */
    Long getFollowingCount(Long userId);

    /**
     * 获取用户粉丝数
     */
    Long getFollowerCount(Long userId);
}
