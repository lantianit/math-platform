package com.zh.mathplatform.interfaces.dto.note;

import com.zh.mathplatform.infrastructure.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询笔记请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NoteQueryRequest extends PageRequest implements Serializable {

    /**
     * 笔记ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 空间ID
     */
    private Long spaceId;

    /**
     * 笔记标题
     */
    private String title;

    /**
     * 分类
     */
    private String category;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 状态：0-草稿 1-已发布
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}

