package com.badmintonclub.clubmanagement.entity.enums;

public enum Role {

    ADMIN("Quản trị viên"),
    MEMBER("Thành viên"),
    TREASURER("Thủ quỹ");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}