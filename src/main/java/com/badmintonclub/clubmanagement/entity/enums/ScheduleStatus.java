package com.badmintonclub.clubmanagement.entity.enums;

public enum ScheduleStatus {

    OPEN("Đang mở"),
    LOCKED("Đã khóa đăng ký"),
    CANCELLED("Đã hủy");

    private final String displayName;

    ScheduleStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}