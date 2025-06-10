package com.bzu.educore.activity.registrar.ui.teacher_management;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bzu.educore.activity.registrar.ui.student_management.DummyClassroom;
import com.bzu.educore.model.school.Subject;
import com.bzu.educore.repository.registrar.MainRepository;
import com.bzu.educore.util.UrlManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeacherManagementViewModel extends AndroidViewModel {

    private final MutableLiveData<List<DummyTeacher>> teachers;
    private final MutableLiveData<List<Subject>> subjects;
    private final MutableLiveData<Integer> teacherId;
    private final MutableLiveData<Boolean> deletionSuccess;
    private MainRepository repo;

    public TeacherManagementViewModel(Application application) {
        super(application);
        teachers = new MutableLiveData<>();
        subjects = new MutableLiveData<>();
        deletionSuccess = new MutableLiveData<>();
        teacherId = new MutableLiveData<>();
        repo = MainRepository.getInstance();
    }

    public LiveData<List<DummyTeacher>> getTeachers() {
        return teachers;
    }

    public LiveData<List<Subject>> getSubjects() {
        return subjects;
    }

    public LiveData<Integer> getTeacherId() {
        return teacherId;
    }

    public LiveData<Boolean> getDeletionSuccess() {
        return deletionSuccess;
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
    
    public void updateTeacher(DummyTeacher modifiedTeacher) {
        repo.updateItem(
                UrlManager.URL_UPDATE_TEACHER,
                modifiedTeacher,
                response -> {
                    Toast.makeText(getApplication(), "Teacher is updated successfully!", LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }
    
    public void deleteTeacherById(int teacherId) {
        repo.deleteItemById(
                UrlManager.URL_DELETE_TEACHER,
                teacherId,
                response -> {
                    Toast.makeText(getApplication(), "Teacher is deleted successfully!", LENGTH_SHORT).show();
                    deletionSuccess.postValue(true);
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }

    public void generateTeacherId() {
        repo.getData(
                UrlManager.URL_GENERATE_TCHR_ID,
                response -> {
                    try {
                        int generatedId = response.getInt("id");
                        teacherId.postValue(generatedId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }

    public void fetchAllTeachers() {
        repo.getAllItems(
                UrlManager.URL_GET_ALL_TEACHERS,
                response -> {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                                    (json, type, context) -> LocalDate.parse(json.getAsString()))
                            .create();
                    List<DummyTeacher> teacherList = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++)
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            DummyTeacher teacher = gson.fromJson(obj.toString(), DummyTeacher.class);
                            teacherList.add(teacher);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    teachers.postValue(teacherList);
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
                    subjects.setValue(subjectList);
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }
    
}