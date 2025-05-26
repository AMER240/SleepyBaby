package com.example.sleepybaby;

import java.util.Date;

public class SleepStatistics {
    private int totalSleepMinutes;
    private double averageSleepQuality;
    private int totalRecords;

    public SleepStatistics(int totalSleepMinutes, double averageSleepQuality, int totalRecords) {
        this.totalSleepMinutes = totalSleepMinutes;
        this.averageSleepQuality = averageSleepQuality;
        this.totalRecords = totalRecords;
    }

    public int getTotalSleepMinutes() {
        return totalSleepMinutes;
    }

    public double getAverageSleepQuality() {
        return averageSleepQuality;
    }

    public int getTotalRecords() {
        return totalRecords;
    }
} 