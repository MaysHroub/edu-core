package com.bzu.educore.activity.registrar.ui.student_registration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StudentRegistrationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public StudentRegistrationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}