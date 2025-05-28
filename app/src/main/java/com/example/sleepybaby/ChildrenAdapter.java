package com.example.sleepybaby;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private List<Child> childList;

    public ChildAdapter() {
        this.childList = new ArrayList<>();
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_item, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = childList.get(position);
        holder.bind(child);
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private TextView txtBirthDate;
        private TextView txtGender;
        private TextView txtSleepTime;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtBirthDate = itemView.findViewById(R.id.txtBirthDate);
            txtGender = itemView.findViewById(R.id.txtGender);
            txtSleepTime = itemView.findViewById(R.id.txtSleepTime);
        }

        public void bind(Child child) {
            txtName.setText(child.getName());
            txtBirthDate.setText(formatDate(child.getBirthDate()));
            txtGender.setText(child.getGender());
            txtSleepTime.setText(formatTime(child.getSleepHour(), child.getSleepMinute()));
        }

        private String formatDate(long date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(new Date(date));
        }

        private String formatTime(int hour, int minute) {
            return String.format("%02d:%02d", hour, minute);
        }
    }
}
