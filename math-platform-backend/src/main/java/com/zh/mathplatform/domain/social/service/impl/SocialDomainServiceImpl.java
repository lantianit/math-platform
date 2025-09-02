package com.zh.mathplatform.domain.social.service.impl;

import com.zh.mathplatform.domain.social.entity.PostFavourite;
import com.zh.mathplatform.domain.social.entity.PostLike;
import com.zh.mathplatform.domain.social.entity.UserFollow;
import com.zh.mathplatform.domain.social.repository.SocialRepository;
import com.zh.mathplatform.domain.social.service.SocialDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 社交交互领域服务实现
 */
@Service
public class SocialDomainServiceImpl implements SocialDomainService {

    @Autowired
    private SocialRepository socialRepository;

    // ===== 点赞相关 =====

    @Override
    public PostLike createPostLike(Long postId, Long userId) {
        // 校验参数
        PostLike.validPostLike(postId, userId);

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLike.setCreateTime(new Date());
        postLike.setUpdateTime(new Date());
        postLike.setIsDelete(0);

        return postLike;
    }

    @Override
    public void deletePostLike(PostLike postLike) {
        if (postLike == null) {
            throw new IllegalArgumentException("点赞记录不存在");
        }
        postLike.markAsDeleted();
        postLike.setUpdateTime(new Date());
    }

    @Override
    @Transactional
    public boolean togglePostLike(Long postId, Long userId) {
        PostLike existingLike = socialRepository.findPostLikeByPostIdAndUserId(postId, userId);

        if (existingLike != null && !existingLike.isDeleted()) {
            // 已点赞，取消点赞
            deletePostLike(existingLike);
            socialRepository.savePostLike(existingLike); // 更新删除状态
            return false; // 返回false表示取消点赞
        } else {
            // 未点赞，添加点赞
            PostLike newLike = createPostLike(postId, userId);
            socialRepository.savePostLike(newLike);
            return true; // 返回true表示添加点赞
        }
    }

    @Override
    public boolean isPostLikedByUser(Long postId, Long userId) {
        return socialRepository.isPostLikedByUser(postId, userId);
    }

    @Override
    public List<Long> getLikedPostIdsByUser(Long userId) {
        return socialRepository.findLikedPostIdsByUserId(userId);
    }

    // ===== 收藏相关 =====

    @Override
    public PostFavourite createPostFavourite(Long postId, Long userId) {
        // 校验参数
        PostFavourite.validPostFavourite(postId, userId);

        PostFavourite postFavourite = new PostFavourite();
        postFavourite.setPostId(postId);
        postFavourite.setUserId(userId);
        postFavourite.setCreateTime(new Date());
        postFavourite.setUpdateTime(new Date());
        postFavourite.setIsDelete(0);

        return postFavourite;
    }

    @Override
    public void deletePostFavourite(PostFavourite postFavourite) {
        if (postFavourite == null) {
            throw new IllegalArgumentException("收藏记录不存在");
        }
        postFavourite.markAsDeleted();
        postFavourite.setUpdateTime(new Date());
    }

    @Override
    @Transactional
    public boolean togglePostFavourite(Long postId, Long userId) {
        PostFavourite existingFavourite = socialRepository.findPostFavouriteByPostIdAndUserId(postId, userId);

        if (existingFavourite != null && !existingFavourite.isDeleted()) {
            // 已收藏，取消收藏
            deletePostFavourite(existingFavourite);
            socialRepository.savePostFavourite(existingFavourite); // 更新删除状态
            return false; // 返回false表示取消收藏
        } else {
            // 未收藏，添加收藏
            PostFavourite newFavourite = createPostFavourite(postId, userId);
            socialRepository.savePostFavourite(newFavourite);
            return true; // 返回true表示添加收藏
        }
    }

    @Override
    public boolean isPostFavouritedByUser(Long postId, Long userId) {
        return socialRepository.isPostFavouritedByUser(postId, userId);
    }

    @Override
    public List<Long> getFavouritePostIdsByUser(Long userId) {
        return socialRepository.findFavouritePostIdsByUserId(userId);
    }

    // ===== 关注相关 =====

    @Override
    public UserFollow createUserFollow(Long followerId, Long followingId) {
        // 校验参数
        UserFollow.validUserFollow(followerId, followingId);

        UserFollow userFollow = new UserFollow();
        userFollow.setFollowerId(followerId);
        userFollow.setFollowingId(followingId);
        userFollow.setCreateTime(new Date());
        userFollow.setUpdateTime(new Date());
        userFollow.setIsDelete(0);

        return userFollow;
    }

    @Override
    public void deleteUserFollow(UserFollow userFollow) {
        if (userFollow == null) {
            throw new IllegalArgumentException("关注记录不存在");
        }
        userFollow.markAsDeleted();
        userFollow.setUpdateTime(new Date());
    }

    @Override
    @Transactional
    public boolean toggleUserFollow(Long followerId, Long followingId) {
        UserFollow existingFollow = socialRepository.findUserFollowByFollowerAndFollowing(followerId, followingId);

        if (existingFollow != null && !existingFollow.isDeleted()) {
            // 已关注，取消关注
            deleteUserFollow(existingFollow);
            socialRepository.saveUserFollow(existingFollow); // 更新删除状态
            return false; // 返回false表示取消关注
        } else {
            // 未关注，添加关注
            UserFollow newFollow = createUserFollow(followerId, followingId);
            socialRepository.saveUserFollow(newFollow);
            return true; // 返回true表示添加关注
        }
    }

    @Override
    public boolean isUserFollowedByUser(Long followerId, Long followingId) {
        return socialRepository.isUserFollowedByUser(followerId, followingId);
    }

    @Override
    public List<Long> getFollowingIdsByUser(Long userId) {
        return socialRepository.findFollowingIdsByFollowerId(userId);
    }

    @Override
    public List<Long> getFollowerIdsByUser(Long userId) {
        return socialRepository.findFollowerIdsByFollowingId(userId);
    }

    @Override
    public Long getFollowingCount(Long userId) {
        return socialRepository.countFollowingByUserId(userId);
    }

    @Override
    public Long getFollowerCount(Long userId) {
        return socialRepository.countFollowersByUserId(userId);
    }
}
