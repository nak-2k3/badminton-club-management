package com.badmintonclub.clubmanagement.entity.enums;

public enum PaymentStatus {

    UNPAID("Chưa đóng"),
    PAID("Đã đóng");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}