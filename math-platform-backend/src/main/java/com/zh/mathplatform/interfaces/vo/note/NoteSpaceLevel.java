package com.zh.mathplatform.interfaces.vo.note;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 笔记空间级别信息
 */
@Data
@AllArgsConstructor
public class NoteSpaceLevel implements Serializable {

    /**
     * 级别值
     */
    private int value;

    /**
     * 级别文本
     */
    private String text;

    /**
     * 最大笔记数量
     */
    private long maxCount;

    /**
     * 最大存储大小
     */
    private long maxSize;

    private static final long serialVersionUID = 1L;
}

