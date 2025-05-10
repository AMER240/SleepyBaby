package com.sleepybaby.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sleepybaby.data.repository.KullaniciTercihYoneticisi;
import com.sleepybaby.domain.usecase.UykuHesaplayici;
import com.sleepybaby.domain.usecase.UykuTavsiyeMotoru;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button addSleepButton;
    private Button addWakeButton;
    private Button viewScheduleButton;
    private KullaniciTercihYoneticisi tercihYoneticisi;
    private UykuTavsiyeMotoru tavsiyeMotoru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Bileşenleri başlat
        initializeComponents();
        
        // Click listener'ları ayarla
        setupClickListeners();

        // Window insets ayarla
        setupWindowInsets();
    }

    private void initializeComponents() {
        addSleepButton = findViewById(R.id.addSleepButton);
        addWakeButton = findViewById(R.id.addWakeButton);
        viewScheduleButton = findViewById(R.id.viewScheduleButton);
        
        tercihYoneticisi = new KullaniciTercihYoneticisi(this);
        tavsiyeMotoru = new UykuTavsiyeMotoru();
    }

    private void setupClickListeners() {
        addSleepButton.setOnClickListener(v -> {
            // TODO: Uyku zamanı ekleme ekranını aç
            Toast.makeText(this, "Uyku zamanı ekleme özelliği yakında eklenecek", Toast.LENGTH_SHORT).show();
        });

        addWakeButton.setOnClickListener(v -> {
            // TODO: Uyanış zamanı ekleme ekranını aç
            Toast.makeText(this, "Uyanış zamanı ekleme özelliği yakında eklenecek", Toast.LENGTH_SHORT).show();
        });

        viewScheduleButton.setOnClickListener(v -> {
            // TODO: Uyku programı görüntüleme ekranını aç
            Toast.makeText(this, "Uyku programı görüntüleme özelliği yakında eklenecek", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addWakeButton), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
} 