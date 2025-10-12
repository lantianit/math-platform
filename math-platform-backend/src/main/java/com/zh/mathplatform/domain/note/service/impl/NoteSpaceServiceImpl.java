package com.zh.mathplatform.domain.note.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zh.mathplatform.domain.note.entity.NoteSpace;
import com.zh.mathplatform.domain.note.service.NoteSpaceService;
import com.zh.mathplatform.domain.note.valueobject.NoteSpaceLevelEnum;
import com.zh.mathplatform.domain.user.entity.User;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.exception.ThrowUtils;
import com.zh.mathplatform.infrastructure.mapper.NoteSpaceMapper;
import com.zh.mathplatform.infrastructure.utils.LockManager;
import com.zh.mathplatform.interfaces.dto.note.NoteSpaceAddRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 笔记空间Service实现
 */
@Service
public class NoteSpaceServiceImpl extends ServiceImpl<NoteSpaceMapper, NoteSpace> implements NoteSpaceService {

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private LockManager lockManager;

    @Override
    public void validNoteSpace(NoteSpace noteSpace, boolean add) {
        ThrowUtils.throwIf(noteSpace == null, ErrorCode.PARAMS_ERROR);

        // 从对象中取值
        String spaceName = noteSpace.getSpaceName();
        Integer spaceLevel = noteSpace.getSpaceLevel();
        NoteSpaceLevelEnum spaceLevelEnum = NoteSpaceLevelEnum.getEnumByValue(spaceLevel);

        // 创建时校验
        if (add) {
            if (StrUtil.isBlank(spaceName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            }
            if (spaceLevel == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            }
        }

        // 修改数据时，如果要改空间级别
        if (spaceLevel != null && spaceLevelEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不存在");
        }

        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
    }

    @Override
    public void fillNoteSpaceByLevel(NoteSpace noteSpace) {
        // 根据空间级别，自动填充限额
        NoteSpaceLevelEnum spaceLevelEnum = NoteSpaceLevelEnum.getEnumByValue(noteSpace.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (noteSpace.getMaxSize() == null) {
                noteSpace.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (noteSpace.getMaxNoteCount() == null) {
                noteSpace.setMaxNoteCount(maxCount);
            }
        }
    }

    @Override
    public long addNoteSpace(NoteSpaceAddRequest noteSpaceAddRequest, User loginUser) {
        // 转换 DTO 为实体
        NoteSpace noteSpace = new NoteSpace();
        BeanUtils.copyProperties(noteSpaceAddRequest, noteSpace);

        // 设置默认值
        if (StrUtil.isBlank(noteSpaceAddRequest.getSpaceName())) {
            noteSpace.setSpaceName("我的笔记空间");
        }
        if (noteSpaceAddRequest.getSpaceLevel() == null) {
            noteSpace.setSpaceLevel(NoteSpaceLevelEnum.COMMON.getValue());
        }

        // 填充限额数据
        this.fillNoteSpaceByLevel(noteSpace);

        // 数据校验
        this.validNoteSpace(noteSpace, true);

        Long userId = loginUser.getId();
        noteSpace.setUserId(userId);
        
        // 初始化使用量为0
        if (noteSpace.getTotalNoteCount() == null) {
            noteSpace.setTotalNoteCount(0L);
        }
        if (noteSpace.getTotalSize() == null) {
            noteSpace.setTotalSize(0L);
        }

        // 权限校验：非管理员只能创建普通版
        if (NoteSpaceLevelEnum.COMMON.getValue() != noteSpaceAddRequest.getSpaceLevel()
                && !loginUser.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }

        // 加锁防止重复创建
        Object lock = lockManager.getLock(userId);
        synchronized (lock) {
            try {
                Long newSpaceId = transactionTemplate.execute(status -> {
                    // 检查是否已有空间
                    boolean exists = this.lambdaQuery()
                            .eq(NoteSpace::getUserId, userId)
                            .exists();
                    ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅能有一个私有笔记空间");

                    // 保存
                    boolean result = this.save(noteSpace);
                    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
                    return noteSpace.getId();
                });
                return Optional.ofNullable(newSpaceId).orElse(-1L);
            } finally {
                // 防止内存泄漏
                lockManager.removeLock(userId);
            }
        }
    }

    @Override
    public void checkNoteSpaceAuth(User loginUser, NoteSpace noteSpace) {
        // 仅空间所有者或管理员可以操作
        if (!loginUser.getId().equals(noteSpace.getUserId()) && !loginUser.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限操作该笔记空间");
        }
    }
}

