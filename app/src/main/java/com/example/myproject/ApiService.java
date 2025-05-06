package com.example.myproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// ממשק שמשמש את Retrofit כדי לבצע קריאה ל-API של OpenWeather
public interface ApiService {

    // מבצע קריאה לכתובת: https://api.openweathermap.org/data/3.0/onecall
    // הנתונים מוחזרים כאובייקט מסוג WeatherResponse

    @GET("onecall")
    Call<WeatherResponse> getWeather(
            @Query("lat") double latitude,     // קו רוחב (Latitude)
            @Query("lon") double longitude,    // קו אורך (Longitude)
            @Query("appid") String apiKey,     // מפתח ה-API
            @Query("units") String units       // יחידות מידה (למשל "metric" בשביל צלזיוס)
    );
}
