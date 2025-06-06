package com.bzu.educore.activity.registrar.ui.teacher_registration;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bzu.educore.model.school.Subject;
import com.bzu.educore.repository.registrar.StatsRepository;
import com.bzu.educore.repository.registrar.TeacherRepository;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeacherRegistrationViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> numOfTeachers;
    private final MutableLiveData<List<Subject>> subjects;
    private TeacherRepository teacherRepo;
    private StatsRepository statsRepo;

    public TeacherRegistrationViewModel(Application application) {
        super(application);
        numOfTeachers = new MutableLiveData<>();
        subjects = new MutableLiveData<>();
        teacherRepo = new TeacherRepository(application);
        statsRepo = new StatsRepository(application);
    }

    public LiveData<Integer> getNumOfTeachers() {
        return numOfTeachers;
    }

    public LiveData<List<Subject>> getSubjects() {
        return subjects;
    }

    public void registerTeacher(DummyTeacher teacher) {
        teacherRepo.addTeacher(
                response -> {
                    Toast.makeText(getApplication(), "Teacher is added successfully!", LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                },
                teacher
        );
    }

    public void fetchNumOfTeachers() {
        statsRepo.getTeacherCount(
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int count = jsonObject.getInt("count");
                        numOfTeachers.postValue(count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }

    public void fetchAllSubjects() {
        teacherRepo.getAllSubjects(
                response -> {
                    Gson gson = new Gson();
                    List<Subject> subjectList = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++)
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Subject subject = gson.fromJson(obj.toString(), Subject.class);
                            subjectList.add(subject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    subjects.postValue(subjectList);
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }

}