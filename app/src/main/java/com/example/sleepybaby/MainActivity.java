package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewChildren;
    private FloatingActionButton btnAddChild;
    private ChildAdapter childAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View'ları initialize et
        recyclerViewChildren = findViewById(R.id.recyclerViewChildren);
        btnAddChild = findViewById(R.id.btnAddChild);

        // RecyclerView setup
        recyclerViewChildren.setLayoutManager(new LinearLayoutManager(this));
        childAdapter = new ChildAdapter();
        recyclerViewChildren.setAdapter(childAdapter);

        // Çocuk ekleme butonu tıklama event'i
        btnAddChild.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddChildActivity.class);
            startActivity(intent);
        });
    }
}
