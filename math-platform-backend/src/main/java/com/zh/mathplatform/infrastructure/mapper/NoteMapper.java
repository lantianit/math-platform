package com.zh.mathplatform.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.mathplatform.domain.note.entity.Note;
import org.apache.ibatis.annotations.Mapper;

/**
 * 笔记Mapper
 */
@Mapper
public interface NoteMapper extends BaseMapper<Note> {
}

