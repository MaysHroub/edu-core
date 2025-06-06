package com.bzu.educore.util;

import android.widget.EditText;
import android.widget.Spinner;

public class InputValidator {

    public static boolean validateEditTexts(EditText... editTexts) {
        for (EditText edtxt: editTexts)
            if (edtxt.getText() == null || edtxt.getText().length() == 0)
                return false;
        return true;
    }

    public static boolean validateSpinners(Spinner... spinners) {
        for (Spinner spn: spinners)
            if (spn.getSelectedItem() == null)
                return false;
        return true;
    }

}
