package com.zh.mathplatform.domain.user.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

import com.zh.mathplatform.domain.user.valueobject.UserRoleEnum;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import lombok.Data;

/**
 * 用户
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 头像版本号（用于发布与缓存一致性）
     */
    @TableField(exist = false)
    private Long userAvatarVersion;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 性别：0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 生日
     */
    @TableField(exist = false)
    private Date birthday;

    /**
     * 所在地
     */
    @TableField(exist = false)
    private String location;

    /**
     * 个人网站
     */
    @TableField(exist = false)
    private String website;

    /**
     * 关注数
     */
    @TableField(exist = false)
    private Integer followingCount;

    /**
     * 粉丝数
     */
    private Integer followerCount;

    /**
     * 发帖数
     */
    private Integer postCount;

    /**
     * 获赞数
     */
    private Integer likeCount;

    /**
     * 状态：0-封禁 1-正常
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

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

    /**
     * 校验用户注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     */
    public static void validUserRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
    }

    /**
     * 校验用户登录
     *
     * @param userAccount
     * @param userPassword
     */
    public static void validUserLogin(String userAccount, String userPassword) {
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
    }

    /**
     * 判断用户是否为管理员
     *
     * @return
     */
    public boolean isAdmin() {
        return UserRoleEnum.ADMIN.getValue().equals(this.getUserRole());
    }
}