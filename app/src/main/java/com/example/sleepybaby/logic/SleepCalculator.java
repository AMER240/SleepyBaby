package com.example.sleepybaby.logic;

import com.example.sleepybaby.SleepRecord;
import com.example.sleepybaby.SleepStatistics;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SleepCalculator {
    
    // Yaşa göre önerilen uyku saatleri (saat cinsinden)
    public static class RecommendedSleepHours {
        public final int minHours;
        public final int maxHours;
        public final int recommendedHours;
        
        public RecommendedSleepHours(int minHours, int maxHours, int recommendedHours) {
            this.minHours = minHours;
            this.maxHours = maxHours;
            this.recommendedHours = recommendedHours;
        }
    }
    
    // Yaşa göre önerilen uyku saatlerini döndür
    public static RecommendedSleepHours getRecommendedSleepHours(int age) {
        if (age < 1) { // 0-12 ay
            return new RecommendedSleepHours(12, 15, 14);
        } else if (age < 2) { // 1-2 yaş
            return new RecommendedSleepHours(11, 14, 12);
        } else if (age < 3) { // 2-3 yaş
            return new RecommendedSleepHours(10, 13, 11);
        } else if (age < 5) { // 3-5 yaş
            return new RecommendedSleepHours(10, 13, 11);
        } else if (age < 13) { // 6-12 yaş
            return new RecommendedSleepHours(9, 12, 10);
        } else { // 13+ yaş
            return new RecommendedSleepHours(8, 10, 9);
        }
    }
    
    // Uyku düzeni önerisi oluştur
    public static String generateSleepScheduleRecommendation(int age, List<SleepRecord> recentRecords) {
        RecommendedSleepHours recommended = getRecommendedSleepHours(age);
        StringBuilder recommendation = new StringBuilder();
        
        // Ortalama uyku süresini hesapla
        long totalSleepMinutes = 0;
        for (SleepRecord record : recentRecords) {
            totalSleepMinutes += record.getDurationInMinutes();
        }
        double averageSleepHours = recentRecords.isEmpty() ? 0 : 
            (double) totalSleepMinutes / (60 * recentRecords.size());
        
        recommendation.append("Yaşınıza göre önerilen uyku süresi: ")
            .append(recommended.recommendedHours)
            .append(" saat\n")
            .append("Minimum uyku süresi: ")
            .append(recommended.minHours)
            .append(" saat\n")
            .append("Maksimum uyku süresi: ")
            .append(recommended.maxHours)
            .append(" saat\n\n");
        
        if (recentRecords.isEmpty()) {
            recommendation.append("Henüz uyku kaydı bulunmuyor.");
        } else {
            recommendation.append("Son kayıtlara göre ortalama uyku süreniz: ")
                .append(String.format("%.1f", averageSleepHours))
                .append(" saat\n\n");
            
            if (averageSleepHours < recommended.minHours) {
                recommendation.append("Uyku süreniz önerilen minimum sürenin altında. ")
                    .append("Daha erken yatmanızı öneririz.");
            } else if (averageSleepHours > recommended.maxHours) {
                recommendation.append("Uyku süreniz önerilen maksimum sürenin üzerinde. ")
                    .append("Daha erken kalkmanızı öneririz.");
            } else {
                recommendation.append("Uyku süreniz önerilen aralıkta. ")
                    .append("Mevcut uyku düzeninizi koruyabilirsiniz.");
            }
        }
        
        return recommendation.toString();
    }
    
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