package com.example.sleepybaby;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
            // Toolbar'ı ayarla
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            // Geri tuşu
            findViewById(R.id.buttonBack).setOnClickListener(v -> onBackPressed());

            // Intent'ten verileri al
            childId = getIntent().getIntExtra("CHILD_ID", -1);
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
            sleepHistoryAdapter = new SleepHistoryAdapter(new ArrayList<>());
            recyclerViewSleepHistory.setAdapter(sleepHistoryAdapter);
            loadSleepHistory();

            // Uyku kaydı ekleme butonu
            fabAddSleepRecord.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddSleepRecordActivity.class);
                intent.putExtra("CHILD_ID", childId);
                intent.putExtra("CHILD_NAME", child.getName());
                startActivity(intent);
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu", Toast.LENGTH_LONG).show();
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
            // Yaş hesapla
            Calendar birthDate = Calendar.getInstance();
            birthDate.setTimeInMillis(child.getBirthDate());
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            textViewAge.setText(age + " yaşında");
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
                } else {
                    child.setWakeHour(hourOfDay);
                    child.setWakeMinute(minute);
                    buttonSetWakeTime.setText(String.format(Locale.getDefault(), 
                        "Uyanma Saati: %02d:%02d", hourOfDay, minute));
                }
                databaseHelper.updateChild(child);
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }

    private void loadSleepHistory() {
        List<SleepRecord> records = databaseHelper.getSleepRecords(childId);
        sleepHistoryAdapter.setSleepRecords(records);
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