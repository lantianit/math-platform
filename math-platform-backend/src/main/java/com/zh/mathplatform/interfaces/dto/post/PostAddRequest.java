package com.zh.mathplatform.interfaces.dto.post;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 帖子发布请求
 */
@Data
public class PostAddRequest implements Serializable {

    /**
     * 帖子标题
     */
    @NotBlank(message = "帖子标题不能为空")
    @Size(min = 1, max = 200, message = "帖子标题长度必须在1-200字符之间")
    private String title;

    /**
     * 帖子内容
     */
    @NotBlank(message = "帖子内容不能为空")
    @Size(min = 1, max = 10000, message = "帖子内容长度必须在1-10000字符之间")
    private String content;

    /**
     * 分类：tech/question/project
     */
    private String category;

    /**
     * 标签，JSON格式
     */
    private String tags;

    /**
     * 图片URL，JSON格式
     */
    private String images;

    private static final long serialVersionUID = 1L;
}
