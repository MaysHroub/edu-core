package com.bzu.educore.activity.registrar.ui.subject_management;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bzu.educore.model.school.ClassGrade;
import com.bzu.educore.model.school.Subject;
import com.bzu.educore.repository.registrar.MainRepository;
import com.bzu.educore.util.UrlManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubjectManagementViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Subject>> subjects;
    private final MutableLiveData<List<Integer>> grades;
    private final MutableLiveData<Boolean> deletionSuccess;
    private final MutableLiveData<Boolean> additionSuccess;
    private final MainRepository repo;

    public SubjectManagementViewModel(@NonNull Application application) {
        super(application);
        repo = MainRepository.getInstance();
        subjects = new MutableLiveData<>();
        grades = new MutableLiveData<>();
        deletionSuccess = new MutableLiveData<>();
        additionSuccess = new MutableLiveData<>();
    }

    public LiveData<List<Subject>> getSubjects() {
        return subjects;
    }

    public LiveData<List<Integer>> getGrades() {
        return grades;
    }

    public LiveData<Boolean> getDeletionSuccess() {
        return deletionSuccess;
    }

    public LiveData<Boolean> getAdditionSuccess() {
        return additionSuccess;
    }


    public void updateSubject(Subject modifiedSubject) {
        repo.updateItem(
                UrlManager.URL_UPDATE_SUBJECT,
                modifiedSubject,
                response -> {
                    Toast.makeText(getApplication(), "Modifications are saved!", LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getApplication(), "Error occurred", LENGTH_SHORT).show();
                }
        );
    }

    public void addNewSubject(Subject subject) {
        repo.addItem(
                UrlManager.URL_ADD_NEW_SUBJECT,
                subject,
                response -> {
                    Toast.makeText(getApplication(), "Subject Added Successfully!", LENGTH_SHORT).show();
                    additionSuccess.postValue(true);
                },
                error -> {
                    Toast.makeText(getApplication(), "Error occurred", LENGTH_SHORT).show();
                    additionSuccess.postValue(false);
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
                    Toast.makeText(getApplication(), "Error occurred", LENGTH_SHORT).show();
                }
        );
    }

    public void fetchAllGrades() {
        repo.getAllItems(
                UrlManager.URL_GET_ALL_GRADES,
                response -> {
                    Gson gson = new Gson();
                    List<Integer> gradeList = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++)
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            ClassGrade grade = gson.fromJson(obj.toString(), ClassGrade.class);
                            Integer gradeNum = grade.getGradeNumber();
                            gradeList.add(gradeNum);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    grades.postValue(gradeList);
                },
                error -> {
                    Toast.makeText(getApplication(), "Error occurred", LENGTH_SHORT).show();
                }
        );
    }

    public void deleteSubjectById(int subjectId) {
        repo.deleteItemById(
                UrlManager.URL_DELETE_SUBJECT,
                subjectId,
                response -> {
                    Toast.makeText(getApplication(), "Deleted Successfully!", LENGTH_SHORT).show();
                    deletionSuccess.postValue(true);
                },
                error -> {
                    Toast.makeText(getApplication(), "Error occurred", LENGTH_SHORT).show();
                }
        );
    }

}