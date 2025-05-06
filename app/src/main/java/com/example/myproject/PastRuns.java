package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PastRuns extends AppCompatActivity {

    // משתני ממשק גרפי
    private RecyclerView rvPastRuns;
    private Adapter runAdapter;
    private HelperDB helperDB;
    private Button btnHome;
    private Button btnOpenSummary;

    // רשימה של ריצות שהמשתמש ביצע
    private List<RunDetails> runList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_runs);

        // קישור רכיבי UI לפי ה-id שלהם בקובץ XML
        rvPastRuns = findViewById(R.id.rv_past_runs);
        btnHome = findViewById(R.id.btn_home);
        btnOpenSummary = findViewById(R.id.btn_open_summary);

        // יצירת אובייקט לעבודה עם מסד הנתונים
        helperDB = new HelperDB(this);

        // שליפת כל הריצות מהמסד
        runList = helperDB.getAllRuns();

        // הפיכה של הרשימה כדי שהריצה האחרונה תופיע למעלה
        Collections.reverse(runList);

        // הגדרה של RecyclerView עם סידור ליניארי (רשימה אנכית)
        rvPastRuns.setLayoutManager(new LinearLayoutManager(this));

        // הוספת קווים בין הפריטים ברשימה
        rvPastRuns.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // אם אין ריצות, נציג הודעה
        if (runList.isEmpty()) {
            Toast.makeText(this, "No past runs available", Toast.LENGTH_SHORT).show();
        } else {
            // אם יש ריצות, נטען אותן לתוך האדפטר שמציג אותן ברשימה
            runAdapter = new Adapter(runList);
            rvPastRuns.setAdapter(runAdapter);
        }

        // לחיצה על כפתור הבית מחזירה למסך הראשי
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(PastRuns.this, HomePage.class);
            startActivity(intent);
            finish(); // סוגר את המסך הנוכחי כדי שהפרגמנט לא יישאר בזיכרון בטעות
        });

        // לחיצה על כפתור "סיכום" פותחת את הפרגמנט שמציג את מספר הריצות
        btnOpenSummary.setOnClickListener(v -> {
            // שולח את רשימת הריצות כ-argument לפרגמנט
            SummaryFragment fragment = SummaryFragment.newInstance(new ArrayList<>(runList));

            // החלפת תוכן המסך בפרגמנט
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment) // ודא שב־XML יש container עם ID הזה
                    .addToBackStack(null) // מאפשר חזרה אחורה עם כפתור "חזור"
                    .commit();
        });
    }
}
