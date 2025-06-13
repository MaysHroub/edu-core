package com.bzu.educore.activity.registrar.ui.schedules;

public class TimetableCell {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_STANDARD = 1;

    private String text;
    private int backgroundColor;
    private int textColor;
    private int cellType;

    public TimetableCell(String text, int backgroundColor, int textColor, int cellType) {
        this.text = text;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.cellType = cellType;
    }

    public String getText() {
        return text;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getCellType() {
        return cellType;
    }
} 