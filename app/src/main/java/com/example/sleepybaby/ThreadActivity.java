package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class ThreadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(ThreadActivity.this, MainActivity.class);
            startActivity(intent);
        }, 1200);
    }
}
