package com.example.sleepybaby;

import java.util.List;

public class UykuHesaplayicisi {

    private List<UykuGirisi> uykuGirisleri;

    public UykuHesaplayicisi(List<UykuGirisi> uykuGirisleri) {
        this.uykuGirisleri = uykuGirisleri;
    }

    // حساب إجمالي ساعات النوم خلال فترة معينة
    public float toplamUykuSaatlariHesapla(long baslamaZamani, long bitisZamani) {
        float toplamSaat = 0;

        for (UykuGirisi entry : uykuGirisleri) {
            // التحقق من أن هذه الفترة ضمن النطاق المطلوب
            if (entry.getUykuZamaniMillis() >= baslamaZamani && entry.getUyanmaZamaniMillis() <= bitisZamani) {
                toplamSaat += entry.getSleepDurationHours();
            }
        }

        return toplamSaat;
    }

    // حساب متوسط ساعات النوم اليومية خلال فترة معينة
    public float ortalamaGunlukUykuyuHesapla(long baslamaZamani, long bitisZamani) {
        float toplamSaat = toplamUykuSaatlariHesapla(baslamaZamani, bitisZamani);

        // حساب عدد الأيام
        long gunlerinFarki = (bitisZamani - baslamaZamani) / (1000 * 60 * 60 * 24);
        // تجنب القسمة على صفر
        if (gunlerinFarki == 0) gunlerinFarki = 1;

        return toplamSaat / gunlerinFarki;
    }

    // التحقق مما إذا كان الطفل ينام بشكل كافٍ بناءً على عمره
    public boolean yeterinceUyuyorMu(int aylarHalindeYas, float ortalamaGunlukUykuSaatleri) {
        // مثال بسيط للتوصيات - يمكن تحسينه بناءً على توصيات طبية
        if (aylarHalindeYas <= 3) { // 0-3 أشهر
            return ortalamaGunlukUykuSaatleri >= 14;
        } else if (aylarHalindeYas <= 12) { // 4-12 شهر
            return ortalamaGunlukUykuSaatleri >= 12;
        } else if (aylarHalindeYas <= 36) { // 1-3 سنوات
            return ortalamaGunlukUykuSaatleri >= 11;
        } else if (aylarHalindeYas <= 60) { // 3-5 سنوات
            return ortalamaGunlukUykuSaatleri >= 10;
        } else { // أكبر من 5 سنوات
            return ortalamaGunlukUykuSaatleri >= 9;
        }
    }
}