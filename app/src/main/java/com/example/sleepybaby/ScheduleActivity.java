package com.example.sleepybaby;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity implements ChildrenAdapter.OnChildClickListener {
    private RecyclerView recyclerView;
    private ChildrenAdapter adapter;
    private List<Child> childList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        try {
            // Toolbar'ı ayarla
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }

            // Veritabanı yardımcısını başlat
            databaseHelper = new DatabaseHelper(this);
            childList = new ArrayList<>();

            // RecyclerView'ı ayarla
            recyclerView = findViewById(R.id.recyclerViewChildren);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ChildrenAdapter(this, childList, this);
            recyclerView.setAdapter(adapter);

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
            childList.clear();
            childList.addAll(databaseHelper.getAllChildren());
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Çocuklar yüklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChildClick(Child child) {
        // Çocuk detaylarını göster
        Toast.makeText(this, child.getName() + " seçildi", Toast.LENGTH_SHORT).show();
    }
}
