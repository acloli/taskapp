package com.example.taskapp.model;

public enum TaskCategory {
    WORK("仕事"),
    PRIVATE("私用"),
    OTHER("その他");

    private final String displayName;

    TaskCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
