package com.bzu.educore.util;

import android.content.Context;
import android.widget.Toast;
import com.android.volley.VolleyError;

public class ErrorHandler {

    public static void handleNetworkError(Context context, VolleyError error) {
        String message = "Network error occurred";
        if (error.networkResponse != null) {
            switch (error.networkResponse.statusCode) {
                case 404:
                    message = "Resource not found";
                    break;
                case 500:
                    message = "Server error";
                    break;
                case 403:
                    message = "Access forbidden";
                    break;
                default:
                    message = "Server error (Code: " + error.networkResponse.statusCode + ")";
            }
        }
        showToast(context, message);
    }

    public static void handleParsingError(Context context) {
        showToast(context, "Error parsing data");
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

