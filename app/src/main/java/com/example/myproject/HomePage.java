package com.example.myproject;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class HomePage extends AppCompatActivity {

    Button buttonStartRun;
    Button buttonViewHistory;
    Context context;
    int requestCode = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        buttonStartRun = findViewById(R.id.buttonStartRun);
        buttonViewHistory = findViewById(R.id.buttonViewHistory);
        context = this;

        // הפעלת התראה יומית
        scheduleAlarm();


        // Listener ללחיצה על כפתור "התחלה"
        buttonStartRun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // מעביר את המשתמש ל-Activity של MainActivity3
                Intent first = new Intent(HomePage.this, MapScreen.class);
                startActivity(first);
            }
        });
        // Listener ללחיצה על כפתור "התחלה"
        buttonViewHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // מעביר את המשתמש ל-Activity של MainActivity3
                Intent first = new Intent(HomePage.this, PastRuns.class);
                startActivity(first);
            }
        });
    }
    // Schedule alarm to send a notification every day at 08:30
    private void scheduleAlarm() {
        createNotificationChannel();

        // Set the message to be sent
        String message = "Don't forget about your daily run";

        // Create intent to be sent to the receiver
        Intent notificationIntent = new Intent(context, ScheduleBroadCastReceiver.class);
        // Pass the message to the intent
        notificationIntent.putExtra("message", message);
        // Use a unique request code to manage the alarm
        notificationIntent.setAction("com.example.myproject.NOTIFICATION" + requestCode);

        // Create a unique PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode, // Use unique request code
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for security
        );

        // Create a calendar instance and set it to 08:30 of the current day
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);

        // If the current time is after 08:30, schedule for the next day
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Create an alarm manager and set the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Set the alarm to trigger daily at the specified time
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, // Repeat daily
                pendingIntent
        );
    }

    // Create a notification channel for the alarm
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotificationChannel";
            String description = "Channel for scheduled notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notifyChannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

