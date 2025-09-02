package com.zh.mathplatform.infrastructure.utils;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.post.entity.Post;
import com.zh.mathplatform.domain.user.entity.User;
import com.zh.mathplatform.infrastructure.mapper.UserMapper;
import com.zh.mathplatform.interfaces.vo.post.PostVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Post到PostVO转换工具类
 */
@Component
public class PostVOConverter {

    @Autowired
    private UserMapper userMapper;

    /**
     * 转换单个Post为PostVO
     */
    public PostVO convertToVO(Post post) {
        if (post == null) {
            return null;
        }

        PostVO postVO = new PostVO();
        BeanUtil.copyProperties(post, postVO);

        // 获取用户信息并设置到PostVO中
        User user = userMapper.selectById(post.getUserId());
        if (user != null) {
            postVO.setUserName(user.getUserName());
            postVO.setUserAvatar(user.getUserAvatar());
        }

        return postVO;
    }

    /**
     * 批量转换Post列表为PostVO列表
     */
    public List<PostVO> convertToVOList(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            return List.of();
        }

        // 获取所有用户ID
        Set<Long> userIds = posts.stream()
                .map(Post::getUserId)
                .collect(Collectors.toSet());

        // 批量查询用户信息
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 转换为PostVO
        return posts.stream()
                .map(post -> {
                    PostVO postVO = new PostVO();
                    BeanUtil.copyProperties(post, postVO);

                    // 设置用户信息
                    User user = userMap.get(post.getUserId());
                    if (user != null) {
                        postVO.setUserName(user.getUserName());
                        postVO.setUserAvatar(user.getUserAvatar());
                    }

                    return postVO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 转换分页Post为分页PostVO
     */
    public IPage<PostVO> convertToVOPage(IPage<Post> postPage) {
        if (postPage == null) {
            return new Page<>();
        }

        // 转换记录
        List<PostVO> postVOList = convertToVOList(postPage.getRecords());

        // 构造分页结果
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        postVOPage.setRecords(postVOList);

        return postVOPage;
    }
}
