package com.badmintonclub.clubmanagement.entity.enums;

public enum Level {

    BEGINNER("Mới bắt đầu"),
    INTERMEDIATE("Trung bình"),
    ADVANCED("Nâng cao");

    private final String displayName;

    Level(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}