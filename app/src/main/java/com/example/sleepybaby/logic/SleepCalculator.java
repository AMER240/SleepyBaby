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
    
    // Günlük uyku istatistiklerini hesapla
    public static SleepStatistics calculateDailyStats(List<SleepRecord> records, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startOfDay = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endOfDay = calendar.getTime();
        
        int totalSleepMinutes = 0;
        double totalQuality = 0;
        int recordCount = 0;
        
        for (SleepRecord record : records) {
            if (record.getSleepTime().after(startOfDay) && record.getSleepTime().before(endOfDay)) {
                long duration = record.getDurationInMinutes();
                totalSleepMinutes += (int)duration;
                totalQuality += record.getSleepQuality();
                recordCount++;
            }
        }
        
        double averageQuality = recordCount > 0 ? totalQuality / recordCount : 0;
        return new SleepStatistics(totalSleepMinutes, averageQuality, recordCount);
    }
    
    // Haftalık uyku istatistiklerini hesapla
    public static SleepStatistics calculateWeeklyStats(List<SleepRecord> records, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_WEEK, -calendar.get(Calendar.DAY_OF_WEEK) + 1);
        Date startOfWeek = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_WEEK, 7);
        Date endOfWeek = calendar.getTime();
        
        int totalSleepMinutes = 0;
        double totalQuality = 0;
        int recordCount = 0;
        
        for (SleepRecord record : records) {
            if (record.getSleepTime().after(startOfWeek) && record.getSleepTime().before(endOfWeek)) {
                long duration = record.getDurationInMinutes();
                totalSleepMinutes += (int)duration;
                totalQuality += record.getSleepQuality();
                recordCount++;
            }
        }
        
        double averageQuality = recordCount > 0 ? totalQuality / recordCount : 0;
        return new SleepStatistics(totalSleepMinutes, averageQuality, recordCount);
    }
    
    // Aylık uyku istatistiklerini hesapla
    public static SleepStatistics calculateMonthlyStats(List<SleepRecord> records, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = calendar.getTime();
        
        calendar.add(Calendar.MONTH, 1);
        Date endOfMonth = calendar.getTime();
        
        int totalSleepMinutes = 0;
        double totalQuality = 0;
        int recordCount = 0;
        
        for (SleepRecord record : records) {
            if (record.getSleepTime().after(startOfMonth) && record.getSleepTime().before(endOfMonth)) {
                long duration = record.getDurationInMinutes();
                totalSleepMinutes += (int)duration;
                totalQuality += record.getSleepQuality();
                recordCount++;
            }
        }
        
        double averageQuality = recordCount > 0 ? totalQuality / recordCount : 0;
        return new SleepStatistics(totalSleepMinutes, averageQuality, recordCount);
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