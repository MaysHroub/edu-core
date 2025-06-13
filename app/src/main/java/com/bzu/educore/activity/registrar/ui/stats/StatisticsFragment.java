package com.bzu.educore.activity.registrar.ui.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bzu.educore.databinding.FragmentStatisticsBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StatisticsViewModel statisticsViewModel =
                new ViewModelProvider(this).get(StatisticsViewModel.class);

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        statisticsViewModel.getNumOfStudents().observe(getViewLifecycleOwner(), count -> binding.txtNumOfStds.setText(count+""));
        statisticsViewModel.getNumOfTeachers().observe(getViewLifecycleOwner(), count -> binding.txtNumOfTeachers.setText(count+""));
        statisticsViewModel.getNumOfClassrooms().observe(getViewLifecycleOwner(), count -> binding.txtNumOfClassrooms.setText(count+""));
        statisticsViewModel.getNumOfSubjects().observe(getViewLifecycleOwner(), count -> binding.txtNumOfSubjects.setText(count+""));

        statisticsViewModel.getTeachersPerSubjectEntries().observe(getViewLifecycleOwner(), entries -> {
            BarDataSet dataSet = new BarDataSet(entries, "Teachers per Subject");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.9f);

            BarChart chart = binding.barChartTeacherSubjectDist;
            chart.setData(barData);
            chart.setFitBars(true);
            chart.getDescription().setEnabled(false);
            chart.animateY(1000);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(statisticsViewModel.getSubjectLabels()));

            YAxis leftAxis = chart.getAxisLeft();
            YAxis rightAxis = chart.getAxisRight();
            leftAxis.setDrawGridLines(false);
            rightAxis.setEnabled(false);

            chart.invalidate(); // Refresh
        });

        statisticsViewModel.getStudentsPerGradeEntries().observe(getViewLifecycleOwner(), entries -> {
            BarDataSet dataSet = new BarDataSet(entries, "Students per Grade");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.9f);

            BarChart chart = binding.barChartStudentGradeDist;
            chart.setData(barData);
            chart.setFitBars(true);
            chart.getDescription().setEnabled(false);
            chart.animateY(1000);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(statisticsViewModel.getGradeLabels()));

            YAxis leftAxis = chart.getAxisLeft();
            YAxis rightAxis = chart.getAxisRight();
            leftAxis.setDrawGridLines(false);
            rightAxis.setEnabled(false);

            chart.invalidate(); // Refresh
        });


        statisticsViewModel.fetchNumOfStudents();
        statisticsViewModel.fetchNumOfTeachers();
        statisticsViewModel.fetchNumOfSubjects();
        statisticsViewModel.fetchNumOfClassrooms();
        statisticsViewModel.fetchNumOfStudentsPerGrade();
        statisticsViewModel.fetchNumOfTeachersPerSubject();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}