package com.example.myproject;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Fragment שמאפשר למשתמש להזין מסלול חדש ולשמור אותו במסד הנתונים.
 */
public class EnterFragment extends Fragment {

    // משתנים לקבלת פרמטרים
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // רכיבי UI
    private EditText time, distance, start, finish, level;
    private Button insert, back;

    // עצם לניהול מסד הנתונים
    private HelperDB dbHelper;

    public EnterFragment() {
        // קונסטרקטור ריק נדרש
    }

    /**
     * פונקציה ליצירת Fragment חדש עם פרמטרים.
     *
     * @param param1 פרמטר 1.
     * @param param2 פרמטר 2.
     * @return אובייקט של EnterFragment.
     */
    public static EnterFragment newInstance(String param1, String param2) {
        EnterFragment fragment = new EnterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // בדיקה אם יש פרמטרים שנשלחו ל-Fragment
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // טעינת הפריסה של ה-Fragment
        View view = inflater.inflate(R.layout.fragment_enter, container, false);

        // קישור רכיבי ה-UI מה-XML
        EditText timeEditText = view.findViewById(R.id.time);
        EditText distanceEditText = view.findViewById(R.id.distance);
        EditText startEditText = view.findViewById(R.id.start);
        EditText finishEditText = view.findViewById(R.id.finish);
        EditText levelEditText = view.findViewById(R.id.level);
        Button insertButton = view.findViewById(R.id.insert);
        Button backButton = view.findViewById(R.id.backToOptions);

        // אתחול מסד הנתונים
        dbHelper = new HelperDB(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // פעולה לחזרה ל-Activity הראשי בלחיצה על כפתור Back
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack(); // מחזיר לערימת ה-Fragment הקודמת
            }
        });

        // פעולה ללחיצה על כפתור Insert
        insertButton.setOnClickListener(v -> {
            // קריאת הערכים מהשדות
            String time = timeEditText.getText().toString().trim();
            String distanceStr = distanceEditText.getText().toString().trim();
            String startStr = startEditText.getText().toString().trim();
            String finishStr = finishEditText.getText().toString().trim();
            String levelStr = levelEditText.getText().toString().trim();

            // בדיקה אם כל השדות מלאים
            if (time.isEmpty() || distanceStr.isEmpty() || startStr.isEmpty() || finishStr.isEmpty() || levelStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // המרת ערכים לשימושים מספריים
                double runDistance = Double.parseDouble(distanceStr);
                double startingPoint = Double.parseDouble(startStr);
                double finishPoint = Double.parseDouble(finishStr);
                int userScore = Integer.parseInt(levelStr);

                // יצירת אובייקט RunDetails עם הנתונים
                RunDetails runDetails = new RunDetails(time, runDistance, startingPoint, finishPoint, userScore);

                // שמירת המסלול במסד הנתונים
                dbHelper.saveRunToDB(runDetails);

                Toast.makeText(getContext(), "Run details saved!", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numerical values", Toast.LENGTH_SHORT).show();
            }
        });

        return view; // מחזיר את ה-View של ה-Fragment
    }
}