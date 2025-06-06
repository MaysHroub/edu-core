package com.bzu.educore.activity.registrar.ui.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bzu.educore.databinding.FragmentStatisticsBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StatisticsViewModel statisticsViewModel =
                new ViewModelProvider(this).get(StatisticsViewModel.class);

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView txtNumOfStds = binding.txtNumOfStds;
        final TextView txtNumOfTeachers = binding.txtNumOfTeachers;
        final TextView txtNumOfClassrooms = binding.txtNumOfSections;
        final TextView txtNumOfSubjects = binding.txtNumOfSubjects;
        final PieChart pieChartTeacherSubjectDist = binding.pieChartTeacherSubjectDist;
        final PieChart pieChartStdGradeDist = binding.pieChartStdGradeDist;

        statisticsViewModel.getNumOfStudents().observe(getViewLifecycleOwner(), txtNumOfStds::setText);
        statisticsViewModel.getNumOfTeachers().observe(getViewLifecycleOwner(), txtNumOfTeachers::setText);
        statisticsViewModel.getNumOfClassrooms().observe(getViewLifecycleOwner(), txtNumOfClassrooms::setText);
        statisticsViewModel.getNumOfSubjects().observe(getViewLifecycleOwner(), txtNumOfSubjects::setText);

        statisticsViewModel.getTeachersPerSubjectEntries().observe(getViewLifecycleOwner(), entries -> {
            PieDataSet dataSet = new PieDataSet(entries, "Teachers per Subject");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            PieData pieData = new PieData(dataSet);
            pieChartTeacherSubjectDist.setData(pieData);
            pieChartTeacherSubjectDist.invalidate(); // refresh chart
        });

        statisticsViewModel.getStudentsPerGradeEntries().observe(getViewLifecycleOwner(), entries -> {
            PieDataSet dataSet = new PieDataSet(entries, "Students per Grade");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            PieData pieData = new PieData(dataSet);
            pieChartStdGradeDist.setData(pieData);
            pieChartStdGradeDist.invalidate();
        });

        statisticsViewModel.fetchNumOfStudents();
        statisticsViewModel.fetchNumOfTeachers();
        statisticsViewModel.fetchNumOfSubjects();
        statisticsViewModel.fetchNumOfClassrooms();
        statisticsViewModel.fetchStudentPerGrade();
        statisticsViewModel.fetchTeacherPerSubject();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}