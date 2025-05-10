package com.sleepybaby.domain.usecase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UykuTavsiyeMotoru {

    // Çocuğun yaşına göre önerilen uyku saatleri
    public int getirOnerilenUykuSaati(int yasAyCinsinden) {
        if (yasAyCinsinden <= 3) { // 0-3 Ay
            return 16;
        } else if (yasAyCinsinden <= 12) { // 4-12 Ay
            return 14;
        } else if (yasAyCinsinden <= 36) { // 1-3 Yıl
            return 12;
        } else if (yasAyCinsinden <= 60) { // 3-5 Yıl
            return 11;
        } else { // 5 yıldan büyük
            return 10;
        }
    }

    // Hedef uyanma zamanı ve çocuğun yaşı temel alınarak önerilen uyku zamanı
    public long getirOnerilenUykuZamani(long hedefUyanmaZamani, int yasAyCinsinden) {
        // Önerilen uyku süresini milisaniye cinsinden hesapla
        long onerilenUykuSuresiMs = getirOnerilenUykuSaati(yasAyCinsinden) * 60 * 60 * 1000;

        // Uyku zamanı = Uyanma zamanı - Uyku süresi
        return hedefUyanmaZamani - onerilenUykuSuresiMs;
    }

    // Haftalık bir uyku çizelgesi oluştur
    public List<UykuCizelgesiOgesi> haftalikUykuCizelgesiOlustur(int yasAyCinsinden, int tercihEdilenUyanmaSaati) {
        List<UykuCizelgesiOgesi> cizelge = new ArrayList<>();

        // Tercih edilen uyanma saatini dakikadan milisaniyeye çevir
        long uyanmaZamaniMs = tercihEdilenUyanmaSaati * 60 * 1000;

        // Önerilen uyku zamanını al
        long uykuZamaniMs = getirOnerilenUykuZamani(uyanmaZamaniMs, yasAyCinsinden);

        // 7 günlük bir çizelge oluştur
        Calendar takvim = Calendar.getInstance();
        takvim.set(Calendar.HOUR_OF_DAY, 0);
        takvim.set(Calendar.MINUTE, 0);
        takvim.set(Calendar.SECOND, 0);
        takvim.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < 7; i++) {
            UykuCizelgesiOgesi ogeler = new UykuCizelgesiOgesi();
            ogeler.setGunAdi(takvim.get(Calendar.DAY_OF_WEEK));
            ogeler.setUykuZamani(uykuZamaniMs);
            ogeler.setUyanmaZamani(uyanmaZamaniMs);

            cizelge.add(ogeler);

            // Bir sonraki güne geç
            takvim.add(Calendar.DAY_OF_WEEK, 1);
        }

        return cizelge;
    }

    // Uyku çizelgesi öğeleri için yardımcı sınıf
    public static class UykuCizelgesiOgesi {
        private int gunAdi;
        private long uykuZamani;
        private long uyanmaZamani;

        // Getter ve Setter metodları
        public int getGunAdi() {
            return gunAdi;
        }

        public void setGunAdi(int gunAdi) {
            this.gunAdi = gunAdi;
        }

        public long getUykuZamani() {
            return uykuZamani;
        }

        public void setUykuZamani(long uykuZamani) {
            this.uykuZamani = uykuZamani;
        }

        public long getUyanmaZamani() {
            return uyanmaZamani;
        }

        public void setUyanmaZamani(long uyanmaZamani) {
            this.uyanmaZamani = uyanmaZamani;
        }
    }
} 