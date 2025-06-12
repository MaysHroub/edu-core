package com.bzu.educore.util.teacher;

import android.graphics.Color;

public class StatusUtils {

    public static String formatStatus(String status) {
        if (status == null) return "Unknown";
        switch (status.toLowerCase()) {
            case "submitted":
                return "Submitted ✓";
            case "overdue":
                return "Overdue ⚠";
            case "pending":
                return "Pending ⏳";
            default:
                return status;
        }
    }

    public static int getStatusColor(String status) {
        if (status == null) return Color.GRAY;
        switch (status.toLowerCase()) {
            case "submitted":
                return Color.parseColor("#4CAF50"); // Green
            case "overdue":
                return Color.parseColor("#F44336"); // Red
            case "pending":
                return Color.parseColor("#FFA000"); // Orange
            default:
                return Color.GRAY;
        }
    }

    public static boolean canMarkSubmission(String status) {
        return "submitted".equalsIgnoreCase(status);
    }

    public static boolean canViewSubmission(String status) {
        return "submitted".equalsIgnoreCase(status);
    }
}

