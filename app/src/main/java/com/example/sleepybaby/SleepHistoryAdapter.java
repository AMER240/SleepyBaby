package com.example.sleepybaby;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SleepHistoryAdapter extends RecyclerView.Adapter<SleepHistoryAdapter.SleepRecordViewHolder> {
    private List<SleepRecord> sleepRecords;
    private SimpleDateFormat dateFormat;

    public SleepHistoryAdapter(List<SleepRecord> sleepRecords) {
        this.sleepRecords = sleepRecords;
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("tr"));
    }

    public void setSleepRecords(List<SleepRecord> sleepRecords) {
        this.sleepRecords = sleepRecords;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SleepRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sleep_record, parent, false);
        return new SleepRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SleepRecordViewHolder holder, int position) {
        SleepRecord record = sleepRecords.get(position);
        
        // Tarih ve saat bilgilerini formatla
        String sleepTime = dateFormat.format(record.getSleepTime());
        String wakeTime = dateFormat.format(record.getWakeTime());
        
        // Uyku süresini hesapla (dakika cinsinden)
        long sleepDuration = (record.getWakeTime().getTime() - record.getSleepTime().getTime()) / (60 * 1000);
        int hours = (int) (sleepDuration / 60);
        int minutes = (int) (sleepDuration % 60);
        
        holder.textViewDate.setText(sleepTime);
        holder.textViewDuration.setText(String.format("%d saat %d dakika", hours, minutes));
        holder.textViewQuality.setText("Kalite: " + record.getSleepQuality() + "/5");
    }

    @Override
    public int getItemCount() {
        return sleepRecords != null ? sleepRecords.size() : 0;
    }

    static class SleepRecordViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewDuration;
        TextView textViewQuality;

        SleepRecordViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            textViewQuality = itemView.findViewById(R.id.textViewQuality);
        }
    }
} 