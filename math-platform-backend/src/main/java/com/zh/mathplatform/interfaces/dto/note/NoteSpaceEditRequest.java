package com.zh.mathplatform.interfaces.dto.note;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑笔记空间请求（用户使用）
 */
@Data
public class NoteSpaceEditRequest implements Serializable {

    /**
     * 空间ID
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    private static final long serialVersionUID = 1L;
}

