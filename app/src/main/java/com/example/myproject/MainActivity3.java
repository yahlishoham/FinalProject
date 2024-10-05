package com.example.myproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RunAdapter runAdapter;
    private HelperDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // אתחול מסד הנתונים ושליפת נתונים
        dbHelper = new HelperDB(this);
        List<RunDetails> runList = fetchRunsFromDB();

        // יצירת האדפטר והצגת נתונים
        runAdapter = new RunAdapter(runList);
        recyclerView.setAdapter(runAdapter);


        // קישור הכפתור
        Button openFragmentButton = findViewById(R.id.open_fragment_button);

        // הגדרת פעולה ללחיצה על הכפתור
        openFragmentButton.setOnClickListener(v -> {
            // פתיחת ה-Fragment
            openFragment();
        });
    }
    // פעולה לשליפת מסלולים מהמסד
    private List<RunDetails> fetchRunsFromDB() {
        List<RunDetails> runList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // שאילתת SELECT לכל הנתונים בטבלה
        String query = "SELECT * FROM " + HelperDB.TABLE_RESULT;
        Cursor cursor = db.rawQuery(query, null);

        // מעבר על הנתונים ושמירתם ברשימה
        if (cursor.moveToFirst()) {
            do {
                String runTime = cursor.getString(cursor.getColumnIndexOrThrow(HelperDB.RUN_TIME));
                double runDistance = cursor.getDouble(cursor.getColumnIndexOrThrow(HelperDB.RUN_DISTANCE));
                double startingPoint = cursor.getDouble(cursor.getColumnIndexOrThrow(HelperDB.STARTING_POINT));
                double finishPoint = cursor.getDouble(cursor.getColumnIndexOrThrow(HelperDB.FINISH_POINT));
                int userScore = cursor.getInt(cursor.getColumnIndexOrThrow(HelperDB.RUN_LEVEL));

                runList.add(new RunDetails(runTime, runDistance, startingPoint, finishPoint, userScore));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return runList;
    }

    private void openFragment() {
        // יצירת אובייקט של ה-Fragment
        Fragment fragment = new EnterFragment();

        // ניהול ה-Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);

        // הוספה לערימת החזרה (אופציונלי)
        fragmentTransaction.addToBackStack(null);

        // ביצוע העסקה
        fragmentTransaction.commit();
    }
}