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

public class Adapter extends RecyclerView.Adapter<Adapter.RunViewHolder> {
    private List<RunDetails> runList;

    public Adapter(List<RunDetails> runList) {
        this.runList = runList;
    }

    @NonNull
    @Override
    public RunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_run, parent, false);
        return new RunViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RunViewHolder holder, int position) {
        RunDetails run = runList.get(position);

        // עדכון הטקסטים עם הנתונים האמיתיים
        holder.tvRunDistance.setText(String.format("Distance: %.2f km", run.getRunDistance()));
        holder.tvRunTime.setText(String.format("Time: %s", run.getRunTime()));

        // עדכון המפה
        holder.mapView.onCreate(null);
        holder.mapView.getMapAsync(googleMap -> {
            LatLng startPoint = new LatLng(run.getStartingPointLatitude(), run.getStartingPointLongitude());
            LatLng finishPoint = new LatLng(run.getFinishPointLatitude(), run.getFinishPointLongitude());

            googleMap.addMarker(new MarkerOptions().position(startPoint).title("Start"));
            googleMap.addMarker(new MarkerOptions().position(finishPoint).title("Finish"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 10));
        });
    }

    @Override
    public int getItemCount() {
        return runList.size();
    }

    static class RunViewHolder extends RecyclerView.ViewHolder {
        TextView tvRunDistance, tvRunTime;
        MapView mapView;

        public RunViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRunDistance = itemView.findViewById(R.id.tv_run_distance);
            tvRunTime = itemView.findViewById(R.id.tv_run_time);
            mapView = itemView.findViewById(R.id.map_view);
        }
    }
}