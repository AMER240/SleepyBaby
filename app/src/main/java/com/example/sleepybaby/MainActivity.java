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

        try {
            // طلب الأذونات المطلوبة
            requestRequiredPermissions();

            MaterialButton buttonAddChild = findViewById(R.id.buttonAddChild);
            buttonAddChild.setOnClickListener(v -> {
                Log.d(TAG, "Add child button clicked");
                Intent intent = new Intent(MainActivity.this, AddChildActivity.class);
                startActivity(intent);
            });
            
            MaterialButton buttonViewChildren = findViewById(R.id.buttonViewChildren);
            buttonViewChildren.setOnClickListener(v -> {
                Log.d(TAG, "View children button clicked");
                Intent intent = new Intent(MainActivity.this, ChildrenListActivity.class);
                startActivity(intent);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "حدث خطأ أثناء بدء التطبيق", Toast.LENGTH_LONG).show();
        }
    }

    private void requestRequiredPermissions() {
        // التحقق من أذونات الإشعارات
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
            }
        }

        // التحقق من إذن المنبهات الدقيقة
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        // طلب الأذونات الأخرى
        String[] permissions = {
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.VIBRATE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "Permission not granted: " + permissions[i]);
                    Toast.makeText(this, 
                        "بعض الميزات قد لا تعمل بشكل صحيح بدون الأذونات المطلوبة",
                        Toast.LENGTH_LONG).show();
                }
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
