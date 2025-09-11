package com.zh.mathplatform.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.AvatarReviewService;
import com.zh.mathplatform.application.service.NotifyService;
import com.zh.mathplatform.domain.notify.entity.Notification;
import com.zh.mathplatform.domain.user.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zh.mathplatform.domain.user.entity.UserAvatarReview;
import com.zh.mathplatform.domain.user.valueobject.AvatarReviewStatusEnum;
import com.zh.mathplatform.infrastructure.api.CosManager;
import com.zh.mathplatform.infrastructure.config.CosClientConfig;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.mapper.UserAvatarReviewMapper;
import com.zh.mathplatform.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class AvatarReviewServiceImpl implements AvatarReviewService {

    @Resource
    private UserAvatarReviewMapper reviewMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private NotifyService notifyService;
    @Resource
    private CosManager cosManager;
    @Resource
    private CosClientConfig cosClientConfig;

    @Override
    public Long submit(UserAvatarReview review) {
        if (review == null || review.getUserId() == null || review.getObjectKey() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 基本元数据校验
        if (review.getSize() != null && review.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 2MB");
        }
        if (review.getFormat() != null) {
            String f = review.getFormat().toLowerCase();
            if (!("jpeg".equals(f) || "jpg".equals(f) || "png".equals(f) || "webp".equals(f))) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
        review.setStatus(AvatarReviewStatusEnum.SUBMITTED.getValue());
        review.setSubmittedAt(new Date());
        review.setBucket(cosClientConfig.getBucket());
        reviewMapper.insert(review);
        // 通知所有管理员
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getUserRole, "admin");
        for (User admin : userMapper.selectList(qw)) {
            Notification n = new Notification();
            n.setReceiverId(admin.getId());
            n.setType("avatar_review");
            n.setContent("有新的头像待审核");
            notifyService.push(n);
        }
        return review.getId();
    }

    @Override
    public Page<UserAvatarReview> page(Integer status, int current, int size) {
        LambdaQueryWrapper<UserAvatarReview> qw = new LambdaQueryWrapper<>();
        if (status != null) {
            qw.eq(UserAvatarReview::getStatus, status);
        }
        qw.orderByDesc(UserAvatarReview::getSubmittedAt);
        return reviewMapper.selectPage(new Page<>(current, size), qw);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approve(Long id, Long reviewerId) {
        UserAvatarReview r = reviewMapper.selectById(id);
        if (r == null || !r.getStatus().equals(AvatarReviewStatusEnum.SUBMITTED.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "非法状态");
        }
        r.setStatus(AvatarReviewStatusEnum.PUBLISHING.getValue());
        r.setReviewerId(reviewerId);
        r.setReviewedAt(new Date());
        reviewMapper.updateById(r);
        // 复制到正式路径
        String targetKey = String.format("/avatar/user_%d/%d_%s", r.getUserId(), System.currentTimeMillis(), r.getSha256());
        cosManager.copyObject(r.getObjectKey(), targetKey);
        // 更新用户头像
        User u = new User();
        u.setId(r.getUserId());
        u.setUserAvatar(cosClientConfig.getHost() + "/" + targetKey);
        userMapper.updateById(u);
        // 完成
        r.setStatus(AvatarReviewStatusEnum.PUBLISHED.getValue());
        r.setPublishedAt(new Date());
        reviewMapper.updateById(r);
        // 通知用户
        Notification n = new Notification();
        n.setReceiverId(r.getUserId());
        n.setType("avatar_review_result");
        n.setContent("头像审核通过");
        notifyService.push(n);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reject(Long id, Long reviewerId, String reason) {
        UserAvatarReview r = reviewMapper.selectById(id);
        if (r == null || !r.getStatus().equals(AvatarReviewStatusEnum.SUBMITTED.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "非法状态");
        }
        r.setStatus(AvatarReviewStatusEnum.REJECTED.getValue());
        r.setReviewerId(reviewerId);
        r.setReason(reason);
        r.setReviewedAt(new Date());
        reviewMapper.updateById(r);
        // 删除隔离对象（忽略异常）
        try { cosManager.deleteObject(r.getObjectKey()); } catch (Exception ignored) {}
        // 通知用户
        Notification n = new Notification();
        n.setReceiverId(r.getUserId());
        n.setType("avatar_review_result");
        n.setContent("头像审核未通过：" + (reason == null ? "不合规" : reason));
        notifyService.push(n);
    }
}


