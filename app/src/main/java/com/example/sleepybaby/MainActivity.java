package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
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
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu", Toast.LENGTH_LONG).show();
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