package com.example.sleepybaby;

import java.util.Date;

public class SleepRecord {
    private int id;
    private int childId;
    private Date sleepTime;
    private Date wakeTime;
    private int sleepQuality;
    private String notes;

    public SleepRecord() {
        // Boş constructor
    }

    public SleepRecord(int id, int childId, Date sleepTime, Date wakeTime, int sleepQuality) {
        this.id = id;
        this.childId = childId;
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.sleepQuality = sleepQuality;
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
        return sleepTime;
    }

    public void setStartTime(Date sleepTime) {
        this.sleepTime = sleepTime;
    }

    public Date getEndTime() {
        return wakeTime;
    }

    public void setEndTime(Date wakeTime) {
        this.wakeTime = wakeTime;
    }

    public int getQuality() {
        return sleepQuality;
    }

    public void setQuality(int sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Uyku süresini dakika cinsinden hesapla
    public long getDurationMinutes() {
        if (sleepTime == null || wakeTime == null) return 0;
        return (wakeTime.getTime() - sleepTime.getTime()) / (60 * 1000);
    }

    public void setDurationMinutes(int minutes) {
        // Bu metod sadece veritabanı işlemleri için kullanılır
        // Gerçek süre sleepTime ve wakeTime'dan hesaplanır
    }
}
