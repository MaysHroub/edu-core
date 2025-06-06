package com.bzu.educore.util.teacher;

import android.graphics.Color;

public class StatusUtils {

    public static String formatStatus(String status) {
        if (status == null) return "Unknown";
        switch (status.toLowerCase()) {
            case Constants.STATUS_SUBMITTED:
                return "Submitted ✓";
            case Constants.STATUS_OVERDUE:
                return "Overdue ⚠";
            case Constants.STATUS_PENDING:
                return "Pending ⏳";
            default:
                return status;
        }
    }

    public static int getStatusColor(String status) {
        if (status == null) return Color.GRAY;
        switch (status.toLowerCase()) {
            case Constants.STATUS_SUBMITTED:
                return Color.parseColor("#4CAF50"); // Green
            case Constants.STATUS_OVERDUE:
                return Color.parseColor("#F44336"); // Red
            case Constants.STATUS_PENDING:
                return Color.parseColor("#FFA000"); // Orange
            default:
                return Color.GRAY;
        }
    }

    public static boolean canMarkSubmission(String status) {
        return Constants.STATUS_SUBMITTED.equalsIgnoreCase(status);
    }

    public static boolean canViewSubmission(String status) {
        return Constants.STATUS_SUBMITTED.equalsIgnoreCase(status);
    }
}

