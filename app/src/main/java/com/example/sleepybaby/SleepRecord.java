package com.example.sleepybaby;

import java.util.Date;

public class SleepRecord {
    private int id;
    private int childId;
    private Date startTime;
    private Date endTime;
    private int quality;
    private String notes;

    public SleepRecord() {
        this.id = -1;
        this.childId = -1;
        this.startTime = new Date();
        this.endTime = new Date();
        this.quality = 0;
        this.notes = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getDurationMinutes() {
        return (endTime.getTime() - startTime.getTime()) / (60 * 1000);
    }

    public void setDurationMinutes(int minutes) {
        // Bu metod sadece veritabanı işlemleri için kullanılır
        // Gerçek süre sleepTime ve wakeTime'dan hesaplanır
    }
}
