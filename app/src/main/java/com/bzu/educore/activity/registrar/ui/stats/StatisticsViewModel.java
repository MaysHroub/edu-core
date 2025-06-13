package com.bzu.educore.activity.registrar.ui.stats;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;
import com.bzu.educore.repository.registrar.MainRepository;
import com.bzu.educore.util.UrlManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StatisticsViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> numOfStudents, numOfTeachers, numOfSubjects, numOfClassrooms;
    private final MutableLiveData<List<BarEntry>> teachersPerSubjectEntries;
    private final MutableLiveData<List<BarEntry>> studentsPerGradeEntries;
    private List<String> gradeLabels, subjectLabels;

    private final MainRepository repo;

    public StatisticsViewModel(Application application) {
        super(application);
        numOfStudents = new MutableLiveData<>();
        numOfTeachers = new MutableLiveData<>();
        numOfSubjects = new MutableLiveData<>();
        numOfClassrooms = new MutableLiveData<>();
        teachersPerSubjectEntries = new MutableLiveData<>();
        studentsPerGradeEntries = new MutableLiveData<>();

        repo = MainRepository.getInstance();
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

    public LiveData<List<BarEntry>> getTeachersPerSubjectEntries() {
        return teachersPerSubjectEntries;
    }

    public LiveData<List<BarEntry>> getStudentsPerGradeEntries() {
        return studentsPerGradeEntries;
    }

    public void fetchNumOfStudents() {
        repo.getData(
                UrlManager.URL_GET_STUDENT_COUNT,
                response -> {
                    try {
                        int count = response.getInt("count");
                        numOfStudents.postValue(count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplication(), "Error occurred", LENGTH_SHORT).show();
                }
        );
    }

    public void fetchNumOfTeachers() {
        repo.getData(
                UrlManager.URL_GET_TEACHER_COUNT,
                response -> {
                    try {
                        int count = response.getInt("count");
                        numOfTeachers.postValue(count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplication(), "Error occurred", LENGTH_SHORT).show();
                }
        );
    }

    public void fetchNumOfSubjects() {
        repo.getData(
                UrlManager.URL_GET_SUBJECT_COUNT,
                response -> {
                    try {
                        int count = response.getInt("count");
                        numOfSubjects.postValue(count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplication(), "Error occurred", LENGTH_SHORT).show();
                }
        );
    }

    public void fetchNumOfClassrooms() {
        repo.getData(
                UrlManager.URL_GET_CLASSROOM_COUNT,
                response -> {
                    try {
                        int count = response.getInt("count");
                        numOfClassrooms.postValue(count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplication(), "Error occurred", LENGTH_SHORT).show();
                }
        );
    }

    public void fetchNumOfTeachersPerSubject() {
        repo.getAllItems(
                UrlManager.URL_GET_TEACHER_PER_SUBJECT,
                response -> {
                    List<BarEntry> entries = new ArrayList<>();
                    subjectLabels = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String subject = obj.getString("title");
                            subjectLabels.add(subject);
                            int count = obj.getInt("count");
                            entries.add(new BarEntry(i, count));
                        }
                        teachersPerSubjectEntries.postValue(entries);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplication(), "Error occurred", LENGTH_SHORT).show();
                }
        );
    }

    public void fetchNumOfStudentsPerGrade() {
        repo.getAllItems(
                UrlManager.URL_GET_STUDENT_PER_GRADE,
                response -> {
                    List<BarEntry> entries = new ArrayList<>();
                    gradeLabels = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int gradeNumber = obj.getInt("grade");
                            int count = obj.getInt("count");
                            entries.add(new BarEntry(i, count));
                            gradeLabels.add(String.valueOf(gradeNumber));
                        }

                        studentsPerGradeEntries.postValue(entries);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplication(), "Error occurred", LENGTH_SHORT).show();
                }
        );
    }


    public List<String> getGradeLabels() {
        return gradeLabels;
    }

    public List<String> getSubjectLabels() {
        return subjectLabels;
    }

}