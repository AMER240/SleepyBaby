package com.example.sleepybaby;

public class Child {
    private int id;
    private String name;
    private long birthDate;
    private String gender;
    private int sleepHour;
    private int sleepMinute;
    private int wakeHour;
    private int wakeMinute;

    // Boş constructor
    public Child() {
        this.id = -1;
        this.name = "";
        this.birthDate = System.currentTimeMillis();
        this.sleepHour = 0;
        this.sleepMinute = 0;
        this.wakeHour = 0;
        this.wakeMinute = 0;
    }

    public Child(int id, String name, long birthDate, int sleepHour, int sleepMinute, int wakeHour, int wakeMinute) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.sleepHour = sleepHour;
        this.sleepMinute = sleepMinute;
        this.wakeHour = wakeHour;
        this.wakeMinute = wakeMinute;
    }

// Getter and Setter methods

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
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

    public int getAge() {
        java.util.Calendar now = java.util.Calendar.getInstance();
        int currentYear = now.get(java.util.Calendar.YEAR);

        // birthDate bir yıl değeri olarak saklanıyor
        int birthYear = (int) birthDate;

        return currentYear - birthYear;
    }
}
