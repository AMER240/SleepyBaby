package com.example.sleepybaby;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;

public class ChildDetailActivity extends AppCompatActivity {
    private static final String TAG = "ChildDetailActivity";
    private int childId;
    private Child child;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewSleepHistory;
    private SleepHistoryAdapter sleepHistoryAdapter;
    private TextView textViewTitle;
    private TextView textViewAge;
    private TextView textViewGender;
    private TextView textViewAverageSleep;
    private TextView textViewSleepQuality;
    private MaterialButton buttonSetSleepTime;
    private MaterialButton buttonSetWakeTime;
    private FloatingActionButton fabAddSleepRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_detail);

        try {
            // Bildirim izinlerini kontrol et
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001
                    );
                }
            }

            // Toolbar'ı ayarla
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            // Geri tuşu
            findViewById(R.id.buttonBack).setOnClickListener(v -> onBackPressed());

            // Intent'ten verileri al
            childId = getIntent().getIntExtra("child_id", -1);
            if (childId == -1) {
                Toast.makeText(this, "Geçersiz çocuk ID'si", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // View'ları initialize et
            initializeViews();
            databaseHelper = new DatabaseHelper(this);

            // Çocuk bilgilerini yükle
            loadChildDetails();

            // Çocuk bilgilerini göster
            textViewTitle.setText(child.getName());

            // Uyku saati ayarlama
            buttonSetSleepTime.setOnClickListener(v -> showTimePicker(true));

            // Uyanma saati ayarlama
            buttonSetWakeTime.setOnClickListener(v -> showTimePicker(false));

            // Uyku geçmişini yükle
            recyclerViewSleepHistory.setLayoutManager(new LinearLayoutManager(this));
            sleepHistoryAdapter = new SleepHistoryAdapter(this, new ArrayList<>());
            recyclerViewSleepHistory.setAdapter(sleepHistoryAdapter);
            loadSleepHistory();

            // Uyku kaydı ekleme butonu
            fabAddSleepRecord.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddSleepRecordActivity.class);
                intent.putExtra("child_id", childId);
                intent.putExtra("child_name", child.getName());
                startActivity(intent);
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewAge = findViewById(R.id.textViewAge);
        textViewGender = findViewById(R.id.textViewGender);
        textViewAverageSleep = findViewById(R.id.textViewAverageSleep);
        textViewSleepQuality = findViewById(R.id.textViewSleepQuality);
        buttonSetSleepTime = findViewById(R.id.buttonSetSleepTime);
        buttonSetWakeTime = findViewById(R.id.buttonSetWakeTime);
        recyclerViewSleepHistory = findViewById(R.id.recyclerViewSleepHistory);
        fabAddSleepRecord = findViewById(R.id.fabAddSleepRecord);
    }

    private void loadChildDetails() {
        child = databaseHelper.getChild(childId);
        if (child != null) {
            textViewTitle.setText(child.getName());
            textViewAge.setText(child.getAge() + " yaşında");
            textViewGender.setText(child.getGender());

            // Uyku istatistiklerini yükle
            loadSleepStats();
        }
    }

    private void loadSleepStats() {
        // Son 7 günlük ortalama uyku süresi
        double avgSleepHours = databaseHelper.getAverageSleepHours(childId, 7);
        textViewAverageSleep.setText(String.format(Locale.getDefault(), 
            "Son 7 günlük ortalama uyku süresi: %.1f saat", avgSleepHours));

        // Uyku kalitesi
        double sleepQuality = databaseHelper.getSleepQuality(childId, 7);
        String qualityText;
        if (sleepQuality >= 0.8) {
            qualityText = "Çok İyi";
        } else if (sleepQuality >= 0.6) {
            qualityText = "İyi";
        } else if (sleepQuality >= 0.4) {
            qualityText = "Orta";
        } else {
            qualityText = "İyileştirilmeli";
        }
        textViewSleepQuality.setText("Uyku kalitesi: " + qualityText);
    }

    private void showTimePicker(boolean isSleepTime) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                if (isSleepTime) {
                    child.setSleepHour(hourOfDay);
                    child.setSleepMinute(minute);
                    buttonSetSleepTime.setText(String.format(Locale.getDefault(), 
                        "Uyku Saati: %02d:%02d", hourOfDay, minute));
                    scheduleSleepTimeNotification(hourOfDay, minute);
                } else {
                    child.setWakeHour(hourOfDay);
                    child.setWakeMinute(minute);
                    buttonSetWakeTime.setText(String.format(Locale.getDefault(), 
                        "Uyanma Saati: %02d:%02d", hourOfDay, minute));
                    scheduleWakeTimeNotification(hourOfDay, minute);
                }
                databaseHelper.updateChild(child);
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }

    private void scheduleSleepTimeNotification(int hour, int minute) {
        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) {
                Log.e(TAG, "AlarmManager is null");
                return;
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.setAction("com.example.sleepybaby.SLEEP_TIME");
            intent.putExtra("child_id", childId);
            intent.putExtra("CHILD_NAME", child.getName());

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                childId * 2,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                    );
                } else {
                    Log.e(TAG, "Cannot schedule exact alarms");
                    Toast.makeText(this, "Tam zamanlı alarmlar için izin gerekli", Toast.LENGTH_LONG).show();
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
                );
            }

            Log.d(TAG, "Sleep time notification scheduled for: " + calendar.getTime());
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling sleep time notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void scheduleWakeTimeNotification(int hour, int minute) {
        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) {
                Log.e(TAG, "AlarmManager is null");
                return;
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.setAction("com.example.sleepybaby.WAKE_TIME");
            intent.putExtra("child_id", childId);
            intent.putExtra("CHILD_NAME", child.getName());

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                childId * 2 + 1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                    );
                } else {
                    Log.e(TAG, "Cannot schedule exact alarms");
                    Toast.makeText(this, "Tam zamanlı alarmlar için izin gerekli", Toast.LENGTH_LONG).show();
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
                );
            }

            Log.d(TAG, "Wake time notification scheduled for: " + calendar.getTime());
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling wake time notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSleepHistory() {
        try {
            List<SleepRecord> records = databaseHelper.getSleepRecords(childId);
            sleepHistoryAdapter = new SleepHistoryAdapter(this, records);
            recyclerViewSleepHistory.setAdapter(sleepHistoryAdapter);
            recyclerViewSleepHistory.setLayoutManager(new LinearLayoutManager(this));
        } catch (Exception e) {
            Log.e(TAG, "Error in loadSleepHistory: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Uyku geçmişi yüklenirken hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildDetails();
    }
} 