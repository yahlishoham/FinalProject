package com.example.myproject;

public class RunDetails {
    private String runTime;
    private double runDistance;
    private double startingPointLatitude;
    private double startingPointLongitude;
    private double finishPointLatitude;
    private double finishPointLongitude;
    private int stepCounter;

    public RunDetails(String runTime, double runDistance, double startingPointLatitude, double startingPointLongitude, double finishPointLatitude, double finishPointLongitude, int stepCounter) {
        this.runTime = runTime;
        this.runDistance = runDistance;
        this.startingPointLatitude = startingPointLatitude;
        this.startingPointLongitude = startingPointLongitude;
        this.finishPointLatitude = finishPointLatitude;
        this.finishPointLongitude = finishPointLongitude;
        this.stepCounter = stepCounter;
    }

    // Getters
    public String getRunTime() {
        return runTime;
    }

    public double getRunDistance() {
        return runDistance;
    }

    public double getStartingPointLatitude() {
        return startingPointLatitude;
    }

    public double getStartingPointLongitude() {
        return startingPointLongitude;
    }

    public double getFinishPointLatitude() {
        return finishPointLatitude;
    }

    public double getFinishPointLongitude() {
        return finishPointLongitude;
    }

    public int getStepCounter() {
        return stepCounter;
    }

    // Setters
    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public void setRunDistance(double runDistance) {
        this.runDistance = runDistance;
    }

    public void setStartingPointLatitude(double startingPointLatitude) {
        this.startingPointLatitude = startingPointLatitude;
    }

    public void setStartingPointLongitude(double startingPointLongitude) {
        this.startingPointLongitude = startingPointLongitude;
    }

    public void setFinishPointLatitude(double finishPointLatitude) {
        this.finishPointLatitude = finishPointLatitude;
    }

    public void setFinishPointLongitude(double finishPointLongitude) {
        this.finishPointLongitude = finishPointLongitude;
    }

    public void setStepCounter(int stepCounter) {
        this.stepCounter = stepCounter;
    }
}