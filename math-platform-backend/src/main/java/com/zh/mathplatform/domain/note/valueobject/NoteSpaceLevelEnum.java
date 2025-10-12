package com.zh.mathplatform.domain.note.valueobject;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 笔记空间级别枚举
 */
@Getter
public enum NoteSpaceLevelEnum {

    COMMON("普通版", 0, 100, 100L * 1024 * 1024),
    PROFESSIONAL("专业版", 1, 1000, 1000L * 1024 * 1024),
    FLAGSHIP("旗舰版", 2, 10000, 10000L * 1024 * 1024);

    private final String text;
    private final int value;
    private final long maxCount;
    private final long maxSize;

    /**
     * @param text 文本
     * @param value 值
     * @param maxCount 最大笔记数量
     * @param maxSize 最大存储大小
     */
    NoteSpaceLevelEnum(String text, int value, long maxCount, long maxSize) {
        this.text = text;
        this.value = value;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    /**
     * 根据 value 获取枚举
     */
    public static NoteSpaceLevelEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (NoteSpaceLevelEnum spaceLevelEnum : NoteSpaceLevelEnum.values()) {
            if (spaceLevelEnum.value == value) {
                return spaceLevelEnum;
            }
        }
        return null;
    }
}

