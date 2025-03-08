package com.example.myproject;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("current")
    private CurrentWeather current;

    public CurrentWeather getCurrent() {
        return current;
    }

    public class CurrentWeather {
        @SerializedName("temp")
        private double temp;

        @SerializedName("weather")
        private Weather[] weather;

        public double getTemp() {
            return temp;
        }

        public Weather[] getWeather() {
            return weather;
        }
    }

    public class Weather {
        @SerializedName("description")
        private String description;

        public String getDescription() {
            return description;
        }
    }
}
