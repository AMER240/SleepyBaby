package com.example.sleepybaby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SleepHistoryAdapter extends RecyclerView.Adapter<SleepHistoryAdapter.ViewHolder> {
    private List<SleepRecord> sleepRecords;
    private Context context;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public SleepHistoryAdapter(Context context, List<SleepRecord> sleepRecords) {
        this.context = context;
        this.sleepRecords = sleepRecords;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sleep_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SleepRecord record = sleepRecords.get(position);
        
        // Tarih formatı
        holder.textViewDate.setText(dateFormat.format(record.getSleepTime()));
        
        // Saat formatı
        holder.textViewStartTime.setText(timeFormat.format(record.getSleepTime()));
        holder.textViewEndTime.setText(timeFormat.format(record.getWakeTime()));
        
        // Kalite
        String qualityText = "Kalite: " + record.getQuality() + "/5";
        holder.textViewQuality.setText(qualityText);
        
        // Süre
        long durationMinutes = record.getDurationMinutes();
        int hours = (int) (durationMinutes / 60);
        int minutes = (int) (durationMinutes % 60);
        String durationText = String.format(Locale.getDefault(), "Süre: %d saat %d dakika", hours, minutes);
        holder.textViewDuration.setText(durationText);
        
        // Notlar
        String notes = record.getNotes();
        if (notes != null && !notes.trim().isEmpty()) {
            holder.textViewNotes.setVisibility(View.VISIBLE);
            holder.textViewNotes.setText("Not: " + notes);
        } else {
            holder.textViewNotes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sleepRecords.size();
    }

    public void updateSleepRecords(List<SleepRecord> newRecords) {
        this.sleepRecords = newRecords;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewStartTime;
        TextView textViewEndTime;
        TextView textViewQuality;
        TextView textViewDuration;
        TextView textViewNotes;

        ViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewStartTime = itemView.findViewById(R.id.textViewStartTime);
            textViewEndTime = itemView.findViewById(R.id.textViewEndTime);
            textViewQuality = itemView.findViewById(R.id.textViewQuality);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            textViewNotes = itemView.findViewById(R.id.textViewNotes);
        }
    }
} 