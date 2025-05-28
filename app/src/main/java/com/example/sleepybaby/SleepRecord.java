package com.example.sleepybaby;

import java.util.Date;

public class SleepRecord {
    private int id;
    private int childId;
    private Date sleepTime;
    private Date wakeTime;
    private int sleepQuality;

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

    public int getChildId() {
        return childId;
    }

    public Date getSleepTime() {
        return sleepTime;
    }

    public Date getWakeTime() {
        return wakeTime;
    }

    public int getSleepQuality() {
        return sleepQuality;
    }

    // Uyku süresini dakika cinsinden hesapla
    public long getDurationInMinutes() {
        if (sleepTime == null || wakeTime == null) return 0;
        return (wakeTime.getTime() - sleepTime.getTime()) / (60 * 1000);
    }
}
