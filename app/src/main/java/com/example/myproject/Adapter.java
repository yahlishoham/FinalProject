package com.example.myproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

// Adapter לרשימת ריצות — מחבר בין הנתונים למסך
public class Adapter extends RecyclerView.Adapter<Adapter.RunViewHolder> {

    // רשימת הריצות שמוצגות ברשימה
    private List<RunDetails> runList;

    // פעולה בונה שמקבלת את הרשימה ומאחסנת אותה
    public Adapter(List<RunDetails> runList) {
        this.runList = runList;
    }

    // יוצרת ViewHolder חדש כשהרשימה צריכה להציג שורה חדשה
    @NonNull
    @Override
    public RunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // טוען את התצוגה של שורת פריט מהרשימה (activity_item_run.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_run, parent, false);
        return new RunViewHolder(view);
    }

    // מחברת בין הנתונים (RunDetails) לבין ה־ViewHolder
    @Override
    public void onBindViewHolder(@NonNull RunViewHolder holder, int position) {
        RunDetails run = runList.get(position);

        // מציג את המרחק, הזמן והצעדים בטקסטים של כל שורה
        holder.tvRunDistance.setText(String.format("Distance: %.2f km", run.getRunDistance()));
        holder.tvRunTime.setText(String.format("Time: %s", run.getRunTime()));
        holder.tvStepCounter.setText(String.format("Steps: %d", run.getStepCounter()));

        // מגדיר את המפה הקטנה בתוך הפריט
        holder.mapView.onCreate(null); // אתחול המפה

        holder.mapView.getMapAsync(googleMap -> {
            // מציב נקודת התחלה וסיום על המפה
            LatLng startPoint = new LatLng(run.getStartingPointLatitude(), run.getStartingPointLongitude());
            LatLng finishPoint = new LatLng(run.getFinishPointLatitude(), run.getFinishPointLongitude());

            googleMap.addMarker(new MarkerOptions().position(startPoint).title("Start"));
            googleMap.addMarker(new MarkerOptions().position(finishPoint).title("Finish"));

            // מזיז את המצלמה לנקודת ההתחלה
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 10));
        });
    }

    // מחזירה את מספר הפריטים ברשימה
    @Override
    public int getItemCount() {
        return runList.size();
    }

    // ViewHolder — מייצג שורת מידע אחת ברשימה
    static class RunViewHolder extends RecyclerView.ViewHolder {
        TextView tvRunDistance, tvRunTime, tvStepCounter;
        MapView mapView;

        public RunViewHolder(@NonNull View itemView) {
            super(itemView);
            // קישור רכיבי התצוגה מתוך activity_item_run.xml
            tvRunDistance = itemView.findViewById(R.id.tv_run_distance);
            tvRunTime = itemView.findViewById(R.id.tv_run_time);
            tvStepCounter = itemView.findViewById(R.id.tv_step_counter);
            mapView = itemView.findViewById(R.id.map_view);
        }
    }
}
