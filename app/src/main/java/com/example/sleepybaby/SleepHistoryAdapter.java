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
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
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
        holder.textViewDate.setText(dateFormat.format(record.getSleepTime()));
        holder.textViewStartTime.setText(timeFormat.format(record.getSleepTime()));
        holder.textViewEndTime.setText(timeFormat.format(record.getWakeTime()));

        // Uyku kalitesi
        int quality = record.getQuality();
        String qualityText;
        if (quality >= 8) {
            qualityText = "Çok İyi";
        } else if (quality >= 6) {
            qualityText = "İyi";
        } else if (quality >= 4) {
            qualityText = "Orta";
        } else {
            qualityText = "İyileştirilmeli";
        }
        holder.textViewQuality.setText("Kalite: " + qualityText);

        // Uyku süresi
        long durationMinutes = (record.getWakeTime().getTime() - record.getSleepTime().getTime()) / (60 * 1000);
        int hours = (int) (durationMinutes / 60);
        int minutes = (int) (durationMinutes % 60);
        holder.textViewDuration.setText(String.format("Süre: %d saat %d dakika", hours, minutes));

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