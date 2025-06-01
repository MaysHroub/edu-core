package com.bzu.educore.activity.registrar.ui.stats;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;
import com.bzu.educore.repository.registrar.StatsRepository;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StatisticsViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> numOfStudents, numOfTeachers, numOfSubjects, numOfClassrooms;
    private final MutableLiveData<List<PieEntry>> teachersPerSubjectEntries, studentsPerGradeEntries;

    private final StatsRepository statsRepo;

    public StatisticsViewModel(Application application) {
        super(application);
        numOfStudents = new MutableLiveData<>();
        numOfTeachers = new MutableLiveData<>();
        numOfSubjects = new MutableLiveData<>();
        numOfClassrooms = new MutableLiveData<>();
        teachersPerSubjectEntries = new MutableLiveData<>();
        studentsPerGradeEntries = new MutableLiveData<>();

        statsRepo = new StatsRepository(application);
    }

    public LiveData<Integer> getNumOfStudents() {
        return numOfStudents;
    }

    public LiveData<Integer> getNumOfTeachers() {
        return numOfTeachers;
    }

    public LiveData<Integer> getNumOfSubjects() {
        return numOfSubjects;
    }

    public LiveData<Integer> getNumOfClassrooms() {
        return numOfClassrooms;
    }

    public LiveData<List<PieEntry>> getTeachersPerSubjectEntries() {
        return teachersPerSubjectEntries;
    }

    public LiveData<List<PieEntry>> getStudentsPerGradeEntries() {
        return studentsPerGradeEntries;
    }

    public void fetchNumOfStudents() {
        statsRepo.getStudentCount(
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int count = jsonObject.getInt("count");
                        numOfStudents.postValue(count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                this::handleError
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
                this::handleError
        );
    }

    public void fetchNumOfSubjects() {
        statsRepo.getSubjectCount(
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int count = jsonObject.getInt("count");
                        numOfSubjects.postValue(count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                this::handleError
        );
    }

    public void fetchNumOfClassrooms() {
        statsRepo.getClassroomCount(
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int count = jsonObject.getInt("count");
                        numOfClassrooms.postValue(count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                this::handleError
        );
    }

    public void fetchTeacherPerSubject() {
        statsRepo.getTeachersPerSubject(
                response -> {
                    List<PieEntry> entries = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String subject = obj.getString("title");
                            int count = obj.getInt("count");
                            entries.add(new PieEntry(count, subject));
                        }
                        teachersPerSubjectEntries.postValue(entries);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                this::handleError
        );
    }

    public void fetchStudentPerGrade() {
        statsRepo.getStudentsPerGrade(
                response -> {
                    List<PieEntry> entries = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int gradeNumber = obj.getInt("grade_number");
                            int count = obj.getInt("count");
                            entries.add(new PieEntry(count, gradeNumber));
                        }
                        studentsPerGradeEntries.postValue(entries);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                this::handleError
        );
    }

    private void handleError(VolleyError error) {
        Toast.makeText(getApplication(), error.getMessage(), LENGTH_SHORT).show();
    }

}