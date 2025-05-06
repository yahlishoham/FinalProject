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

    // מפתח להעברת הרשימה דרך Bundle
    private static final String ARG_RUNS = "runs";

    // רשימת הריצות שתתקבל מהמסך הקודם
    private ArrayList<RunDetails> runList;

    // יצירת מופע חדש של הפרגמנט עם רשימת ריצות כפרמטר
    public static SummaryFragment newInstance(ArrayList<RunDetails> runs) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RUNS, runs); // שומר את הרשימה כ־Serializable
        fragment.setArguments(args); // מצרף את הנתונים לפרגמנט
        return fragment;
    }

    // נקרא כשהפרגמנט נוצר – כאן נטען את רשימת הריצות אם הועברה
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // שולפים את הרשימה מה־Bundle
            runList = (ArrayList<RunDetails>) getArguments().getSerializable(ARG_RUNS);
        } else {
            // אם לא הועברה רשימה – נשתמש ברשימה ריקה
            runList = new ArrayList<>();
        }
    }

    // יוצר את התצוגה הגרפית של הפרגמנט וממלא את הנתונים
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // טוען את קובץ העיצוב של הפרגמנט
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        // קישור רכיבי UI
        TextView tvTotalRuns = view.findViewById(R.id.tv_total_runs);
        TextView tvMotivation = view.findViewById(R.id.tv_motivation);
        Button btnClose = view.findViewById(R.id.btn_close);

        // מחשב את מספר הריצות שבוצעו
        int totalRuns = runList.size();

        // מציג את מספר הריצות ומשפט מוטיבציה
        tvTotalRuns.setText("Total Runs: " + totalRuns);
        tvMotivation.setText("Every run brings you closer to your goals! 🏃‍♂️🔥");

        // כפתור לסגירת הפרגמנט וחזרה אחורה
        btnClose.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view; // מחזיר את התצוגה להצגה על המסך
    }
}
