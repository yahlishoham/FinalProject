package com.example.myproject;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Calendar;

public class HomePage extends AppCompatActivity {

    private static final String API_KEY = "YOUR_API_KEY"; // הכנס כאן את מפתח ה-API שלך
    private static final double TEL_AVIV_LAT = 32.0853;
    private static final double TEL_AVIV_LON = 34.7818;

    private TextView tvWeather;
    private Retrofit retrofit;
    private Context context;
    private int requestCode = 123;

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

        // שליפת נתוני מזג האוויר עבור תל אביב
        fetchWeatherForTelAviv();
    }

    // 📌 שליחת בקשה ל-API של OpenWeatherMap לקבלת מזג האוויר בתל אביב
    private void fetchWeatherForTelAviv() {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<WeatherResponse> call = apiService.getWeather(TEL_AVIV_LAT, TEL_AVIV_LON, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    String weatherInfo = "Tel Aviv\n" +
                            "Temperature: " + weather.getCurrent().getTemp() + "°C\n" +
                            "Description: " + weather.getCurrent().getWeather()[0].getDescription();
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

    // 📌 הפעלת התראה יומית (כפי שהיה קודם)
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

    // 📌 יצירת ערוץ התראות (כפי שהיה קודם)
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
