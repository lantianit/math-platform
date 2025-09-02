package com.zh.mathplatform.domain.post.service.impl;

import com.zh.mathplatform.domain.post.entity.Post;
import com.zh.mathplatform.domain.post.repository.PostRepository;
import com.zh.mathplatform.domain.post.service.PostDomainService;
import com.zh.mathplatform.domain.post.valueobject.PostCategoryEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 帖子领域服务实现
 */
@Service
public class PostDomainServiceImpl implements PostDomainService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public Post createPost(String title, String content, Long userId, String category, String tags, String images) {
        // 校验参数
        Post.validPostAdd(title, content, userId);

        // 校验分类
        if (category != null && PostCategoryEnum.getEnumByValue(category) == null) {
            category = PostCategoryEnum.TECH.getValue();
        }

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(userId);
        post.setCategory(category != null ? category : PostCategoryEnum.TECH.getValue());
        post.setTags(tags);
        post.setImages(images);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setFavouriteCount(0);
        post.setStatus(1);
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());
        post.setIsDelete(0);

        return post;
    }

    @Override
    public Post updatePost(Post post, String title, String content, String category, String tags, String images) {
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }

        // 校验参数
        Post.validPostUpdate(post.getId(), title, content, post.getUserId());

        // 校验分类
        if (category != null && PostCategoryEnum.getEnumByValue(category) == null) {
            category = post.getCategory();
        }

        post.setTitle(title);
        post.setContent(content);
        if (category != null) {
            post.setCategory(category);
        }
        post.setTags(tags);
        post.setImages(images);
        post.setUpdateTime(new Date());

        return post;
    }

    @Override
    public void deletePost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
        post.markAsDeleted();
        post.setUpdateTime(new Date());
    }

    @Override
    public void increaseViewCount(Post post) {
        if (post == null) {
            return;
        }
        post.increaseViewCount();
        post.setUpdateTime(new Date());
    }

    @Override
    public void handleLike(Post post, boolean isLike) {
        if (post == null) {
            return;
        }
        if (isLike) {
            post.increaseLikeCount();
        } else {
            post.decreaseLikeCount();
        }
        post.setUpdateTime(new Date());
    }

    @Override
    public void handleFavourite(Post post, boolean isFavourite) {
        if (post == null) {
            return;
        }
        if (isFavourite) {
            post.increaseFavouriteCount();
        } else {
            post.decreaseFavouriteCount();
        }
        post.setUpdateTime(new Date());
    }

    @Override
    public void handleComment(Post post, boolean isAddComment) {
        if (post == null) {
            return;
        }
        if (isAddComment) {
            post.increaseCommentCount();
        } else {
            post.decreaseCommentCount();
        }
        post.setUpdateTime(new Date());
    }
}
