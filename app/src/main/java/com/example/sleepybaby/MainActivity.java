package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            MaterialCardView cardViewAddChild = findViewById(R.id.cardViewAddChild);
            cardViewAddChild.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddChildActivity.class);
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
        // Veritabanında çocuk var mı kontrol et
        DatabaseHelper db = new DatabaseHelper(this);
        if (!db.getAllChildren().isEmpty()) {
            // Çocuk varsa direkt çocuklar listesine git
            startActivity(new Intent(this, ChildrenListActivity.class));
            finish();
        }
    }
}
