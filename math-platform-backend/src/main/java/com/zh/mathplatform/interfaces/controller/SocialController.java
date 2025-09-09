package com.zh.mathplatform.interfaces.controller;

import com.zh.mathplatform.domain.social.service.SocialDomainService;
import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.utils.UserHolder;
import com.zh.mathplatform.application.service.NotifyService;
import com.zh.mathplatform.domain.notify.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 社交交互接口
 */
@RestController
@RequestMapping("/social")
@Slf4j
public class SocialController {

    @Autowired
    private SocialDomainService socialDomainService;

    @Autowired(required = false)
    private NotifyService notifyService;

    // ===== 点赞相关接口 =====

    /**
     * 点赞/取消点赞帖子
     */
    @PostMapping("/post/like")
    public BaseResponse<Boolean> togglePostLike(@Valid @RequestBody PostLikeRequest postLikeRequest, HttpServletRequest request) {
        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean isLiked = socialDomainService.togglePostLike(postLikeRequest.getPostId(), userId);
        // 通知帖子作者（仅点赞时）
        try {
            if (isLiked) {
                Notification n = new Notification();
                // 生产中应查询帖子作者ID，这里示例略过
                n.setReceiverId(0L);
                n.setType("like");
                n.setContent("你的帖子获得了一个点赞");
                n.setExtra("{\"postId\":" + postLikeRequest.getPostId() + "}");
                notifyService.push(n);
            }
        } catch (Exception ignored) {}
        return ResultUtils.success(isLiked);
    }

    /**
     * 检查用户是否已点赞帖子
     */
    @GetMapping("/post/isLiked")
    public BaseResponse<Boolean> isPostLikedByUser(@RequestParam Long postId, HttpServletRequest request) {
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean isLiked = socialDomainService.isPostLikedByUser(postId, userId);
        return ResultUtils.success(isLiked);
    }

    /**
     * 获取用户点赞的帖子ID列表
     */
    @GetMapping("/post/liked")
    public BaseResponse<List<Long>> getLikedPostIds(HttpServletRequest request) {
        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        List<Long> likedPostIds = socialDomainService.getLikedPostIdsByUser(userId);
        return ResultUtils.success(likedPostIds);
    }

    // ===== 收藏相关接口 =====

    /**
     * 收藏/取消收藏帖子
     */
    @PostMapping("/post/favourite")
    public BaseResponse<Boolean> togglePostFavourite(@RequestBody PostFavouriteRequest postFavouriteRequest, HttpServletRequest request) {
        if (postFavouriteRequest == null || postFavouriteRequest.getPostId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean isFavourited = socialDomainService.togglePostFavourite(postFavouriteRequest.getPostId(), userId);
        return ResultUtils.success(isFavourited);
    }

    /**
     * 检查用户是否已收藏帖子
     */
    @GetMapping("/post/isFavourited")
    public BaseResponse<Boolean> isPostFavouritedByUser(@RequestParam Long postId, HttpServletRequest request) {
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean isFavourited = socialDomainService.isPostFavouritedByUser(postId, userId);
        return ResultUtils.success(isFavourited);
    }

    /**
     * 获取用户收藏的帖子ID列表
     */
    @GetMapping("/post/favourited")
    public BaseResponse<List<Long>> getFavouritePostIds(HttpServletRequest request) {
        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        List<Long> favouritePostIds = socialDomainService.getFavouritePostIdsByUser(userId);
        return ResultUtils.success(favouritePostIds);
    }

    // ===== 关注相关接口 =====

    /**
     * 关注/取消关注用户
     */
    @PostMapping("/user/follow")
    public BaseResponse<Boolean> toggleUserFollow(@RequestBody UserFollowRequest userFollowRequest, HttpServletRequest request) {
        if (userFollowRequest == null || userFollowRequest.getFollowingId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean isFollowed = socialDomainService.toggleUserFollow(userId, userFollowRequest.getFollowingId());
        // 通知被关注者（仅关注时）
        try {
            if (isFollowed) {
                Notification n = new Notification();
                n.setReceiverId(userFollowRequest.getFollowingId());
                n.setType("follow");
                n.setContent("你获得了一个新关注");
                n.setExtra("{}");
                notifyService.push(n);
            }
        } catch (Exception ignored) {}
        return ResultUtils.success(isFollowed);
    }

    /**
     * 检查用户是否已关注
     */
    @GetMapping("/user/isFollowed")
    public BaseResponse<Boolean> isUserFollowedByUser(@RequestParam Long followingId, HttpServletRequest request) {
        if (followingId == null || followingId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean isFollowed = socialDomainService.isUserFollowedByUser(userId, followingId);
        return ResultUtils.success(isFollowed);
    }

    /**
     * 获取用户的关注列表
     */
    @GetMapping("/user/following")
    public BaseResponse<List<Long>> getFollowingIds(HttpServletRequest request) {
        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        List<Long> followingIds = socialDomainService.getFollowingIdsByUser(userId);
        return ResultUtils.success(followingIds);
    }

    /**
     * 获取用户的粉丝列表
     */
    @GetMapping("/user/followers")
    public BaseResponse<List<Long>> getFollowerIds(HttpServletRequest request) {
        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        List<Long> followerIds = socialDomainService.getFollowerIdsByUser(userId);
        return ResultUtils.success(followerIds);
    }

    /**
     * 获取用户关注数
     */
    @GetMapping("/user/following/count")
    public BaseResponse<Long> getFollowingCount(HttpServletRequest request) {
        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        Long count = socialDomainService.getFollowingCount(userId);
        return ResultUtils.success(count);
    }

    /**
     * 获取用户粉丝数
     */
    @GetMapping("/user/followers/count")
    public BaseResponse<Long> getFollowerCount(HttpServletRequest request) {
        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        Long count = socialDomainService.getFollowerCount(userId);
        return ResultUtils.success(count);
    }

    /**
     * 获取指定用户的关注数
     */
    @GetMapping("/user/{userId}/following/count")
    public BaseResponse<Long> getFollowingCountByUserId(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long count = socialDomainService.getFollowingCount(userId);
        return ResultUtils.success(count);
    }

    /**
     * 获取指定用户的粉丝数
     */
    @GetMapping("/user/{userId}/followers/count")
    public BaseResponse<Long> getFollowerCountByUserId(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long count = socialDomainService.getFollowerCount(userId);
        return ResultUtils.success(count);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getLoginUserId(HttpServletRequest request) {
        return UserHolder.getLoginUserId(request);
    }

    // ===== 请求类定义 =====

    /**
     * 帖子点赞请求
     */
    public static class PostLikeRequest {
        private Long postId;

        // getters and setters
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
    }

    /**
     * 帖子收藏请求
     */
    public static class PostFavouriteRequest {
        private Long postId;

        // getters and setters
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
    }

    /**
     * 用户关注请求
     */
    public static class UserFollowRequest {
        private Long followingId;

        // getters and setters
        public Long getFollowingId() { return followingId; }
        public void setFollowingId(Long followingId) { this.followingId = followingId; }
    }
}
