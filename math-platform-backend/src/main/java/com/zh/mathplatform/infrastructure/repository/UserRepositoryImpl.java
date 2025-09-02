package com.zh.mathplatform.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zh.mathplatform.domain.user.entity.User;
import com.zh.mathplatform.domain.user.repository.UserRepository;
import com.zh.mathplatform.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * 用户仓储实现
 */
@Service
public class UserRepositoryImpl extends ServiceImpl<UserMapper, User> implements UserRepository {
}