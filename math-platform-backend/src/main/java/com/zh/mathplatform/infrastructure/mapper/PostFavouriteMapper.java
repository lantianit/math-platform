package com.zh.mathplatform.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.mathplatform.domain.social.entity.PostFavourite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子收藏Mapper
 */
@Mapper
public interface PostFavouriteMapper extends BaseMapper<PostFavourite> {

}
