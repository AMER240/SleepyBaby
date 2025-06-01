package com.example.sleepybaby;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SleepHistoryAdapter extends RecyclerView.Adapter<SleepHistoryAdapter.ViewHolder> {
    private List<SleepRecord> sleepRecords;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat timeFormat;

    public SleepHistoryAdapter(List<SleepRecord> sleepRecords) {
        this.sleepRecords = sleepRecords;
        this.dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("tr"));
        this.timeFormat = new SimpleDateFormat("HH:mm", new Locale("tr"));
    }

    public void setSleepRecords(List<SleepRecord> sleepRecords) {
        this.sleepRecords = sleepRecords;
        notifyDataSetChanged();
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
        
        // Tarih ve saat formatlaması
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        
        holder.textViewDate.setText(dateFormat.format(record.getStartTime()));
        holder.textViewTime.setText(String.format("%s - %s", 
            timeFormat.format(record.getStartTime()),
            timeFormat.format(record.getEndTime())));
        
        // Uyku süresi
        long durationMinutes = record.getDurationMinutes();
        int hours = (int) (durationMinutes / 60);
        int minutes = (int) (durationMinutes % 60);
        holder.textViewDuration.setText(String.format("%d saat %d dakika", hours, minutes));
        
        // Uyku kalitesi
        int quality = record.getQuality();
        String qualityText;
        if (quality >= 4) {
            qualityText = "Çok İyi";
        } else if (quality >= 3) {
            qualityText = "İyi";
        } else if (quality >= 2) {
            qualityText = "Orta";
        } else {
            qualityText = "Kötü";
        }
        holder.textViewQuality.setText(qualityText);

        // Notlar
        if (record.getNotes() != null && !record.getNotes().isEmpty()) {
            holder.textViewNotes.setVisibility(View.VISIBLE);
            holder.textViewNotes.setText(record.getNotes());
        } else {
            holder.textViewNotes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sleepRecords.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewDuration;
        TextView textViewTime;
        TextView textViewQuality;
        TextView textViewNotes;

        ViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewQuality = itemView.findViewById(R.id.textViewQuality);
            textViewNotes = itemView.findViewById(R.id.textViewNotes);
        }
    }
} 