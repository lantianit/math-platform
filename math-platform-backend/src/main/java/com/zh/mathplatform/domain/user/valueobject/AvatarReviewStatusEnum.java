package com.zh.mathplatform.domain.user.valueobject;

public enum AvatarReviewStatusEnum {
    SUBMITTED(0),
    AUTO_REVIEWING(1),
    AUTO_PASSED(2),
    AUTO_FLAGGED(3),
    HUMAN_PENDING(4),
    APPROVED(5),
    REJECTED(6),
    PUBLISHING(7),
    PUBLISHED(8),
    ERROR(9);

    private final int value;

    AvatarReviewStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}


