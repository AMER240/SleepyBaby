package com.example.sleepybaby;

import java.util.Date;

public class SleepRecord {
    private long id;
    private long childId;
    private Date startTime;
    private Date endTime;
    private int quality; // 1-5 arası uyku kalitesi
    private String notes;

    public SleepRecord(long childId, Date startTime, Date endTime, int quality, String notes) {
        this.childId = childId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.quality = quality;
        this.notes = notes;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public long getChildId() { return childId; }
    public void setChildId(long childId) { this.childId = childId; }
    
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    
    public int getQuality() { return quality; }
    public void setQuality(int quality) { this.quality = quality; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Uyku süresini dakika cinsinden hesapla
    public long getDurationInMinutes() {
        if (startTime == null || endTime == null) return 0;
        return (endTime.getTime() - startTime.getTime()) / (60 * 1000);
    }
} 