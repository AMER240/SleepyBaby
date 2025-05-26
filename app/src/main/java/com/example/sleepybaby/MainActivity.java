package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

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

public class MainActivity extends AppCompatActivity implements ChildAdapter.OnChildDeleteListener {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerViewChildren;
    private FloatingActionButton btnAddChild;
    private ChildAdapter childAdapter;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // View'ları initialize et
            recyclerViewChildren = findViewById(R.id.recyclerViewChildren);
            btnAddChild = findViewById(R.id.btnAddChild);
            databaseHelper = new DatabaseHelper(this);

            // RecyclerView setup
            recyclerViewChildren.setLayoutManager(new LinearLayoutManager(this));
            childAdapter = new ChildAdapter();
            childAdapter.setOnChildDeleteListener(this);
            recyclerViewChildren.setAdapter(childAdapter);

            // Veritabanından çocukları yükle
            loadChildrenFromDatabase();

            // Çocuk ekleme butonu tıklama event'i
            btnAddChild.setOnClickListener(v -> {
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
        loadChildrenFromDatabase();
    }

    private void loadChildrenFromDatabase() {
        try {
            List<Child> children = databaseHelper.getAllChildren();
            childAdapter.setChildList(children);
        } catch (Exception e) {
            Log.e(TAG, "Error loading children: " + e.getMessage());
            Toast.makeText(this, "Çocuklar yüklenirken hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChildDelete(int childId) {
        try {
            boolean deleted = databaseHelper.deleteChild(childId);
            if (deleted) {
                Toast.makeText(this, "Çocuk başarıyla silindi", Toast.LENGTH_SHORT).show();
                loadChildrenFromDatabase();
            } else {
                Toast.makeText(this, "Çocuk silinirken hata oluştu", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting child: " + e.getMessage());
            Toast.makeText(this, "Çocuk silinirken hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }
}
