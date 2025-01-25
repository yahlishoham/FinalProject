package com.example.myproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    // רכיבי ה-UI
    TextView textOpening;
    ImageView imageLogo;
    TextView textFraze;
    Button buttonStart;
    TextView cdtShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // קישור רכיבי ה-UI מה-XML
        textOpening = findViewById(R.id.textOpening);
        imageLogo = findViewById(R.id.imageLogo);
        textFraze = findViewById(R.id.textFraze);
        buttonStart = findViewById(R.id.buttonStart);
        cdtShow = findViewById(R.id.cdtShow);

        // Listener ללחיצה על כפתור "התחלה"
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // מעביר את המשתמש ל-Activity של MainActivity3
                Intent first = new Intent(SplashScreen.this, HomePage.class);
                startActivity(first);
            }
        });
    }
}