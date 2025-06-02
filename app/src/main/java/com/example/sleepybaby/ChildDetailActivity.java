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
import android.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;

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
    private TextView textViewMonthlyAverage;
    private TextView textViewMonthlyQuality;
    private TextView textViewRecommendedSleep;
    private MaterialButton buttonSetSleepTime;
    private MaterialButton buttonSetWakeTime;
    private FloatingActionButton fabAddSleepRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_detail);

        try {
            Log.d(TAG, "ChildDetailActivity onCreate started");
            
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
                Log.e(TAG, "Invalid child ID");
                Toast.makeText(this, "Geçersiz çocuk ID'si", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            Log.d(TAG, "Child ID: " + childId);

            // Initialize database helper
            databaseHelper = new DatabaseHelper(this);
            
            // Get child details
            child = databaseHelper.getChild(childId);
            if (child == null) {
                Log.e(TAG, "Child not found for ID: " + childId);
                Toast.makeText(this, "Çocuk bulunamadı", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            Log.d(TAG, "Child found: " + child.getName());

            // View'ları initialize et
            initializeViews();

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
            fabAddSleepRecord.setOnClickListener(v -> startAddSleepRecordActivity());

            // Check if we should show sleep quality dialog
            String action = getIntent().getAction();
            Log.d(TAG, "Activity created with action: " + action);
            if ("com.example.sleepybaby.SHOW_SLEEP_QUALITY".equals(action)) {
                Log.d(TAG, "Showing sleep quality dialog for child: " + child.getName());
                showSleepQualityDialog();
            }

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
        textViewMonthlyAverage = findViewById(R.id.textViewMonthlyAverage);
        textViewMonthlyQuality = findViewById(R.id.textViewMonthlyQuality);
        textViewRecommendedSleep = findViewById(R.id.textViewRecommendedSleep);
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

            // Yaşa göre önerilen uyku saatlerini göster
            String recommendedSleep = getRecommendedSleepHours(child.getAge());
            textViewRecommendedSleep.setText("Önerilen uyku süresi: " + recommendedSleep);

            // Uyku ve uyanma saatlerini göster
            if (child.getSleepHour() != -1 && child.getSleepMinute() != -1) {
                buttonSetSleepTime.setText(String.format(Locale.getDefault(), 
                    "Uyku Saati: %02d:%02d", child.getSleepHour(), child.getSleepMinute()));
            } else {
                buttonSetSleepTime.setText("Uyku Saati Ayarla");
            }

            if (child.getWakeHour() != -1 && child.getWakeMinute() != -1) {
                buttonSetWakeTime.setText(String.format(Locale.getDefault(), 
                    "Uyanma Saati: %02d:%02d", child.getWakeHour(), child.getWakeMinute()));
            } else {
                buttonSetWakeTime.setText("Uyanma Saati Ayarla");
            }

            // Uyku istatistiklerini yükle
            updateSleepStatistics();
        }
    }

    private String getRecommendedSleepHours(int age) {
        if (age < 1) {
            return "Gece: 9-11 saat\nGündüz: 3-4 kısa uyku (toplam 3-4 saat)\nToplam: 12-15 saat";
        } else if (age < 2) {
            return "Gece: 10-12 saat\nGündüz: 1-2 kısa uyku (toplam 1-2 saat)\nToplam: 11-14 saat";
        } else if (age < 3) {
            return "Gece: 10-12 saat\nGündüz: 1 kısa uyku (1-2 saat)\nToplam: 10-13 saat";
        } else if (age < 5) {
            return "Gece: 10-12 saat\nGündüz: 0-1 kısa uyku (0-1 saat)\nToplam: 10-13 saat";
        } else if (age < 13) {
            return "Gece: 9-11 saat\nGündüz: 0 saat\nToplam: 9-11 saat";
        } else if (age < 18) {
            return "Gece: 8-10 saat\nGündüz: 0 saat\nToplam: 8-10 saat";
        } else {
            return "Gece: 7-9 saat\nGündüz: 0 saat\nToplam: 7-9 saat";
        }
    }

    private void updateSleepStatistics() {
        // Haftalık ve aylık ortalama uyku süreleri
        double weeklyAvgSleepHours = databaseHelper.getAverageSleepHours(childId, 7);
        double monthlyAvgSleepHours = databaseHelper.getAverageSleepHours(childId, 30);
        
        // Haftalık ve aylık ortalama uyku kaliteleri
        double weeklyAvgQuality = databaseHelper.getSleepQuality(childId, 7);
        double monthlyAvgQuality = databaseHelper.getSleepQuality(childId, 30);
        
        // Haftalık kalite değerlendirmesi
        String weeklyQualityText;
        if (weeklyAvgQuality >= 4.5) {
            weeklyQualityText = "Çok İyi";
        } else if (weeklyAvgQuality >= 3.5) {
            weeklyQualityText = "İyi";
        } else if (weeklyAvgQuality >= 2.5) {
            weeklyQualityText = "Orta";
        } else if (weeklyAvgQuality >= 1.5) {
            weeklyQualityText = "Kötü";
        } else {
            weeklyQualityText = "Çok Kötü";
        }
        
        // Aylık kalite değerlendirmesi
        String monthlyQualityText;
        if (monthlyAvgQuality >= 4.5) {
            monthlyQualityText = "Çok İyi";
        } else if (monthlyAvgQuality >= 3.5) {
            monthlyQualityText = "İyi";
        } else if (monthlyAvgQuality >= 2.5) {
            monthlyQualityText = "Orta";
        } else if (monthlyAvgQuality >= 1.5) {
            monthlyQualityText = "Kötü";
        } else {
            monthlyQualityText = "Çok Kötü";
        }
        
        // Haftalık istatistikler
        textViewAverageSleep.setText(String.format(Locale.getDefault(), 
            "Haftalık ortalama: %.1f saat", weeklyAvgSleepHours));
            
        textViewSleepQuality.setText(String.format(Locale.getDefault(), 
            "Haftalık ortalama kalite: %s", weeklyQualityText));
            
        // Aylık istatistikler
        textViewMonthlyAverage.setText(String.format(Locale.getDefault(), 
            "Aylık ortalama: %.1f saat", monthlyAvgSleepHours));
            
        textViewMonthlyQuality.setText(String.format(Locale.getDefault(), 
            "Aylık ortalama kalite: %s", monthlyQualityText));
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
        loadSleepHistory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Uyku kaydı eklendiğinde detayları yenile
            loadChildDetails();
        }
    }

    private void startAddSleepRecordActivity() {
        Intent intent = new Intent(this, AddSleepRecordActivity.class);
        intent.putExtra("child_id", childId);
        intent.putExtra("child_name", child.getName());
        startActivityForResult(intent, 1);
    }

    private void showSleepQualityDialog() {
        try {
            Log.d(TAG, "Showing sleep quality dialog");

            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChildDetailActivity.this);
                builder.setTitle("Uyku Kalitesi");

                final String[] qualities = {"1 - Çok Kötü", "2 - Kötü", "3 - Orta", "4 - İyi", "5 - Çok İyi"};
                final int[] selectedQuality = {3}; // Default to "Orta"

                builder.setSingleChoiceItems(qualities, 2, (dialog, which) -> {
                    selectedQuality[0] = which + 1;
                    Log.d(TAG, "Selected quality: " + selectedQuality[0]);
                });

                builder.setPositiveButton("Tamam", (dialog, which) -> {
                    Log.d(TAG, "Quality dialog confirmed with value: " + selectedQuality[0]);
                    showNotesDialog(selectedQuality[0]);
                });

                builder.setNegativeButton("İptal", (dialog, which) -> {
                    Log.d(TAG, "Quality dialog cancelled");
                    dialog.cancel();
                });

                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(dialogInterface -> {
                    Log.d(TAG, "Dialog shown, setting default selection");
                    dialog.getListView().setItemChecked(2, true); // Set "Orta" as default
                });
                dialog.show();
                Log.d(TAG, "Sleep quality dialog shown");
            });
        } catch (Exception e) {
            Log.e(TAG, "Error showing sleep quality dialog: " + e.getMessage(), e);
            Toast.makeText(this, "Kalite seçimi gösterilirken hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotesDialog(int quality) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notlar");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(3);
        input.setGravity(Gravity.TOP | Gravity.START);
        builder.setView(input);

        builder.setPositiveButton("Kaydet", (dialog, which) -> {
            String notes = input.getText().toString();
            saveSleepRecord(quality, notes);
        });

        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    private void saveSleepRecord(int quality, String notes) {
        Calendar sleepCalendar = Calendar.getInstance();
        sleepCalendar.set(Calendar.HOUR_OF_DAY, child.getSleepHour());
        sleepCalendar.set(Calendar.MINUTE, child.getSleepMinute());
        sleepCalendar.set(Calendar.SECOND, 0);

        Calendar wakeCalendar = Calendar.getInstance();
        wakeCalendar.set(Calendar.HOUR_OF_DAY, child.getWakeHour());
        wakeCalendar.set(Calendar.MINUTE, child.getWakeMinute());
        wakeCalendar.set(Calendar.SECOND, 0);

        if (wakeCalendar.before(sleepCalendar)) {
            wakeCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        SleepRecord record = new SleepRecord();
        record.setChildId(childId);
        record.setSleepTime(sleepCalendar.getTime());
        record.setSleepHour(child.getSleepHour());
        record.setSleepMinute(child.getSleepMinute());
        record.setWakeHour(child.getWakeHour());
        record.setWakeMinute(child.getWakeMinute());
        record.setQuality(quality);
        record.setNotes(notes);

        long result = databaseHelper.addSleepRecord(record);
        if (result != -1) {
            Toast.makeText(this, "Uyku kaydı eklendi", Toast.LENGTH_SHORT).show();
            loadSleepHistory();
        } else {
            Toast.makeText(this, "Uyku kaydı eklenirken hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }
} 