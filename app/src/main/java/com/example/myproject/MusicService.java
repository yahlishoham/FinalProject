package com.example.myproject;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

// שירות רקע שמנגן מוזיקה באפליקציה
public class MusicService extends Service {

    private MediaPlayer mediaPlayer; // מנגן המוזיקה
    public static boolean isPaused = false; // משתנה סטטי שמציין האם המוזיקה במצב "הפסקה"

    // מופעל פעם אחת כשיוצרים את השירות
    @Override
    public void onCreate() {
        super.onCreate();

        // טוען את קובץ המוזיקה ומגדיר אותו כלולאה
        mediaPlayer = MediaPlayer.create(this, R.raw.mymusic);
        mediaPlayer.setLooping(true);
    }

    // מופעל כל פעם ששירות מתחיל (כולל כל Intent חדש)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // אם נוצר מחדש אחרי השמדה - נטען שוב את המוזיקה
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.mymusic);
            mediaPlayer.setLooping(true);
        }

        // בודק אם הועברה פעולה (pause / resume)
        String action = intent.getStringExtra("action");
        if ("pause".equals(action)) {
            pauseMusic();  // עוצר את הנגינה
        } else if ("resume".equals(action)) {
            resumeMusic(); // ממשיך לנגן
        } else {
            // אם אין פעולה – ננגן רגיל
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPaused = false;
            }
        }

        // מבטיח שהשירות ימשיך לפעול גם אם האפליקציה תיסגר (START_STICKY)
        return START_STICKY;
    }

    // פעולה פנימית להפסקת המוזיקה
    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }

    // פעולה פנימית לחידוש הנגינה
    private void resumeMusic() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer.start();
            isPaused = false;
        }
    }

    // מתבצעת כשהשירות נהרס (לדוג' אם המשתמש עצר אותו)
    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release(); // משחרר את המשאבים של הנגן
            mediaPlayer = null;
        }
        isPaused = false;
        super.onDestroy();
    }

    // אין צורך בקישור (binding) לשירות – לכן מחזיר null
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
