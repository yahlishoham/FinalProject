package com.example.myproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunViewHolder> {

    private final List<RunDetails> runList; // רשימת המסלולים להצגה

    // קונסטרקטור - מקבל את רשימת המסלולים
    public RunAdapter(List<RunDetails> runList) {
        this.runList = runList;
    }

    // פעולה ליצירת ViewHolder חדש
    @NonNull
    @Override
    public RunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // יצירת תצוגה מבוססת על layout בשם run_item
        return new RunViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.run_item, parent, false));
    }

    // פעולה לקישור נתונים לכל שורה
    @Override
    public void onBindViewHolder(@NonNull RunViewHolder holder, int position) {
        RunDetails run = runList.get(position); // קבלת פרטי המסלול לפי המיקום ברשימה
        holder.bind(run); // קישור הנתונים ל-View
    }

    // פעולה שמחזירה את כמות המסלולים להצגה
    @Override
    public int getItemCount() {
        return runList.size();
    }

    // מחלקה פנימית ליצוג כל שורה ברשימה
    static class RunViewHolder extends RecyclerView.ViewHolder {
        private final TextView runTime, runDistance, startFinishPoints;

        // קישור רכיבי ה-View ל-ID שלהם ב-layout
        public RunViewHolder(@NonNull View itemView) {
            super(itemView);
            runTime = itemView.findViewById(R.id.run_time); // שדה להצגת זמן הריצה
            runDistance = itemView.findViewById(R.id.run_distance); // שדה להצגת מרחק הריצה
            startFinishPoints = itemView.findViewById(R.id.start_finish_points); // שדה להצגת נקודות התחלה וסיום
        }

        // פעולה שמבצעת את הקישור של הנתונים ישירות ל-View
        public void bind(RunDetails runDetails) {
            runTime.setText(runDetails.getRunTime()); // הצגת זמן הריצה
            runDistance.setText("Distance: " + runDetails.getRunDistance() + " km"); // הצגת מרחק הריצה
            startFinishPoints.setText("Start: " + runDetails.getStartingPoint() + ", Finish: " + runDetails.getFinishPoint()); // הצגת נקודות התחלה וסיום
        }
    }
}