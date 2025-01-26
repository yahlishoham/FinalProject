package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PastRuns extends AppCompatActivity {

    private RecyclerView rvPastRuns;
    private Adapter runAdapter;
    private HelperDB helperDB;
    private Button btnHome; // כפתור למסך הבית


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_runs);

        rvPastRuns = findViewById(R.id.rv_past_runs);
        btnHome = findViewById(R.id.btn_home); // קישור לכפתור
        helperDB = new HelperDB(this);

        // שליפת כל הריצות ממסד הנתונים
        List<RunDetails> runList = helperDB.getAllRuns();

        // הגדרת LayoutManager עבור ה-RecyclerView
        rvPastRuns.setLayoutManager(new LinearLayoutManager(this));

        if (runList.isEmpty()) {
            // הצגת הודעה אם הרשימה ריקה
            Toast.makeText(this, "No past runs available", Toast.LENGTH_SHORT).show();
        } else {
            // יצירת האדפטר עם הרשימה הקיימת
            runAdapter = new Adapter(runList);
            rvPastRuns.setAdapter(runAdapter);
        }
        // הגדרת מאזין לכפתור
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(PastRuns.this, HomePage.class);
            startActivity(intent);
        });
    }
}
