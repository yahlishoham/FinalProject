package com.example.myproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HelperDB extends SQLiteOpenHelper {
    // נתוני הטבלה
    public static final String DB_FILE = "Running_Stats.db"; // שם קובץ מסד הנתונים
    public static final String TABLE_RESULT = "Results"; // שם הטבלה במסד הנתונים

    // נתוני העמודות
    public static final String RUN_TIME = "RunTime";
    public static final String RUN_DISTANCE = "RunDistance";
    public static final String STARTING_POINT = "StartingPoint";
    public static final String FINISH_POINT = "FinishPoint";
    public static final String RUN_LEVEL = "UserScore";

    // קונסטרקטור של מסד הנתונים
    public HelperDB(@Nullable Context context) {
        super(context, DB_FILE, null, 1); // אתחול מסד הנתונים
    }

    // יצירת הטבלה כאשר מסד הנתונים נוצר
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(buildResultsTable()); // קריאה לבניית הטבלה
    }

    // שדרוג מסד הנתונים אם יש שינוי בגרסה
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULT); // מחיקת טבלה ישנה
            onCreate(db); // יצירת הטבלה מחדש
        }
    }

    // פעולה שבונה את מבנה הטבלה
    public String buildResultsTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_RESULT + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RUN_TIME + " REAL, " +
                RUN_DISTANCE + " REAL, " +
                STARTING_POINT + " REAL, " +
                FINISH_POINT + " REAL, " +
                RUN_LEVEL + " INTEGER);";
    }

    // שמירת נתוני ריצה למסד הנתונים
    public void saveRunToDB(RunDetails runDetails) {
        ContentValues cv = new ContentValues();
        cv.put(HelperDB.RUN_TIME, runDetails.getRunTime());
        cv.put(HelperDB.RUN_DISTANCE, runDetails.getRunDistance());
        cv.put(HelperDB.STARTING_POINT, runDetails.getStartingPoint());
        cv.put(HelperDB.FINISH_POINT, runDetails.getFinishPoint());
        cv.put(HelperDB.RUN_LEVEL, runDetails.getUserScore());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(HelperDB.TABLE_RESULT, null, cv); // הכנסת הנתונים לטבלה
        db.close(); // סגירת החיבור למסד הנתונים
    }
}