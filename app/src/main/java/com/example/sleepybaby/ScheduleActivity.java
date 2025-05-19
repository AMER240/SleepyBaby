package com.example.sleepybaby;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    ChildrenAdapter adapter;
    DatabaseHelper databaseHelper;
    List<Child> childList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerView = findViewById(R.id.recyclerViewChildren);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        childList = databaseHelper.getAllChildren();

        if (childList.isEmpty()) {
            Toast.makeText(this, "Henüz çocuk eklenmedi!", Toast.LENGTH_SHORT).show();
        }

        adapter = new ChildrenAdapter(childList);
        recyclerView.setAdapter(adapter);

        // Listener حذف الطفل
        adapter.setOnChildDeleteListener(childId -> {
            boolean deleted = databaseHelper.deleteChild(childId);
            if (deleted) {
                Toast.makeText(this, "Çocuk silindi", Toast.LENGTH_SHORT).show();
                // تحديث القائمة
                childList.clear();
                childList.addAll(databaseHelper.getAllChildren());
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Silme başarısız oldu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
