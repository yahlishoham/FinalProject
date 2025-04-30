package com.example.myproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SummaryFragment extends Fragment {

    private static final String ARG_RUNS = "runs";
    private ArrayList<RunDetails> runList;

    public static SummaryFragment newInstance(ArrayList<RunDetails> runs) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RUNS, runs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            runList = (ArrayList<RunDetails>) getArguments().getSerializable(ARG_RUNS);
        } else {
            runList = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        TextView tvTotalRuns = view.findViewById(R.id.tv_total_runs);
        TextView tvMotivation = view.findViewById(R.id.tv_motivation);
        Button btnClose = view.findViewById(R.id.btn_close);

        int totalRuns = runList.size();

        tvTotalRuns.setText("Total Runs: " + totalRuns);
        tvMotivation.setText("Every run brings you closer to your goals! 🏃‍♂️🔥");

        btnClose.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }
}
