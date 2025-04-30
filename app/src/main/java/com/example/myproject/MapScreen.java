package com.example.myproject;


import static android.text.format.DateUtils.formatElapsedTime;


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
import android.media.MediaPlayer;



import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapScreen extends FragmentActivity implements SensorEventListener, OnMapReadyCallback {


    private TextView tvTimer, tvDistance;
    private Button btnStartStop, btnToggleMusic;
    private TextView tvCountdown;



    private Handler handler = new Handler();
    private long startTime = 0L;
    private boolean isRunning = false;


    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int stepCounterInitialValue = -1;
    private int stepsDuringRun = 0;


    private GoogleMap googleMap;
    private double startingPointLatitude;
    private double startingPointLongitude;
    private double finishPointLatitude;
    private double finishPointLongitude;


    private double totalDistance = 0.0;
    private Location lastLocation;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);


        // Initialize Views
        tvTimer = findViewById(R.id.tv_timer);
        tvDistance = findViewById(R.id.tv_distance);
        btnStartStop = findViewById(R.id.btn_start_stop);
        btnToggleMusic = findViewById(R.id.btn_toggle_music);
        tvCountdown = findViewById(R.id.tv_countdown);



        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Location setup
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationUpdates();


        // Sensor setup
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);


        // כפתור להתחלה ועצירה
        btnStartStop.setOnClickListener(v -> {
            if (isRunning) {
                stopTracking();
                stopMusic();
            } else {
                if (checkPermissions()) {
                    startTracking();
                    startMusic();
                } else {
                    requestPermissions();
                }
            }
        });

        // כפתור עצירה והמשך של המוזיקה
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
    }


    private void setupLocationUpdates() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) return;


                Location currentLocation = locationResult.getLastLocation();
                if (lastLocation != null) {
                    double distanceInMeters = lastLocation.distanceTo(currentLocation);
                    totalDistance += distanceInMeters / 1000.0;
                }
                lastLocation = currentLocation;


                tvDistance.setText(String.format("Distance: %.2f km", totalDistance));


                // עדכון מיקום נוכחי על המפה
                if (googleMap != null) {
                    googleMap.clear();
                    LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                }
            }
        };
    }


    private void startTracking() {
        tvCountdown.setVisibility(View.VISIBLE); // מציגים את הספירה
        new android.os.CountDownTimer(4000, 1000) {
            int count = 3;

            public void onTick(long millisUntilFinished) {
                if (count > 0) {
                    tvCountdown.setText(String.valueOf(count)); // מציג מספר
                    animateCountdown();
                    count--;
                }
            }

            public void onFinish() {
                tvCountdown.setText("GO!"); // מציג GO בסוף
                new android.os.Handler().postDelayed(() -> {
                    tvCountdown.setVisibility(View.GONE); // מסתיר את הספירה

                    // כאן מתחיל המעקב הרגיל
                    isRunning = true;
                    totalDistance = 0f;
                    stepsDuringRun = 0;
                    stepCounterInitialValue = -1;
                    lastLocation = null;
                    startTime = System.currentTimeMillis();

                    if (ActivityCompat.checkSelfPermission(MapScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

                        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                            if (location != null) {
                                startingPointLatitude = location.getLatitude();
                                startingPointLongitude = location.getLongitude();
                            }
                        });
                    }

                    if (stepCounterSensor != null) {
                        sensorManager.registerListener(MapScreen.this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
                    }

                    handler.post(updateTimer);

                    btnStartStop.setText("Stop");
                    btnStartStop.setBackgroundTintList(ContextCompat.getColorStateList(MapScreen.this, android.R.color.holo_red_light));
                }, 800); // ממתין 800ms לפני שמתחילים אחרי ה-GO
            }
        }.start();
    }




    private void stopTracking() {
        isRunning = false;
        handler.removeCallbacks(updateTimer);
        fusedLocationClient.removeLocationUpdates(locationCallback);

        sensorManager.unregisterListener(this);

        long elapsedTimeMillis = System.currentTimeMillis() - startTime;
        long elapsedTimeSeconds = elapsedTimeMillis / 1000;
        String runTime = formatElapsedTime(elapsedTimeSeconds);

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

        Intent intent = new Intent(MapScreen.this, HomePage.class);
        startActivity(intent);
    }



    private void saveRunToDatabase(String runTime, double distance, int steps,
                                   double startLat, double startLon, double finishLat, double finishLon) {
        HelperDB helperDB = new HelperDB(this);
        RunDetails runDetails = new RunDetails(
                runTime,
                distance,
                startLat,
                startLon,
                finishLat,
                finishLon,
                steps
        );


        helperDB.saveRunToDB(runDetails);
        Toast.makeText(this, "Run saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void startMusic() {
        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);
    }

    private void stopMusic() {
        Intent musicIntent = new Intent(this, MusicService.class);
        stopService(musicIntent);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (stepCounterInitialValue == -1) {
                stepCounterInitialValue = (int) event.values[0];
            }
            stepsDuringRun = (int) event.values[0] - stepCounterInitialValue;
        }
    }

    private final Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            long elapsedTime = System.currentTimeMillis() - startTime;
            int seconds = (int) (elapsedTime / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;


            String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            tvTimer.setText(time);


            if (isRunning) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void animateCountdown() {
        tvCountdown.setScaleX(0.5f);
        tvCountdown.setScaleY(0.5f);
        tvCountdown.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .start();
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
    }


    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Location permission is needed to track your run.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
