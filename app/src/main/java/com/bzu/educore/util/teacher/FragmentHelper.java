package com.bzu.educore.util.teacher;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class FragmentHelper {

    private static final String TAG = "FragmentHelper";

    // Toast methods
    public static void showToast(Fragment fragment, String message) {
        Context context = fragment.getContext();
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showErrorToast(Fragment fragment, String message) {
        Context context = fragment.getContext();
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }


    // Argument extraction helpers
    public static int getIntArgument(Fragment fragment, String key, int defaultValue) {
        Bundle args = fragment.getArguments();
        return args != null ? args.getInt(key, defaultValue) : defaultValue;
    }

    public static String getStringArgument(Fragment fragment, String key, String defaultValue) {
        Bundle args = fragment.getArguments();
        return args != null ? args.getString(key, defaultValue) : defaultValue;
    }


    // Navigation helpers
    public static void navigateBack(Fragment fragment) {
        if (fragment.getParentFragmentManager().getBackStackEntryCount() > 0) {
            fragment.getParentFragmentManager().popBackStack();
        } else if (fragment.getActivity() != null) {
            fragment.getActivity().finish();
        }
    }

    // Error handling
    public static void handleError(Fragment fragment, Exception e, String userMessage) {
        Log.e(TAG, "Error in fragment: " + fragment.getClass().getSimpleName(), e);

        String message = userMessage != null ? userMessage : "An error occurred";
        showErrorToast(fragment, message);
    }

    public static void handleNetworkError(Fragment fragment, VolleyError error, String defaultMessage) {
        String message = getNetworkErrorMessage(error, defaultMessage);
        showErrorToast(fragment, message);
        Log.e(TAG, "Network error in fragment: " + fragment.getClass().getSimpleName(), error);
    }

    private static String getNetworkErrorMessage(VolleyError error, String defaultMessage) {
        if (error instanceof TimeoutError) {
            return "Request timeout. Please try again.";
        } else if (error instanceof NoConnectionError || error instanceof NetworkError) {
            return "No internet connection. Please check your network.";
        } else if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            switch (statusCode) {
                case 400:
                    return "Bad request. Please check your input.";
                case 401:
                    return "Unauthorized. Please login again.";
                case 403:
                    return "Access denied.";
                case 404:
                    return "Resource not found.";
                case 500:
                    return "Server error. Please try again later.";
                default:
                    return defaultMessage != null ? defaultMessage : "Network error occurred";
            }
        }
        return defaultMessage != null ? defaultMessage : "Unknown error occurred";
    }

    // Validation helpers
    public static boolean validateRequiredFields(Fragment fragment, String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                showToast(fragment, "All fields are required");
                return false;
            }
        }
        return true;
    }

    public static boolean validatePositiveNumber(Fragment fragment, String numberStr, String fieldName) {
        try {
            double number = Double.parseDouble(numberStr);
            if (number <= 0) {
                showToast(fragment, fieldName + " must be greater than 0");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showToast(fragment, "Invalid " + fieldName.toLowerCase());
            return false;
        }
    }
}
