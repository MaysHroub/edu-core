package com.bzu.educore.util;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class DialogUtils {

    public static void showConfirmationDialog(Context context, String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (onConfirm != null)
                        onConfirm.run();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

}
