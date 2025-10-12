package com.zh.mathplatform.interfaces.vo.note;

import com.zh.mathplatform.domain.note.entity.Note;
import com.zh.mathplatform.interfaces.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 笔记视图
 */
@Data
public class NoteVO implements Serializable {

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
     * 内容摘要
     */
    private String contentSummary;

    /**
     * 创建者ID
     */
    private Long userId;

    /**
     * 空间ID（为空表示公共笔记）
     */
    private Long spaceId;

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
     * 附件总大小（字节）
     */
    private Long attachmentSize;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer favouriteCount;

    /**
     * 状态：0-草稿 1-已发布
     */
    private Integer status;

    /**
     * 审核状态：0-待审核 1-通过 2-拒绝
     */
    private Integer auditStatus;

    /**
     * 发布时间
     */
    private Date publishTime;

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
    public static Note voToObj(NoteVO noteVO) {
        if (noteVO == null) {
            return null;
        }
        Note note = new Note();
        BeanUtils.copyProperties(noteVO, note);
        return note;
    }

    /**
     * 对象转封装类
     */
    public static NoteVO objToVo(Note note) {
        if (note == null) {
            return null;
        }
        NoteVO noteVO = new NoteVO();
        BeanUtils.copyProperties(note, noteVO);
        return noteVO;
    }
}

