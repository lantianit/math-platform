package com.zh.mathplatform.domain.note.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 笔记空间实体
 */
@TableName(value = "note_space")
@Data
public class NoteSpace implements Serializable {
    
    /**
     * 空间ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 当前笔记数量
     */
    private Long totalNoteCount;

    /**
     * 最大存储大小（字节）
     */
    private Long maxSize;

    /**
     * 当前使用大小（字节）
     */
    private Long totalSize;

    /**
     * 创建用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

