package com.zh.mathplatform.domain.note.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zh.mathplatform.domain.note.entity.Note;
import com.zh.mathplatform.domain.note.entity.NoteSpace;
import com.zh.mathplatform.domain.note.service.NoteService;
import com.zh.mathplatform.domain.note.service.NoteSpaceService;
import com.zh.mathplatform.domain.user.entity.User;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.exception.ThrowUtils;
import com.zh.mathplatform.infrastructure.mapper.NoteMapper;
import com.zh.mathplatform.interfaces.dto.note.NoteAddRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * 笔记Service实现
 */
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private NoteSpaceService noteSpaceService;

    @Override
    public void validNote(Note note, boolean add) {
        ThrowUtils.throwIf(note == null, ErrorCode.PARAMS_ERROR);

        // 从对象中取值
        String title = note.getTitle();
        String content = note.getContent();

        // 创建时校验
        if (add) {
            if (StrUtil.isBlank(title)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "笔记标题不能为空");
            }
            if (StrUtil.isBlank(content)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "笔记内容不能为空");
            }
        }

        // 修改数据时的校验
        if (StrUtil.isNotBlank(title) && title.length() > 200) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "笔记标题过长");
        }
        if (StrUtil.isNotBlank(content) && content.length() > 50000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "笔记内容过长");
        }
    }

    @Override
    public void checkNoteAuth(User loginUser, Note note) {
        Long spaceId = note.getSpaceId();
        if (spaceId == null) {
            // 公共笔记：本人或管理员可操作
            if (!note.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        } else {
            // 私有空间笔记：仅空间所有者可操作
            if (!note.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
    }

    @Override
    public Long createNote(NoteAddRequest noteAddRequest, User loginUser) {
        // 转换 DTO 为实体
        Note note = new Note();
        BeanUtils.copyProperties(noteAddRequest, note);
        note.setUserId(loginUser.getId());

        // 校验空间权限和额度
        Long spaceId = noteAddRequest.getSpaceId();
        if (spaceId != null) {
            NoteSpace space = noteSpaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "笔记空间不存在");

            // 权限校验：仅空间所有者可创建
            if (!loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限在该空间创建笔记");
            }

            // 额度校验
            if (space.getTotalNoteCount() >= space.getMaxNoteCount()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间笔记数量已达上限");
            }
            
            // 计算附件大小
            long attachmentSize = calculateAttachmentSize(note.getAttachments());
            long newTotalSize = space.getTotalSize() + attachmentSize;
            if (newTotalSize > space.getMaxSize()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间存储已满");
            }
        }

        // 数据校验
        this.validNote(note, true);

        // 计算附件大小
        long attachmentSize = calculateAttachmentSize(note.getAttachments());
        note.setAttachmentSize(attachmentSize);
        
        // 初始化计数器
        if (note.getViewCount() == null) {
            note.setViewCount(0);
        }
        if (note.getLikeCount() == null) {
            note.setLikeCount(0);
        }
        if (note.getFavouriteCount() == null) {
            note.setFavouriteCount(0);
        }
        
        // 设置默认审核状态
        if (spaceId != null) {
            // 私有笔记默认通过
            note.setAuditStatus(1);
        } else {
            // 公共笔记默认待审核
            note.setAuditStatus(0);
        }

        // 事务保存
        Long finalSpaceId = spaceId;
        return transactionTemplate.execute(status -> {
            boolean result = this.save(note);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建笔记失败");

            // 更新空间额度
            if (finalSpaceId != null) {
                boolean update = noteSpaceService.lambdaUpdate()
                        .eq(NoteSpace::getId, finalSpaceId)
                        .setSql("total_note_count = total_note_count + 1")
                        .setSql("total_size = total_size + " + attachmentSize)
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            return note.getId();
        });
    }

    @Override
    public void deleteNote(Long noteId, User loginUser) {
        ThrowUtils.throwIf(noteId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);

        // 判断是否存在
        Note oldNote = this.getById(noteId);
        ThrowUtils.throwIf(oldNote == null, ErrorCode.NOT_FOUND_ERROR);

        // 权限校验
        checkNoteAuth(loginUser, oldNote);

        // 开启事务
        transactionTemplate.execute(status -> {
            // 删除笔记
            boolean result = this.removeById(noteId);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

            // 释放额度
            Long spaceId = oldNote.getSpaceId();
            if (spaceId != null) {
                long attachmentSize = oldNote.getAttachmentSize() != null ? oldNote.getAttachmentSize() : 0L;
                boolean update = noteSpaceService.lambdaUpdate()
                        .eq(NoteSpace::getId, spaceId)
                        .setSql("total_note_count = total_note_count - 1")
                        .setSql("total_size = total_size - " + attachmentSize)
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            return true;
        });
    }

    @Override
    public long calculateAttachmentSize(String attachments) {
        if (StrUtil.isBlank(attachments)) {
            return 0L;
        }
        try {
            // 假设attachments是一个JSON数组，每个对象包含size字段
            JSONArray jsonArray = JSONUtil.parseArray(attachments);
            long totalSize = 0L;
            for (int i = 0; i < jsonArray.size(); i++) {
                Object obj = jsonArray.get(i);
                if (obj instanceof cn.hutool.json.JSONObject) {
                    cn.hutool.json.JSONObject jsonObj = (cn.hutool.json.JSONObject) obj;
                    Long size = jsonObj.getLong("size");
                    if (size != null) {
                        totalSize += size;
                    }
                }
            }
            return totalSize;
        } catch (Exception e) {
            // 如果解析失败，返回0
            return 0L;
        }
    }
}

