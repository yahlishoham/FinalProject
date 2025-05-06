package com.example.myproject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
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

    // 🔑 קבועים (API key, קוד בקשת הרשאות)
    private static final String API_KEY = "a4674faf81cd3ab9005ca15c6b243603";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 2001;

    // 🧱 משתנים עבור ממשק המשתמש
    private TextView tvWeather;
    private ImageView ivLogo;
    private Button buttonStartRun;
    private Button buttonViewHistory;

    // 🛠 אובייקטים לעבודה עם מיקום, אינטרנט והתראות
    private Retrofit retrofit;
    private Context context;
    private int requestCode = 123;
    private FusedLocationProviderClient fusedLocationClient;

    // 🟢 פעולה ראשית שמופעלת כשמסך הבית נוצר
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // קישור רכיבים מה־XML לקוד
        buttonStartRun = findViewById(R.id.buttonStartRun);
        buttonViewHistory = findViewById(R.id.buttonViewHistory);
        tvWeather = findViewById(R.id.tv_weather);
        ivLogo = findViewById(R.id.iv_logo);
        context = this;

        // הגדרת שירות למיקום
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // ✨ סיבוב של הלוגו בלחיצה
        ivLogo.setOnClickListener(v -> {
            v.animate().rotationBy(360).setDuration(900).start();
        });

        // 🔌 חיבור ל־API של מזג האוויר עם Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/3.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 📱 בקשת הרשאה להתראות באנדרואיד 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }

        // ⏰ הגדרת התראת תזכורת יומית
        scheduleAlarm();

        // ▶️ כפתור התחלת ריצה
        buttonStartRun.setOnClickListener(v -> {
            buttonStartRun.setEnabled(false); // מונע לחיצה כפולה
            Intent first = new Intent(HomePage.this, MapScreen.class);
            startActivity(first);
            v.postDelayed(() -> buttonStartRun.setEnabled(true), 1000); // מחזיר את הכפתור לאחר שנייה
        });

        // 📊 כפתור לצפייה בריצות קודמות
        buttonViewHistory.setOnClickListener(v -> {
            buttonViewHistory.setEnabled(false); // מונע לחיצה כפולה
            Intent first = new Intent(HomePage.this, PastRuns.class);
            startActivity(first);
            v.postDelayed(() -> buttonViewHistory.setEnabled(true), 1000);
        });

        // ⛅ שליפת מזג האוויר הנוכחי לפי מיקום
        getWeatherForCurrentLocation();

        // ✨ אנימציות ל־UI
        animateViews();
    }

    // ✨ הפעלת אנימציות על הרכיבים בכניסת המסך
    private void animateViews() {
        fadeInView(tvWeather, 0);
        fadeInView(buttonStartRun, 200);
        fadeInView(buttonViewHistory, 400);
        bounceButton(buttonStartRun, 600);
        bounceButton(buttonViewHistory, 800);
        slideInWeather(tvWeather);
    }

    // 🎨 אנימציית הופעה הדרגתית
    private void fadeInView(View view, long delay) {
        view.setAlpha(0f);
        view.animate().alpha(1f).setStartDelay(delay).setDuration(1000).start();
    }

    // 🎨 אנימציית "ניתור" לכפתורים
    private void bounceButton(View view, long delay) {
        view.setScaleX(0.7f);
        view.setScaleY(0.7f);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(delay)
                .setDuration(800)
                .setInterpolator(new BounceInterpolator())
                .start();
    }

    // 🎨 אנימציית כניסה אנכית לטקסט מזג האוויר
    private void slideInWeather(View view) {
        view.setTranslationY(300f);
        view.animate().translationY(0f).setDuration(1000).start();
    }

    // 📍 בקשת מיקום נוכחי ואז שליפת מזג האוויר
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

    // 🔄 שליחת בקשת מזג אוויר לשרת
    private void fetchWeatherFromAPI(double latitude, double longitude) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<WeatherResponse> call = apiService.getWeather(latitude, longitude, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    double temperature = weather.getCurrent().getTemp();
                    String description = weather.getCurrent().getWeather()[0].getDescription().toLowerCase();
                    String weatherMessage = getWeatherMessage(temperature, description);

                    String weatherInfo = weatherMessage + "\n" +
                            "Temperature: " + temperature + "°C\n" +
                            "Description: " + description;
                    tvWeather.setText(weatherInfo);
                } else {
                    tvWeather.setText("Unable to fetch weather.");
                    Toast.makeText(HomePage.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                tvWeather.setText("Weather unavailable.");
                Toast.makeText(HomePage.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // 📢 תרגום של מזג אוויר לטקסט מוטיבציוני
    private String getWeatherMessage(double temp, String desc) {
        if (desc.contains("rain") || desc.contains("storm") || desc.contains("drizzle")) return "Not ideal for running today, it's rainy ☔";
        if (desc.contains("snow")) return "It's too cold for a run, snowy outside ❄️";
        if (desc.contains("fog") || desc.contains("mist") || desc.contains("haze")) return "Foggy conditions, be careful 🌫️";
        if (temp < 5) return "Very cold weather for running 🥶";
        if (temp < 15) return "Good weather for running, but a bit chilly 🏃‍♂️❄️";
        if (temp < 25) return "Perfect weather for a run! 🏃‍♂️☀️";
        if (temp < 32) return "Warm weather, stay hydrated 💦";
        return "Very hot weather! It's better to avoid running 🌞🔥";
    }

    // 🛡 טיפול בתוצאה של בקשת הרשאות
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherForCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 🌐 בדיקת זמינות אינטרנט
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            } else {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnected();
            }
        }
        return false;
    }

    // ⏰ קביעת תזכורת יומית לריצה באמצעות AlarmManager
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

        // אם השעה כבר עברה – קובע למחר
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

    // 🔔 יצירת ערוץ התראות (נדרש מאנדרואיד 8 ומעלה)
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
