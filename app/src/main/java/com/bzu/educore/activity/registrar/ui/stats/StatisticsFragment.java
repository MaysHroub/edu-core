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

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private TextView txtNumOfStds, txtNumOfTeachers, txtNumOfSections, txtClassGrades;
    private PieChart pieChartGenderDist, pieChartStdClassDist;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StatisticsViewModel statisticsViewModel =
                new ViewModelProvider(this).get(StatisticsViewModel.class);

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView txtNumOfStds = binding.txtNumOfStds;
        TextView txtNumOfTeachers = binding.txtNumOfTeachers;
        TextView txtNumOfSections = binding.txtNumOfSections;
        TextView txtClassGrades = binding.txtClassGrades;

        PieChart pieChartGenderDist = binding.pieChartGenderDist;
        PieChart pieChartStdClassDist = binding.pieChartStdClassDist;

        // statisticsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}