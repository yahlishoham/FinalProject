package com.example.myproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HelperDB extends SQLiteOpenHelper {
    // נתוני הטבלה
    public static final String DB_FILE = "Running_Stats.db"; // שם קובץ מסד הנתונים
    public static final String TABLE_RESULT = "Results"; // שם הטבלה במסד הנתונים

    // נתוני העמודות
    public static final String STARTING_POINT_LAT = "StartingPointLatitude"; // קו רוחב נקודת התחלה
    public static final String STARTING_POINT_LON = "StartingPointLongitude"; // קו אורך נקודת התחלה
    public static final String FINISH_POINT_LAT = "FinishPointLatitude"; // קו רוחב נקודת סיום
    public static final String FINISH_POINT_LON = "FinishPointLongitude"; // קו אורך נקודת סיום
    public static final String RUN_TIME = "RunTime";
    public static final String RUN_DISTANCE = "RunDistance";
    public static final String STEP_COUNTER = "StepCounter";

    // קונסטרקטור של מסד הנתונים
    public HelperDB(@Nullable Context context) {
        super(context, DB_FILE, null, 4); // עדכון לגרסת מסד הנתונים ל-4
    }

    // יצירת הטבלה כאשר מסד הנתונים נוצר
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(buildResultsTable()); // קריאה לבניית הטבלה
    }

    // שדרוג מסד הנתונים אם יש שינוי בגרסה
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // עדכון לגרסה 4 - הוספת עמודות לקואורדינטות
            db.execSQL("ALTER TABLE " + TABLE_RESULT + " ADD COLUMN " + STARTING_POINT_LAT + " REAL;");
            db.execSQL("ALTER TABLE " + TABLE_RESULT + " ADD COLUMN " + STARTING_POINT_LON + " REAL;");
            db.execSQL("ALTER TABLE " + TABLE_RESULT + " ADD COLUMN " + FINISH_POINT_LAT + " REAL;");
            db.execSQL("ALTER TABLE " + TABLE_RESULT + " ADD COLUMN " + FINISH_POINT_LON + " REAL;");
        }
    }

    // פעולה שבונה את מבנה הטבלה
    public String buildResultsTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_RESULT + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RUN_TIME + " REAL, " +
                RUN_DISTANCE + " REAL, " +
                STARTING_POINT_LAT + " REAL, " +
                STARTING_POINT_LON + " REAL, " +
                FINISH_POINT_LAT + " REAL, " +
                FINISH_POINT_LON + " REAL, " +
                STEP_COUNTER + " INTEGER DEFAULT 0);";
    }

    // שמירת נתוני ריצה למסד הנתונים
    public void saveRunToDB(RunDetails runDetails) {
        ContentValues cv = new ContentValues();
        cv.put(HelperDB.RUN_TIME, runDetails.getRunTime());
        cv.put(HelperDB.RUN_DISTANCE, runDetails.getRunDistance());
        cv.put(HelperDB.STARTING_POINT_LAT, runDetails.getStartingPointLatitude());
        cv.put(HelperDB.STARTING_POINT_LON, runDetails.getStartingPointLongitude());
        cv.put(HelperDB.FINISH_POINT_LAT, runDetails.getFinishPointLatitude());
        cv.put(HelperDB.FINISH_POINT_LON, runDetails.getFinishPointLongitude());
        cv.put(HelperDB.STEP_COUNTER, runDetails.getStepCounter());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(HelperDB.TABLE_RESULT, null, cv); // הכנסת הנתונים לטבלה
        db.close(); // סגירת החיבור למסד הנתונים
    }

    // שליפת כל נתוני הריצה ממסד הנתונים
    public List<RunDetails> getAllRuns() {
        List<RunDetails> runList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESULT, null);

        if (cursor.moveToFirst()) {
            do {
                String runTime = cursor.getString(cursor.getColumnIndexOrThrow(RUN_TIME));
                double runDistance = cursor.getDouble(cursor.getColumnIndexOrThrow(RUN_DISTANCE));
                double startingPointLatitude = cursor.getDouble(cursor.getColumnIndexOrThrow(STARTING_POINT_LAT));
                double startingPointLongitude = cursor.getDouble(cursor.getColumnIndexOrThrow(STARTING_POINT_LON));
                double finishPointLatitude = cursor.getDouble(cursor.getColumnIndexOrThrow(FINISH_POINT_LAT));
                double finishPointLongitude = cursor.getDouble(cursor.getColumnIndexOrThrow(FINISH_POINT_LON));
                int stepCounter = cursor.getInt(cursor.getColumnIndexOrThrow(STEP_COUNTER));

                runList.add(new RunDetails(runTime, runDistance, startingPointLatitude, startingPointLongitude, finishPointLatitude, finishPointLongitude, stepCounter));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return runList;
    }
}