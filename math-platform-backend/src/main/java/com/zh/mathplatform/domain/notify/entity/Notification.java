package com.zh.mathplatform.domain.notify.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 简易通知实体（存储于 Redis List / ZSet，可选入库扩展）
 */
public class Notification implements Serializable {
    private Long id; // 可选
    private Long receiverId;
    private String type; // comment, reply, like, follow
    private String content;
    private String extra; // JSON 扩展，如 postId/commentId 等
    private Integer readFlag; // 0 未读 1 已读
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getExtra() { return extra; }
    public void setExtra(String extra) { this.extra = extra; }
    public Integer getReadFlag() { return readFlag; }
    public void setReadFlag(Integer readFlag) { this.readFlag = readFlag; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}


