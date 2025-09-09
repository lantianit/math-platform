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
import com.zh.mathplatform.infrastructure.utils.PostVOConverter;
import com.zh.mathplatform.infrastructure.utils.UserHolder;
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
@RequestMapping("/feed")
@Slf4j
public class FeedController {

    @Autowired
    private PostApplicationService postApplicationService;

    @Autowired
    private PostVOConverter postVOConverter;

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
        IPage<Post> postPage = postApplicationService.getRecommendPosts(page, UserHolder.getLoginUserId(request));
        
        // 转换为VO
        IPage<PostVO> postVOPage = postVOConverter.convertToVOPage(postPage);
        
        return ResultUtils.success(postVOPage);
    }

    /**
     * 获取关注用户的帖子列表（关注流）
     */
    @PostMapping("/following")
    public BaseResponse<IPage<PostVO>> getFollowingPosts(@RequestBody PageRequest pageRequest, HttpServletRequest request) {
        // 检查用户登录状态
        Long userId = UserHolder.getLoginUserIdRequired(request);

        if (pageRequest == null) {
            pageRequest = new PageRequest();
        }

        // 创建分页对象
        Page<Post> page = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize());
        
        // 获取关注用户的帖子
        IPage<Post> postPage = postApplicationService.getFollowingPosts(page, userId);
        
        // 转换为VO
        IPage<PostVO> postVOPage = postVOConverter.convertToVOPage(postPage);
        
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
        IPage<PostVO> postVOPage = postVOConverter.convertToVOPage(postPage);
        
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
        IPage<PostVO> postVOPage = postVOConverter.convertToVOPage(postPage);
        
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
        IPage<PostVO> postVOPage = postVOConverter.convertToVOPage(postPage);
        
        return ResultUtils.success(postVOPage);
    }

}
