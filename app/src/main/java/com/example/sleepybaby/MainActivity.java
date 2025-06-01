package com.example.sleepybaby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChildAdapter adapter;
    private List<Child> childrenList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Üst barı gizle
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new DatabaseHelper(this);
        childrenList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewChildren);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChildAdapter(childrenList, this::onChildClick);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddChild = findViewById(R.id.fabAddChild);
        fabAddChild.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddChildActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildren();
    }

    private void loadChildren() {
        childrenList.clear();
        childrenList.addAll(dbHelper.getAllChildren());
        adapter.notifyDataSetChanged();
    }

    private void onChildClick(Child child) {
        Intent intent = new Intent(MainActivity.this, ChildDetailActivity.class);
        intent.putExtra("CHILD_ID", child.getId());
        intent.putExtra("CHILD_NAME", child.getName());
        startActivity(intent);
    }
}
