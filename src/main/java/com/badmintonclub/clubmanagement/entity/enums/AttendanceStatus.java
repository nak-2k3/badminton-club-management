package com.badmintonclub.clubmanagement.entity.enums;

public enum AttendanceStatus {
    NOT_MARKED("Chưa điểm danh"),
    PRESENT("Có mặt"),
    ABSENT("Vắng");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}