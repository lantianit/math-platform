package com.zh.mathplatform.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.PostApplicationService;
import com.zh.mathplatform.domain.post.entity.Post;
import com.zh.mathplatform.domain.post.repository.PostRepository;
import com.zh.mathplatform.domain.post.service.PostDomainService;
import com.zh.mathplatform.domain.social.service.SocialDomainService;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 帖子应用服务实现
 */
@Service
public class PostApplicationServiceImpl implements PostApplicationService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostDomainService postDomainService;

    @Autowired
    private SocialDomainService socialDomainService;

    @Override
    @Transactional
    public Post addPost(String title, String content, Long userId, String category, String tags, String images) {
        // 创建帖子
        Post post = postDomainService.createPost(title, content, userId, category, tags, images);

        // 保存到数据库
        boolean success = postRepository.save(post);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发布帖子失败");
        }

        return post;
    }

    @Override
    @Transactional
    public Post updatePost(Long postId, String title, String content, String category, String tags, String images, Long userId) {
        // 获取原帖子
        Post existingPost = postRepository.findById(postId);
        if (existingPost == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }

        // 检查权限
        if (!existingPost.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限编辑此帖子");
        }

        // 更新帖子
        Post updatedPost = postDomainService.updatePost(existingPost, title, content, category, tags, images);

        // 保存到数据库
        boolean success = postRepository.updateById(updatedPost);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新帖子失败");
        }

        return updatedPost;
    }

    @Override
    @Transactional
    public boolean deletePost(Long postId, Long userId) {
        // 获取帖子
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }

        // 检查权限
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除此帖子");
        }

        // 删除帖子
        postDomainService.deletePost(post);

        // 保存到数据库
        boolean success = postRepository.updateById(post);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除帖子失败");
        }

        return true;
    }

    @Override
    public Post getPostById(Long postId) {
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }
        return post;
    }

    @Override
    public IPage<Post> listPostsByPage(Page<Post> page, String category, String searchText, String sortField, String sortOrder) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

        // 添加条件
        wrapper.eq(Post::getIsDelete, 0);

        // 分类筛选
        if (StrUtil.isNotBlank(category)) {
            wrapper.eq(Post::getCategory, category);
        }

        // 搜索条件
        if (StrUtil.isNotBlank(searchText)) {
            wrapper.and(w -> w.like(Post::getTitle, searchText)
                             .or()
                             .like(Post::getContent, searchText));
        }

        // 排序
        if ("asc".equalsIgnoreCase(sortOrder)) {
            if ("createTime".equals(sortField)) {
                wrapper.orderByAsc(Post::getCreateTime);
            } else if ("viewCount".equals(sortField)) {
                wrapper.orderByAsc(Post::getViewCount);
            } else if ("likeCount".equals(sortField)) {
                wrapper.orderByAsc(Post::getLikeCount);
            } else {
                wrapper.orderByAsc(Post::getCreateTime);
            }
        } else {
            if ("createTime".equals(sortField)) {
                wrapper.orderByDesc(Post::getCreateTime);
            } else if ("viewCount".equals(sortField)) {
                wrapper.orderByDesc(Post::getViewCount);
            } else if ("likeCount".equals(sortField)) {
                wrapper.orderByDesc(Post::getLikeCount);
            } else {
                wrapper.orderByDesc(Post::getCreateTime);
            }
        }

        return postRepository.findByPage(page, wrapper);
    }

    @Override
    public List<Post> listPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public boolean togglePostLike(Long postId, Long userId) {
        // 检查帖子是否存在
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }

        // 处理点赞
        boolean isLiked = socialDomainService.togglePostLike(postId, userId);

        // 更新帖子点赞数
        if (isLiked) {
            postRepository.increaseLikeCount(postId);
            postDomainService.handleLike(post, true);
        } else {
            postRepository.decreaseLikeCount(postId);
            postDomainService.handleLike(post, false);
        }

        return isLiked;
    }

    @Override
    @Transactional
    public boolean togglePostFavourite(Long postId, Long userId) {
        // 检查帖子是否存在
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }

        // 处理收藏
        boolean isFavourited = socialDomainService.togglePostFavourite(postId, userId);

        // 更新帖子收藏数
        if (isFavourited) {
            postRepository.increaseFavouriteCount(postId);
            postDomainService.handleFavourite(post, true);
        } else {
            postRepository.decreaseFavouriteCount(postId);
            postDomainService.handleFavourite(post, false);
        }

        return isFavourited;
    }

    @Override
    public void increasePostViewCount(Long postId) {
        Post post = postRepository.findById(postId);
        if (post != null) {
            postRepository.increaseViewCount(postId);
            postDomainService.increaseViewCount(post);
        }
    }

    @Override
    public List<Post> getHotPosts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getIsDelete, 0)
               .orderByDesc(Post::getViewCount)
               .orderByDesc(Post::getLikeCount)
               .last("LIMIT " + limit);

        return postRepository.findByCondition(wrapper);
    }

    @Override
    public List<Post> getLatestPosts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getIsDelete, 0)
               .orderByDesc(Post::getCreateTime)
               .last("LIMIT " + limit);

        return postRepository.findByCondition(wrapper);
    }

    @Override
    public IPage<Post> getHotPosts(Page<Post> page) {
        // 按热度评分排序获取热门帖子
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getIsDelete, 0)
               .orderByDesc(Post::getViewCount)
               .orderByDesc(Post::getLikeCount)
               .orderByDesc(Post::getCreateTime);

        return postRepository.findByConditionPage(page, wrapper);
    }

    @Override
    public IPage<Post> getLatestPosts(Page<Post> page) {
        // 按创建时间排序获取最新帖子
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getIsDelete, 0)
               .orderByDesc(Post::getCreateTime);

        return postRepository.findByConditionPage(page, wrapper);
    }

    @Override
    public IPage<Post> getRecommendPosts(Page<Post> page, Long userId) {
        // 推荐算法：结合热度、用户兴趣等
        // 这里简化处理，按综合评分排序
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getIsDelete, 0);
        
        if (userId != null) {
            // 可以根据用户的关注、点赞历史等进行个性化推荐
            // 这里暂时按浏览量和点赞数综合排序
            wrapper.orderByDesc(Post::getLikeCount)
                   .orderByDesc(Post::getViewCount)
                   .orderByDesc(Post::getCreateTime);
        } else {
            // 未登录用户显示热门内容
            wrapper.orderByDesc(Post::getViewCount)
                   .orderByDesc(Post::getCreateTime);
        }

        return postRepository.findByConditionPage(page, wrapper);
    }

    @Override
    public IPage<Post> getFollowingPosts(Page<Post> page, Long userId) {
        // 获取关注用户的帖子
        return postRepository.findFollowingUserPosts(page, userId);
    }

    @Override
    public IPage<Post> getPostsByCategory(Page<Post> page, String category) {
        // 根据分类获取帖子
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getIsDelete, 0);
        
        if (StrUtil.isNotBlank(category)) {
            wrapper.eq(Post::getCategory, category);
        }
        
        wrapper.orderByDesc(Post::getCreateTime);

        return postRepository.findByConditionPage(page, wrapper);
    }
}
