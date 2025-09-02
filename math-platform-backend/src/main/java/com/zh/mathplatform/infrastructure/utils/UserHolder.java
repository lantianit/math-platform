package com.zh.mathplatform.infrastructure.utils;

import com.zh.mathplatform.domain.user.entity.User;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息持有者工具类
 */
public class UserHolder {

    private static final String USER_LOGIN_STATE = "user_login";

    /**
     * 从请求中获取当前登录用户ID
     */
    public static Long getLoginUserId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        // 从Session中获取用户信息
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            return null;
        }

        if (userObj instanceof User) {
            return ((User) userObj).getId();
        }

        return null;
    }

    /**
     * 从请求中获取当前登录用户
     */
    public static User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj instanceof User) {
            return (User) userObj;
        }

        return null;
    }

    /**
     * 从请求中获取当前登录用户ID（必须登录）
     */
    public static Long getLoginUserIdRequired(HttpServletRequest request) {
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return userId;
    }

    /**
     * 从请求中获取当前登录用户（必须登录）
     */
    public static User getLoginUserRequired(HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    /**
     * 检查是否为管理员
     */
    public static boolean isAdmin(HttpServletRequest request) {
        User user = getLoginUser(request);
        return user != null && user.isAdmin();
    }

    /**
     * 检查是否为管理员（必须登录）
     */
    public static void checkAdmin(HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

    /**
     * 检查是否为本人或管理员
     */
    public static void checkUserSelfOrAdmin(HttpServletRequest request, Long targetUserId) {
        User loginUser = getLoginUserRequired(request);
        if (!loginUser.getId().equals(targetUserId) && !loginUser.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }
}
