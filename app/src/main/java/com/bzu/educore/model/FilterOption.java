package com.bzu.educore.model;

public class FilterOption {
    private final int id;
    private final String displayName;

    public FilterOption(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() { return id; }
    public String getDisplayName() { return displayName; }

    @Override
    public String toString() {
        return displayName;
    }
} 