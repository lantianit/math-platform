package com.zh.mathplatform.domain.post.valueobject;

/**
 * 帖子状态枚举
 */
public enum PostStatusEnum {

    NORMAL(1, "正常"),
    DELETED(0, "删除");

    private final Integer value;
    private final String description;

    PostStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据值获取枚举
     */
    public static PostStatusEnum getEnumByValue(Integer value) {
        for (PostStatusEnum statusEnum : PostStatusEnum.values()) {
            if (statusEnum.value.equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}
