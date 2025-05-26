package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ChildrenListActivity extends AppCompatActivity implements ChildrenAdapter.OnChildClickListener {
    private static final String TAG = "ChildrenListActivity";
    private RecyclerView recyclerViewChildren;
    private ChildrenAdapter childrenAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);

        try {
            // Toolbar setup
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Çocuklarım");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // View'ları initialize et
            recyclerViewChildren = findViewById(R.id.recyclerViewChildren);
            FloatingActionButton fabAddSleepRecord = findViewById(R.id.fabAddSleepRecord);
            databaseHelper = new DatabaseHelper(this);

            // RecyclerView setup
            recyclerViewChildren.setLayoutManager(new LinearLayoutManager(this));
            childrenAdapter = new ChildrenAdapter(new ArrayList<>());
            childrenAdapter.setOnChildClickListener(this);
            recyclerViewChildren.setAdapter(childrenAdapter);

            // Veritabanından çocukları yükle
            loadChildrenFromDatabase();

            // Uyku kaydı ekleme butonu
            fabAddSleepRecord.setOnClickListener(v -> {
                // TODO: Uyku kaydı ekleme aktivitesini başlat
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildrenFromDatabase();
    }

    private void loadChildrenFromDatabase() {
        try {
            List<Child> children = databaseHelper.getAllChildren();
            childrenAdapter.setChildList(children);
        } catch (Exception e) {
            Log.e(TAG, "Error loading children: " + e.getMessage());
            Toast.makeText(this, "Çocuklar yüklenirken hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChildClick(Child child) {
        Intent intent = new Intent(this, ChildDetailActivity.class);
        intent.putExtra("child_id", child.getId());
        startActivity(intent);
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