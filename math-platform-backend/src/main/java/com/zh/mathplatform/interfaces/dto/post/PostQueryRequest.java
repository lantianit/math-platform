package com.zh.mathplatform.interfaces.dto.post;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子查询请求
 */
@Data
public class PostQueryRequest implements Serializable {

    /**
     * 分类：tech/question/project
     */
    private String category;

    /**
     * 搜索文本
     */
    private String searchText;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（升序或降序）
     */
    private String sortOrder;

    /**
     * 当前页
     */
    private long current = 1;

    /**
     * 页面大小
     */
    private long pageSize = 10;

    private static final long serialVersionUID = 1L;
}
