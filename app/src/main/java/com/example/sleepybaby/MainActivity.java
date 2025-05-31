package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.content.pm.PackageManager;
import android.Manifest;
import android.app.AlarmManager;
import android.provider.Settings;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // İzinleri kontrol et
        checkAndRequestPermissions();

        // AlarmManager izinlerini kontrol et
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                // Kullanıcıyı ayarlara yönlendir
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        // View'ları initialize et
        MaterialButton buttonAddChild = findViewById(R.id.buttonAddChild);
        MaterialButton buttonViewChildren = findViewById(R.id.buttonViewChildren);

        // Çocuk ekleme butonu
        buttonAddChild.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddChildActivity.class);
            startActivity(intent);
        });

        // Çocukları görüntüleme butonu
        buttonViewChildren.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChildrenListActivity.class);
            startActivity(intent);
        });
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi
                Toast.makeText(this, "Bildirim izni verildi", Toast.LENGTH_SHORT).show();
            } else {
                // İzin reddedildi
                Toast.makeText(this, "Bildirim izni reddedildi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Log.d(TAG, "onResume called, checking for existing children...");
            // Veritabanında çocuk var mı kontrol et
            DatabaseHelper db = new DatabaseHelper(this);
            List<Child> children = db.getAllChildren();
            Log.d(TAG, "Found " + children.size() + " children in database");
            
            // Geçici olarak otomatik yönlendirmeyi devre dışı bırak
            /*
            if (!children.isEmpty()) {
                Log.d(TAG, "Children found, navigating to ChildrenListActivity");
                // Çocuk varsa direkt çocuklar listesine git
                startActivity(new Intent(this, ChildrenListActivity.class));
                finish();
            } else {
                Log.d(TAG, "No children found, staying on MainActivity");
            }
            */
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Çocuklar yüklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
