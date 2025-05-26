package com.example.sleepybaby;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChildDetailActivity extends AppCompatActivity {
    private static final String TAG = "ChildDetailActivity";
    private int childId;
    private Child child;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewSleepHistory;
    private SleepHistoryAdapter sleepHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_detail);

        try {
            // Toolbar setup
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Child ID'yi al
            childId = getIntent().getIntExtra("child_id", -1);
            if (childId == -1) {
                Toast.makeText(this, "Çocuk bilgisi bulunamadı", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Database helper'ı initialize et
            databaseHelper = new DatabaseHelper(this);

            // View'ları initialize et
            TextView textViewChildName = findViewById(R.id.textViewChildName);
            TextView textViewChildAge = findViewById(R.id.textViewChildAge);
            TextView textViewSleepSchedule = findViewById(R.id.textViewSleepSchedule);
            TextView textViewRecommendedHours = findViewById(R.id.textViewRecommendedHours);
            TextView textViewWeeklyStats = findViewById(R.id.textViewWeeklyStats);
            TextView textViewMonthlyStats = findViewById(R.id.textViewMonthlyStats);
            recyclerViewSleepHistory = findViewById(R.id.recyclerViewSleepHistory);
            FloatingActionButton fabAddSleepRecord = findViewById(R.id.fabAddSleepRecord);

            // Çocuk bilgilerini yükle
            loadChildDetails();

            // Çocuk bilgilerini göster
            textViewChildName.setText(child.getName());
            textViewChildAge.setText(child.getAge() + " yaşında");
            textViewSleepSchedule.setText(String.format("Uyku: %02d:%02d - Uyanma: %02d:%02d",
                    child.getSleepHour(), child.getSleepMinute(),
                    child.getWakeHour(), child.getWakeMinute()));

            // Önerilen uyku saatlerini göster
            String recommendedHours = getRecommendedSleepHours(child.getAge());
            textViewRecommendedHours.setText(recommendedHours);

            // İstatistikleri yükle
            loadStatistics(textViewWeeklyStats, textViewMonthlyStats);

            // Uyku geçmişini yükle
            recyclerViewSleepHistory.setLayoutManager(new LinearLayoutManager(this));
            sleepHistoryAdapter = new SleepHistoryAdapter(new ArrayList<>());
            recyclerViewSleepHistory.setAdapter(sleepHistoryAdapter);
            loadSleepHistory();

            // Uyku kaydı ekleme butonu
            fabAddSleepRecord.setOnClickListener(v -> {
                // TODO: Uyku kaydı ekleme aktivitesini başlat
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu", Toast.LENGTH_LONG).show();
        }
    }

    private void loadChildDetails() {
        List<Child> children = databaseHelper.getAllChildren();
        for (Child c : children) {
            if (c.getId() == childId) {
                child = c;
                break;
            }
        }
        if (child == null) {
            Toast.makeText(this, "Çocuk bilgisi bulunamadı", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String getRecommendedSleepHours(int age) {
        if (age < 1) {
            return "0-1 yaş: 12-15 saat\n" +
                   "Gündüz: 2-3 uyku\n" +
                   "Gece: 9-11 saat";
        } else if (age < 3) {
            return "1-3 yaş: 11-14 saat\n" +
                   "Gündüz: 1-2 uyku\n" +
                   "Gece: 10-12 saat";
        } else if (age < 6) {
            return "3-6 yaş: 10-13 saat\n" +
                   "Gündüz: 0-1 uyku\n" +
                   "Gece: 10-12 saat";
        } else if (age < 13) {
            return "6-13 yaş: 9-11 saat\n" +
                   "Gece: 9-11 saat";
        } else {
            return "13+ yaş: 8-10 saat\n" +
                   "Gece: 8-10 saat";
        }
    }

    private void loadStatistics(TextView weeklyStatsView, TextView monthlyStatsView) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        // Haftalık istatistikler
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date weekAgo = calendar.getTime();
        SleepStatistics weeklyStats = databaseHelper.getSleepStatistics(childId, weekAgo);
        if (weeklyStats != null) {
            weeklyStatsView.setText(String.format("Haftalık Ortalama:\n" +
                    "Toplam Uyku: %d saat\n" +
                    "Günlük Ortalama: %.1f saat\n" +
                    "Uyku Kalitesi: %.1f/5",
                    weeklyStats.getTotalSleepMinutes() / 60,
                    (double) weeklyStats.getTotalSleepMinutes() / (7 * 60),
                    weeklyStats.getAverageSleepQuality()));
        }

        // Aylık istatistikler
        calendar.setTime(now);
        calendar.add(Calendar.MONTH, -1);
        Date monthAgo = calendar.getTime();
        SleepStatistics monthlyStats = databaseHelper.getSleepStatistics(childId, monthAgo);
        if (monthlyStats != null) {
            monthlyStatsView.setText(String.format("Aylık Ortalama:\n" +
                    "Toplam Uyku: %d saat\n" +
                    "Günlük Ortalama: %.1f saat\n" +
                    "Uyku Kalitesi: %.1f/5",
                    monthlyStats.getTotalSleepMinutes() / 60,
                    (double) monthlyStats.getTotalSleepMinutes() / (30 * 60),
                    monthlyStats.getAverageSleepQuality()));
        }
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
} 