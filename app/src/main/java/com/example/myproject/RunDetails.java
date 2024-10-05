package com.example.myproject;

public class RunDetails {
    private String runTime;
    private double runDistance;
    private double startingPoint;
    private double finishPoint;
    private int userScore;

    // Constructor - All fields
    public RunDetails(String runTime, double runDistance, double startingPoint, double finishPoint, int userScore) {
        this.runTime = runTime;
        this.runDistance = runDistance;
        this.startingPoint = startingPoint;
        this.finishPoint = finishPoint;
        this.userScore = userScore;
    }

    // Getters and Setters
    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public double getRunDistance() {
        return runDistance;
    }

    public void setRunDistance(double runDistance) {
        this.runDistance = runDistance;
    }

    public double getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(double startingPoint) {
        this.startingPoint = startingPoint;
    }

    public double getFinishPoint() {
        return finishPoint;
    }

    public void setFinishPoint(double finishPoint) {
        this.finishPoint = finishPoint;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    // toString method for debugging or displaying
    @Override
    public String toString() {
        return "RunDetails{" +
                "runTime='" + runTime + '\'' +
                ", runDistance=" + runDistance +
                ", startingPoint=" + startingPoint +
                ", finishPoint=" + finishPoint +
                ", userScore=" + userScore +
                '}';
    }
}
