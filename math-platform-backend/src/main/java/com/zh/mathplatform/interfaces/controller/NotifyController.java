package com.zh.mathplatform.interfaces.controller;

import com.zh.mathplatform.application.service.NotifyService;
import com.zh.mathplatform.domain.notify.entity.Notification;
import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.infrastructure.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/notify")
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    @GetMapping("/list")
    public BaseResponse<List<Notification>> list(@RequestParam(required = false, defaultValue = "50") Integer limit, HttpServletRequest request) {
        Long uid = UserHolder.getLoginUserIdRequired(request);
        return ResultUtils.success(notifyService.list(uid, limit));
    }

    @GetMapping("/unread/count")
    public BaseResponse<Long> unreadCount(HttpServletRequest request) {
        Long uid = UserHolder.getLoginUserIdRequired(request);
        return ResultUtils.success(notifyService.unreadCount(uid));
    }

    @PostMapping("/markAllRead")
    public BaseResponse<Boolean> markAllRead(HttpServletRequest request) {
        Long uid = UserHolder.getLoginUserIdRequired(request);
        notifyService.markAllRead(uid);
        return ResultUtils.success(true);
    }
}


