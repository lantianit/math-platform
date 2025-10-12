package com.zh.mathplatform.interfaces.vo.note;

import com.zh.mathplatform.domain.note.entity.NoteSpace;
import com.zh.mathplatform.interfaces.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 笔记空间视图
 */
@Data
public class NoteSpaceVO implements Serializable {

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
     * 当前笔记数量
     */
    private Long totalNoteCount;

    /**
     * 最大存储大小
     */
    private Long maxSize;

    /**
     * 当前使用大小
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
     * 创建用户信息
     */
    private UserVO user;

    private static final long serialVersionUID = 1L;

    /**
     * 封装类转对象
     */
    public static NoteSpace voToObj(NoteSpaceVO noteSpaceVO) {
        if (noteSpaceVO == null) {
            return null;
        }
        NoteSpace noteSpace = new NoteSpace();
        BeanUtils.copyProperties(noteSpaceVO, noteSpace);
        return noteSpace;
    }

    /**
     * 对象转封装类
     */
    public static NoteSpaceVO objToVo(NoteSpace noteSpace) {
        if (noteSpace == null) {
            return null;
        }
        NoteSpaceVO noteSpaceVO = new NoteSpaceVO();
        BeanUtils.copyProperties(noteSpace, noteSpaceVO);
        return noteSpaceVO;
    }
}

