package com.example.sleepybaby;

import java.util.Date;

public class SleepStatistics {
    private long childId;
    private Date date;
    private long totalSleepMinutes;
    private int numberOfSleeps;
    private double averageSleepQuality;
    private long longestSleepMinutes;
    private long shortestSleepMinutes;
    private int totalRecords;

    public SleepStatistics(long childId, Date date) {
        this.childId = childId;
        this.date = date;
        this.totalSleepMinutes = 0;
        this.numberOfSleeps = 0;
        this.averageSleepQuality = 0;
        this.longestSleepMinutes = 0;
        this.shortestSleepMinutes = Long.MAX_VALUE;
        this.totalRecords = 0;
    }

    // Getters and Setters
    public long getChildId() { return childId; }
    public void setChildId(long childId) { this.childId = childId; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public long getTotalSleepMinutes() { return totalSleepMinutes; }
    public void setTotalSleepMinutes(long totalSleepMinutes) { this.totalSleepMinutes = totalSleepMinutes; }
    
    public int getNumberOfSleeps() { return numberOfSleeps; }
    public void setNumberOfSleeps(int numberOfSleeps) { this.numberOfSleeps = numberOfSleeps; }
    
    public double getAverageSleepQuality() { return averageSleepQuality; }
    public void setAverageSleepQuality(double averageSleepQuality) { this.averageSleepQuality = averageSleepQuality; }
    
    public long getLongestSleepMinutes() { return longestSleepMinutes; }
    public void setLongestSleepMinutes(long longestSleepMinutes) { this.longestSleepMinutes = longestSleepMinutes; }
    
    public long getShortestSleepMinutes() { return shortestSleepMinutes; }
    public void setShortestSleepMinutes(long shortestSleepMinutes) { this.shortestSleepMinutes = shortestSleepMinutes; }

    public int getTotalRecords() {
        return totalRecords;
    }

    // Uyku kaydı ekleme ve istatistikleri güncelleme
    public void addSleepRecord(SleepRecord record) {
        long duration = record.getDurationInMinutes();
        totalSleepMinutes += duration;
        numberOfSleeps++;
        
        // En uzun ve en kısa uyku sürelerini güncelle
        if (duration > longestSleepMinutes) {
            longestSleepMinutes = duration;
        }
        if (duration < shortestSleepMinutes) {
            shortestSleepMinutes = duration;
        }
        
        // Ortalama uyku kalitesini güncelle
        averageSleepQuality = ((averageSleepQuality * (numberOfSleeps - 1)) + record.getQuality()) / numberOfSleeps;
        totalRecords++;
    }

    // Ortalama uyku süresini hesapla
    public double getAverageSleepMinutes() {
        return numberOfSleeps > 0 ? (double) totalSleepMinutes / numberOfSleeps : 0;
    }
} 