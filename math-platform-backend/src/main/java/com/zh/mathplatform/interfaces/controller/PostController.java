package com.zh.mathplatform.interfaces.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.PostApplicationService;
import com.zh.mathplatform.domain.post.entity.Post;
import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/api/post")
@Slf4j
public class PostController {

    @Autowired
    private PostApplicationService postApplicationService;

    /**
     * 发布帖子
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        Post post = postApplicationService.addPost(
            postAddRequest.getTitle(),
            postAddRequest.getContent(),
            userId,
            postAddRequest.getCategory(),
            postAddRequest.getTags(),
            postAddRequest.getImages()
        );

        return ResultUtils.success(post.getId());
    }

    /**
     * 更新帖子
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest, HttpServletRequest request) {
        if (postUpdateRequest == null || postUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        postApplicationService.updatePost(
            postUpdateRequest.getId(),
            postUpdateRequest.getTitle(),
            postUpdateRequest.getContent(),
            postUpdateRequest.getCategory(),
            postUpdateRequest.getTags(),
            postUpdateRequest.getImages(),
            userId
        );

        return ResultUtils.success(true);
    }

    /**
     * 删除帖子
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody PostDeleteRequest postDeleteRequest, HttpServletRequest request) {
        if (postDeleteRequest == null || postDeleteRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean result = postApplicationService.deletePost(postDeleteRequest.getId(), userId);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取帖子详情
     */
    @GetMapping("/get")
    public BaseResponse<Post> getPostById(@RequestParam Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Post post = postApplicationService.getPostById(id);

        // 增加浏览量（异步处理）
        try {
            postApplicationService.increasePostViewCount(id);
        } catch (Exception e) {
            log.error("增加浏览量失败", e);
        }

        return ResultUtils.success(post);
    }

    /**
     * 分页获取帖子列表
     */
    @PostMapping("/list/page")
    public BaseResponse<IPage<Post>> listPostsByPage(@RequestBody PostQueryRequest postQueryRequest) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();

        Page<Post> page = new Page<>(current, size);
        IPage<Post> postPage = postApplicationService.listPostsByPage(
            page,
            postQueryRequest.getCategory(),
            postQueryRequest.getSearchText(),
            postQueryRequest.getSortField(),
            postQueryRequest.getSortOrder()
        );

        return ResultUtils.success(postPage);
    }

    /**
     * 获取热门帖子
     */
    @GetMapping("/hot")
    public BaseResponse<List<Post>> getHotPosts(@RequestParam(defaultValue = "10") Integer limit) {
        List<Post> hotPosts = postApplicationService.getHotPosts(limit);
        return ResultUtils.success(hotPosts);
    }

    /**
     * 获取最新帖子
     */
    @GetMapping("/latest")
    public BaseResponse<List<Post>> getLatestPosts(@RequestParam(defaultValue = "10") Integer limit) {
        List<Post> latestPosts = postApplicationService.getLatestPosts(limit);
        return ResultUtils.success(latestPosts);
    }

    /**
     * 按用户分页获取帖子列表
     */
    @PostMapping("/user/list/page")
    public BaseResponse<IPage<Post>> listUserPostsByPage(@RequestBody UserPostQueryRequest req) {
        if (req == null || req.getUserId() == null || req.getUserId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Post> page = new Page<>(req.getCurrent() == null ? 1 : req.getCurrent(),
                req.getPageSize() == null ? 10 : req.getPageSize());
        IPage<Post> postPage = postApplicationService.listPostsByUserIdPage(page, req.getUserId(),
                req.getSortField(), req.getSortOrder());
        return ResultUtils.success(postPage);
    }

    /**
     * 用户帖子统计：帖子数、被点赞总数、被收藏总数、总浏览数
     */
    @GetMapping("/user/stats")
    public BaseResponse<UserPostStatsVO> getUserPostStats(@RequestParam Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Post> list = postApplicationService.listPostsByUserId(userId);
        long postCount = list.size();
        long likeTotal = list.stream().mapToLong(p -> p.getLikeCount() == null ? 0 : p.getLikeCount()).sum();
        long favTotal = list.stream().mapToLong(p -> p.getFavouriteCount() == null ? 0 : p.getFavouriteCount()).sum();
        long viewTotal = list.stream().mapToLong(p -> p.getViewCount() == null ? 0 : p.getViewCount()).sum();
        UserPostStatsVO vo = new UserPostStatsVO();
        vo.setPostCount(postCount);
        vo.setLikeTotal(likeTotal);
        vo.setFavouriteTotal(favTotal);
        vo.setViewTotal(viewTotal);
        return ResultUtils.success(vo);
    }

    /**
     * 处理帖子点赞
     */
    @PostMapping("/like")
    public BaseResponse<Boolean> togglePostLike(@RequestBody PostLikeRequest postLikeRequest, HttpServletRequest request) {
        if (postLikeRequest == null || postLikeRequest.getPostId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean isLiked = postApplicationService.togglePostLike(postLikeRequest.getPostId(), userId);
        return ResultUtils.success(isLiked);
    }

    /**
     * 处理帖子收藏
     */
    @PostMapping("/favourite")
    public BaseResponse<Boolean> togglePostFavourite(@RequestBody PostFavouriteRequest postFavouriteRequest, HttpServletRequest request) {
        if (postFavouriteRequest == null || postFavouriteRequest.getPostId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean isFavourited = postApplicationService.togglePostFavourite(postFavouriteRequest.getPostId(), userId);
        return ResultUtils.success(isFavourited);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getLoginUserId(HttpServletRequest request) {
        return UserHolder.getLoginUserId(request);
    }

    // ===== 请求类定义 =====

    /**
     * 帖子发布请求
     */
    public static class PostAddRequest {
        private String title;
        private String content;
        private String category;
        private String tags;
        private String images;

        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
        public String getImages() { return images; }
        public void setImages(String images) { this.images = images; }
    }

    /**
     * 帖子更新请求
     */
    public static class PostUpdateRequest {
        private Long id;
        private String title;
        private String content;
        private String category;
        private String tags;
        private String images;

        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
        public String getImages() { return images; }
        public void setImages(String images) { this.images = images; }
    }

    /**
     * 帖子删除请求
     */
    public static class PostDeleteRequest {
        private Long id;

        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    /**
     * 帖子查询请求
     */
    public static class PostQueryRequest {
        private String category;
        private String searchText;
        private String sortField;
        private String sortOrder;
        private long current = 1;
        private long pageSize = 10;

        // getters and setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getSearchText() { return searchText; }
        public void setSearchText(String searchText) { this.searchText = searchText; }
        public String getSortField() { return sortField; }
        public void setSortField(String sortField) { this.sortField = sortField; }
        public String getSortOrder() { return sortOrder; }
        public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
        public long getCurrent() { return current; }
        public void setCurrent(long current) { this.current = current; }
        public long getPageSize() { return pageSize; }
        public void setPageSize(long pageSize) { this.pageSize = pageSize; }
    }

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
     * 用户帖子查询请求
     */
    public static class UserPostQueryRequest {
        private Long userId;
        private Long current;
        private Long pageSize;
        private String sortField = "createTime";
        private String sortOrder = "desc";

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getCurrent() { return current; }
        public void setCurrent(Long current) { this.current = current; }
        public Long getPageSize() { return pageSize; }
        public void setPageSize(Long pageSize) { this.pageSize = pageSize; }
        public String getSortField() { return sortField; }
        public void setSortField(String sortField) { this.sortField = sortField; }
        public String getSortOrder() { return sortOrder; }
        public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
    }

    /**
     * 用户帖子统计返回
     */
    public static class UserPostStatsVO {
        private long postCount;
        private long likeTotal;
        private long favouriteTotal;
        private long viewTotal;

        public long getPostCount() { return postCount; }
        public void setPostCount(long postCount) { this.postCount = postCount; }
        public long getLikeTotal() { return likeTotal; }
        public void setLikeTotal(long likeTotal) { this.likeTotal = likeTotal; }
        public long getFavouriteTotal() { return favouriteTotal; }
        public void setFavouriteTotal(long favouriteTotal) { this.favouriteTotal = favouriteTotal; }
        public long getViewTotal() { return viewTotal; }
        public void setViewTotal(long viewTotal) { this.viewTotal = viewTotal; }
    }
}
