package com.bzu.educore.util;

import android.text.TextUtils;

public class MarkValidator {

    public static class ValidationResult {
        public final boolean isValid;
        public final Double value;
        public final String error;

        private ValidationResult(boolean isValid, Double value, String error) {
            this.isValid = isValid;
            this.value = value;
            this.error = error;
        }

        public static ValidationResult valid(Double value) {
            return new ValidationResult(true, value, null);
        }

        public static ValidationResult invalid(String error) {
            return new ValidationResult(false, null, error);
        }
    }

    public static ValidationResult validateMark(String input, double maxMark) {
        if (TextUtils.isEmpty(input)) {
            return ValidationResult.valid(null);
        }

        try {
            double value = Double.parseDouble(input);
            if (value < 0 || value > maxMark) {
                return ValidationResult.invalid("Mark must be between " + 0 + " and " + maxMark);
            }
            return ValidationResult.valid(value);
        } catch (NumberFormatException e) {
            return ValidationResult.invalid("Invalid number format");
        }
    }

    public static ValidationResult validateMark(String input) {
        return validateMark(input, 100);
    }
}
