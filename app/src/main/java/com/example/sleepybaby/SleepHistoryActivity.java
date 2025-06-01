package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SleepHistoryActivity extends AppCompatActivity {
    private TextView textViewTitle;
    private MaterialButtonToggleGroup toggleGroupPeriod;
    private MaterialButton buttonWeek;
    private MaterialButton buttonMonth;
    private MaterialButton buttonYear;
    private TextView textViewAverageSleep;
    private TextView textViewSleepQuality;
    private TextView textViewBestSleep;
    private RecyclerView recyclerViewSleepHistory;
    private SleepHistoryAdapter adapter;
    private DatabaseHelper dbHelper;
    private long childId;
    private String childName;
    private int selectedPeriod = 7; // Varsayılan olarak haftalık

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_history);

        // Toolbar'ı ayarla
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Geri tuşu
        findViewById(R.id.buttonBack).setOnClickListener(v -> onBackPressed());

        // Intent'ten verileri al
        childId = getIntent().getLongExtra("CHILD_ID", -1);
        childName = getIntent().getStringExtra("CHILD_NAME");

        if (childId == -1 || childName == null) {
            Toast.makeText(this, "Çocuk bilgileri alınamadı", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // View'ları initialize et
        initializeViews();
        dbHelper = new DatabaseHelper(this);

        // Başlığı ayarla
        textViewTitle.setText(childName + " - Uyku Geçmişi");

        // RecyclerView'ı ayarla
        recyclerViewSleepHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SleepHistoryAdapter(new ArrayList<>());
        recyclerViewSleepHistory.setAdapter(adapter);

        // Filtre butonlarını ayarla
        toggleGroupPeriod.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.buttonWeek) {
                    selectedPeriod = 7;
                } else if (checkedId == R.id.buttonMonth) {
                    selectedPeriod = 30;
                } else if (checkedId == R.id.buttonYear) {
                    selectedPeriod = 365;
                }
                loadSleepHistory();
            }
        });

        // Varsayılan olarak haftalık seçili
        buttonWeek.setChecked(true);
    }

    private void initializeViews() {
        textViewTitle = findViewById(R.id.textViewTitle);
        toggleGroupPeriod = findViewById(R.id.toggleGroupPeriod);
        buttonWeek = findViewById(R.id.buttonWeek);
        buttonMonth = findViewById(R.id.buttonMonth);
        buttonYear = findViewById(R.id.buttonYear);
        textViewAverageSleep = findViewById(R.id.textViewAverageSleep);
        textViewSleepQuality = findViewById(R.id.textViewSleepQuality);
        textViewBestSleep = findViewById(R.id.textViewBestSleep);
        recyclerViewSleepHistory = findViewById(R.id.recyclerViewSleepHistory);
    }

    private void loadSleepHistory() {
        // Seçilen periyoda göre tarih aralığını hesapla
        Calendar endDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -selectedPeriod);

        // Uyku kayıtlarını getir
        List<SleepRecord> records = dbHelper.getSleepRecords(childId, startDate.getTimeInMillis(), 
            endDate.getTimeInMillis());
        adapter.setSleepRecords(records);

        // İstatistikleri güncelle
        updateStatistics(records);
    }

    private void updateStatistics(List<SleepRecord> records) {
        if (records.isEmpty()) {
            textViewAverageSleep.setText("Henüz uyku kaydı yok");
            textViewSleepQuality.setText("Uyku kalitesi: -");
            textViewBestSleep.setText("En iyi uyku: -");
            return;
        }

        // Ortalama uyku süresi
        double totalHours = 0;
        for (SleepRecord record : records) {
            totalHours += record.getDurationMinutes() / 60.0;
        }
        double avgHours = totalHours / records.size();
        textViewAverageSleep.setText(String.format(Locale.getDefault(), 
            "Ortalama uyku süresi: %.1f saat", avgHours));

        // Ortalama uyku kalitesi
        double totalQuality = 0;
        for (SleepRecord record : records) {
            totalQuality += record.getQuality();
        }
        double avgQuality = totalQuality / records.size();
        String qualityText;
        if (avgQuality >= 4.5) {
            qualityText = "Çok İyi";
        } else if (avgQuality >= 3.5) {
            qualityText = "İyi";
        } else if (avgQuality >= 2.5) {
            qualityText = "Orta";
        } else if (avgQuality >= 1.5) {
            qualityText = "Kötü";
        } else {
            qualityText = "Çok Kötü";
        }
        textViewSleepQuality.setText(String.format(Locale.getDefault(), 
            "Uyku kalitesi: %s (%.1f/5)", qualityText, avgQuality));

        // En iyi uyku
        SleepRecord bestSleep = records.get(0);
        for (SleepRecord record : records) {
            if (record.getQuality() > bestSleep.getQuality() || 
                (record.getQuality() == bestSleep.getQuality() && 
                 record.getDurationMinutes() > bestSleep.getDurationMinutes())) {
                bestSleep = record;
            }
        }
        textViewBestSleep.setText(String.format(Locale.getDefault(), 
            "En iyi uyku: %.1f saat, Kalite: %d/5", 
            bestSleep.getDurationMinutes() / 60.0, bestSleep.getQuality()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSleepHistory();
    }
} 