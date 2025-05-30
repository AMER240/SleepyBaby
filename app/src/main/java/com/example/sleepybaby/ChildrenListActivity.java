package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ChildrenListActivity extends AppCompatActivity implements ChildrenAdapter.OnChildClickListener, ChildrenAdapter.OnChildDeleteListener {
    private static final String TAG = "ChildrenListActivity";
    private RecyclerView recyclerViewChildren;
    private ChildrenAdapter childrenAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ChildrenListActivity onCreate started");
        
        try {
            setContentView(R.layout.activity_children_list);
            Log.d(TAG, "Layout set successfully");

            // View'ları initialize et
            recyclerViewChildren = findViewById(R.id.recyclerViewChildren);
            FloatingActionButton fabAddSleepRecord = findViewById(R.id.fabAddSleepRecord);
            databaseHelper = new DatabaseHelper(this);
            Log.d(TAG, "Views initialized");

            // RecyclerView setup
            recyclerViewChildren.setLayoutManager(new LinearLayoutManager(this));
            childrenAdapter = new ChildrenAdapter(new ArrayList<>());
            childrenAdapter.setOnChildClickListener(this);
            childrenAdapter.setOnChildDeleteListener(this);
            recyclerViewChildren.setAdapter(childrenAdapter);
            Log.d(TAG, "RecyclerView setup completed");

            // Veritabanından çocukları yükle
            loadChildrenFromDatabase();

            // Uyku kaydı ekleme butonu
            fabAddSleepRecord.setOnClickListener(v -> {
                Log.d(TAG, "FAB clicked - starting AddSleepRecordActivity");
                Intent intent = new Intent(this, AddSleepRecordActivity.class);
                startActivity(intent);
            });
            
            Log.d(TAG, "ChildrenListActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildrenFromDatabase();
    }

    private void loadChildrenFromDatabase() {
        try {
            Log.d(TAG, "Loading children from database...");
            
            if (databaseHelper == null) {
                Log.e(TAG, "DatabaseHelper is null, initializing...");
                databaseHelper = new DatabaseHelper(this);
            }
            
            List<Child> children = databaseHelper.getAllChildren();
            Log.d(TAG, "Loaded " + children.size() + " children from database");
            
            for (Child child : children) {
                Log.d(TAG, "Child: " + child.getName() + ", Age: " + child.getAge() + ", Birth Year: " + child.getBirthDate());
            }
            
            if (childrenAdapter != null) {
                childrenAdapter.setChildList(children);
                Log.d(TAG, "Children list updated in adapter");
            } else {
                Log.e(TAG, "ChildrenAdapter is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading children: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Çocuklar yüklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChildClick(Child child) {
        Intent intent = new Intent(this, ChildDetailActivity.class);
        intent.putExtra("child_id", child.getId());
        startActivity(intent);
    }

    @Override
    public void onChildDelete(Child child, int position) {
        // Silme onayı dialog'u göster
        new AlertDialog.Builder(this)
                .setTitle("Çocuğu Sil")
                .setMessage(child.getName() + " adlı çocuğu silmek istediğinizden emin misiniz?\n\nBu işlem geri alınamaz ve tüm uyku kayıtları da silinecektir.")
                .setPositiveButton("Sil", (dialog, which) -> {
                    try {
                        boolean success = databaseHelper.deleteChild(child.getId());
                        if (success) {
                            Toast.makeText(this, child.getName() + " başarıyla silindi", Toast.LENGTH_SHORT).show();
                            loadChildrenFromDatabase(); // Listeyi yenile
                        } else {
                            Toast.makeText(this, "Çocuk silinirken hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error deleting child: " + e.getMessage());
                        Toast.makeText(this, "Silme işlemi sırasında hata: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("İptal", null)
                .show();
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