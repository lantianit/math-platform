package com.zh.mathplatform.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.mathplatform.domain.note.entity.NoteSpace;
import org.apache.ibatis.annotations.Mapper;

/**
 * 笔记空间Mapper
 */
@Mapper
public interface NoteSpaceMapper extends BaseMapper<NoteSpace> {
}

