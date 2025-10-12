package com.zh.mathplatform.interfaces.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.note.entity.Note;
import com.zh.mathplatform.domain.note.service.NoteService;
import com.zh.mathplatform.domain.user.constant.UserConstant;
import com.zh.mathplatform.domain.user.entity.User;
import com.zh.mathplatform.domain.user.service.UserDomainService;
import com.zh.mathplatform.infrastructure.annotation.AuthCheck;
import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.DeleteRequest;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.exception.ThrowUtils;
import com.zh.mathplatform.interfaces.dto.note.NoteAddRequest;
import com.zh.mathplatform.interfaces.dto.note.NoteEditRequest;
import com.zh.mathplatform.interfaces.dto.note.NoteQueryRequest;
import com.zh.mathplatform.interfaces.vo.note.NoteVO;
import com.zh.mathplatform.interfaces.vo.user.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 笔记接口
 */
@RestController
@RequestMapping("/note")
@Slf4j
@Api(tags = "noteController")
public class NoteController {

    @Resource
    private NoteService noteService;

    @Resource
    private UserDomainService userDomainService;

    /**
     * 创建笔记
     */
    @PostMapping("/add")
    @ApiOperation("创建笔记")
    public BaseResponse<Long> addNote(@RequestBody NoteAddRequest noteAddRequest,
                                      HttpServletRequest request) {
        ThrowUtils.throwIf(noteAddRequest == null, ErrorCode.PARAMS_ERROR);

        User loginUser = userDomainService.getLoginUser(request);
        Long noteId = noteService.createNote(noteAddRequest, loginUser);
        return ResultUtils.success(noteId);
    }

    /**
     * 删除笔记
     */
    @PostMapping("/delete")
    @ApiOperation("删除笔记")
    public BaseResponse<Boolean> deleteNote(@RequestBody DeleteRequest deleteRequest,
                                            HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userDomainService.getLoginUser(request);
        long id = deleteRequest.getId();

        noteService.deleteNote(id, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 编辑笔记
     */
    @PostMapping("/edit")
    @ApiOperation("编辑笔记")
    public BaseResponse<Boolean> editNote(@RequestBody NoteEditRequest noteEditRequest,
                                          HttpServletRequest request) {
        if (noteEditRequest == null || noteEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userDomainService.getLoginUser(request);

        // 判断是否存在
        long id = noteEditRequest.getId();
        Note oldNote = noteService.getById(id);
        ThrowUtils.throwIf(oldNote == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可编辑
        noteService.checkNoteAuth(loginUser, oldNote);

        // DTO 转实体
        Note note = new Note();
        BeanUtils.copyProperties(noteEditRequest, note);

        // 重新计算附件大小
        long newAttachmentSize = noteService.calculateAttachmentSize(note.getAttachments());
        note.setAttachmentSize(newAttachmentSize);

        // 数据校验
        noteService.validNote(note, false);

        // 操作数据库
        boolean result = noteService.updateById(note);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取笔记（封装类）
     */
    @GetMapping("/get/vo")
    @ApiOperation("根据id获取笔记")
    public BaseResponse<NoteVO> getNoteVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 查询数据库
        Note note = noteService.getById(id);
        ThrowUtils.throwIf(note == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取封装类
        NoteVO noteVO = NoteVO.objToVo(note);

        // 关联查询用户信息
        Long userId = note.getUserId();
        if (userId != null && userId > 0) {
            User user = userDomainService.getById(userId);
            UserVO userVO = userDomainService.getUserVO(user);
            noteVO.setUser(userVO);
        }

        // 增加浏览量
        noteService.lambdaUpdate()
                .eq(Note::getId, id)
                .setSql("view_count = view_count + 1")
                .update();

        return ResultUtils.success(noteVO);
    }

    /**
     * 分页获取笔记列表（封装类）
     */
    @PostMapping("/list/page/vo")
    @ApiOperation("分页获取笔记列表")
    public BaseResponse<Page<NoteVO>> listNoteVOByPage(@RequestBody NoteQueryRequest noteQueryRequest,
                                                        HttpServletRequest request) {
        long current = noteQueryRequest.getCurrent();
        long size = noteQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 构建查询条件
        Page<Note> notePage = noteService.page(new Page<>(current, size),
                noteService.lambdaQuery()
                        .eq(noteQueryRequest.getId() != null, Note::getId, noteQueryRequest.getId())
                        .eq(noteQueryRequest.getUserId() != null, Note::getUserId, noteQueryRequest.getUserId())
                        .eq(noteQueryRequest.getSpaceId() != null, Note::getSpaceId, noteQueryRequest.getSpaceId())
                        .like(StrUtil.isNotBlank(noteQueryRequest.getTitle()), Note::getTitle, noteQueryRequest.getTitle())
                        .eq(StrUtil.isNotBlank(noteQueryRequest.getCategory()), Note::getCategory, noteQueryRequest.getCategory())
                        .eq(noteQueryRequest.getStatus() != null, Note::getStatus, noteQueryRequest.getStatus())
                        .like(StrUtil.isNotBlank(noteQueryRequest.getSearchText()), Note::getContent, noteQueryRequest.getSearchText())
                        .orderByDesc(Note::getCreateTime)
                        .getWrapper());

        // 获取封装类
        Page<NoteVO> noteVOPage = new Page<>(current, size, notePage.getTotal());
        List<NoteVO> noteVOList = notePage.getRecords().stream()
                .map(NoteVO::objToVo)
                .collect(Collectors.toList());

        // 关联查询用户信息
        Set<Long> userIdSet = notePage.getRecords().stream()
                .map(Note::getUserId)
                .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userDomainService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        noteVOList.forEach(noteVO -> {
            Long userId = noteVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            noteVO.setUser(userDomainService.getUserVO(user));
        });

        noteVOPage.setRecords(noteVOList);
        return ResultUtils.success(noteVOPage);
    }

    /**
     * 分页获取笔记列表（管理员）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation("分页获取笔记列表")
    public BaseResponse<Page<Note>> listNoteByPage(@RequestBody NoteQueryRequest noteQueryRequest) {
        long current = noteQueryRequest.getCurrent();
        long size = noteQueryRequest.getPageSize();

        // 构建查询条件
        Page<Note> notePage = noteService.page(new Page<>(current, size),
                noteService.lambdaQuery()
                        .eq(noteQueryRequest.getId() != null, Note::getId, noteQueryRequest.getId())
                        .eq(noteQueryRequest.getUserId() != null, Note::getUserId, noteQueryRequest.getUserId())
                        .eq(noteQueryRequest.getSpaceId() != null, Note::getSpaceId, noteQueryRequest.getSpaceId())
                        .like(StrUtil.isNotBlank(noteQueryRequest.getTitle()), Note::getTitle, noteQueryRequest.getTitle())
                        .eq(StrUtil.isNotBlank(noteQueryRequest.getCategory()), Note::getCategory, noteQueryRequest.getCategory())
                        .eq(noteQueryRequest.getStatus() != null, Note::getStatus, noteQueryRequest.getStatus())
                        .orderByDesc(Note::getCreateTime)
                        .getWrapper());

        return ResultUtils.success(notePage);
    }
}

