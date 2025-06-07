package com.bzu.educore.activity.registrar.ui.teacher_management;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bzu.educore.model.school.Subject;
import com.bzu.educore.repository.registrar.MainRepository;
import com.bzu.educore.repository.registrar.StatsRepository;
import com.bzu.educore.repository.registrar.TeacherRepository;
import com.bzu.educore.util.UrlManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeacherManagementViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Subject>> subjects;
    private MainRepository repo;

    public TeacherManagementViewModel(Application application) {
        super(application);
        subjects = new MutableLiveData<>();
        repo = MainRepository.getInstance();
    }

    public LiveData<List<Subject>> getSubjects() {
        return subjects;
    }

    public void registerTeacher(DummyTeacher teacher) {
        repo.addItem(
                UrlManager.URL_ADD_NEW_TEACHER,
                teacher,
                response -> {
                    Toast.makeText(getApplication(), "Teacher is added successfully!", LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }

    public void fetchAllSubjects() {
        repo.getAllItems(
                UrlManager.URL_GET_ALL_SUBJECTS,
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