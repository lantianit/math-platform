package com.zh.mathplatform.interfaces.dto.note;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新笔记空间请求（管理员使用）
 */
@Data
public class NoteSpaceUpdateRequest implements Serializable {

    /**
     * 空间ID
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 最大笔记数量
     */
    private Long maxNoteCount;

    /**
     * 最大存储大小
     */
    private Long maxSize;

    private static final long serialVersionUID = 1L;
}

