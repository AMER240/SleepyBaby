package com.example.sleepybaby;

public class UykuGirisi {

    private long id;
    private long uykuZamaniMillis;    // milisaniye cinsinden uyku zamani
    private long uyanmaZamaniMillis;     // milisaniye cinsinden uyanis zamani
    private String notlar;            // ek notlar

    // Constructors
    public UykuGirisi() {}

    public UykuGirisi(long uykuZamaniMillis, long uyanmaZamaniMillis) {
        this.uykuZamaniMillis = uykuZamaniMillis;
        this.uyanmaZamaniMillis = uyanmaZamaniMillis;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUykuZamaniMillis() {
        return uykuZamaniMillis;
    }

    public void setUykuZamaniMillis(long uykuZamaniMillis) {
        this.uykuZamaniMillis = uykuZamaniMillis;
    }

    public long getUyanmaZamaniMillis() {
        return uyanmaZamaniMillis;
    }

    public void setUyanmaZamaniMillis(long uyanmaZamaniMillis) {
        this.uyanmaZamaniMillis = uyanmaZamaniMillis;
    }

    public String getNotlar() {
        return notlar;
    }

    public void setNotlar(String notlar) {
        this.notlar = notlar;
    }

    // dakikalar olarak uyku zamani hesplama
    public long getSleepDurationMinutes() {
        return (uyanmaZamaniMillis - uykuZamaniMillis) / (1000 * 60);
    }

    // saatlar olarak uyku zamani hesaplama
    public float getSleepDurationHours() {
        return getSleepDurationMinutes() / 60.0f;
    }
}
