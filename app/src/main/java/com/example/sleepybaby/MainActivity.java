package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import com.example.sleepybaby.ChildAdapter;
import com.example.sleepybaby.DatabaseHelper;
import com.example.sleepybaby.Child;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewChildren;
    private FloatingActionButton btnAddChild;
    private ChildAdapter childAdapter;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View'ları initialize et
        recyclerViewChildren = findViewById(R.id.recyclerViewChildren);
        btnAddChild = findViewById(R.id.btnAddChild);
        databaseHelper = new DatabaseHelper(this);

        // RecyclerView setup
        recyclerViewChildren.setLayoutManager(new LinearLayoutManager(this));
        childAdapter = new ChildAdapter();
        recyclerViewChildren.setAdapter(childAdapter);

        // Veritabanından çocukları yükle
        loadChildrenFromDatabase();

        // Çocuk ekleme butonu tıklama event'i
        btnAddChild.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddChildActivity.class);
            startActivity(intent);
        });
    }

    private void loadChildrenFromDatabase() {
        List<Child> children = databaseHelper.getAllChildren();
        childAdapter.setChildList(children);
    }
}
