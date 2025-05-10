package com.sleepybaby.domain.model;

public class UykuKaydi {
    private long id;
    private long uykuZamani;       // Uyku başlangıç zamanı (milisaniye cinsinden)
    private long uyanmaZamani;     // Uyanma zamanı (milisaniye cinsinden)
    private String notlar;         // Ek notlar

    // Varsayılan kurucu metod
    public UykuKaydi() {}

    // Parametreli kurucu metod
    public UykuKaydi(long uykuZamani, long uyanmaZamani) {
        this.uykuZamani = uykuZamani;
        this.uyanmaZamani = uyanmaZamani;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getNotlar() {
        return notlar;
    }

    public void setNotlar(String notlar) {
        this.notlar = notlar;
    }

    // Uyku süresini dakika olarak hesapla
    public long getUykuSuresiDakika() {
        return (uyanmaZamani - uykuZamani) / (1000 * 60);
    }

    // Uyku süresini saat olarak hesapla
    public float getUykuSuresiSaat() {
        return getUykuSuresiDakika() / 60.0f;
    }
} 