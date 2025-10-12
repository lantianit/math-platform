package com.zh.mathplatform.domain.note.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zh.mathplatform.domain.note.entity.NoteSpace;
import com.zh.mathplatform.domain.user.entity.User;
import com.zh.mathplatform.interfaces.dto.note.NoteSpaceAddRequest;

/**
 * 笔记空间Service
 */
public interface NoteSpaceService extends IService<NoteSpace> {

    /**
     * 校验笔记空间数据
     */
    void validNoteSpace(NoteSpace noteSpace, boolean add);

    /**
     * 根据空间级别自动填充限额
     */
    void fillNoteSpaceByLevel(NoteSpace noteSpace);

    /**
     * 创建笔记空间
     */
    long addNoteSpace(NoteSpaceAddRequest noteSpaceAddRequest, User loginUser);

    /**
     * 校验笔记空间权限
     */
    void checkNoteSpaceAuth(User loginUser, NoteSpace noteSpace);
}

