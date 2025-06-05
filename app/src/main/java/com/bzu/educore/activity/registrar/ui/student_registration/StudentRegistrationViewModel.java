package com.bzu.educore.activity.registrar.ui.student_registration;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.VolleyError;
import com.bzu.educore.model.user.Student;
import com.bzu.educore.repository.registrar.StudentRepository;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentRegistrationViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> numOfStudentsForCurrentYear;
    private final StudentRepository studentRepo;

    public StudentRegistrationViewModel(Application application) {
        super(application);
        numOfStudentsForCurrentYear = new MutableLiveData<>();
        studentRepo = new StudentRepository(application);
    }

    public LiveData<Integer> getNumOfStudentsForCurrentYear() {
        return numOfStudentsForCurrentYear;
    }

    public void registerStudent(DummyStudent student) {
        studentRepo.addStudent(
                response -> {
                    Toast.makeText(getApplication(), "Student is added successfully!", LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                },
                student
        );
    }

    public void fetchNumOfStudentsForCurrentYear() {
        studentRepo.getStudentCountForCurrentYear(
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int count = jsonObject.getInt("count");
                        numOfStudentsForCurrentYear.postValue(count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }

}