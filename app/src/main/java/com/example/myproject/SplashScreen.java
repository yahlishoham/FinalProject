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

    // הצהרת משתנים עבור רכיבי התצוגה
    TextView textOpening;
    ImageView imageLogo;
    TextView textFraze;
    Button buttonStart;

    // פעולה שמופעלת כשהמסך נוצר
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // הגדרת עיצוב המסך מתוך קובץ XML
        setContentView(R.layout.splash_screen);

        // קישור בין הרכיבים בקובץ העיצוב לקוד
        textOpening = findViewById(R.id.textOpening);
        imageLogo = findViewById(R.id.imageLogo);
        textFraze = findViewById(R.id.textFraze);
        buttonStart = findViewById(R.id.buttonStart);

        // הפעלת אנימציית הופעה הדרגתית על הכותרת הראשית
        fadeInView(textOpening, 0);

        // הפעלת אנימציית הופעה הדרגתית על המשפט המוטיבציוני
        fadeInView(textFraze, 400);

        // הפעלת אנימציית קפיצה על כפתור ההתחלה
        bounceButton(buttonStart, 800);

        // הפעלת אנימציית סיבוב על הלוגו
        rotateLogo(imageLogo);

        // כאשר לוחצים על כפתור ההתחלה – מעבר למסך הראשי (HomePage)
        buttonStart.setOnClickListener(v -> {
            Intent first = new Intent(SplashScreen.this, HomePage.class);
            startActivity(first);
        });
    }

    // פעולה שמציגה View בהדרגתיות עם שקיפות
    private void fadeInView(View view, long delay) {
        view.setAlpha(0f); // מתחיל בלתי נראה
        view.animate()
                .alpha(1f) // הופך לגלוי
                .setDuration(1500) // משך האנימציה: 1.5 שניות
                .setInterpolator(new AccelerateDecelerateInterpolator()) // התחלה וסיום איטיים, אמצע מהיר
                .setStartDelay(delay) // התחלה לאחר דיליי
                .start();
    }

    // פעולה שמפעילה אנימציית "ניתור" על כפתור
    private void bounceButton(View view, long delay) {
        view.setScaleX(0.7f); // מתחיל בגודל מוקטן
        view.setScaleY(0.7f);
        view.animate()
                .scaleX(1f) // חוזר לגודל רגיל
                .scaleY(1f)
                .setDuration(900) // משך האנימציה: 0.9 שניות
                .setStartDelay(delay) // התחלה לאחר דיליי
                .setInterpolator(new BounceInterpolator()) // אפקט קפיצי
                .start();
    }

    // פעולה שמסובבת את הלוגו סיבוב שלם
    private void rotateLogo(ImageView logo) {
        logo.setRotation(0f); // התחלה מ-0 מעלות
        logo.animate()
                .rotationBy(360f) // סיבוב של 360 מעלות
                .setDuration(1200) // משך הסיבוב: 1.2 שניות
                .setInterpolator(new AccelerateDecelerateInterpolator()) // סיבוב חלק
                .start();
    }
}
