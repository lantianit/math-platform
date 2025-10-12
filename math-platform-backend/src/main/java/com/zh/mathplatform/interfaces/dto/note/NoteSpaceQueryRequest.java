package com.zh.mathplatform.interfaces.dto.note;

import com.zh.mathplatform.infrastructure.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询笔记空间请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NoteSpaceQueryRequest extends PageRequest implements Serializable {

    /**
     * 空间ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    private static final long serialVersionUID = 1L;
}

