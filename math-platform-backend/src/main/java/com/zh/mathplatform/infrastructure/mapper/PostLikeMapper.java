package com.zh.mathplatform.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.mathplatform.domain.social.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子点赞Mapper
 */
@Mapper
public interface PostLikeMapper extends BaseMapper<PostLike> {

}
