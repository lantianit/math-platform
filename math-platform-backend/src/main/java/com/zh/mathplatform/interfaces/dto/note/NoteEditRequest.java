package com.zh.mathplatform.interfaces.dto.note;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑笔记请求
 */
@Data
public class NoteEditRequest implements Serializable {

    /**
     * 笔记ID
     */
    private Long id;

    /**
     * 笔记标题
     */
    private String title;

    /**
     * 笔记内容（Markdown格式）
     */
    private String content;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签，JSON格式
     */
    private String tags;

    /**
     * 附件URL，JSON格式
     */
    private String attachments;

    /**
     * 状态：0-草稿 1-已发布
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}

