package com.bzu.educore.activity.registrar.ui.student_management;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bzu.educore.repository.registrar.MainRepository;
import com.bzu.educore.util.UrlManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentManagementViewModel extends AndroidViewModel {

    private final MutableLiveData<List<DummyClassroom>> classrooms;
    private final MutableLiveData<List<DummyStudent>> students;
    private final MutableLiveData<Boolean> deletionSuccess;
    private final MutableLiveData<Integer> studentId;
    private final MainRepository repo;

    public StudentManagementViewModel(Application application) {
        super(application);
        repo = MainRepository.getInstance();
        classrooms = new MutableLiveData<>();
        students = new MutableLiveData<>();
        deletionSuccess = new MutableLiveData<>();
        studentId = new MutableLiveData<>();
    }

    public LiveData<List<DummyClassroom>> getClassrooms() {
        return classrooms;
    }

    public LiveData<List<DummyStudent>> getStudents() {
        return students;
    }

    public LiveData<Boolean> getDeletionSuccess() {
        return deletionSuccess;
    }

    public LiveData<Integer> getStudentId() {
        return studentId;
    }

    public void generateStudentId() {
        repo.getData(
                UrlManager.URL_GENERATE_STD_ID,
                response -> {
                    try {
                        int generatedId = response.getInt("id");
                        studentId.postValue(generatedId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }

    public void registerStudent(DummyStudent student) {
        repo.addItem(
                UrlManager.URL_ADD_NEW_STUDENT,
                student,
                response -> {
                    Toast.makeText(getApplication(), "Student is added successfully!", LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }

    public void updateStudent(DummyStudent modifiedStudent) {
        repo.updateItem(
                UrlManager.URL_UPDATE_STUDENT,
                modifiedStudent,
                response -> {
                    Toast.makeText(getApplication(), "Student is updated successfully!", LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }

    public void deleteStudentById(int studentId) {
        repo.deleteItemById(
                UrlManager.URL_DELETE_STUDENT,
                studentId,
                response -> {
                    Toast.makeText(getApplication(), "Student is deleted successfully!", LENGTH_SHORT).show();
                    deletionSuccess.postValue(true);
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }


    public void fetchAllClassrooms() {
        repo.getAllItems(
                UrlManager.URL_GET_ALL_CLASSROOMS,
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

    public void fetchAllStudents() {
        repo.getAllItems(
                UrlManager.URL_GET_ALL_STUDENTS,
                response -> {
                    Gson gson = new Gson();
                    List<DummyStudent> studentList = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++)
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            DummyStudent student = gson.fromJson(obj.toString(), DummyStudent.class);
                            studentList.add(student);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    students.postValue(studentList);
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }

    public void updateClassroom(DummyClassroom modifiedClassroom) {
        repo.updateItem(
                UrlManager.URL_UPDATE_CLASSROOM,
                modifiedClassroom,
                response -> {
                    Toast.makeText(getApplication(), "Classroom is updated successfully!", LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
                }
        );
    }
}