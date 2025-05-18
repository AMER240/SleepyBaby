package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    Button btnViewSchedule;
    Button btnAddChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnViewSchedule = findViewById(R.id.btnViewSchedule);
        btnAddChild = findViewById(R.id.btnAddChild);

        btnViewSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        btnAddChild.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddChildActivity.class);
            startActivity(intent);
        });
    }

}
