package com.zh.mathplatform.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.user.entity.UserAvatarReview;

public interface AvatarReviewService {
    Long submit(UserAvatarReview review);
    Page<UserAvatarReview> page(Integer status, int current, int size);
    void approve(Long id, Long reviewerId);
    void reject(Long id, Long reviewerId, String reason);
}


