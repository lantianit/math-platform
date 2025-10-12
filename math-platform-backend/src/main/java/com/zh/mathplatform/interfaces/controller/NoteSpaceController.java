package com.zh.mathplatform.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.note.entity.NoteSpace;
import com.zh.mathplatform.domain.note.service.NoteSpaceService;
import com.zh.mathplatform.domain.note.valueobject.NoteSpaceLevelEnum;
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
import com.zh.mathplatform.interfaces.dto.note.NoteSpaceAddRequest;
import com.zh.mathplatform.interfaces.dto.note.NoteSpaceEditRequest;
import com.zh.mathplatform.interfaces.dto.note.NoteSpaceQueryRequest;
import com.zh.mathplatform.interfaces.dto.note.NoteSpaceUpdateRequest;
import com.zh.mathplatform.interfaces.vo.note.NoteSpaceLevel;
import com.zh.mathplatform.interfaces.vo.note.NoteSpaceVO;
import com.zh.mathplatform.interfaces.vo.user.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 笔记空间接口
 */
@RestController
@RequestMapping("/note_space")
@Slf4j
@Api(tags = "noteSpaceController")
public class NoteSpaceController {

    @Resource
    private NoteSpaceService noteSpaceService;

    @Resource
    private UserDomainService userDomainService;

    /**
     * 创建笔记空间
     */
    @PostMapping("/add")
    @ApiOperation("创建笔记空间")
    public BaseResponse<Long> addNoteSpace(@RequestBody NoteSpaceAddRequest noteSpaceAddRequest,
                                           HttpServletRequest request) {
        ThrowUtils.throwIf(noteSpaceAddRequest == null, ErrorCode.PARAMS_ERROR);

        User loginUser = userDomainService.getLoginUser(request);
        long result = noteSpaceService.addNoteSpace(noteSpaceAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 删除笔记空间
     */
    @PostMapping("/delete")
    @ApiOperation("删除笔记空间")
    public BaseResponse<Boolean> deleteNoteSpace(@RequestBody DeleteRequest deleteRequest,
                                                  HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userDomainService.getLoginUser(request);
        long id = deleteRequest.getId();

        // 判断是否存在
        NoteSpace oldNoteSpace = noteSpaceService.getById(id);
        ThrowUtils.throwIf(oldNoteSpace == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可删除
        noteSpaceService.checkNoteSpaceAuth(loginUser, oldNoteSpace);

        // 操作数据库
        boolean result = noteSpaceService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新笔记空间（仅管理员）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation("更新笔记空间")
    public BaseResponse<Boolean> updateNoteSpace(@RequestBody NoteSpaceUpdateRequest noteSpaceUpdateRequest) {
        if (noteSpaceUpdateRequest == null || noteSpaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // DTO 转实体
        NoteSpace noteSpace = new NoteSpace();
        BeanUtils.copyProperties(noteSpaceUpdateRequest, noteSpace);

        // 自动填充数据
        noteSpaceService.fillNoteSpaceByLevel(noteSpace);

        // 数据校验
        noteSpaceService.validNoteSpace(noteSpace, false);

        // 判断是否存在
        long id = noteSpaceUpdateRequest.getId();
        NoteSpace oldNoteSpace = noteSpaceService.getById(id);
        ThrowUtils.throwIf(oldNoteSpace == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = noteSpaceService.updateById(noteSpace);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 编辑笔记空间（用户）
     */
    @PostMapping("/edit")
    @ApiOperation("编辑笔记空间")
    public BaseResponse<Boolean> editNoteSpace(@RequestBody NoteSpaceEditRequest noteSpaceEditRequest,
                                                HttpServletRequest request) {
        if (noteSpaceEditRequest == null || noteSpaceEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userDomainService.getLoginUser(request);

        // 判断是否存在
        long id = noteSpaceEditRequest.getId();
        NoteSpace oldNoteSpace = noteSpaceService.getById(id);
        ThrowUtils.throwIf(oldNoteSpace == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可编辑
        noteSpaceService.checkNoteSpaceAuth(loginUser, oldNoteSpace);

        // DTO 转实体
        NoteSpace noteSpace = new NoteSpace();
        BeanUtils.copyProperties(noteSpaceEditRequest, noteSpace);

        // 数据校验
        noteSpaceService.validNoteSpace(noteSpace, false);

        // 操作数据库
        boolean result = noteSpaceService.updateById(noteSpace);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取笔记空间（封装类）
     */
    @GetMapping("/get/vo")
    @ApiOperation("根据id获取笔记空间")
    public BaseResponse<NoteSpaceVO> getNoteSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 查询数据库
        NoteSpace noteSpace = noteSpaceService.getById(id);
        ThrowUtils.throwIf(noteSpace == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取封装类
        NoteSpaceVO noteSpaceVO = NoteSpaceVO.objToVo(noteSpace);

        // 关联查询用户信息
        Long userId = noteSpace.getUserId();
        if (userId != null && userId > 0) {
            User user = userDomainService.getById(userId);
            UserVO userVO = userDomainService.getUserVO(user);
            noteSpaceVO.setUser(userVO);
        }

        return ResultUtils.success(noteSpaceVO);
    }

    /**
     * 分页获取笔记空间列表（封装类）
     */
    @PostMapping("/list/page/vo")
    @ApiOperation("分页获取笔记空间列表")
    public BaseResponse<Page<NoteSpaceVO>> listNoteSpaceVOByPage(@RequestBody NoteSpaceQueryRequest noteSpaceQueryRequest) {
        long current = noteSpaceQueryRequest.getCurrent();
        long size = noteSpaceQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 查询数据库
        Page<NoteSpace> noteSpacePage = noteSpaceService.page(new Page<>(current, size),
                noteSpaceService.lambdaQuery()
                        .eq(noteSpaceQueryRequest.getId() != null, NoteSpace::getId, noteSpaceQueryRequest.getId())
                        .eq(noteSpaceQueryRequest.getUserId() != null, NoteSpace::getUserId, noteSpaceQueryRequest.getUserId())
                        .like(noteSpaceQueryRequest.getSpaceName() != null, NoteSpace::getSpaceName, noteSpaceQueryRequest.getSpaceName())
                        .eq(noteSpaceQueryRequest.getSpaceLevel() != null, NoteSpace::getSpaceLevel, noteSpaceQueryRequest.getSpaceLevel())
                        .orderByDesc(NoteSpace::getCreateTime)
                        .getWrapper());

        // 获取封装类
        Page<NoteSpaceVO> noteSpaceVOPage = new Page<>(current, size, noteSpacePage.getTotal());
        List<NoteSpaceVO> noteSpaceVOList = noteSpacePage.getRecords().stream()
                .map(NoteSpaceVO::objToVo)
                .collect(Collectors.toList());

        // 关联查询用户信息
        Set<Long> userIdSet = noteSpacePage.getRecords().stream()
                .map(NoteSpace::getUserId)
                .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userDomainService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        noteSpaceVOList.forEach(noteSpaceVO -> {
            Long userId = noteSpaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            noteSpaceVO.setUser(userDomainService.getUserVO(user));
        });

        noteSpaceVOPage.setRecords(noteSpaceVOList);
        return ResultUtils.success(noteSpaceVOPage);
    }

    /**
     * 分页获取笔记空间列表（管理员）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation("分页获取笔记空间列表")
    public BaseResponse<Page<NoteSpace>> listNoteSpaceByPage(@RequestBody NoteSpaceQueryRequest noteSpaceQueryRequest) {
        long current = noteSpaceQueryRequest.getCurrent();
        long size = noteSpaceQueryRequest.getPageSize();

        // 查询数据库
        Page<NoteSpace> noteSpacePage = noteSpaceService.page(new Page<>(current, size),
                noteSpaceService.lambdaQuery()
                        .eq(noteSpaceQueryRequest.getId() != null, NoteSpace::getId, noteSpaceQueryRequest.getId())
                        .eq(noteSpaceQueryRequest.getUserId() != null, NoteSpace::getUserId, noteSpaceQueryRequest.getUserId())
                        .like(noteSpaceQueryRequest.getSpaceName() != null, NoteSpace::getSpaceName, noteSpaceQueryRequest.getSpaceName())
                        .eq(noteSpaceQueryRequest.getSpaceLevel() != null, NoteSpace::getSpaceLevel, noteSpaceQueryRequest.getSpaceLevel())
                        .orderByDesc(NoteSpace::getCreateTime)
                        .getWrapper());

        return ResultUtils.success(noteSpacePage);
    }

    /**
     * 获取空间级别列表
     */
    @GetMapping("/list/level")
    @ApiOperation("获取空间级别列表")
    public BaseResponse<List<NoteSpaceLevel>> listNoteSpaceLevel() {
        List<NoteSpaceLevel> spaceLevelList = Arrays.stream(NoteSpaceLevelEnum.values())
                .map(spaceLevelEnum -> new NoteSpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
        return ResultUtils.success(spaceLevelList);
    }
}

