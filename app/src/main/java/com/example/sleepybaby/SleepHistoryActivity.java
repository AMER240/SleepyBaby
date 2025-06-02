package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SleepHistoryActivity extends AppCompatActivity {
    private static final String TAG = "SleepHistoryActivity";
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
    private int childId;
    private String childName;
    private int selectedPeriod = 7; // Varsayılan olarak haftalık

    private enum FilterType {
        WEEK, MONTH, YEAR
    }

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
        childId = getIntent().getIntExtra("child_id", -1);
        childName = getIntent().getStringExtra("child_name");
        if (childId == -1 || childName == null) {
            Toast.makeText(this, "Geçersiz çocuk bilgisi", Toast.LENGTH_SHORT).show();
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
        adapter = new SleepHistoryAdapter(this, new ArrayList<>());
        recyclerViewSleepHistory.setAdapter(adapter);

        // Uyku kayıtlarını yükle
        loadSleepRecords();

        // Filtreleme butonlarını ayarla
        buttonWeek.setOnClickListener(v -> filterRecords(FilterType.WEEK));
        buttonMonth.setOnClickListener(v -> filterRecords(FilterType.MONTH));
        buttonYear.setOnClickListener(v -> filterRecords(FilterType.YEAR));
    }

    private void filterRecords(FilterType type) {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        
        switch (type) {
            case WEEK:
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, -1);
                break;
            case YEAR:
                calendar.add(Calendar.YEAR, -1);
                break;
        }
        
        long startTime = calendar.getTimeInMillis();
        List<SleepRecord> filteredRecords = dbHelper.getSleepRecords(childId, startTime, endTime);
        adapter.updateSleepRecords(filteredRecords);
        updateStatistics(filteredRecords);
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

    private void loadSleepRecords() {
        try {
            List<SleepRecord> records = dbHelper.getSleepRecords(childId);
            adapter.updateSleepRecords(records);
            updateStatistics(records);
        } catch (Exception e) {
            Log.e(TAG, "Error in loadSleepRecords: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Uyku kayıtları yüklenirken hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateStatistics(List<SleepRecord> records) {
        if (records.isEmpty()) {
            textViewAverageSleep.setText("0 saat");
            textViewSleepQuality.setText("Veri yok");
            textViewBestSleep.setText("En iyi uyku: -");
            return;
        }
        
        // Ortalama uyku süresi
        long totalMinutes = 0;
        double totalQuality = 0;
        
        for (SleepRecord record : records) {
            totalMinutes += record.getDurationMinutes();
            totalQuality += record.getQuality();
        }
        
        int averageHours = (int) (totalMinutes / records.size() / 60);
        int averageMinutes = (int) ((totalMinutes / records.size()) % 60);
        textViewAverageSleep.setText(String.format("%d saat %d dakika", averageHours, averageMinutes));
        
        // Ortalama uyku kalitesi
        double averageQuality = totalQuality / records.size();
        String qualityText;
        if (averageQuality >= 4) {
            qualityText = "Çok İyi";
        } else if (averageQuality >= 3) {
            qualityText = "İyi";
        } else if (averageQuality >= 2) {
            qualityText = "Orta";
        } else {
            qualityText = "Kötü";
        }
        textViewSleepQuality.setText(qualityText);

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
        loadSleepRecords();
    }
} 