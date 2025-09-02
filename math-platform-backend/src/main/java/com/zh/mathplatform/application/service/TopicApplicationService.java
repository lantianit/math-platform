package com.zh.mathplatform.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.topic.entity.Topic;

import java.util.List;

/**
 * 话题应用服务接口
 */
public interface TopicApplicationService {

    /**
     * 创建话题
     */
    Topic createTopic(String name, String description, String avatar);

    /**
     * 更新话题
     */
    Topic updateTopic(Long topicId, String name, String description, String avatar);

    /**
     * 删除话题
     */
    boolean deleteTopic(Long topicId);

    /**
     * 根据ID获取话题
     */
    Topic getTopicById(Long topicId);

    /**
     * 分页获取话题列表
     */
    IPage<Topic> getTopicPage(Page<Topic> page, String keyword);

    /**
     * 获取热门话题
     */
    List<Topic> getHotTopics(Integer limit);

    /**
     * 关联帖子到话题
     */
    boolean addPostToTopic(Long postId, Long topicId);

    /**
     * 取消帖子话题关联
     */
    boolean removePostFromTopic(Long postId, Long topicId);

    /**
     * 获取帖子关联的话题
     */
    List<Topic> getPostTopics(Long postId);

    /**
     * 增加话题帖子数
     */
    boolean increasePostCount(Long topicId);

    /**
     * 减少话题帖子数
     */
    boolean decreasePostCount(Long topicId);
}
