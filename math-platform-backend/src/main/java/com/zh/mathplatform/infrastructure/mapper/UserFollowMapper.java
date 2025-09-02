package com.zh.mathplatform.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.mathplatform.domain.social.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户关注Mapper
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

}
