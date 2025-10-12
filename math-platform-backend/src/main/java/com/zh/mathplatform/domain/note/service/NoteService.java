package com.zh.mathplatform.domain.note.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zh.mathplatform.domain.note.entity.Note;
import com.zh.mathplatform.domain.user.entity.User;
import com.zh.mathplatform.interfaces.dto.note.NoteAddRequest;

/**
 * 笔记Service
 */
public interface NoteService extends IService<Note> {

    /**
     * 校验笔记数据
     */
    void validNote(Note note, boolean add);

    /**
     * 校验笔记权限
     */
    void checkNoteAuth(User loginUser, Note note);

    /**
     * 创建笔记
     */
    Long createNote(NoteAddRequest noteAddRequest, User loginUser);

    /**
     * 删除笔记
     */
    void deleteNote(Long noteId, User loginUser);

    /**
     * 计算附件大小
     */
    long calculateAttachmentSize(String attachments);
}

