package com.sleepybaby.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class KullaniciTercihYoneticisi {

    // SharedPreferences dosya adı
    private static final String TERCİH_DOSYASI_ADI = "SleepyBabyTercihleri";

    // Anahtarlar
    private static final String ANAHTAR_BEBEK_ADI = "bebek_adi";
    private static final String ANAHTAR_BEBEK_YASI_AY = "bebek_yasi_ay";
    private static final String ANAHTAR_TERCİH_EDİLEN_UYKU_ZAMANI = "tercih_edilen_uyku_zamani";
    private static final String ANAHTAR_TERCİH_EDİLEN_UYANMA_ZAMANI = "tercih_edilen_uyanma_zamani";
    private static final String ANAHTAR_BILDİRİM_ETKIN = "bildirim_etkin";

    private SharedPreferences tercihler;

    public KullaniciTercihYoneticisi(Context context) {
        tercihler = context.getSharedPreferences(TERCİH_DOSYASI_ADI, Context.MODE_PRIVATE);
    }

    // 👶 Bebek adını kaydet
    public void bebegiKaydet(String ad) {
        tercihler.edit().putString(ANAHTAR_BEBEK_ADI, ad).apply();
    }

    // 👶 Bebek adını al
    public String getBebekAdi() {
        return tercihler.getString(ANAHTAR_BEBEK_ADI, "");
    }

    // 📆 Bebek yaşını ay olarak kaydet
    public void bebeginYasiniAyOlarakKaydet(int yasAyCinsinden) {
        tercihler.edit().putInt(ANAHTAR_BEBEK_YASI_AY, yasAyCinsinden).apply();
    }

    // 📅 Bebek yaşını ay olarak al
    public int getBebekYasiAyCinsinden() {
        return tercihler.getInt(ANAHTAR_BEBEK_YASI_AY, 0);
    }

    // 🛌 Tercih edilen uyku zamanını kaydet (gece yarısından itibaren dakika cinsinden)
    public void tercihEdilenUykuZamaniniKaydet(int geceYarisiSonraDakika) {
        tercihler.edit().putInt(ANAHTAR_TERCİH_EDİLEN_UYKU_ZAMANI, geceYarisiSonraDakika).apply();
    }

    // 🛌 Tercih edilen uyku zamanını al
    public int getTercihEdilenUykuZamani() {
        return tercihler.getInt(ANAHTAR_TERCİH_EDİLEN_UYKU_ZAMANI, 20 * 60); // Varsayılan: 20:00 (8 PM)
    }

    // ☀️ Tercih edilen uyanma zamanını kaydet (gece yarısından itibaren dakika cinsinden)
    public void tercihEdilenUyanmaZamaniniKaydet(int geceYarisiSonraDakika) {
        tercihler.edit().putInt(ANAHTAR_TERCİH_EDİLEN_UYANMA_ZAMANI, geceYarisiSonraDakika).apply();
    }

    // ☀️ Tercih edilen uyanma zamanını al
    public int getTercihEdilenUyanmaZamani() {
        return tercihler.getInt(ANAHTAR_TERCİH_EDİLEN_UYANMA_ZAMANI, 7 * 60); // Varsayılan: 07:00 (7 AM)
    }

    // 🔔 Bildirim durumunu ayarla
    public void bildirimlereIzinVer(boolean izinliMi) {
        tercihler.edit().putBoolean(ANAHTAR_BILDİRİM_ETKIN, izinliMi).apply();
    }

    // 🔔 Bildirim durumunu kontrol et
    public boolean bildirimlerEtkinMi() {
        return tercihler.getBoolean(ANAHTAR_BILDİRİM_ETKIN, true); // Varsayılan: Etkin
    }
} 