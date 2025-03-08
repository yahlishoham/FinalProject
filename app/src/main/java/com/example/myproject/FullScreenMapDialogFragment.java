package com.example.myproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FullScreenMapDialogFragment extends DialogFragment implements OnMapReadyCallback {

    private double startLat, startLng, finishLat, finishLng;
    private GoogleMap mMap;

    // יצירת מופע חדש של הדיאלוג עם קואורדינטות
    public static FullScreenMapDialogFragment newInstance(double startLat, double startLng, double finishLat, double finishLng) {
        FullScreenMapDialogFragment fragment = new FullScreenMapDialogFragment();
        Bundle args = new Bundle();
        args.putDouble("startLat", startLat);
        args.putDouble("startLng", startLng);
        args.putDouble("finishLat", finishLat);
        args.putDouble("finishLng", finishLng);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_screen_map_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // קבלת הנתונים שנשלחו
        if (getArguments() != null) {
            startLat = getArguments().getDouble("startLat");
            startLng = getArguments().getDouble("startLng");
            finishLat = getArguments().getDouble("finishLat");
            finishLng = getArguments().getDouble("finishLng");
        }

        // טעינת המפה
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fullscreen_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng startPoint = new LatLng(startLat, startLng);
        LatLng finishPoint = new LatLng(finishLat, finishLng);

        // הוספת סימונים לנקודת התחלה וסיום
        mMap.addMarker(new MarkerOptions().position(startPoint).title("Start"));
        mMap.addMarker(new MarkerOptions().position(finishPoint).title("Finish"));

        // מיקוד לנקודת ההתחלה עם זום ברירת מחדל
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 12));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
