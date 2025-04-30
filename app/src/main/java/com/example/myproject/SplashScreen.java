package com.example.myproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    TextView textOpening;
    ImageView imageLogo;
    TextView textFraze;
    Button buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        textOpening = findViewById(R.id.textOpening);
        imageLogo = findViewById(R.id.imageLogo);
        textFraze = findViewById(R.id.textFraze);
        buttonStart = findViewById(R.id.buttonStart);

        // ✅ אנימציית fade-in משופרת
        fadeInView(textOpening, 0);
        fadeInView(textFraze, 400);

        // ✅ אנימציית bounce לכפתור
        bounceButton(buttonStart, 800);

        // ✅ אנימציית סיבוב ללוגו
        rotateLogo(imageLogo);

        buttonStart.setOnClickListener(v -> {
            Intent first = new Intent(SplashScreen.this, HomePage.class);
            startActivity(first);
        });
    }

    private void fadeInView(View view, long delay) {
        view.setAlpha(0f);
        view.animate()
                .alpha(1f)
                .setDuration(1500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setStartDelay(delay)
                .start();
    }

    private void bounceButton(View view, long delay) {
        view.setScaleX(0.7f);
        view.setScaleY(0.7f);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(900)
                .setStartDelay(delay)
                .setInterpolator(new BounceInterpolator())
                .start();
    }

    private void rotateLogo(ImageView logo) {
        logo.setRotation(0f);
        logo.animate()
                .rotationBy(360f)
                .setDuration(1200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }
}
