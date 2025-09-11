package com.zh.mathplatform.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.infrastructure.annotation.AuthCheck;
import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.DeleteRequest;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.interfaces.assembler.UserAssembler;
import com.zh.mathplatform.domain.user.constant.UserConstant;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.exception.ThrowUtils;
import com.zh.mathplatform.domain.user.entity.User;
import com.zh.mathplatform.interfaces.dto.user.*;
import com.zh.mathplatform.application.service.AvatarReviewService;
import com.zh.mathplatform.domain.user.entity.UserAvatarReview;
import com.zh.mathplatform.infrastructure.utils.UserHolder;
import com.zh.mathplatform.interfaces.vo.user.LoginUserVO;
import com.zh.mathplatform.interfaces.vo.user.UserVO;
import com.zh.mathplatform.application.service.UserApplicationService;
import com.zh.mathplatform.infrastructure.manager.upload.FilePictureUpload;
import com.zh.mathplatform.infrastructure.manager.upload.model.dto.file.UploadPictureResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private AvatarReviewService avatarReviewService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        long result = userApplicationService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        LoginUserVO loginUserVO = userApplicationService.userLogin(userLoginRequest, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userApplicationService.getLoginUser(request);
        return ResultUtils.success(userApplicationService.getLoginUserVO(loginUser));
    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userApplicationService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User userEntity = UserAssembler.toUserEntity(userAddRequest);
        return ResultUtils.success(userApplicationService.saveUser(userEntity));
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userApplicationService.getUserById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userApplicationService.getUserVO(user));
    }

    /**
     * 根据 id 获取公开的用户信息（不需要管理员）
     */
    @GetMapping("/public/get/vo")
    public BaseResponse<UserVO> getUserVOPublic(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userApplicationService.getUserById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(userApplicationService.getUserVO(user));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userApplicationService.deleteUser(deleteRequest);
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 对象转换
        User userEntity = UserAssembler.toUserEntity(userUpdateRequest);
        userApplicationService.updateUser(userEntity);
        return ResultUtils.success(true);
    }

    /**
     * 用户自助更新资料（昵称/头像/简介），需登录
     */
    @PostMapping("/update/profile")
    public BaseResponse<Boolean> updateProfile(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userApplicationService.getLoginUser(request);
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 仅允许修改自己的资料
        userUpdateRequest.setId(loginUser.getId());
        User userEntity = UserAssembler.toUserEntity(userUpdateRequest);
        userApplicationService.updateUser(userEntity);
        return ResultUtils.success(true);
    }

    /**
     * 上传并更新当前登录用户头像（COS 存储）
     */
    @PostMapping(value = "/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<String> uploadAvatar(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未选择文件");
        }
        // 获取当前登录用户
        User loginUser = userApplicationService.getLoginUser(request);
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 上传到 COS，使用 avatar 作为路径前缀
        UploadPictureResult uploadResult = filePictureUpload.uploadPicture(file, "avatar");
        String avatarUrl = uploadResult.getUrl() != null ? uploadResult.getUrl() : uploadResult.getThumbnailUrl();
        // 持久化用户头像
        User toUpdate = new User();
        toUpdate.setId(loginUser.getId());
        toUpdate.setUserAvatar(avatarUrl);
        userApplicationService.updateUser(toUpdate);
        return ResultUtils.success(avatarUrl);
    }

    /**
     * 用户提交头像审核（从隔离区已上传完成后提交）
     */
    @PostMapping("/avatar/review/submit")
    public BaseResponse<Long> submitAvatarReview(@RequestBody AvatarReviewSubmitRequest req, HttpServletRequest request) {
        if (req == null || req.getObjectKey() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = UserHolder.getLoginUserIdRequired(request);
        UserAvatarReview r = new UserAvatarReview();
        r.setUserId(userId);
        r.setObjectKey(req.getObjectKey());
        r.setUrl(req.getUrl());
        r.setThumbnailKey(req.getThumbnailKey());
        r.setThumbnailUrl(req.getThumbnailUrl());
        r.setEtag(req.getEtag());
        r.setSha256(req.getSha256());
        r.setWidth(req.getWidth());
        r.setHeight(req.getHeight());
        r.setFormat(req.getFormat());
        r.setSize(req.getSize());
        Long id = avatarReviewService.submit(r);
        return ResultUtils.success(id);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userApplicationService.listUserVOByPage(userQueryRequest));
    }

    /**
     * 管理员分页查看头像审核任务
     */
    @GetMapping("/admin/avatar/review/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.zh.mathplatform.domain.user.entity.UserAvatarReview>> listAvatarReviews(
            Integer status, Integer current, Integer pageSize) {
        int c = current == null ? 1 : current;
        int s = pageSize == null ? 10 : pageSize;
        return ResultUtils.success(avatarReviewService.page(status, c, s));
    }

    /**
     * 管理员审批通过
     */
    @PostMapping("/admin/avatar/review/{id}/approve")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> approveAvatar(@PathVariable Long id, HttpServletRequest request) {
        Long reviewerId = UserHolder.getLoginUserIdRequired(request);
        avatarReviewService.approve(id, reviewerId);
        return ResultUtils.success(true);
    }

    /**
     * 管理员审批驳回
     */
    @PostMapping("/admin/avatar/review/{id}/reject")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> rejectAvatar(@PathVariable Long id, @RequestParam(required = false) String reason, HttpServletRequest request) {
        Long reviewerId = UserHolder.getLoginUserIdRequired(request);
        avatarReviewService.reject(id, reviewerId, reason);
        return ResultUtils.success(true);
    }
}
