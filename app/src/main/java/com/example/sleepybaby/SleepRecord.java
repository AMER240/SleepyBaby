package com.example.sleepybaby;

import java.util.Date;
import java.util.Calendar;

public class SleepRecord {
    private int id;
    private int childId;
    private Date sleepTime;
    private int sleepHour;
    private int sleepMinute;
    private int wakeHour;
    private int wakeMinute;
    private int quality;
    private String notes;

    public SleepRecord() {
        this.sleepHour = -1;
        this.sleepMinute = -1;
        this.wakeHour = -1;
        this.wakeMinute = -1;
        this.quality = 3; // Varsayılan orta kalite
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

    public Date getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Date sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getSleepHour() {
        return sleepHour;
    }

    public void setSleepHour(int sleepHour) {
        this.sleepHour = sleepHour;
    }

    public int getSleepMinute() {
        return sleepMinute;
    }

    public void setSleepMinute(int sleepMinute) {
        this.sleepMinute = sleepMinute;
    }

    public int getWakeHour() {
        return wakeHour;
    }

    public void setWakeHour(int wakeHour) {
        this.wakeHour = wakeHour;
    }

    public int getWakeMinute() {
        return wakeMinute;
    }

    public void setWakeMinute(int wakeMinute) {
        this.wakeMinute = wakeMinute;
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

    public Date getStartTime() {
        if (sleepTime == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sleepTime);
        calendar.set(Calendar.HOUR_OF_DAY, sleepHour);
        calendar.set(Calendar.MINUTE, sleepMinute);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public Date getEndTime() {
        if (sleepTime == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sleepTime);
        calendar.set(Calendar.HOUR_OF_DAY, wakeHour);
        calendar.set(Calendar.MINUTE, wakeMinute);
        calendar.set(Calendar.SECOND, 0);
        
        // Eğer uyanma saati uyku saatinden küçükse, ertesi güne geçmiş demektir
        if (wakeHour < sleepHour || (wakeHour == sleepHour && wakeMinute < sleepMinute)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        return calendar.getTime();
    }

    public long getDurationMinutes() {
        Date start = getStartTime();
        Date end = getEndTime();
        if (start == null || end == null) return 0;
        return (end.getTime() - start.getTime()) / (60 * 1000);
    }

    public void setDurationMinutes(int minutes) {
        // Bu metod sadece veritabanı işlemleri için kullanılır
        // Gerçek süre sleepTime ve wakeTime'dan hesaplanır
    }
}
