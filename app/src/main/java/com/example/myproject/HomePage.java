package com.example.myproject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Calendar;

public class HomePage extends AppCompatActivity {

    private static final String API_KEY = "a4674faf81cd3ab9005ca15c6b243603"; // המפתח שסיפקת
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private TextView tvWeather;
    private Retrofit retrofit;
    private Context context;
    private int requestCode = 123;
    private FusedLocationProviderClient fusedLocationClient;

    private Button buttonStartRun;
    private Button buttonViewHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        buttonStartRun = findViewById(R.id.buttonStartRun);
        buttonViewHistory = findViewById(R.id.buttonViewHistory);
        tvWeather = findViewById(R.id.tv_weather);
        context = this;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // יצירת אובייקט Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/3.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // הפעלת התראה יומית
        scheduleAlarm();

        // Listener ללחיצה על כפתור "התחלה"
        buttonStartRun.setOnClickListener(v -> {
            Intent first = new Intent(HomePage.this, MapScreen.class);
            startActivity(first);
        });

        // Listener ללחיצה על כפתור "היסטוריה"
        buttonViewHistory.setOnClickListener(v -> {
            Intent first = new Intent(HomePage.this, PastRuns.class);
            startActivity(first);
        });

        // קבלת נתוני מזג אוויר עבור המיקום הנוכחי של המשתמש
        getWeatherForCurrentLocation();
    }

    // 📌 קבלת המיקום הנוכחי של המשתמש
    private void getWeatherForCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                fetchWeatherFromAPI(latitude, longitude);
            } else {
                tvWeather.setText("Unable to get location.");
            }
        });
    }

    private void fetchWeatherFromAPI(double latitude, double longitude) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<WeatherResponse> call = apiService.getWeather(latitude, longitude, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    double temperature = weather.getCurrent().getTemp();
                    String weatherDescription = weather.getCurrent().getWeather()[0].getDescription().toLowerCase();

                    // 📌 קביעת הודעה בהתאם למזג האוויר
                    String weatherMessage = getWeatherMessage(temperature, weatherDescription);

                    // הצגת המידע למשתמש
                    String weatherInfo = weatherMessage + "\n" +
                            "Temperature: " + temperature + "°C\n" +
                            "Description: " + weatherDescription;
                    tvWeather.setText(weatherInfo);
                } else {
                    tvWeather.setText("Error fetching weather data.");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                tvWeather.setText("Error: " + t.getMessage());
            }
        });
    }

    // 📌 פונקציה לקביעת ההודעה בהתאם למזג האוויר
    private String getWeatherMessage(double temp, String description) {
        if (description.contains("rain") || description.contains("storm") || description.contains("drizzle")) {
            return "לא מומלץ לרוץ היום, יש גשם ☔";
        } else if (description.contains("snow")) {
            return "מזג האוויר קר מדי לריצה ❄️";
        } else if (description.contains("fog") || description.contains("mist") || description.contains("haze")) {
            return "מזג האוויר מעורפל, יש לשים לב לראייה מוגבלת 🌫️";
        } else if (temp < 5) {
            return "מזג האוויר קר מאוד לריצה 🥶";
        } else if (temp >= 5 && temp < 15) {
            return "מזג האוויר מתאים לריצה אך קצת קריר 🏃‍♂️❄️";
        } else if (temp >= 15 && temp < 25) {
            return "מזג האוויר מושלם לריצה! 🏃‍♂️☀️";
        } else if (temp >= 25 && temp < 32) {
            return "מזג האוויר חם, יש לשתות מים 💦";
        } else {
            return "מזג האוויר חם מאוד! מומלץ להימנע מריצה 🌞🔥";
        }
    }


    // 📌 בקשת הרשאות מיקום
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherForCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 📌 הפעלת התראה יומית
    private void scheduleAlarm() {
        createNotificationChannel();
        String message = "Don't forget about your daily run";

        Intent notificationIntent = new Intent(context, ScheduleBroadCastReceiver.class);
        notificationIntent.putExtra("message", message);
        notificationIntent.setAction("com.example.myproject.NOTIFICATION" + requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, requestCode, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);

        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    // 📌 יצירת ערוץ התראות
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotificationChannel";
            String description = "Channel for scheduled notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notifyChannel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
