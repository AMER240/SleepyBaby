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

public class ChildrenListActivity extends AppCompatActivity implements ChildrenAdapter.OnChildClickListener {
    private static final String TAG = "ChildrenListActivity";
    
    private RecyclerView recyclerView;
    private ChildrenAdapter childrenAdapter;
    private List<Child> childrenList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);

        try {
            // Toolbar'ı ayarla
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }

            // Veritabanı yardımcısını başlat
            databaseHelper = new DatabaseHelper(this);
            childrenList = new ArrayList<>();

            // RecyclerView'ı ayarla
            recyclerView = findViewById(R.id.recyclerViewChildren);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            childrenAdapter = new ChildrenAdapter(this, childrenList, this);
            recyclerView.setAdapter(childrenAdapter);

            // Yeni çocuk ekleme butonu
            FloatingActionButton fabAddChild = findViewById(R.id.fabAddChild);
            fabAddChild.setOnClickListener(v -> {
                Intent intent = new Intent(ChildrenListActivity.this, AddChildActivity.class);
                startActivity(intent);
            });

            // Çocukları yükle
            loadChildren();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildren();
    }

    private void loadChildren() {
        try {
            childrenList.clear();
            childrenList.addAll(databaseHelper.getAllChildren());
            childrenAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Çocuklar yüklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChildClick(Child child) {
        Intent intent = new Intent(this, ChildDetailActivity.class);
        intent.putExtra("child_id", child.getId());
        intent.putExtra("child_name", child.getName());
        startActivity(intent);
    }
} 