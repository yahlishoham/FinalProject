package com.example.myproject;

// שימוש בפורמט זמן מובן (לדוגמה: 00:04:32)
import static android.text.format.DateUtils.formatElapsedTime;

// ייבוא מחלקות חיוניות: מיקום, חיישנים, ממשק, הרשאות
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class MapScreen extends FragmentActivity implements SensorEventListener, OnMapReadyCallback {

    // רכיבי UI – לא צריך הסבר פרטני עליהם
    private TextView tvTimer, tvDistance, tvCountdown;
    private Button btnStartStop, btnToggleMusic, btnReturnHome;

    // מנהל משימות שמריץ פעולות חוזרות – למשל הטיימר
    private Handler handler = new Handler();

    // זמן התחלה של הריצה
    private long startTime = 0L;

    // האם הריצה כרגע פעילה
    private boolean isRunning = false;

    // אובייקטים לשירות מיקום של Google
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    // אובייקטים לחיישן צעדים (Step Counter)
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int stepCounterInitialValue = -1; // שומר את ערך הספירה בתחילת הריצה
    private int stepsDuringRun = 0;

    // Google Map והנקודות בהן מתחילים ומסיימים את הריצה
    private GoogleMap googleMap;
    private double startingPointLatitude, startingPointLongitude;
    private double finishPointLatitude, finishPointLongitude;

    // מרחק כולל שנצבר במהלך הריצה
    private double totalDistance = 0.0;
    private Location lastLocation;

    // מזהה לבקשת הרשאה מהמכשיר
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);

        // קישור רכיבי UI
        tvTimer = findViewById(R.id.tv_timer);
        tvDistance = findViewById(R.id.tv_distance);
        tvCountdown = findViewById(R.id.tv_countdown);
        btnStartStop = findViewById(R.id.btn_start_stop);
        btnToggleMusic = findViewById(R.id.btn_toggle_music);
        btnReturnHome = findViewById(R.id.btn_return_home);

        // טוען את המפה מתוך ה־XML
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // מגדיר את ספק המיקום ומכין את קריאת המיקום
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationUpdates();

        // מגדיר את החיישן לצעדים
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // לחיצה על כפתור התחלה/עצירה
        btnStartStop.setOnClickListener(v -> {
            if (isRunning) {
                stopTracking();
                stopMusic(); // חשוב לעצור גם את המוזיקה
            } else {
                if (checkPermissions()) {
                    startTracking();
                    startMusic();
                } else {
                    requestPermissions();
                }
            }
        });

        // לחצן לשליטה במוזיקה (עצור/המשך)
        btnToggleMusic.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicService.class);
            if (MusicService.isPaused) {
                intent.putExtra("action", "resume");
                btnToggleMusic.setText("⏸️ Pause Music");
            } else {
                intent.putExtra("action", "pause");
                btnToggleMusic.setText("▶️ Play Music");
            }
            startService(intent);
        });

        // כפתור חדש: חזרה למסך הבית
        btnReturnHome.setOnClickListener(v -> {
            if (isRunning) {
                stopTracking(); // עצירה מלאה ושמירה
                stopMusic();    // לא לשכוח להפסיק את המוזיקה!
            } else {
                startActivity(new Intent(MapScreen.this, HomePage.class));
            }
        });
    }

    // הגדרה של קבלת מיקום כל 5 שניות ועדכון מרחק
    private void setupLocationUpdates() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                if (result == null) return;
                Location currentLocation = result.getLastLocation();
                if (lastLocation != null) {
                    double distanceInMeters = lastLocation.distanceTo(currentLocation);
                    totalDistance += distanceInMeters / 1000.0;
                }
                lastLocation = currentLocation;

                // מעדכן את המרחק במסך
                tvDistance.setText(String.format("Distance: %.2f km", totalDistance));

                // מוסיף סימון על המפה
                if (googleMap != null) {
                    googleMap.clear();
                    LatLng pos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(pos).title("Current Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
                }
            }
        };
    }

    // התחלת הריצה – כולל ספירה לאחור
    private void startTracking() {
        tvCountdown.setVisibility(View.VISIBLE);
        new android.os.CountDownTimer(4000, 1000) {
            int count = 3;

            public void onTick(long millisUntilFinished) {
                if (count > 0) {
                    tvCountdown.setText(String.valueOf(count));
                    animateCountdown();
                    count--;
                }
            }

            public void onFinish() {
                tvCountdown.setText("GO!");
                new Handler().postDelayed(() -> {
                    tvCountdown.setVisibility(View.GONE);
                    isRunning = true;
                    totalDistance = 0.0;
                    stepsDuringRun = 0;
                    stepCounterInitialValue = -1;
                    lastLocation = null;
                    startTime = System.currentTimeMillis();

                    // התחלת מעקב מיקום
                    if (ActivityCompat.checkSelfPermission(MapScreen.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                            if (location != null) {
                                startingPointLatitude = location.getLatitude();
                                startingPointLongitude = location.getLongitude();
                            }
                        });
                    }

                    // רישום חיישן צעדים
                    if (stepCounterSensor != null) {
                        sensorManager.registerListener(MapScreen.this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
                    }

                    handler.post(updateTimer);
                    btnStartStop.setText("Stop");
                    btnStartStop.setBackgroundTintList(ContextCompat.getColorStateList(MapScreen.this, android.R.color.holo_red_light));
                }, 800);
            }
        }.start();
    }

    // עצירת ריצה, שמירה ומעבר הביתה
    private void stopTracking() {
        isRunning = false;
        handler.removeCallbacks(updateTimer);
        fusedLocationClient.removeLocationUpdates(locationCallback);
        sensorManager.unregisterListener(this);

        long elapsedTimeMillis = System.currentTimeMillis() - startTime;
        String runTime = formatElapsedTime(elapsedTimeMillis / 1000);

        if (lastLocation != null) {
            finishPointLatitude = lastLocation.getLatitude();
            finishPointLongitude = lastLocation.getLongitude();

            saveRunToDatabase(runTime, totalDistance, stepsDuringRun,
                    startingPointLatitude, startingPointLongitude,
                    finishPointLatitude, finishPointLongitude);
        } else {
            Toast.makeText(this, "Unable to save run. Missing location data.", Toast.LENGTH_SHORT).show();
        }

        btnStartStop.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_green_light));
        btnStartStop.setText("Start");

        startActivity(new Intent(MapScreen.this, HomePage.class));
    }

    // שמירת הריצה למסד הנתונים
    private void saveRunToDatabase(String time, double distance, int steps,
                                   double startLat, double startLon, double endLat, double endLon) {
        HelperDB db = new HelperDB(this);
        RunDetails run = new RunDetails(time, distance, startLat, startLon, endLat, endLon, steps);
        db.saveRunToDB(run);
        Toast.makeText(this, "Run saved successfully!", Toast.LENGTH_SHORT).show();
    }

    // הפעלת שירות המוזיקה
    private void startMusic() {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }

    // עצירת שירות המוזיקה
    private void stopMusic() {
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
    }

    // חישוב צעדים ע\"י חיישן
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (stepCounterInitialValue == -1) {
                stepCounterInitialValue = (int) event.values[0];
            }
            stepsDuringRun = (int) event.values[0] - stepCounterInitialValue;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // טיימר שמעדכן כל שנייה
    private final Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            long elapsedTime = System.currentTimeMillis() - startTime;
            int seconds = (int) (elapsedTime / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds %= 60;
            minutes %= 60;
            tvTimer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            if (isRunning) handler.postDelayed(this, 1000);
        }
    };

    // אנימציה לטקסט של הספירה לאחור
    private void animateCountdown() {
        tvCountdown.setScaleX(0.5f);
        tvCountdown.setScaleY(0.5f);
        tvCountdown.animate().scaleX(1f).scaleY(1f).setDuration(500).start();
    }

    // כשהמפה מוכנה – שומר את האובייקט GoogleMap
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
    }

    // בדיקת הרשאה למיקום
    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    // בקשת הרשאה למיקום
    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Location permission is needed to track your run.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    // תגובה לבקשת הרשאה
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. You can start tracking now.", Toast.LENGTH_SHORT).show();
                startTracking();
            } else {
                Toast.makeText(this, "Permission denied. Location tracking is not available.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
