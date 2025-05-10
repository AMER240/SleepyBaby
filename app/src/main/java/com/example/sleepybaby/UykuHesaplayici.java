package com.example.sleepybaby;

import java.util.List;

public class UykuHesaplayici {

    private List<UykuKaydi> uykuKayitlari;

    public UykuHesaplayici(List<UykuKaydi> uykuKayitlari) {
        this.uykuKayitlari = uykuKayitlari;
    }

    // Belirtilen zaman aralığında toplam uyku süresini saat cinsinden hesapla
    public float toplamUykuSuresiniHesapla(long baslangicZamani, long bitisZamani) {
        float toplamSaat = 0f;

        for (UykuKaydi kayit : uykuKayitlari) {
            // Kayıt zamanı verilen aralığa ait mi kontrol et
            if (kayit.getUykuZamani() >= baslangicZamani && kayit.getUyanmaZamani() <= bitisZamani) {
                toplamSaat += kayit.getUykuSuresiSaat();
            }
        }

        return toplamSaat;
    }

    // Belirli bir zaman aralığında günlük ortalama uyku süresini hesapla
    public float gunlukOrtalamaUykuSuresiHesapla(long baslangicZamani, long bitisZamani) {
        float toplamSaat = toplamUykuSuresiniHesapla(baslangicZamani, bitisZamani);

        // Geçen gün sayısını hesapla
        long gecenGunSayisi = (bitisZamani - baslangicZamani) / (1000 * 60 * 60 * 24);
        // Sıfıra bölme hatasını önle
        if (gecenGunSayisi == 0) {
            gecenGunSayisi = 1;
        }

        return toplamSaat / gecenGunSayisi;
    }

    // Çocuğun yaşına göre yeterli miktarda uyuduğunu kontrol et
    public boolean yeterliMiktardaUyuyorMu(int yasAyCinsinden, float ortalamaGunlukUykuSaati) {
        // Basit bir örnek öneri tablosu - tıbbi önerilere göre güncellenebilir
        if (yasAyCinsinden <= 3) { // 0-3 Ay
            return ortalamaGunlukUykuSaati >= 14;
        } else if (yasAyCinsinden <= 12) { // 4-12 Ay
            return ortalamaGunlukUykuSaati >= 12;
        } else if (yasAyCinsinden <= 36) { // 1-3 Yıl
            return ortalamaGunlukUykuSaati >= 11;
        } else if (yasAyCinsinden <= 60) { // 3-5 Yıl
            return ortalamaGunlukUykuSaati >= 10;
        } else { // 5 yıldan büyük
            return ortalamaGunlukUykuSaati >= 9;
        }
    }
}