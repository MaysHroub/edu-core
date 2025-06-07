package com.bzu.educore.activity.registrar.ui.student_management;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bzu.educore.repository.registrar.StudentRepository;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentManagementViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> numOfStudentsForCurrentYear;
    private final MutableLiveData<List<DummyClassroom>> classrooms;
    private final StudentRepository studentRepo;

    public StudentManagementViewModel(Application application) {
        super(application);
        studentRepo = new StudentRepository(application);
        numOfStudentsForCurrentYear = new MutableLiveData<>();
        classrooms = new MutableLiveData<>();
    }

    public LiveData<Integer> getNumOfStudentsForCurrentYear() {
        return numOfStudentsForCurrentYear;
    }

    public LiveData<List<DummyClassroom>> getClassrooms() {
        return classrooms;
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

    public void fetchAllClassrooms() {
        studentRepo.getAllClassrooms(
                response -> {
                    Gson gson = new Gson();
                    List<DummyClassroom> classroomList = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++)
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            DummyClassroom classroom = gson.fromJson(obj.toString(), DummyClassroom.class);
                            classroomList.add(classroom);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    classrooms.postValue(classroomList);
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }
}