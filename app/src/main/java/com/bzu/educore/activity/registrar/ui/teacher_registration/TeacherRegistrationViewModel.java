package com.bzu.educore.activity.registrar.ui.teacher_registration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TeacherRegistrationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TeacherRegistrationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}