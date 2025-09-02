package com.zh.mathplatform.domain.post.valueobject;

/**
 * 帖子分类枚举
 */
public enum PostCategoryEnum {

    TECH("tech", "技术分享"),
    QUESTION("question", "问题求助"),
    PROJECT("project", "项目展示"),
    DISCUSS("discuss", "交流讨论");

    private final String value;
    private final String description;

    PostCategoryEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据值获取枚举
     */
    public static PostCategoryEnum getEnumByValue(String value) {
        for (PostCategoryEnum categoryEnum : PostCategoryEnum.values()) {
            if (categoryEnum.value.equals(value)) {
                return categoryEnum;
            }
        }
        return null;
    }
}
