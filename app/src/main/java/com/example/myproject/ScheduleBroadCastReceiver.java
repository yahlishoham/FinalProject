package com.example.myproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class ScheduleBroadCastReceiver extends BroadcastReceiver {

    // משתנה לשמירת ההודעה שהועברה מהאינטנט
    String message = " ";

    // הפעולה מופעלת אוטומטית כאשר מתקבל שידור מהמערכת (כמו מה-AlarmManager)
    @Override
    public void onReceive(Context context, Intent intent) {
        // שולף את ההודעה שהועברה עם האינטנט (מהקוד שקובע את ההתראה)
        message = intent.getStringExtra("message");

        // מציג הודעה קופצת (Toast) למשתמש עם ההודעה
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        // פעולה שתתבצע כשלוחצים על ההתראה – כאן היא מחזירה למסך הבית של האפליקציה
        Intent notificationIntent = new Intent(context, HomePage.class);

        // יוצר PendingIntent להפעלה מאוחרת של HomePage כשמשתמש לוחץ על ההתראה
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE // דרוש באנדרואיד 12+
        );

        // יוצר מזהה ייחודי להתראה לפי הזמן הנוכחי (כדי שכל התראה תהיה נפרדת)
        int notificationId = (int) System.currentTimeMillis();

        // בונה את ההתראה עצמה עם כותרת, טקסט, אייקון ופעולה בלחיצה
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // אייקון קטן בהתראה
                .setContentTitle("Scheduled Notification")        // כותרת ההתראה
                .setContentText(message)                          // תוכן ההתראה
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // רמת חשיבות
                .setContentIntent(contentIntent)                  // מה יקרה כשילחצו
                .setAutoCancel(true);                             // סגירה אוטומטית לאחר לחיצה

        // שולח את ההתראה למערכת שתציג אותה למשתמש
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }
}
