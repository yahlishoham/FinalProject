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

    private RecyclerView rvPastRuns;
    private Adapter runAdapter;
    private HelperDB helperDB;
    private Button btnHome;
    private Button btnOpenSummary;
    private List<RunDetails> runList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_runs);

        rvPastRuns = findViewById(R.id.rv_past_runs);
        btnHome = findViewById(R.id.btn_home);
        btnOpenSummary = findViewById(R.id.btn_open_summary);
        helperDB = new HelperDB(this);

        // שליפת כל הריצות ממסד הנתונים
        runList = helperDB.getAllRuns();
        Collections.reverse(runList);

        // הגדרת RecyclerView
        rvPastRuns.setLayoutManager(new LinearLayoutManager(this));
        rvPastRuns.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        if (runList.isEmpty()) {
            Toast.makeText(this, "No past runs available", Toast.LENGTH_SHORT).show();
        } else {
            runAdapter = new Adapter(runList);
            rvPastRuns.setAdapter(runAdapter);
        }

        // מעבר לדף הבית
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(PastRuns.this, HomePage.class);
            startActivity(intent);
        });

        // פתיחת פרגמנט הסיכום
        btnOpenSummary.setOnClickListener(v -> {
            SummaryFragment fragment = SummaryFragment.newInstance(new ArrayList<>(runList));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
