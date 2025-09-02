package com.zh.mathplatform.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.mathplatform.domain.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子Mapper
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 获取关注用户的帖子
     */
    List<Post> findFollowingUserPosts(@Param("userId") Long userId, 
                                     @Param("offset") Long offset, 
                                     @Param("size") Long size);

    /**
     * 获取关注用户的帖子总数
     */
    Long countFollowingUserPosts(@Param("userId") Long userId);

    /**
     * 获取热门帖子
     */
    List<Post> findHotPosts(@Param("limit") Integer limit);

}
