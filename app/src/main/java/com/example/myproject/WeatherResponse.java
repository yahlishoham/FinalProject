package com.example.myproject;

import com.google.gson.annotations.SerializedName;

// מחלקה שמייצגת את מבנה התשובה שמתקבלת מה-API של מזג האוויר
public class WeatherResponse {

    // אובייקט בשם "current" שמכיל את כל המידע הרלוונטי למזג האוויר הנוכחי
    @SerializedName("current")
    private CurrentWeather current;

    // פונקציה שמחזירה את האובייקט current
    public CurrentWeather getCurrent() {
        return current;
    }

    // מחלקה פנימית שמכילה את הנתונים של מזג האוויר הנוכחי
    public class CurrentWeather {

        // הטמפרטורה הנוכחית
        @SerializedName("temp")
        private double temp;

        // מערך של פרטי מזג אוויר (תיאור, מצב עננים וכו')
        @SerializedName("weather")
        private Weather[] weather;

        // פונקציה שמחזירה את הטמפרטורה
        public double getTemp() {
            return temp;
        }

        // פונקציה שמחזירה את מערך פרטי מזג האוויר
        public Weather[] getWeather() {
            return weather;
        }
    }

    // מחלקה פנימית שמייצגת את האובייקט שבתוך המערך "weather"
    public class Weather {

        // תיאור מילולי של מזג האוויר (למשל: "clear sky", "rain")
        @SerializedName("description")
        private String description;

        // פונקציה שמחזירה את התיאור
        public String getDescription() {
            return description;
        }
    }
}
