package com.zh.mathplatform.interfaces.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.PostApplicationService;
import com.zh.mathplatform.domain.post.entity.Post;
import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.PageRequest;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.interfaces.vo.post.PostVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 首页推荐流接口
 */
@RestController
@RequestMapping("/api/feed")
@Slf4j
public class FeedController {

    @Autowired
    private PostApplicationService postApplicationService;

    /**
     * 获取推荐帖子列表（首页推荐）
     */
    @PostMapping("/recommend")
    public BaseResponse<IPage<PostVO>> getRecommendPosts(@RequestBody PageRequest pageRequest, HttpServletRequest request) {
        if (pageRequest == null) {
            pageRequest = new PageRequest();
        }

        // 创建分页对象
        Page<Post> page = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        
        // 获取推荐帖子（这里可以根据用户兴趣、热度等算法推荐）
        IPage<Post> postPage = postApplicationService.getRecommendPosts(page, getLoginUserId(request));
        
        // 转换为VO（这里需要实现转换逻辑）
        IPage<PostVO> postVOPage = convertToPostVOPage(postPage);
        
        return ResultUtils.success(postVOPage);
    }

    /**
     * 获取关注用户的帖子列表（关注流）
     */
    @PostMapping("/following")
    public BaseResponse<IPage<PostVO>> getFollowingPosts(@RequestBody PageRequest pageRequest, HttpServletRequest request) {
        // 检查用户登录状态
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        if (pageRequest == null) {
            pageRequest = new PageRequest();
        }

        // 创建分页对象
        Page<Post> page = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        
        // 获取关注用户的帖子
        IPage<Post> postPage = postApplicationService.getFollowingPosts(page, userId);
        
        // 转换为VO
        IPage<PostVO> postVOPage = convertToPostVOPage(postPage);
        
        return ResultUtils.success(postVOPage);
    }

    /**
     * 获取热门帖子列表
     */
    @PostMapping("/hot")
    public BaseResponse<IPage<PostVO>> getHotPosts(@RequestBody PageRequest pageRequest, HttpServletRequest request) {
        if (pageRequest == null) {
            pageRequest = new PageRequest();
        }

        // 创建分页对象
        Page<Post> page = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        
        // 获取热门帖子
        IPage<Post> postPage = postApplicationService.getHotPosts(page);
        
        // 转换为VO
        IPage<PostVO> postVOPage = convertToPostVOPage(postPage);
        
        return ResultUtils.success(postVOPage);
    }

    /**
     * 获取最新帖子列表
     */
    @PostMapping("/latest")
    public BaseResponse<IPage<PostVO>> getLatestPosts(@RequestBody PageRequest pageRequest, HttpServletRequest request) {
        if (pageRequest == null) {
            pageRequest = new PageRequest();
        }

        // 创建分页对象
        Page<Post> page = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        
        // 获取最新帖子
        IPage<Post> postPage = postApplicationService.getLatestPosts(page);
        
        // 转换为VO
        IPage<PostVO> postVOPage = convertToPostVOPage(postPage);
        
        return ResultUtils.success(postVOPage);
    }

    /**
     * 根据分类获取帖子列表
     */
    @PostMapping("/category/{category}")
    public BaseResponse<IPage<PostVO>> getPostsByCategory(
            @PathVariable String category,
            @RequestBody PageRequest pageRequest, 
            HttpServletRequest request) {
        
        if (pageRequest == null) {
            pageRequest = new PageRequest();
        }

        // 创建分页对象
        Page<Post> page = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        
        // 获取分类帖子
        IPage<Post> postPage = postApplicationService.getPostsByCategory(page, category);
        
        // 转换为VO
        IPage<PostVO> postVOPage = convertToPostVOPage(postPage);
        
        return ResultUtils.success(postVOPage);
    }

    // ===== 私有方法 =====

    /**
     * 获取当前登录用户ID
     */
    private Long getLoginUserId(HttpServletRequest request) {
        // 这里需要实现从session或token中获取用户ID的逻辑
        // 暂时返回null，需要根据实际的认证方式实现
        Object userIdObj = request.getSession().getAttribute("userId");
        if (userIdObj != null) {
            return Long.valueOf(userIdObj.toString());
        }
        return null;
    }

    /**
     * 转换Post分页为PostVO分页
     */
    private IPage<PostVO> convertToPostVOPage(IPage<Post> postPage) {
        // TODO: 实现Post到PostVO的转换逻辑
        // 这里需要调用PostVO的转换方法，包括用户信息、点赞状态等
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        // 转换逻辑需要在PostVO中实现
        return postVOPage;
    }
}
