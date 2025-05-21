package com.example.sleepybaby.logic;

import com.example.sleepybaby.SleepRecord;
import com.example.sleepybaby.SleepStatistics;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SleepCalculator {
    
    // Günlük istatistikleri hesapla
    public static SleepStatistics calculateDailyStatistics(long childId, List<SleepRecord> records, Date date) {
        SleepStatistics stats = new SleepStatistics(childId, date);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startOfDay = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endOfDay = cal.getTime();
        
        for (SleepRecord record : records) {
            if (record.getStartTime().after(startOfDay) && record.getStartTime().before(endOfDay)) {
                stats.addSleepRecord(record);
            }
        }
        
        return stats;
    }
    
    // Haftalık istatistikleri hesapla
    public static SleepStatistics calculateWeeklyStatistics(long childId, List<SleepRecord> records, Date date) {
        SleepStatistics stats = new SleepStatistics(childId, date);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DAY_OF_WEEK, -cal.get(Calendar.DAY_OF_WEEK) + 1); // Haftanın başlangıcı
        Date startOfWeek = cal.getTime();
        
        cal.add(Calendar.DAY_OF_WEEK, 7);
        Date endOfWeek = cal.getTime();
        
        for (SleepRecord record : records) {
            if (record.getStartTime().after(startOfWeek) && record.getStartTime().before(endOfWeek)) {
                stats.addSleepRecord(record);
            }
        }
        
        return stats;
    }
    
    // Aylık istatistikleri hesapla
    public static SleepStatistics calculateMonthlyStatistics(long childId, List<SleepRecord> records, Date date) {
        SleepStatistics stats = new SleepStatistics(childId, date);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1); // Ayın başlangıcı
        Date startOfMonth = cal.getTime();
        
        cal.add(Calendar.MONTH, 1);
        Date endOfMonth = cal.getTime();
        
        for (SleepRecord record : records) {
            if (record.getStartTime().after(startOfMonth) && record.getStartTime().before(endOfMonth)) {
                stats.addSleepRecord(record);
            }
        }
        
        return stats;
    }
    
    // Uyku süresini formatla (saat:dakika)
    public static String formatDuration(long minutes) {
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        return String.format("%02d:%02d", hours, remainingMinutes);
    }
    
    // Uyku kalitesini yıldız olarak formatla
    public static String formatQuality(int quality) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < quality; i++) {
            stars.append("★");
        }
        for (int i = quality; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }
} 